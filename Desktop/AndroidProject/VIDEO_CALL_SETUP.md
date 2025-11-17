# Video Call Setup & Troubleshooting

This guide explains the exact steps required to make the Agora-based video call flow work in MentorConnect. Follow every section in order—most "it doesn't connect" reports are caused by missing the credential setup or launching the call outside the booked slot window.

## 1. Create your Agora project
1. Sign in at [console.agora.io](https://console.agora.io/).
2. Create a **New Project** (Classic) and note the generated **App ID**.
3. If you plan to ship beyond internal testing, also enable an **App Certificate**. You will use it to mint time-limited tokens on your own backend.

> ✅ Keep the App ID/App Certificate private. Never hardcode them in source control.

## 2. Provide the App ID to the Android build
The app now reads the value at build time via `BuildConfig.AGORA_APP_ID`.

### Local development (recommended)
Add this line to your local `local.properties` (same file that holds `sdk.dir`):

```
# local.properties (not under version control)
agora.app.id=YOUR_REAL_AGORA_APP_ID
```

### CI/Cloud builds
Set an environment variable before running Gradle:

```powershell
$env:AGORA_APP_ID="YOUR_REAL_AGORA_APP_ID"
./gradlew.bat assembleDebug
```

Gradle injects the value into `BuildConfig`, so `VideoCallActivity` will refuse to start a call if the App ID is empty—this helps catch misconfiguration early.

## 3. (Recommended) Token service
Production Agora apps must not join a channel with a null/temporary token. Spin up a tiny token service (Node/Go/Python) that uses your App ID + App Certificate to issue tokens per channel. Helpful starter:
- https://github.com/AgoraIO-Community/Agora-Token-Service

Expose one endpoint like `/rtcToken?channel=mentor_call_xxx&uid=123`. Replace the `rtcEngine?.joinChannel(null, …)` call with the fetched token when your service is ready.

## 4. Firebase prerequisites
1. `videoCallSessions` collection must exist (it is created automatically on the first call). Ensure your Firestore security rules allow mentors/students who own the slot to read/write their session documents.
2. Device clocks must be roughly correct—slots are unlocked based on the mentor's `date`, `startTime`, and `endTime` values (formatted `yyyy-MM-dd hh:mm a`).

## 5. Happy-path test script
1. **Student** books a slot from `MentorDetailActivity`.
2. Wait until the slot's start time (or adjust your device clock for testing).
3. Student taps **Start Video Call** on the mentor profile. This creates the Agora channel + Firestore session.
4. **Mentor** goes to the **Video Calls** tab, selects the same slot, and joins. The app now reuses the existing session, so both sides share the same channel name.
5. Verify audio/video, mute toggle, camera switch, and the automatic end-of-slot behavior.

## 6. Troubleshooting checklist
- **"Missing Agora App ID" toast** → `local.properties` or `AGORA_APP_ID` env var is empty.
- **Two users never meet in channel** → One of them likely started outside the booked slot or deleted `videoCallSessions`. Check Firestore to confirm there's only one ACTIVE doc per slot.
- **Call drops instantly** → Using a null token while the project has an App Certificate enabled. Deploy a token server and pass the generated token into `RtcEngine.joinChannel`.
- **Permissions denied** → Verify the runtime prompts for `CAMERA` + `RECORD_AUDIO`. The manifest already declares them.
- **Debugging logs** → Run `adb logcat | findstr Agora` (PowerShell) to watch the native Agora SDK output.

Once these steps are in place, the video call flow should work end-to-end for both mentors and students.
