# SmartNotes AI

SmartNotes AI is a modern, cross-platform task management application powered by AI. Built with Kotlin Multiplatform and Jetpack Compose Multiplatform, it provides a seamless experience across Android, iOS, Desktop (JVM), and Web platforms. The app leverages Google's Gemini AI to provide intelligent task suggestions and productivity insights.

## 🏗️ Project Structure

The project follows a **modular architecture** pattern, organized into clear layers for maintainability and scalability:

```
SmartNotes AI/
├── composeApp/              # Main application module (entry point)
├── core/                    # Core modules (shared business logic)
│   ├── domain/             # Domain models and business entities
│   ├── data/               # Data layer (repositories, API clients)
│   ├── ui/                 # Shared UI components
│   ├── util/               # Utility functions and platform-specific implementations
│   ├── di/                 # Dependency injection modules
│   └── widget/             # Reusable widget components (e.g., Calendar)
├── feature/                # Feature modules (UI and business logic per feature)
│   ├── home/              # Home screen feature
│   ├── tasks/             # Task management feature
│   └── settings/          # Settings feature
└── iosApp/                 # iOS-specific entry point
```

## 🏛️ Architecture

The project implements **Clean Architecture** principles combined with **MVVM (Model-View-ViewModel)** pattern:

### Architecture Layers

1. **Domain Layer** (`core:domain`)
   - Pure Kotlin business logic
   - Domain models (e.g., `Task`, `AiTaskSuggestion`)
   - No dependencies on other layers

2. **Data Layer** (`core:data`)
   - Repository implementations
   - API clients (Ktor HTTP client)
   - Data source abstractions
   - Handles network requests and data transformation

3. **UI Layer** (`core:ui` + `feature/*`)
   - Compose Multiplatform UI components
   - ViewModels for state management
   - Feature-specific screens and components

4. **Presentation Layer** (`composeApp`)
   - Main application entry point
   - Navigation and app-level state management
   - Theme configuration

### Key Architectural Patterns

- **MVVM**: ViewModels manage UI state and business logic
- **Repository Pattern**: Abstracts data sources (API, local storage)
- **Dependency Injection**: Koin for managing dependencies
- **State Management**: Kotlin StateFlow and Compose state
- **Unidirectional Data Flow**: Data flows from ViewModel → UI

## 🛠️ Technology Stack

### Core Technologies
- **Kotlin Multiplatform (KMP)**: Shared business logic across platforms
- **Jetpack Compose Multiplatform**: Declarative UI framework
- **Ktor**: HTTP client for network requests
- **Koin**: Dependency injection framework
- **Kotlinx Serialization**: JSON serialization/deserialization
- **Kotlinx Coroutines**: Asynchronous programming
- **Kotlinx DateTime**: Date and time handling

### Platform-Specific
- **Android**: Jetpack Compose, Activity Compose
- **iOS**: SwiftUI integration, Darwin HTTP client
- **Desktop (JVM)**: Compose Desktop
- **Web**: Kotlin/Wasm and Kotlin/JS targets

### AI Integration
- **Google Gemini API**: AI-powered task suggestions and productivity insights

## 📦 Module Dependencies

```
composeApp
├── core:domain      (business models)
├── core:data        (repositories, API)
├── core:ui          (shared UI components)
├── core:util        (utilities)
├── core:di          (dependency injection)
├── core:widget      (widgets)
├── feature:home     (home feature)
├── feature:tasks    (tasks feature)
└── feature:settings (settings feature)
```

## 🎨 UI Components

The app includes reusable UI components in `core:ui`:

- **TaskListGrid**: Grid/list view for tasks with reorderable support
- **SwipeableTaskItem**: Swipe-to-delete task items
- **RotatingFabWithSheet**: Animated FAB with bottom sheet
- **PrioritySelectorRow**: Priority selection component
- **NavItem**: Navigation item component

## 🤖 AI Features

SmartNotes AI integrates with Google's Gemini API to provide:

- **Intelligent Task Suggestions**: AI analyzes your tasks and suggests what to focus on
- **Contextual Recommendations**: Personalized suggestions based on your task list
- **Productivity Insights**: Get motivational text and task prioritization

## 🚀 Building and Running

### Prerequisites
- JDK 11 or higher
- Android Studio or IntelliJ IDEA
- For iOS: Xcode (macOS only)
- For Desktop: No additional requirements
- For Web: Modern browser with WebAssembly support

### Build Android Application

```bash
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Build Desktop (JVM) Application

```bash
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

### Build Web Application

**Wasm target** (recommended, faster, modern browsers):
```bash
# macOS/Linux
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Windows
.\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
```

**JS target** (slower, supports older browsers):
```bash
# macOS/Linux
./gradlew :composeApp:jsBrowserDevelopmentRun

# Windows
.\gradlew.bat :composeApp:jsBrowserDevelopmentRun
```

### Build iOS Application

1. Open the `iosApp` directory in Xcode
2. Build and run from Xcode, or use the run configuration in your IDE

## 📱 Platform Support

- ✅ **Android** (API 24+)
- ✅ **iOS** (iOS 13+)
- ✅ **Desktop** (Windows, macOS, Linux)
- ✅ **Web** (Wasm and JS targets)

## 🔧 Development

### Project Setup

1. Clone the repository
2. Open in Android Studio or IntelliJ IDEA
3. Sync Gradle dependencies
4. Configure API keys (if needed for Gemini integration)
5. Run on your preferred platform

### Code Organization

- **Common code**: `commonMain` source sets
- **Platform-specific code**: `androidMain`, `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`
- **Shared UI**: Compose Multiplatform components in `core:ui`
- **Feature modules**: Self-contained features in `feature/`

## 📄 License

This project is part of a development portfolio. Please refer to the license file for details.

## 🔗 Resources

- [Kotlin Multiplatform Documentation](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- [Kotlin/Wasm](https://kotl.in/wasm/)
- [Ktor Documentation](https://ktor.io/)
- [Koin Documentation](https://insert-koin.io/)

---

**Note**: This project demonstrates modern cross-platform development practices using Kotlin Multiplatform and Compose Multiplatform, showcasing how to build a production-ready application that runs seamlessly across multiple platforms with shared business logic and UI code.
