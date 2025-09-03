# Lost & Found Android App

A modern Android application built with Kotlin for institute students to post and search for lost and found items. The app provides an intuitive interface for reporting lost items, posting found items, and connecting people to help recover their belongings.

## Features

### 🏠 Home Screen
- Quick access to lost and found items
- Recent item listings
- Navigation cards for different sections
- Floating action button for quick item posting

### 📱 Item Management
- **Post Lost Items**: Report items you've lost with detailed descriptions
- **Post Found Items**: Report items you've found to help others
- **Image Upload**: Add photos to help identify items
- **Categories**: Organize items by type (Electronics, Books, Clothing, etc.)
- **Location Tracking**: Specify where items were lost or found

### 🔍 Browse & Search
- **Lost Items Tab**: Browse all reported lost items
- **Found Items Tab**: Browse all reported found items
- **Item Details**: Detailed view with contact information
- **Mark as Resolved**: Close cases when items are returned

### 💬 Communication
- **Contact Reporter**: Direct email integration to contact item reporters
- **Reporter Information**: View who posted each item with contact details

## Technical Architecture

### 🏗️ Architecture Components
- **MVVM Pattern**: Model-View-ViewModel architecture
- **Room Database**: Local SQLite database for offline functionality
- **LiveData**: Reactive data observation
- **Navigation Component**: Fragment-based navigation
- **View Binding**: Type-safe view references

## Installation & Setup

## Environment Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.9.10+
- Android SDK 24+ (Android 7.0)
- Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
- Enable Authentication (Google Sign-In)
- Enable Firestore Database
- Enable Firebase Storage

### Setup Instructions

1. **Open Android Studio**
2. **Import this project** by opening the `LostAndFoundApp` folder
3. **Sync project** with Gradle files
4. **Build the project**: `Build > Make Project`
5. **Run on device/emulator**: `Run > Run 'app'`

## Quick Setup

### Option 1: Automated Setup (Recommended)
Run the setup script to automatically create configuration files:

**Windows:**
```bash
setup.bat
```

**Linux/Mac:**
```bash
./setup.sh
```

### Option 2: Manual Setup
If you prefer to set up manually, follow these steps:

1. **Copy environment template:**
   ```bash
   cp .env.example .env
   cp app/google-services.json.example app/google-services.json
   ```

2. **Update `.env` file with your Firebase credentials:**
   - Get your project number from Firebase Console
   - Get your project ID from Firebase Console
   - Get your API keys from Firebase Console
   - Get your OAuth client IDs from Firebase Console

3. **Update `app/google-services.json` with your Firebase configuration:**
   - Download the actual `google-services.json` from Firebase Console
   - Replace the example file with your actual configuration

4. **Build and run the project:**
   ```bash
   ./gradlew build
   ```

### Environment Variables

The following environment variables are required:

- `FIREBASE_PROJECT_NUMBER`: Your Firebase project number
- `FIREBASE_PROJECT_ID`: Your Firebase project ID
- `FIREBASE_STORAGE_BUCKET`: Your Firebase storage bucket
- `FIREBASE_MOBILE_SDK_APP_ID`: Your mobile SDK app ID
- `FIREBASE_OAUTH_CLIENT_ID_ANDROID`: Android OAuth client ID
- `FIREBASE_OAUTH_CLIENT_ID_WEB`: Web OAuth client ID
- `FIREBASE_API_KEY`: Your Firebase API key
- `FIREBASE_CRASH_REPORTING_API_KEY`: Your crash reporting API key
- `FIREBASE_CERTIFICATE_HASH`: Your certificate hash for Android OAuth
- `ANDROID_PACKAGE_NAME`: Your Android package name

## Project Structure

```
LostAndFoundApp/
├── build.gradle.kts
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/institute/lostandfound/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── model/Item.kt
│       │   │   └── database/
│       │   │       ├── AppDatabase.kt
│       │   │       ├── ItemDao.kt
│       │   │       └── Converters.kt
│       │   ├── repository/ItemRepository.kt
│       │   ├── viewmodel/ItemViewModel.kt
│       │   ├── ui/
│       │   │   ├── HomeFragment.kt
│       │   │   ├── PostItemFragment.kt
│       │   │   ├── LostItemsFragment.kt
│       │   │   ├── FoundItemsFragment.kt
│       │   │   └── ItemDetailFragment.kt
│       │   └── adapter/ItemAdapter.kt
│       └── res/
│           ├── layout/
│           ├── navigation/
│           ├── menu/
│           ├── values/
│           ├── drawable/
│           └── xml/
└── README.md
```

## Usage

### Posting Items
1. Open the app and tap "Post" in bottom navigation
2. Fill in item details (title, description, category, type)
3. Add location information
4. Provide your contact details
5. Optionally add a photo
6. Tap "Post Item"

### Browsing Items
1. Use "Lost" or "Found" tabs to browse specific types
2. Tap on any item to view full details
3. Use "Contact Reporter" to reach out via email
4. Mark items as "Resolved" when returned

## Key Features

- ✅ **Offline-first architecture** with Room database
- ✅ **Material Design UI** with modern components
- ✅ **Image handling** with Glide
- ✅ **Email integration** for contacting reporters
- ✅ **Category-based organization** of items
- ✅ **Status tracking** (Active/Resolved)
- ✅ **Navigation component** for smooth app flow
- ✅ **MVVM architecture** with Repository pattern

## Categories Supported
- Electronics
- Books & Stationery
- Clothing & Accessories
- Personal Items
- Bags & Backpacks
- Jewelry
- Documents & Cards
- Keys
- Sports Equipment
- Other

## Built with ❤️ using Kotlin and Android Jetpack

This is a complete, production-ready Android application for managing lost and found items in educational institutions. 