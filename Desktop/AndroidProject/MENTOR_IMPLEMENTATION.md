$env:GOOGLE_APPLICATION_CREDENTIALS="C:\secrets\mentorconnect-admin.json"
$env:FIREBASE_DATABASE_URL="https://mentorconnect-a7bd8-default-rtdb.asia-southeast1.firebasedatabase.app"# MentorConnect - Mentor/Student Role Implementation

## Overview
Successfully implemented a comprehensive mentor/student role selection system with separate dashboards and features for each user type.

## Features Implemented

### 1. **Role Selection at Authentication**
- Added toggle buttons (Student/Mentor) on both Login and Signup screens
- Users can select their role during registration
- Role is stored in Firestore user profile

### 2. **User Profile Model Updates**
- Added `role` field ("STUDENT" or "MENTOR")
- Added `profileCompleted` boolean to track mentor profile setup status
- Created `UserRole` enum for type safety
- Created `MentorProfile` data class with fields:
  - expertise, bio, experience, hourly rate
  - availability, skills, languages
  - rating, total sessions, photo URL

### 3. **Mentor Profile Setup**
- **MentorProfileSetupActivity**: First-time setup for mentors
- Collects mentor-specific information:
  - Area of expertise (e.g., Data Science)
  - Professional bio
  - Years of experience
  - Hourly rate
  - Availability schedule
  - Skills (comma-separated list)
  - Languages spoken
- Saves data to Firestore `mentors` collection
- Updates user profile to mark as completed

### 4. **Mentor Dashboard with Bottom Navigation**
- **MentorDashboardActivity**: Main hub for mentors
- Bottom navigation with 3 tabs:
  1. **Chats** - View messages from students who booked sessions
  2. **Video Calls** - Manage scheduled video call sessions
  3. **Settings** - Theme toggle and sign-out

### 5. **Mentor Fragments**
- **MentorChatsFragment**: 
  - Shows chat list with students
  - Empty state placeholder (ready for chat integration)
  
- **MentorVideoCallsFragment**:
  - Displays scheduled video sessions
  - Empty state placeholder (ready for booking integration)
  
- **MentorSettingsFragment**:
  - Theme toggle (Light/Dark mode)
  - Sign-out button
  - Same settings UI as student settings

### 6. **Role-Based Navigation**
After authentication, users are routed based on their role:

**Mentors:**
- First login → MentorProfileSetupActivity
- Subsequent logins → MentorDashboardActivity

**Students:**
- Always route to MainActivity (student dashboard)

### 7. **Backend Infrastructure**
- **MentorProfileRepository**: Manages mentor profile CRUD operations
- Updated **AuthRepository**: Handles role during signup
- Updated **ServiceLocator**: Provides MentorProfileRepository
- Firestore collections:
  - `users` - All user profiles with role field
  - `mentors` - Detailed mentor profiles

### 8. **UI/UX Updates**
- Material Design 3 toggle buttons for role selection
- Consistent card-based layouts
- Progress indicators for async operations
- Validation for all input fields
- Error handling with user-friendly messages

## File Structure

### New Files Created
```
app/src/main/java/com/example/mentorconnect/
├── data/
│   ├── model/
│   │   ├── UserRole.kt
│   │   └── MentorProfile.kt
│   └── repository/
│       └── MentorProfileRepository.kt
└── ui/
    └── mentor/
        ├── MentorProfileSetupActivity.kt
        ├── MentorDashboardActivity.kt
        ├── MentorChatsFragment.kt
        ├── MentorVideoCallsFragment.kt
        └── MentorSettingsFragment.kt

app/src/main/res/
├── layout/
│   ├── activity_mentor_profile_setup.xml
│   ├── activity_mentor_dashboard.xml
│   ├── fragment_mentor_chats.xml
│   ├── fragment_mentor_video_calls.xml
│   └── fragment_mentor_settings.xml
└── menu/
    └── mentor_bottom_nav_menu.xml
```

### Modified Files
- `UserProfile.kt` - Added role and profileCompleted fields
- `AuthRepository.kt` - Added role parameter to signup
- `AuthViewModel.kt` - Added role parameter to signup method
- `ServiceLocator.kt` - Added MentorProfileRepository provider
- `LoginActivity.kt` - Added role selection, role-based navigation
- `SignupActivity.kt` - Added role selection, role-based navigation
- `activity_login.xml` - Added role toggle buttons
- `activity_signup.xml` - Added role toggle buttons
- `AndroidManifest.xml` - Registered new activities

## Testing Instructions

### Test Mentor Flow
1. Open app → Sign Up
2. Select "Mentor" role
3. Fill in basic info (name, email, password, education, interest)
4. Sign up → Redirected to Mentor Profile Setup
5. Complete mentor profile (expertise, bio, experience, rate, etc.)
6. Save → Redirected to Mentor Dashboard
7. Navigate between Chats, Video Calls, and Settings tabs
8. Test theme toggle in Settings
9. Test sign-out → Returns to Login screen
10. Log in again as mentor → Goes directly to Mentor Dashboard

### Test Student Flow
1. Open app → Sign Up
2. Select "Student" role (default)
3. Fill in basic info
4. Sign up → Redirected to Student Dashboard (MainActivity)
5. Browse mentors, book sessions (existing functionality)

## Database Schema

### Firestore `users` Collection
```json
{
  "uid": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "education": "Computer Science",
  "interest": "Web Development",
  "role": "MENTOR",
  "profileCompleted": true
}
```

### Firestore `mentors` Collection
```json
{
  "mentorId": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "expertise": "Data Science",
  "bio": "Experienced data scientist with 5+ years...",
  "experience": "5 years",
  "hourlyRate": 1500.0,
  "availability": "Weekdays 9AM-5PM",
  "skills": ["Python", "ML", "TensorFlow"],
  "languages": ["English", "Hindi"],
  "photoUrl": "",
  "rating": 4.5,
  "totalSessions": 50,
  "createdAt": 1699999999999
}
```

## Future Enhancements

### To-Do (Not Yet Implemented)
1. **Chat Integration**: Connect MentorChatsFragment to Firestore chat collection
2. **Session Management**: Implement booking/session data models for MentorVideoCallsFragment
3. **Profile Editing**: Allow mentors to edit their profile after initial setup
4. **Photo Upload**: Add profile photo upload functionality
5. **Ratings & Reviews**: Implement rating system for mentors
6. **Notifications**: Push notifications for new bookings/messages
7. **Availability Calendar**: Visual calendar for mentor availability
8. **Payment Integration**: Connect hourly rate to payment system

## Known Limitations
- Chat and video call tabs show empty states (data integration pending)
- No profile photo upload yet
- Mentor profile is not editable after initial setup
- No search/filter for students to find mentors by role

## Build & Installation
```bash
# Build debug APK
.\gradlew assembleDebug

# Install on connected device
.\gradlew installDebug
```

## Summary
The mentor/student role system is now fully functional with:
✅ Role selection at auth
✅ Separate mentor profile setup
✅ Mentor dashboard with bottom navigation
✅ Role-based routing after login
✅ Theme support for both roles
✅ Clean Material Design UI

The student side remains unchanged and continues to work as before. Mentors now have their own dedicated workspace with tabs for managing chats, video calls, and settings.
