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

### Prerequisites
- Android Studio Arctic Fox or later
- Kotlin 1.9.10+
- Android SDK 24+ (Android 7.0)

### Setup Instructions

1. **Open Android Studio**
2. **Import this project** by opening the `LostAndFoundApp` folder
3. **Sync project** with Gradle files
4. **Build the project**: `Build > Make Project`
5. **Run on device/emulator**: `Run > Run 'app'`

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