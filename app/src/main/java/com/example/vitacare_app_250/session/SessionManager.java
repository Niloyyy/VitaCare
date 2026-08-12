package com.example.vitacare_app_250.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.data.model.Role;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The single source of truth for who is signed in and what they may see.
 *
 * <p>The previous design treated the presence of a cached ID token in
 * SharedPreferences as proof of a valid session. That was wrong in both
 * directions and in a way that mattered:
 *
 * <ul>
 *   <li>An ID token expires after an hour, but the cached copy never changed,
 *       so a stale string kept granting entry indefinitely — while the token
 *       itself was never sent anywhere, making it pure liability with no use.
 *   <li>Clearing the token on sign-out did not call
 *       {@code FirebaseAuth.signOut()}, so the Firebase user stayed signed in.
 *       The next launch found no token, showed the landing screen, and the
 *       previous account was still authenticated underneath.
 * </ul>
 *
 * <p>Now Firebase Auth owns the session and this class stores only the role —
 * the one fact that is not derivable from the auth state and would otherwise
 * cost a database round trip on every launch. Both must agree for a session to
 * count as valid, and the token is never persisted.
 */
public final class SessionManager {

    private static final String PREFS_NAME = "vitacare_session";
    private static final String KEY_ROLE = "role";
    private static final String KEY_UID = "uid";
    private static final String KEY_DISPLAY_NAME = "display_name";

    private final SharedPreferences prefs;
    private final FirebaseAuth auth;

    public SessionManager(@NonNull Context context) {
        this(context, FirebaseAuth.getInstance());
    }

    SessionManager(@NonNull Context context, @NonNull FirebaseAuth auth) {
        this.prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.auth = auth;
    }

    /**
     * Records a completed sign-in.
     *
     * @param uid  the Firebase uid the role was resolved for
     * @param role the account's role
     */
    public void onSignedIn(@NonNull String uid, @NonNull Role role, @Nullable String displayName) {
        prefs.edit()
                .putString(KEY_UID, uid)
                .putString(KEY_ROLE, role.key())
                .putString(KEY_DISPLAY_NAME, displayName)
                .apply();
    }

    /**
     * Ends the session on both sides. Signing out of Firebase and clearing the
     * local record must happen together; doing only one is what left the old
     * app in a half-authenticated state.
     */
    public void signOut() {
        auth.signOut();
        prefs.edit().clear().apply();
    }

    /**
     * The role of the signed-in user.
     *
     * <p>Returns {@code null} unless Firebase reports a current user <em>and</em>
     * the stored role belongs to that same uid. A mismatch means the record is
     * stale — a different account signed in elsewhere, or the app was restored
     * from a backup — so it is discarded rather than trusted.
     */
    public @Nullable Role currentRole() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return null;
        }
        String storedUid = prefs.getString(KEY_UID, null);
        if (storedUid == null || !storedUid.equals(user.getUid())) {
            return null;
        }
        return Role.fromKey(prefs.getString(KEY_ROLE, null));
    }

    /** True when Firebase and the local record agree on a signed-in user. */
    public boolean isSignedIn() {
        return currentRole() != null;
    }

    /** @return the current Firebase uid, or {@code null} if signed out */
    public @Nullable String uid() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getUid();
    }

    public @Nullable FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    /** @return the signed-in user's email, or {@code null} */
    public @Nullable String email() {
        FirebaseUser user = auth.getCurrentUser();
        return user == null ? null : user.getEmail();
    }

    /**
     * A name to greet the user by: their profile display name, the name saved
     * with their practice profile, or the local part of their email address as
     * a last resort.
     *
     * @return a non-empty name, or {@code null} if nothing usable is known
     */
    public @Nullable String displayName() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return null;
        }
        String fromProfile = user.getDisplayName();
        if (fromProfile != null && !fromProfile.trim().isEmpty()) {
            return fromProfile.trim();
        }
        String cached = prefs.getString(KEY_DISPLAY_NAME, null);
        if (cached != null && !cached.trim().isEmpty()) {
            return cached.trim();
        }
        String email = user.getEmail();
        if (email != null) {
            int at = email.indexOf('@');
            if (at > 0) {
                return email.substring(0, at);
            }
        }
        return null;
    }

    /** Caches the name shown in greetings, e.g. after the doctor form saves. */
    public void cacheDisplayName(@Nullable String displayName) {
        prefs.edit().putString(KEY_DISPLAY_NAME, displayName).apply();
    }

    /**
     * Discards the local record without touching Firebase Auth. Used when the
     * two have diverged and the user must sign in again to resolve it.
     */
    public void clearLocal() {
        prefs.edit().clear().apply();
    }
}
