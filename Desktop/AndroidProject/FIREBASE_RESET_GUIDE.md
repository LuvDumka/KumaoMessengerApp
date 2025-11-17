# 🚨 Firebase Emergency Reset & App Recovery Guide

## Critical Issue
App crashes on mentor login and slot booking due to:
1. Restrictive Firebase rules blocking reads/writes
2. Potential data structure mismatches

## Step 1: Revert Firebase Rules (DO THIS FIRST)

### Firestore Rules
1. Go to Firebase Console → Firestore Database → Rules
2. Replace with:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```
3. Click **Publish**

### Realtime Database Rules
1. Go to Firebase Console → Realtime Database → Rules
2. Replace with:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```
3. Click **Publish**

## Step 2: Reseed Firebase Data

Run these commands in PowerShell:

```powershell
cd C:\Users\ASUS\Desktop\AndroidProject\tools\firebase-seeder

# Set environment variables
$env:GOOGLE_APPLICATION_CREDENTIALS="C:\secrets\mentorconnect-admin.json"
$env:FIREBASE_DATABASE_URL="https://mentorconnect-a7bd8-default-rtdb.asia-southeast1.firebasedatabase.app"

# Reset and reseed everything
npm run seed -- --reset
npm run seed:auth -- --reset
```

## Step 3: Verify Seeded Data

### Check Firestore
1. Open Firebase Console → Firestore Database
2. Verify these collections exist with data:
   - `users` (4 documents)
   - `mentors` (2 documents)
   - `timeSlots` (5 documents)
   - `videoCallSessions` (2 documents)

### Check Realtime Database
1. Open Firebase Console → Realtime Database
2. Verify these nodes exist:
   - `messages/mentor_jane_ds_student_steve`
   - `chatThreads/mentor_jane_ds`
   - `chatThreads/student_steve`

### Check Authentication
1. Open Firebase Console → Authentication → Users
2. Should see 4 users:
   - jane@example.com (mentor)
   - rahul@example.com (mentor)
   - steve@example.com (student)
   - ayesha@example.com (student)

## Step 4: Rebuild & Install App

```powershell
cd C:\Users\ASUS\Desktop\AndroidProject
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## Step 5: Test Core Flows

### Test 1: Mentor Login
1. Open app
2. Login as: `jane@example.com` / `Mentor@123`
3. Should navigate to Mentor Dashboard
4. ✅ No crash

### Test 2: Student Login & Browse Mentors
1. Logout, login as: `steve@example.com` / `Student@123`
2. Should see mentor list
3. Tap on "Dr. Jane Patel"
4. ✅ Profile opens without crash

### Test 3: Booking Slots
1. In mentor profile, select a date (Nov 27 or 28)
2. Should see available slots
3. Tap an AVAILABLE slot
4. Enter message, book
5. ✅ Booking succeeds

### Test 4: Chat
1. In mentor profile, tap chat icon
2. Type a message, send
3. ✅ Message appears in chat

### Test 5: Mentor View Bookings
1. Login as mentor (jane@example.com)
2. Go to Video Calls tab
3. Should see booked sessions
4. ✅ Sessions display correctly

## Seeded Test Accounts

| Email | Password | Role | Purpose |
|-------|----------|------|---------|
| jane@example.com | Mentor@123 | MENTOR | Data Science mentor |
| rahul@example.com | Mentor@123 | MENTOR | UI/UX mentor |
| steve@example.com | Student@123 | STUDENT | Test student 1 |
| ayesha@example.com | Student@123 | STUDENT | Test student 2 |

## Expected Behavior After Reset

✅ Mentor login works
✅ Student can browse mentors
✅ Mentor profiles open without crash
✅ Slot booking works
✅ Chat messaging works
✅ Calendar shows dates properly
✅ Video call tab shows sessions

## If Issues Persist

### Get Crash Logs
```powershell
adb logcat -c
adb logcat | Select-String "AndroidRuntime|FATAL|MentorDetailActivity"
```

Run app until crash, copy the stack trace.

### Check Firebase Connection
1. Enable debug logging in app
2. Check if FirebaseApp is initialized
3. Verify google-services.json is correct

### Common Issues

**Issue**: "Permission denied" errors
**Fix**: Verify rules are set to `if true`

**Issue**: Mentor profile crashes
**Fix**: Check if `mentors` collection has `hourlyRate` as number

**Issue**: No slots showing
**Fix**: Verify timeSlots have dates in Nov 2025 (27-28)

**Issue**: Chat not working
**Fix**: Check Realtime DB URL in BuildConfig

## Production-Ready Rules (Apply After Testing)

Once everything works in test mode, gradually tighten rules:

### Firestore (Gradual)
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isSignedIn() {
      return request.auth != null;
    }
    
    match /users/{userId} {
      allow read, write: if isSignedIn();
    }
    
    match /mentors/{mentorId} {
      allow read: if true;
      allow write: if isSignedIn();
    }
    
    match /timeSlots/{slotId} {
      allow read: if isSignedIn();
      allow write: if isSignedIn();
    }
    
    match /videoCallSessions/{sessionId} {
      allow read, write: if isSignedIn();
    }
  }
}
```

### Realtime Database (Gradual)
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## Summary

1. **Revert rules to test mode** (5 min)
2. **Reseed data** (2 min)
3. **Rebuild app** (1 min)
4. **Test all flows** (10 min)
5. **Gradually tighten rules** (when stable)

The app will be fully functional in test mode. We can add proper security later once core features are confirmed working.
