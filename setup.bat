@echo off
REM Lost & Found App - Setup Script for Windows
REM This script helps contributors set up the project with their own Firebase credentials

echo 🚀 Lost & Found App - Environment Setup
echo ======================================

REM Check if .env file exists
if exist ".env" (
    echo ⚠️  .env file already exists. Do you want to overwrite it? (y/n)
    set /p response=
    if /i "%response%"=="y" (
        echo 📝 Creating new .env file...
    ) else (
        echo ✅ Keeping existing .env file
        goto :end
    )
) else (
    echo 📝 Creating .env file...
)

REM Create .env file with template
(
echo # Firebase Configuration
echo # Replace these values with your own Firebase project credentials
echo.
echo # Project Information
echo FIREBASE_PROJECT_NUMBER=your_project_number_here
echo FIREBASE_PROJECT_ID=your_project_id_here
echo FIREBASE_STORAGE_BUCKET=your_project_id.firebasestorage.app
echo.
echo # Mobile SDK App ID
echo FIREBASE_MOBILE_SDK_APP_ID=1:your_project_number:android:your_app_id_hash
echo.
echo # OAuth Client IDs
echo FIREBASE_OAUTH_CLIENT_ID_ANDROID=your_project_number-your_android_client_id.apps.googleusercontent.com
echo FIREBASE_OAUTH_CLIENT_ID_WEB=your_project_number-your_web_client_id.apps.googleusercontent.com
echo.
echo # API Keys
echo FIREBASE_API_KEY=your_firebase_api_key_here
echo FIREBASE_CRASH_REPORTING_API_KEY=your_crash_reporting_api_key_here
echo.
echo # Certificate Hash (for Android OAuth)
echo FIREBASE_CERTIFICATE_HASH=your_certificate_hash_here
echo.
echo # Package Name
echo ANDROID_PACKAGE_NAME=com.institute.lostandfound
) > .env

echo ✅ .env file created successfully!

REM Check if google-services.json.example exists
if exist "app\google-services.json.example" (
    echo 📝 Creating google-services.json from template...
    copy "app\google-services.json.example" "app\google-services.json"
    echo ✅ google-services.json created from template!
    echo ⚠️  Remember to replace the placeholder values with your actual Firebase configuration
) else (
    echo ⚠️  google-services.json.example not found. Please create it manually.
)

echo.
echo 📋 Next Steps:
echo 1. Go to Firebase Console: https://console.firebase.google.com/
echo 2. Create a new project or use existing one
echo 3. Enable Authentication (Google Sign-In)
echo 4. Enable Firestore Database
echo 5. Enable Firebase Storage
echo 6. Download google-services.json and replace the template file
echo 7. Update the .env file with your actual Firebase credentials
echo 8. Run: gradlew.bat build
echo.
echo 🎉 Setup complete! Happy coding!

:end
pause
