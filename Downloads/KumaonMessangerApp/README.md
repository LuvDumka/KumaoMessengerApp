# KumaonMessenger 

A modern, feature-rich real-time messaging application built for Android using Java and Firebase. KumaonMessenger delivers an engaging chat experience with advanced features like live typing indicators, emoji reactions, and seamless real-time synchronization.

## 🚀 Features

### Core Messaging
- **Real-time Messaging** - Instant message delivery using Firebase Realtime Database
- **User Authentication** - Secure login and registration with Firebase Auth
- **Profile Management** - Custom profile pictures with Firebase Storage integration
- **Online Status Tracking** - See when users are online or their last seen time

### Advanced Features
- **Live Typing Indicator** - Real-time indication when someone is typing
- **Emoji Reactions** - React to messages with 6 different emojis (❤️👍😂😮😢😡)
- **Message Status** - Track message delivery status (Sent, Delivered, Read)
- **Cross-device Sync** - Messages synchronized across all user devices instantly

### User Experience
- **Material Design UI** - Clean, modern interface following Google's design principles
- **Custom Animations** - Smooth transitions and engaging visual feedback
- **Responsive Layout** - Optimized for different screen sizes and orientations
- **Dark/Light Theme** - Automatic theme switching based on system preferences

## 🛠️ Technical Stack

- **Frontend**: Java, Android SDK
- **Backend**: Firebase (Authentication, Realtime Database, Storage)
- **UI Framework**: Material Design Components, RecyclerView
- **Architecture**: MVVM pattern with lifecycle-aware components
- **Real-time**: Firebase Realtime Database listeners
- **Image Loading**: Picasso library for efficient image handling

## 📱 Screenshots

*Coming soon - Screenshots of the app in action*

## 🔧 Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK (API level 16 or higher)
- Firebase project with Authentication, Realtime Database, and Storage enabled
- Google Services configuration file (`google-services.json`)

### Clone and Setup
```bash
git clone https://github.com/LuvDumka/KumaonMessengerApp.git
cd KumaonMessenger
```

### Firebase Configuration
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication, Realtime Database, and Storage
3. Download `google-services.json` and place it in the `app/` directory
4. Update Firebase security rules as needed

### Build and Run
```bash
./gradlew assembleDebug
```

## 🏗️ Project Structure

```
app/
├── src/main/java/com/luvdumka/kumaonmessenger/
│   ├── MainActivity.java          # Main user list activity
│   ├── chatwindo.java            # Chat interface with typing indicators
│   ├── messagesAdpter.java       # Messages RecyclerView adapter with reactions
│   ├── msgModelclass.java        # Message data model
│   ├── login.java                # Authentication interface
│   ├── registration.java         # User registration
│   └── setting.java              # App settings
├── src/main/res/
│   ├── layout/                   # XML layout files
│   ├── drawable/                 # Vector drawables and custom shapes
│   ├── values/                   # Colors, strings, themes
│   └── font/                     # Custom typography
└── google-services.json         # Firebase configuration
```

## 🔑 Key Implementation Highlights

### Real-time Typing Indicator
- Uses Firebase Realtime Database to sync typing status
- Automatic cleanup after 2 seconds of inactivity
- Smooth animations for better user experience

### Emoji Reactions System
- Long-press gesture to show reaction options
- Real-time synchronization across all devices
- Visual feedback with custom animations

### Message Status Tracking
- Three-state system: Sent → Delivered → Read
- Visual indicators using custom vector drawables
- Firebase-based status updates

### Error Handling & Performance
- Comprehensive null checks and exception handling
- Memory-efficient image loading with Picasso
- Lifecycle-aware database listeners
- Optimized RecyclerView with ViewHolder pattern

## 🎯 Future Enhancements

- [ ] Voice messages support
- [ ] File sharing capabilities
- [ ] Group chat functionality
- [ ] Push notifications
- [ ] Message encryption
- [ ] Video calling integration

## 🐛 Known Issues

- None currently reported

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Firebase team for the excellent real-time database
- Material Design team for the UI guidelines
- Android development community for inspiration and support

## 📞 Contact

For any questions or suggestions, feel free to reach out:

**Developed with ❤️ by LuvDumka**
