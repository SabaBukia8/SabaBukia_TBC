# MTG Collection Manager App

## Architecture Refactoring

This project has been refactored to improve the architecture and follow better clean architecture principles. The main changes include:

### Repository Layer Improvements

1. **Single Responsibility Principle**
   - Separated sync logic from repositories
   - Created dedicated sync managers for collection and card operations
   - Simplified repository implementations

2. **Reduced Duplication**
   - Created common extensions for error handling (`toAppError`)
   - Added utilities for ID conversions (`toFirestoreId`, `toPositiveLongId`)
   - Implemented flow helpers (`resourceFlow`) for cleaner code

3. **Base Repository**
   - Created a BaseRepository class with common functionality
   - Standardized network-aware operations
   - All repositories now extend from this base class

### Sync Layer

1. **Dedicated Sync Managers**
   - CollectionSyncManager: Handles synchronization of collections between Firestore and local database
   - CardSyncManager: Handles synchronization of cards between Firestore and local database

### Utility Classes

1. **Error Handling**
   - Added ErrorMapper to convert domain errors to user-friendly messages
   - Standardized error flow throughout the application

### Interface Updates

1. **NetworkAwareRepository**
   - Updated the interface to include syncFromRemote method
   - Standardized approach to network operations

## Benefits

- **Cleaner Code**: Reduced duplication, better organization
- **Better Separation of Concerns**: Each class has a clear responsibility
- **Improved Testability**: Easier to test individual components
- **Enhanced Maintainability**: Easier to understand and extend
- **Simplified Error Handling**: Consistent approach to errors across the app