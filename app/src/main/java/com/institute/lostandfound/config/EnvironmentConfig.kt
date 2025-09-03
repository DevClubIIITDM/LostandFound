package com.institute.lostandfound.config

import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.Properties

/**
 * Environment configuration manager
 * Loads configuration from environment variables or .env file
 */
object EnvironmentConfig {
    private const val TAG = "EnvironmentConfig"
    
    // Firebase configuration keys
    const val FIREBASE_PROJECT_NUMBER = "FIREBASE_PROJECT_NUMBER"
    const val FIREBASE_PROJECT_ID = "FIREBASE_PROJECT_ID"
    const val FIREBASE_STORAGE_BUCKET = "FIREBASE_STORAGE_BUCKET"
    const val FIREBASE_MOBILE_SDK_APP_ID = "FIREBASE_MOBILE_SDK_APP_ID"
    const val FIREBASE_OAUTH_CLIENT_ID_ANDROID = "FIREBASE_OAUTH_CLIENT_ID_ANDROID"
    const val FIREBASE_OAUTH_CLIENT_ID_WEB = "FIREBASE_OAUTH_CLIENT_ID_WEB"
    const val FIREBASE_API_KEY = "FIREBASE_API_KEY"
    const val FIREBASE_CRASH_REPORTING_API_KEY = "FIREBASE_CRASH_REPORTING_API_KEY"
    const val FIREBASE_CERTIFICATE_HASH = "FIREBASE_CERTIFICATE_HASH"
    const val ANDROID_PACKAGE_NAME = "ANDROID_PACKAGE_NAME"
    
    private var properties: Properties? = null
    
    /**
     * Initialize environment configuration
     */
    fun init(context: Context) {
        try {
            loadEnvFile(context)
            Log.d(TAG, "Environment configuration initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize environment configuration", e)
        }
    }
    
    /**
     * Load environment variables from .env file
     */
    private fun loadEnvFile(context: Context) {
        properties = Properties()
        try {
            val inputStream = context.assets.open(".env")
            properties?.load(inputStream)
            inputStream.close()
            Log.d(TAG, "Loaded .env file from assets")
        } catch (e: IOException) {
            Log.w(TAG, ".env file not found in assets, using system environment variables")
        }
    }
    
    /**
     * Get environment variable value
     */
    fun getEnvVar(key: String, defaultValue: String = ""): String {
        // First try system environment variable
        val systemEnv = System.getenv(key)
        if (!systemEnv.isNullOrEmpty()) {
            return systemEnv
        }
        
        // Then try .env file
        val envFileValue = properties?.getProperty(key)
        if (!envFileValue.isNullOrEmpty()) {
            return envFileValue
        }
        
        // Finally return default value
        return defaultValue
    }
    
    /**
     * Get Firebase project number
     */
    fun getFirebaseProjectNumber(): String {
        return getEnvVar(FIREBASE_PROJECT_NUMBER, "your_project_number_here")
    }
    
    /**
     * Get Firebase project ID
     */
    fun getFirebaseProjectId(): String {
        return getEnvVar(FIREBASE_PROJECT_ID, "your_project_id_here")
    }
    
    /**
     * Get Firebase storage bucket
     */
    fun getFirebaseStorageBucket(): String {
        return getEnvVar(FIREBASE_STORAGE_BUCKET, "your_project_id.firebasestorage.app")
    }
    
    /**
     * Get Firebase mobile SDK app ID
     */
    fun getFirebaseMobileSdkAppId(): String {
        return getEnvVar(FIREBASE_MOBILE_SDK_APP_ID, "1:your_project_number:android:your_app_id_hash")
    }
    
    /**
     * Get Firebase OAuth client ID for Android
     */
    fun getFirebaseOAuthClientIdAndroid(): String {
        return getEnvVar(FIREBASE_OAUTH_CLIENT_ID_ANDROID, "your_project_number-your_android_client_id.apps.googleusercontent.com")
    }
    
    /**
     * Get Firebase OAuth client ID for Web
     */
    fun getFirebaseOAuthClientIdWeb(): String {
        return getEnvVar(FIREBASE_OAUTH_CLIENT_ID_WEB, "your_project_number-your_web_client_id.apps.googleusercontent.com")
    }
    
    /**
     * Get Firebase API key
     */
    fun getFirebaseApiKey(): String {
        return getEnvVar(FIREBASE_API_KEY, "your_firebase_api_key_here")
    }
    
    /**
     * Get Firebase crash reporting API key
     */
    fun getFirebaseCrashReportingApiKey(): String {
        return getEnvVar(FIREBASE_CRASH_REPORTING_API_KEY, "your_crash_reporting_api_key_here")
    }
    
    /**
     * Get Firebase certificate hash
     */
    fun getFirebaseCertificateHash(): String {
        return getEnvVar(FIREBASE_CERTIFICATE_HASH, "your_certificate_hash_here")
    }
    
    /**
     * Get Android package name
     */
    fun getAndroidPackageName(): String {
        return getEnvVar(ANDROID_PACKAGE_NAME, "com.institute.lostandfound")
    }
}
