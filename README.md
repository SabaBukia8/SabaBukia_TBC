# Authentication App

A modern Android authentication application built with Kotlin, featuring login and registration
functionality using the ReqRes API.

## Features

### 🔐 Authentication

- **Login**: Secure user authentication with email and password
- **Registration**: New user registration with validation
- **Form Validation**: Client-side validation with detailed error messages
- **API Integration**: Real-time communication with ReqRes API

### 🎨 Modern UI/UX

- **Material Design 3**: Latest Material Design principles
- **Modern Color Palette**: Professional blue theme with great accessibility
- **Smooth Animations**: Fluid navigation transitions
- **Responsive Layout**: Optimized for different screen sizes
- **Loading States**: Visual feedback during API calls

### 🏗️ Architecture

- **MVVM Pattern**: Clean separation of concerns
- **StateFlow**: Reactive state management with Kotlin Coroutines
- **Navigation Component**: Type-safe navigation with animations
- **Repository Pattern**: Clean data layer abstraction
- **Retrofit + OkHttp**: Modern networking with logging

## Technical Stack

### Core Technologies

- **Kotlin**: 100% Kotlin codebase
- **Android Architecture Components**: ViewModel, LiveData, Navigation
- **Coroutines**: Asynchronous programming with StateFlow
- **View Binding**: Type-safe view access

### Networking

- **Retrofit 2.9.0**: REST API client
- **OkHttp 4.12.0**: HTTP client with logging interceptor
- **Kotlinx Serialization**: JSON serialization/deserialization

### UI/UX

- **Material Design 3**: Modern UI components
- **Custom Animations**: Smooth screen transitions
- **Vector Drawables**: Scalable icons and graphics

## API Integration

### Login Endpoint

```
POST https://reqres.in/api/login
Content-Type: application/json

{
  "email": "eve.holt@reqres.in",
  "password": "cityslicka"
}
```

### Registration Endpoint

```
POST https://reqres.in/api/register
Content-Type: application/json

{
  "email": "eve.holt@reqres.in",
  "password": "any_password"
}
```

**Note**: Registration is only allowed with the email `eve.holt@reqres.in`

## Project Structure

```
app/src/main/java/
├── model/
│   └── AuthModels.kt          # Data models for API requests/responses
├── network/
│   ├── AuthApiService.kt      # Retrofit API interface
│   └── NetworkClient.kt       # Network configuration
├── repository/
│   └── AuthRepository.kt      # Data repository with API calls
├── screen/auth/
│   ├── AuthViewModel.kt       # Shared ViewModel for auth screens
│   ├── LoginFragment.kt       # Login UI and logic
│   └── RegisterFragment.kt    # Registration UI and logic
├── util/
│   ├── AuthConstants.kt       # App constants
│   └── ValidationUtil.kt      # Input validation utilities
└── basics/
    └── BaseFragment.kt        # Base fragment class
```

## Validation Rules

### Email Validation

- Must not be empty
- Must be a valid email format
- For registration: Must be exactly `eve.holt@reqres.in`

### Password Validation

- Must not be empty
- Must be at least 4 characters long

## Error Handling

### Client-side Validation

- Empty fields detection
- Email format validation
- Password length validation
- Registration email restriction

### Server-side Error Handling

- Network error messages
- API error responses
- Timeout handling
- User-friendly error display

## UI Features

### Login Screen

- Welcome message with app branding
- Email input with validation
- Password input with visibility toggle
- Loading indicator during authentication
- Error message display
- Navigation to registration

### Registration Screen

- Create account branding
- Email input with restriction notice
- Password input with validation
- Loading indicator during registration
- Success feedback
- Navigation back to login

### Visual Design

- **Primary Color**: Royal Blue (#4169E1)
- **Background**: Light Blue (#FAFBFF)
- **Text**: High contrast dark colors
- **Inputs**: Rounded corners with subtle borders
- **Buttons**: Material Design with proper states

## Development Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app on device/emulator

### Requirements

- Android Studio Flamingo or newer
- Android SDK 24+ (API level 24)
- Compile SDK 36
- Kotlin 1.9+

## Architecture Highlights

### State Management

- **StateFlow**: Reactive UI updates
- **Loading States**: Visual feedback during operations
- **Error Handling**: Comprehensive error state management

### Navigation

- **Single Activity**: Modern single-activity architecture
- **Navigation Component**: Type-safe navigation
- **Animations**: Smooth screen transitions

### Best Practices

- **Separation of Concerns**: Clean architecture principles
- **Reactive Programming**: Kotlin Coroutines and Flow
- **Type Safety**: Null safety and type-safe navigation
- **Resource Management**: Proper lifecycle handling

## Testing

The app includes comprehensive input validation and error handling:

- Empty field validation
- Email format validation
- Network error handling
- API response validation

## Future Enhancements

- [ ] Biometric authentication
- [ ] Remember me functionality
- [ ] Password reset feature
- [ ] Social media login integration
- [ ] Dark theme support
- [ ] Offline mode support

---

Built with ❤️ using modern Android development practices