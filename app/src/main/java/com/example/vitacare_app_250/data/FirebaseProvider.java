package com.example.vitacare_app_250.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vitacare_app_250.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Single point of access to the Firebase SDKs.
 *
 * <p>Two problems this solves.
 *
 * <p><b>The database URL.</b> This project's {@code google-services.json} has no
 * {@code firebase_url} entry, so the no-argument
 * {@code FirebaseDatabase.getInstance()} the old code called cannot infer an
 * endpoint and throws {@code DatabaseException} — every data-backed screen would
 * die on first use. The URL is therefore supplied explicitly from
 * {@code R.string.firebase_database_url}. See the comment on that resource for
 * how to make the override unnecessary.
 *
 * <p><b>Failing safely.</b> If the URL is missing or malformed the failure is
 * caught once, here, and reported through {@link #isAvailable()} so screens can
 * show a real message instead of crashing.
 */
public final class FirebaseProvider {

    private static final String TAG = "FirebaseProvider";

    private static volatile @Nullable FirebaseDatabase database;
    private static volatile boolean installed;

    private FirebaseProvider() {
    }

    /**
     * Resolves the database and enables offline persistence. Call once, from
     * {@code Application.onCreate()} — {@code setPersistenceEnabled} throws if
     * any database reference already exists.
     */
    public static synchronized void install(@NonNull Context context) {
        if (installed) {
            return;
        }
        installed = true;

        Context app = context.getApplicationContext();
        String url = app.getString(R.string.firebase_database_url).trim();
        if (url.isEmpty()) {
            Log.e(TAG, "firebase_database_url is empty; database features are disabled.");
            return;
        }

        try {
            FirebaseDatabase instance = FirebaseDatabase.getInstance(url);
            if (app.getResources().getBoolean(R.bool.firebase_persistence_enabled)) {
                // Keeps the doctor, hospital and donor lists readable offline.
                instance.setPersistenceEnabled(true);
            }
            database = instance;
        } catch (IllegalStateException e) {
            // Persistence was already locked in by an earlier database access.
            // The instance itself is still usable.
            Log.w(TAG, "Could not enable persistence; continuing without it.", e);
            try {
                database = FirebaseDatabase.getInstance(url);
            } catch (RuntimeException inner) {
                Log.e(TAG, "Realtime Database is unavailable: " + url, inner);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "Realtime Database is unavailable: " + url, e);
        }
    }

    /**
     * @return whether the database resolved. When false, screens should show
     *         {@code R.string.error_database_unavailable} rather than attempting
     *         a read that cannot succeed.
     */
    public static boolean isAvailable() {
        return database != null;
    }

    /**
     * A reference to {@code path} at the root of the database.
     *
     * @throws IllegalStateException if the database never resolved; guard with
     *                               {@link #isAvailable()} at screen level
     */
    public static @NonNull DatabaseReference reference(@NonNull String path) {
        FirebaseDatabase instance = database;
        if (instance == null) {
            throw new IllegalStateException(
                    "Realtime Database is not configured. See R.string.firebase_database_url.");
        }
        return instance.getReference(path);
    }

    public static @NonNull FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }

    /**
     * Test seam: lets unit tests reset the one-time install guard.
     */
    static synchronized void resetForTesting() {
        installed = false;
        database = null;
    }
}
