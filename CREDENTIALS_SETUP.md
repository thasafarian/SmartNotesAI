# Credentials Setup Guide

This project requires API credentials to be configured securely. **Never commit credentials to version control.**

## Required Credentials

The following credentials need to be configured:

- `API_BASE_URL`: Base URL for the task API (from MockAPI)
- `GEMINI_API_KEY`: Google Gemini API key (from Google AI Studio)
- `GEMINI_BASE_URL`: Base URL for Gemini API (optional, has default)
- `GEMINI_MODEL`: Gemini model name (optional, has default)

## Step 1: Create Base URL from MockAPI

[MockAPI.io](https://mockapi.io/) is a free service that allows you to create mock REST APIs for testing and development.

### Instructions:

1. **Visit MockAPI.io:**
   - Go to [https://mockapi.io/](https://mockapi.io/)
   - Click on **"Sign Up"** or **"Log In"** if you already have an account

2. **Create a New Project:**
   - After logging in, click the **"New Project"** button (or "+" icon)
   - Enter a project name (e.g., "MyNotesCaretaker")
   - Click **"Create"**

3. **Create a Resource:**
   - In your project, click **"New Resource"** or **"Add Resource"**
   - Set the resource name to `todo` (or any name you prefer)
   - Add the following fields to your resource:
     - `id` (String) - will be auto-generated
     - `title` (String)
     - `status` (Number) - 0 for pending, 1 for done
     - `createdAt` (String) - ISO-8601 datetime format
   - Click **"Create"** or **"Save"**

4. **Get Your Base URL:**
   - After creating the resource, MockAPI will display your API endpoint
   - Your base URL will look like: `https://<random-id>.mockapi.io/api/v1/todo`
   - **Copy this URL** - you'll need it for the `API_BASE_URL` configuration
   - Example format: `https://6902f0f3d0f10a340b21f140.mockapi.io/api/v1/todo`

### MockAPI Tips:
- The resource name in the URL should match what you created (e.g., `todo`)
- You can test your API directly in the MockAPI dashboard
- MockAPI provides a free tier with rate limits

## Step 2: Get API Key from Google AI Studio

[Google AI Studio](https://makersuite.google.com/app/apikey) provides access to Google's Gemini AI models.

### Instructions:

1. **Visit Google AI Studio:**
   - Go to [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey)
   - Sign in with your Google account

2. **Create API Key:**
   - Click on **"Create API Key"** or **"Get API Key"** button
   - If prompted, select or create a Google Cloud project
   - Your API key will be generated and displayed

3. **Copy Your API Key:**
   - **Important:** Copy the API key immediately - you won't be able to see it again
   - The key will look like: `AIzaSyAbKxL7rxqUKtcKilj48ryQ0zAhExMKBYE`
   - Store it securely - you'll need it for the `GEMINI_API_KEY` configuration

4. **Optional - Restrict API Key (Recommended for Production):**
   - Click on your API key to manage it
   - Set application restrictions (e.g., Android app, iOS app)
   - Set API restrictions to limit which APIs can be called
   - This helps secure your API key

### Google AI Studio Tips:
- Free tier includes generous usage limits
- API keys are free but have usage quotas
- Keep your API key secure and never commit it to version control

## Setup Instructions

### Android / JVM (Desktop)

1. Open `local.properties` in the project root
2. Add the following properties with your actual values:

```properties
# API Configuration
# Replace with your MockAPI base URL from Step 1
API_BASE_URL=https://your-project-id.mockapi.io/api/v1/todo

# Replace with your Google AI Studio API key from Step 2
GEMINI_API_KEY=your-actual-gemini-api-key-here

# Optional - these have defaults but can be customized
GEMINI_BASE_URL=https://generativelanguage.googleapis.com/v1beta/models
GEMINI_MODEL=gemini-2.5-flash-lite:generateContent
```

**Note:** 
- Replace `your-project-id.mockapi.io` with your actual MockAPI project URL
- Replace `your-actual-gemini-api-key-here` with your actual Google AI Studio API key
- `local.properties` is already in `.gitignore` and will not be committed to version control

### iOS

For iOS, you can set environment variables in Xcode:

1. Open your Xcode project
2. Go to Product → Scheme → Edit Scheme
3. Select "Run" → "Arguments"
4. Add environment variables:
   - `API_BASE_URL`
   - `GEMINI_API_KEY`
   - `GEMINI_BASE_URL` (optional)
   - `GEMINI_MODEL` (optional)

Alternatively, you can add them to `Info.plist` (though this is less secure).

### Web (JS/Wasm)

For web builds, environment variables need to be injected at build time through your build configuration (webpack, gradle, etc.).

## Security Best Practices

1. **DO**: Store credentials in `local.properties` (Android/JVM)
2. **DO**: Use environment variables for CI/CD pipelines
3. **DO**: Keep `local.properties` in `.gitignore`
4. ❌ **DON'T**: Commit API keys to version control
5. ❌ **DON'T**: Hardcode credentials in source files
6. ❌ **DON'T**: Share credentials in public repositories

## Quick Reference Links

- **MockAPI.io**: [https://mockapi.io/](https://mockapi.io/) - Create your task API endpoint
- **Google AI Studio**: [https://makersuite.google.com/app/apikey](https://makersuite.google.com/app/apikey) - Get your Gemini API key

## Verification

After setting up credentials, rebuild the project. The app should be able to make API calls without errors.

