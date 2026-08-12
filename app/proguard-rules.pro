# ---------------------------------------------------------------------------
# VitaCare ProGuard / R8 configuration
# ---------------------------------------------------------------------------

# Keep line numbers so release crash reports stay readable, but hide the
# original source file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Annotations are used by the Firebase Realtime Database mapper (@PropertyName,
# @Exclude, @IgnoreExtraProperties) and must survive shrinking.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ---------------------------------------------------------------------------
# Firebase Realtime Database
#
# The RTDB mapper instantiates model classes reflectively and matches getters /
# setters by name. Without these rules every field silently deserialises to
# null in release builds.
# ---------------------------------------------------------------------------
-keepclassmembers class com.example.vitacare_app_250.data.model.** {
    <init>();
    public <methods>;
}
-keep class com.example.vitacare_app_250.data.model.** { *; }

-keepclassmembers class * {
    @com.google.firebase.database.PropertyName <methods>;
    @com.google.firebase.database.PropertyName <fields>;
}

-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firebase Auth / Play Services surface reachable via reflection.
-keep class com.google.android.gms.common.annotation.KeepName
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

# ---------------------------------------------------------------------------
# AndroidX / Material
# ---------------------------------------------------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Views inflated from XML need their (Context, AttributeSet) constructor.
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ---------------------------------------------------------------------------
# Strip verbose logging from release builds.
# ---------------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
