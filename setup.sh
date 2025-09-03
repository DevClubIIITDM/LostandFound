#!/bin/bash

# Lost & Found App - Setup Script
# This script helps contributors set up the project with their own Firebase credentials

echo "🚀 Lost & Found App - Environment Setup"
echo "======================================"

# Check if .env file exists
if [ -f ".env" ]; then
    echo "⚠️  .env file already exists. Do you want to overwrite it? (y/n)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        echo "📝 Creating new .env file..."
    else
        echo "✅ Keeping existing .env file"
        exit 0
    fi
else
    echo "📝 Creating .env file..."
fi

# Create .env file with template
cat > .env << 'EOF'
# Firebase Configuration
# Replace these values with your own Firebase project credentials

# Project Information
FIREBASE_PROJECT_NUMBER=your_project_number_here
FIREBASE_PROJECT_ID=your_project_id_here
FIREBASE_STORAGE_BUCKET=your_project_id.firebasestorage.app

# Mobile SDK App ID
FIREBASE_MOBILE_SDK_APP_ID=1:your_project_number:android:your_app_id_hash

# OAuth Client IDs
FIREBASE_OAUTH_CLIENT_ID_ANDROID=your_project_number-your_android_client_id.apps.googleusercontent.com
FIREBASE_OAUTH_CLIENT_ID_WEB=your_project_number-your_web_client_id.apps.googleusercontent.com

# API Keys
FIREBASE_API_KEY=your_firebase_api_key_here
FIREBASE_CRASH_REPORTING_API_KEY=your_crash_reporting_api_key_here

# Certificate Hash (for Android OAuth)
FIREBASE_CERTIFICATE_HASH=your_certificate_hash_here

# Package Name
ANDROID_PACKAGE_NAME=com.institute.lostandfound
EOF

echo "✅ .env file created successfully!"

# Check if google-services.json.example exists
if [ -f "app/google-services.json.example" ]; then
    echo "📝 Creating google-services.json from template..."
    cp app/google-services.json.example app/google-services.json
    echo "✅ google-services.json created from template!"
    echo "⚠️  Remember to replace the placeholder values with your actual Firebase configuration"
else
    echo "⚠️  google-services.json.example not found. Please create it manually."
fi

echo ""
echo "📋 Next Steps:"
echo "1. Go to Firebase Console: https://console.firebase.google.com/"
echo "2. Create a new project or use existing one"
echo "3. Enable Authentication (Google Sign-In)"
echo "4. Enable Firestore Database"
echo "5. Enable Firebase Storage"
echo "6. Download google-services.json and replace the template file"
echo "7. Update the .env file with your actual Firebase credentials"
echo "8. Run: ./gradlew build"
echo ""
echo "🎉 Setup complete! Happy coding!"
