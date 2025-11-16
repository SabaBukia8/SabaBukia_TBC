# Migration Steps - Making the Refactored Code Compile

## Overview
This document outlines the steps needed to complete the migration to the new Clean Architecture structure.

---

## Files to Delete (Old Structure)

These files are now obsolete and should be deleted:

```bash
# Old DTOs (replaced by individual files in data/remote/dto/)
app/src/main/java/model/AuthModels.kt

# Old Repository (replaced by data/repository/AuthRepositoryImpl.kt)
app/src/main/java/repository/AuthRepository.kt

# Old Network files (moved to data/remote/network/)
app/src/main/java/network/AuthApiService.kt
app/src/main/java/network/AuthInterceptor.kt

# Old util files (moved to appropriate layers)
app/src/main/java/util/AuthConstants.kt
app/src/main/java/util/DatastoreKeys.kt
app/src/main/java/util/ValidationUtil.kt
app/src/main/java/util/StringResourceResolver.kt
```

---

## Fragments That Need Updating

The following Fragment files need to be updated to use the new UI state classes and handle navigation properly:

### 1. **SplashFragment.kt**
**Location**: `app/src/main/java/presentation/screen/splash/SplashFragment.kt`

**Status**: ✅ **ALREADY UPDATED**
- Package changed to `presentation.screen.splash`
- Uses `presentation.ui.state.SplashUiState`
- Navigation logic updated

### 2. **LoginFragment.kt**
**Location**: `app/src/main/java/presentation/screen/auth/LoginFragment.kt`

**Status**: ✅ **ALREADY UPDATED**
- Package changed to `presentation.screen.auth`
- Imports updated to `presentation.ui.state.AuthUiState`

### 3. **RegisterFragment.kt**
**Location**: `app/src/main/java/presentation/screen/auth/RegisterFragment.kt`

**Status**: ✅ **ALREADY UPDATED**
- Package changed to `presentation.screen.auth`
- Imports updated to `presentation.ui.state.AuthUiState`

### 4. **HomeFragment.kt**
**Location**: `app/src/main/java/presentation/screen/home/HomeFragment.kt`

**Status**: ✅ **ALREADY UPDATED**
- Package changed to `presentation.screen.home`
- Imports updated to `presentation.ui.state.HomeUiState`

### 5. **Navigation Graph**
**Location**: `app/src/main/res/Navigation/nav_graph.xml`

**Status**: ✅ **ALREADY UPDATED**
- All fragment references updated to `presentation.screen.*`

---

## Application Class Updates

**Location**: `app/src/main/java/com/example/sababukia_tbc/MyApplication.kt`

**Required Change**:
```kotlin
import presentation.util.StringResourceResolver

override fun onCreate() {
    super.onCreate()
    StringResourceResolver.initialize(this)
}
```

---

## Build Configuration

Ensure your `build.gradle.kts` (or `build.gradle`) has:

```kotlin
android {
    // ... other config

    // Enable ViewBinding if not already enabled
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
}
```

---

## Compilation Checklist

Follow these steps in order:

### Step 1: Delete Old Files
```bash
# Delete obsolete files listed above
rm app/src/main/java/model/AuthModels.kt
rm app/src/main/java/repository/AuthRepository.kt
rm -r app/src/main/java/network/
rm app/src/main/java/util/AuthConstants.kt
rm app/src/main/java/util/DatastoreKeys.kt
rm app/src/main/java/util/ValidationUtil.kt
rm app/src/main/java/util/StringResourceResolver.kt
```

### Step 2: Update Fragments
1. Update `SplashFragment.kt` to use `SplashUiState`
2. Update other fragments to import new UI state classes

### Step 3: Update Application Class
1. Import `presentation.util.StringResourceResolver`
2. Initialize in `onCreate()`

### Step 4: Clean and Rebuild
```bash
./gradlew clean
./gradlew build
```

### Step 5: Fix Any Remaining Import Errors
Run Android Studio's "Optimize Imports" on all files:
- `Code` → `Optimize Imports` (Ctrl+Alt+O on Windows/Linux, Cmd+Opt+O on Mac)

---

## Common Import Errors and Fixes

### Error: "Unresolved reference: AuthResult"
**Solution**: Replace with Kotlin's built-in `Result<T>`

```kotlin
// OLD:
import repository.AuthResult
when (val result = useCase()) {
    is AuthResult.Success -> { /* ... */ }
    is AuthResult.Error -> { /* ... */ }
}

// NEW:
useCase()
    .onSuccess { data -> /* ... */ }
    .onFailure { exception -> /* ... */ }
```

### Error: "Unresolved reference: model"
**Solution**: Update imports to use `domain.model` or `data.remote.dto`

```kotlin
// OLD:
import model.LoginRequest

// NEW:
import domain.model.LoginCredentials  // For domain layer
// OR
import data.remote.dto.LoginRequestDTO  // For data layer
```

### Error: "Unresolved reference: repository"
**Solution**: Update to use interface

```kotlin
// OLD:
import repository.AuthRepository

// NEW:
import domain.repository.IAuthRepository
```

### Error: "Unresolved reference: network"
**Solution**: Update to data layer network package

```kotlin
// OLD:
import network.AuthApiService

// NEW:
import data.remote.network.AuthApiService
```

### Error: "Unresolved reference: util"
**Solution**: Identify which util and use correct new location

```kotlin
// OLD:
import util.ValidationUtil
import util.StringResourceResolver
import util.DatastoreKeys
import util.AuthConstants

// NEW:
import presentation.util.ValidationUtil
import presentation.util.StringResourceResolver
import data.local.DatastoreKeys
import data.remote.ApiConstants
import domain.constants.ValidationConstants
```

---

## Testing the Migration

After compilation succeeds, test the following flows:

### 1. **Splash Screen**
- [ ] App starts successfully
- [ ] Splash screen shows for 2 seconds
- [ ] Navigates to Home if authenticated
- [ ] Navigates to Welcome if not authenticated

### 2. **Registration Flow**
- [ ] Can navigate to register screen
- [ ] Validation works correctly
- [ ] Can register with `eve.holt@reqres.in`
- [ ] Token is saved
- [ ] Navigates to home after registration

### 3. **Login Flow**
- [ ] Can navigate to login screen
- [ ] Validation works correctly
- [ ] Can login with credentials
- [ ] Token is saved
- [ ] Navigates to home after login

### 4. **Home Screen**
- [ ] Shows user information
- [ ] Logout button works
- [ ] Returns to welcome screen after logout

---

## Rollback Plan

If the migration causes issues, you can:

1. **Revert to previous commit**:
   ```bash
   git log --oneline  # Find the commit before refactoring
   git revert <commit-hash>
   ```

2. **Keep new structure but fix compilation**:
   - The new files don't conflict with old ones
   - You can keep both temporarily during migration
   - Delete old files only after verifying new ones work

---

## Support

If you encounter issues:

1. Check the `ARCHITECTURE_REFACTORING.md` for architecture details
2. Verify all imports are correct
3. Ensure Hilt modules are properly configured
4. Clean and rebuild the project
5. Invalidate Android Studio caches: `File` → `Invalidate Caches / Restart`

---

## Summary

The refactoring introduces:
- ✅ Clean Architecture (Presentation → Domain → Data)
- ✅ SOLID principles throughout
- ✅ Proper dependency injection with interfaces
- ✅ Separation of concerns
- ✅ Better testability and maintainability

The migration requires:
1. Deleting old files
2. Updating Fragment imports and logic
3. Updating Application class
4. Clean rebuild

**Estimated time**: 30-60 minutes for a careful migration
