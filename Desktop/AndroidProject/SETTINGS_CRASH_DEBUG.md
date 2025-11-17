# Settings Screen Crash Troubleshooting Guide

## Current Status
✅ **Build is successful** - The code compiles without errors
⚠️ **App crashes when navigating to Settings** - Need to identify the exact cause

## Steps to Diagnose the Crash

### Step 1: View Crash Logs
Run this command in your terminal to see the crash details:

```powershell
adb logcat -c  # Clear previous logs
adb logcat | Select-String "AndroidRuntime|FATAL|Exception"
```

Then open your app and navigate to Settings. The crash log will appear.

### Step 2: Common Crash Causes & Solutions

#### A. **ViewBinding Not Initialized**
**Symptom**: `NullPointerException` on binding
**Solution**: Already handled with try-catch blocks

#### B. **ServiceLocator Not Initialized**
**Symptom**: `UninitializedPropertyAccessException` or `NullPointerException`
**Check**: Ensure `MentorApp` is declared in `AndroidManifest.xml`
```xml
<application android:name=".MentorApp" ...>
```

#### C. **Theme Repository Context Issue**
**Symptom**: Context-related exceptions
**Solution**: Already implemented fallback UI

#### D. **ViewModel Factory Issue**
**Symptom**: `InstantiationException` or factory errors
**Solution**: Using proper ViewModelProvider pattern

### Step 3: Quick Test - Simplified Settings Fragment

If the crash persists, try this minimal version to isolate the issue:

```kotlin
// Temporary simplified version for testing
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Test 1: Just log
    android.util.Log.d("SettingsFragment", "Fragment created successfully")
    
    // Test 2: Try accessing binding
    try {
        binding.buttonLogout.text = "Test"
        android.util.Log.d("SettingsFragment", "Binding works")
    } catch (e: Exception) {
        android.util.Log.e("SettingsFragment", "Binding failed: ${e.message}")
    }
    
    // Test 3: Try ServiceLocator
    try {
        val auth = ServiceLocator.provideAuthRepository()
        android.util.Log.d("SettingsFragment", "ServiceLocator works")
    } catch (e: Exception) {
        android.util.Log.e("SettingsFragment", "ServiceLocator failed: ${e.message}")
    }
}
```

## Enhanced Error Handling (Already Implemented)

The current SettingsFragment has **multiple layers of protection**:

1. ✅ Try-catch around ViewModel initialization
2. ✅ Try-catch around theme observation
3. ✅ Try-catch around UI setup
4. ✅ Fallback UI if ViewModel fails
5. ✅ Try-catch in fallback UI
6. ✅ Proper null handling for binding

## Installation & Testing

### Install the APK:
```powershell
.\gradlew installDebug
```

Or manually:
```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

### Watch logs while testing:
```powershell
adb logcat MentorConnect:V SettingsFragment:V AndroidRuntime:E *:S
```

## What to Look For in Logs

Search for these patterns:

1. **Fatal Exception**:
```
FATAL EXCEPTION: main
```

2. **Caused by**:
```
Caused by: java.lang.NullPointerException
```

3. **At line**:
```
at com.example.mentorconnect.ui.main.settings.SettingsFragment
```

## Expected Behavior (After Fix)

1. ✅ Settings screen opens without crash
2. ✅ Theme radio buttons are visible
3. ✅ Current theme is pre-selected
4. ✅ Switching theme applies immediately
5. ✅ Logout button returns to login screen

## Next Steps

**Please provide the crash log by**:
1. Running: `adb logcat -c` to clear logs
2. Opening the app and clicking Settings
3. Copying the error from the terminal
4. Sharing the log lines that show the exception

This will help identify the **exact line** causing the crash.

## Alternative: Check Android Studio Logcat

If using Android Studio:
1. Open **Logcat** tab at the bottom
2. Filter by "SettingsFragment" or "FATAL"
3. Run the app and navigate to Settings
4. Copy the red error lines

---

**Note**: The code has been enhanced with comprehensive error handling. If it still crashes, we need the specific error message to identify if it's a resource issue, context problem, or something else.
