# Quick Commands to Debug Settings Crash

## Start Fresh Logcat Monitoring
Run this in PowerShell **before** opening Settings:

```powershell
adb logcat -c; adb logcat | Select-String "SettingsFragment|AndroidRuntime|FATAL"
```

## Alternative: See All Errors
```powershell
adb logcat *:E
```

## After You See the Crash
Press `Ctrl+C` to stop, then share the error lines.

## What to Look For
The crash will show something like:
```
E AndroidRuntime: FATAL EXCEPTION: main
E AndroidRuntime: Process: com.example.mentorconnect, PID: 12345
E AndroidRuntime: java.lang.NullPointerException: ...
E AndroidRuntime:     at com.example.mentorconnect.ui.main.settings.SettingsFragment.onViewCreated(SettingsFragment.kt:XX)
```

Share the complete stack trace from your terminal!
