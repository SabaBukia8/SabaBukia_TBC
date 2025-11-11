# Troubleshooting Guide

## Build Issues Resolution

If the app is not launching and has build errors, follow these steps:

### 1. Clean and Rebuild Project

In Android Studio:

```
Build > Clean Project
Build > Rebuild Project
```

Or via command line:

```bash
./gradlew clean
./gradlew build
```

### 2. Sync Gradle Dependencies

In Android Studio:

```
File > Sync Project with Gradle Files
```

### 3. Check Common Issues

#### Missing Gradle Dependencies

Ensure these dependencies are in `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

#### Android SDK Version Issues

Check `app/build.gradle.kts`:

```kotlin
android {
    compileSdk = 34
    targetSdk = 34
    minSdk = 24
}
```

#### Java Version Compatibility

Ensure Java 8 compatibility:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
kotlinOptions {
    jvmTarget = "1.8"
}
```

### 4. Resolve Import Issues

If you see import errors:

1. Check that all package names are correct
2. Ensure all required files exist:
    - `app/src/main/java/network/AuthApiService.kt`
    - `app/src/main/java/network/NetworkClient.kt`
    - `app/src/main/java/repository/AuthRepository.kt`
    - `app/src/main/java/model/AuthModels.kt`

### 5. Navigation Issues

If navigation fails:

1. Ensure `nav_graph.xml` exists in `res/navigation/`
2. Check that fragment names match exactly
3. Verify animation files exist in `res/anim/`

### 6. Resource Issues

Missing resources can cause build failures:

**Colors** - Check `res/values/colors.xml` has:

- `primary_color`
- `background_color`
- `text_primary`
- `text_secondary`
- `error_color`
- `success_color`

**Strings** - Check `res/values/strings.xml` has all required strings

**Drawables** - Ensure these exist:

- `ic_app_logo.xml`
- `ic_email.xml`
- `ic_success.xml`

### 7. Quick Fix Steps

1. **Delete build directory**:
   ```bash
   rm -rf app/build
   ```

2. **Invalidate Caches**:
   In Android Studio: `File > Invalidate Caches and Restart`

3. **Update Gradle Wrapper** (if needed):
   ```bash
   ./gradlew wrapper --gradle-version=8.0
   ```

### 8. Minimal Working Configuration

If issues persist, ensure your `app/build.gradle.kts` matches:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.navigation.safeargs.kotlin)
}

android {
    namespace = "com.example.sababukia_tbc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sababukia_tbc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        viewBinding = true
    }
}
```

### 9. Test Basic Functionality

Once the app builds successfully:

1. **Login Test**:
    - Email: `eve.holt@reqres.in`
    - Password: Any password

2. **Registration Test**:
    - Email: `eve.holt@reqres.in`
    - Password: Any password

### 10. Common Error Messages

**"Unresolved reference"** → Missing dependency or import
**"Cannot resolve symbol"** → Missing resource file
**"Duplicate class"** → Clean and rebuild
**"Navigation error"** → Check nav_graph.xml and fragment names

---

## Support

If you continue experiencing issues:

1. Check the exact error message in Build Output
2. Look for missing files or incorrect imports
3. Ensure all dependencies are synced
4. Verify resource files exist and are properly named

The app should build and run successfully after following these steps!