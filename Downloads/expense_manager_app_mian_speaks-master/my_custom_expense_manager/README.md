# My Expense Manager 📱💰

[![Android](https://img.shields.io/badge/Platform-Android-green?style=flat-square&logo=android)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Language-Java-orange?style=flat-square&logo=java)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Firebase-Enabled-yellow?style=flat-square&logo=firebase)](https://firebase.google.com)
[![Min API](https://img.shields.io/badge/Min%20API-24%2B-blue?style=flat-square&logo=android)](https://developer.android.com/studio/releases/platforms)
[![License](https://img.shields.io/badge/License-MIT-red?style=flat-square)](LICENSE)

## 👋 About This Project

**My Expense Manager** is a comprehensive Android application developed by **Luv Dumka** to help users effectively track, manage, and analyze their personal finances. This app provides an intuitive and modern interface for managing income, expenses, and financial goals with powerful visualization and reporting features.

This project showcases modern Android development practices, clean architecture, and seamless integration with cloud services for data synchronization.

## Key Features

### Financial Management**
- **Income & Expense Tracking**: Easily record and categorize all financial transactions
- **Multiple Accounts**: Support for managing different bank accounts and wallets
- **Custom Categories**: Create personalized categories for better organization
- **Transaction History**: Complete transaction history with search and filter options

### Analytics & Reports
- **Interactive Charts**: Beautiful visualizations using AnyChart library
- **Monthly Reports**: Detailed monthly and yearly financial summaries
- **Spending Insights**: Identify spending patterns and trends
- **Budget Tracking**: Set and monitor budget goals

## Technologies Used

### **Core Technologies**
- **Language**: Java 17
- **Framework**: Android SDK (API 24+)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build Tool**: Gradle 8.5

### **Database & Storage**
- **Local Database**: Realm Database
- **Cloud Database**: Firebase Firestore
- **File Storage**: Firebase Storage (Ready for implementation)

### **UI & Visualization**
- **UI Framework**: Constraint, Linear, Relative layouts
- **Charts Library**: AnyChart Android
- **Icons**: Material Design Icons
- **Fonts**: Custom Roboto font family

### **Cloud Services**
- **Authentication**: Firebase Authentication
- **Database**: Firebase Firestore
- **Analytics**: Firebase Analytics (Ready)
- **Crash Reporting**: Firebase Crashlytics (Ready)

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Arctic Fox or later
- **JDK**: Java 17 (automatically configured)
- **Android SDK**: API 24+ (Android 7.0+)
- **Firebase Account**: For cloud features

### Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/LuvDumka/my-expense-manager.git
   cd my-expense-manager
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Configure Firebase (Optional)**
   - Add your `google-services.json` to the `app/` directory
   - Enable Authentication and Firestore in Firebase Console
   - Update Firebase configuration if needed

4. **Build and Run**
   - Connect an Android device or start emulator
   - Click "Run" (▶️) in Android Studio
   - Select your target device

## Download & Install

### **Latest Release**
Download the latest version of My Expense Manager:

[![Download APK](https://img.shields.io/badge/Download-APK-blue?style=for-the-badge&logo=android)](https://drive.google.com/file/d/YOUR_APK_LINK_HERE/view?usp=sharing)

### **Installation Steps**
1. **Download** the APK file from the link above
2. **Enable Unknown Sources** on your Android device:
   - Go to Settings → Security → Unknown Sources
   - Enable the option to install apps from unknown sources
3. **Install** the APK file by tapping on it
4. **Open** the app and start managing your expenses!

### **System Requirements**
- **Android Version**: 7.0 (API 24) or higher
- **Storage**: ~50MB free space
- **Permissions**: Storage access for data backup

## 🚀 Getting Started

```
my-expense-manager/
├── app/
│   ├── src/main/
│   │   ├── java/com/custom/expensemanager/
│   │   │   ├── models/          # Data models (Transaction, Account, Category)
│   │   │   ├── viewmodels/      # MVVM ViewModels
│   │   │   ├── views/           # Activities and Fragments
│   │   │   ├── adapters/        # RecyclerView adapters
│   │   │   ├── utils/           # Helper classes and constants
│   │   │   └── database/        # Database configuration
│   │   ├── res/                 # UI resources
│   │   │   ├── layout/          # XML layouts
│   │   │   ├── values/          # Colors, strings, themes
│   │   │   ├── drawable/        # Icons and images
│   │   │   └── font/            # Custom fonts
│   │   └── AndroidManifest.xml  # App configuration
│   ├── build.gradle             # App-level dependencies
│   └── google-services.json     # Firebase configuration
├── build.gradle                 # Project-level configuration
├── gradle.properties           # Build properties
└── settings.gradle             # Project settings
```

## 🔧 Configuration

### **Custom Colors**
The app uses a custom blue color scheme defined in `res/values/colors.xml`:
- Primary Blue: `#2196F3`
- Dark Blue: `#0D47A1`
- Accent Colors: Various shades for different UI elements

### **Firebase Setup**
To enable cloud features:
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Authentication and Firestore
3. Download `google-services.json`
4. Replace the placeholder file in `app/` directory

## 📈 Performance & Optimization

- **Fast Loading**: Optimized Realm queries for instant data access
- **Memory Efficient**: Proper memory management and leak prevention
- **Battery Optimized**: Minimal background processing
- **Offline First**: Full functionality without internet connection
- **Smooth UI**: 60fps animations and transitions

## 🐛 Known Issues & Future Enhancements

### **Current Status**
- ✅ Local data storage with Realm
- ✅ Firebase configuration ready
- ✅ Modern UI with custom theme
- ✅ All core features implemented
- 🔄 Cloud sync (ready for implementation)
- 🔄 User authentication (ready for implementation)

### Development Guidelines**
- Follow MVVM architecture patterns
- Write clean, documented code
- Test on multiple device sizes
- Maintain backward compatibility
- Use meaningful commit messages

## Contact & Support

**Developer**: Luv Dumka
- **Email**: luvdumka785@gmail.com
- **LinkedIn**: [Luv Dumka](https://linkedin.com/in/luvdumka)

**⭐ Star this repository if you find it helpful!**

**Made by Luv Dumka**
