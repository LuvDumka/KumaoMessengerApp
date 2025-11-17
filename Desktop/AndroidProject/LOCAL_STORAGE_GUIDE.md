# Local Storage Implementation Guide

## Overview

Your app now includes a complete **in-memory local storage system** that can replace Firebase entirely for development and testing. This allows you to use the app without Firebase when the backend is having issues.

## What Was Built

### 1. Local Data Store (`LocalDataStore.kt`)
- **In-memory storage** using `ConcurrentHashMap` (thread-safe)
- Stores: users, mentors, time slots, chat messages, video sessions
- Pre-seeded with test data:
  - **4 test users** (2 mentors, 2 students)
  - **2 mentors** with profiles
  - **5 time slots** across Nov 27-28, 2025
  - **2 pre-existing chat messages** between Jane and Steve

### 2. Local Repositories

All local repositories match the Firebase repository interfaces:

- **`LocalAuthRepository`**: Login/signup with hardcoded passwords
- **`LocalUserRepository`**: User profile operations
- **`LocalMentorRepository`**: Mentor listing
- **`LocalBookingRepository`**: Slot booking and management
- **`LocalChatRepository`**: Messaging functionality

### 3. Service Locator

`ServiceLocator.kt` has a toggle constant:
```kotlin
private const val USE_LOCAL_STORAGE = false  // Currently using Firebase
```

## Test Accounts

### Mentors
| Email | Password | Name | Expertise | Rate |
|-------|----------|------|-----------|------|
| jane@example.com | Mentor@123 | Dr. Jane Patel | Data Science | ₹1800/hr |
| rahul@example.com | Mentor@123 | Rahul Mehta | UI/UX Design | ₹1500/hr |

### Students
| Email | Password | Name |
|-------|----------|------|
| steve@example.com | Student@123 | Steve Lee |
| ayesha@example.com | Student@123 | Ayesha Khan |

## Pre-Seeded Data

### Time Slots
- **Jane (Nov 27)**: 09:00 AM (available), 10:00 AM (booked by Steve)
- **Jane (Nov 28)**: 06:30 PM (available)
- **Rahul (Nov 27)**: 03:00 PM (available)
- **Rahul (Nov 28)**: 11:00 AM (booked by Ayesha)

### Chat Messages
- Conversation between Jane and Steve with 2 messages

## How to Enable Local Storage

### Current Status
**Firebase mode is active** (USE_LOCAL_STORAGE = false). The app currently compiles and works with Firebase.

### To Switch to Local Storage (No Firebase):

1. **Open `ServiceLocator.kt`** (`app/src/main/java/com/example/mentorconnect/core/ServiceLocator.kt`)

2. **Change the constant**:
   ```kotlin
   private const val USE_LOCAL_STORAGE = true  // Enable local storage
   ```

3. **Update provider methods** to return common interfaces or `Any`:
   ```kotlin
   fun provideAuthRepository(): Any = 
       if (USE_LOCAL_STORAGE) localAuthRepository else firebaseAuthRepository
   ```

4. **Fix type casting** in activities/fragments that use repositories:
   ```kotlin
   // Before:
   val authRepo = ServiceLocator.provideAuthRepository()
   
   // After:
   val authRepo = ServiceLocator.provideAuthRepository() as AuthRepository
   ```

5. **Update ViewModel constructors** to accept both types or use `Any`

### Files That Need Updates for Full Local Storage:

- [ ] `LoginActivity.kt` - Cast authRepository, userRepository
- [ ] `SignupActivity.kt` - Cast authRepository
- [ ] `MentorListFragment.kt` - Cast mentorRepository
- [ ] `ProfileFragment.kt` - Cast userRepository, authRepository
- [ ] `SettingsFragment.kt` - Use ServiceLocator.logout() instead of authRepository.logout()
- [ ] `MentorSettingsFragment.kt` - Same as SettingsFragment
- [ ] `MentorProfileSetupActivity.kt` - Cast userRepository
- [ ] `BookingViewModel.kt` - Get from ServiceLocator instead of direct instantiation
- [ ] `ChatViewModel.kt` - Get from ServiceLocator
- [ ] `MentorChatsViewModel.kt` - Get from ServiceLocator
- [ ] `MentorVideoCallsViewModel.kt` - Get from ServiceLocator

## Recommended Approach: Create Common Interfaces

For a cleaner solution, create base interfaces that both Firebase and Local repositories implement:

```kotlin
interface IAuthRepository {
    suspend fun login(email: String, password: String): Resource<Unit>
    suspend fun signup(...): Resource<Unit>
    fun logout()
}

class AuthRepository(...) : IAuthRepository { ... }
class LocalAuthRepository : IAuthRepository { ... }
```

Then ServiceLocator can return the interface type:
```kotlin
fun provideAuthRepository(): IAuthRepository = 
    if (USE_LOCAL_STORAGE) localAuthRepository else firebaseAuthRepository
```

## Using ServiceLocator Helper Methods

ServiceLocator now provides helper methods that work with both modes:

```kotlin
// Check if user is logged in
if (ServiceLocator.isUserLoggedIn()) {
    // Navigate to main screen
}

// Get current user (works with both Firebase and Local)
val currentUser = ServiceLocator.getCurrentUser()

// Logout
ServiceLocator.logout()
```

## Benefits of Local Storage

✅ **No Firebase dependency** - Work offline or when Firebase has issues  
✅ **Instant testing** - Pre-seeded data ready to go  
✅ **Fast iteration** - No network delays  
✅ **Consistent test data** - Same data every time you restart  
✅ **Easy debugging** - All data in memory, easy to inspect  

## Limitations

⚠️ **Data is temporary** - Lost when app closes  
⚠️ **No persistence** - Can't save changes between sessions  
⚠️ **Single device** - Data not synced across devices  
⚠️ **Hardcoded passwords** - Not secure, only for testing  

## Next Steps

1. **Test with Firebase first** - Make sure existing functionality works
2. **Fix Firebase rules** - Open rules temporarily for testing
3. **Implement local storage toggle** - Follow the update guide above
4. **Test local mode** - Login with test accounts, book slots, send messages
5. **Switch back to Firebase** - Once backend issues are resolved

## When to Use Which Mode

### Use Firebase Mode When:
- Deploying to production
- Testing with multiple users
- Need data persistence
- Testing auth flows

### Use Local Mode When:
- Firebase is down
- Rapid prototyping
- UI development
- Offline development
- Demo/presentation

## Support

If you encounter issues:
1. Check ServiceLocator.USE_LOCAL_STORAGE setting
2. Verify test credentials (Mentor@123 / Student@123)
3. Clear app data and restart
4. Check logcat for "LocalDataStore" or "LocalAuthRepository" tags

---

**Created**: December 2024  
**Status**: Ready for integration (currently in Firebase mode)
