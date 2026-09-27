# Keep Compose runtime metadata
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }

# Keep ViewModel & related
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep our app model classes
-keep class com.terminal.app.** { *; }
