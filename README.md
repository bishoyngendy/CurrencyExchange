# CurrencyExchange

A modern Android currency exchange application built with Jetpack Compose, following Clean Architecture principles and modular design.

## 🎯 Goal

CurrencyExchange is a mobile application that provides real-time currency exchange rates and a calculator for converting between different currencies. The app fetches live exchange rate data from the DolarApp API and allows users to:

- View real-time exchange rates for various currencies (MXN, ARS, BRL, COP, etc.)
- Calculate currency conversions with a built-in calculator
- Swap between source and target currencies
- View bid/ask prices and last updated timestamps

## 🏗️ Architecture

This project follows **Clean Architecture** principles with a modular structure:

```
┌─────────────────────────────────────────┐
│           Presentation Layer            │
│         (UI, ViewModels, State)         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│            Domain Layer                  │
│    (Repositories, Mappers, Models)      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│             Data Layer                   │
│      (API, DTOs, Data Models)           │
└─────────────────────────────────────────┘
```

### Architecture Principles

- **Separation of Concerns**: Each layer has a single responsibility
- **Dependency Inversion**: Dependencies point inward toward the domain
- **Testability**: Each layer can be tested independently
- **Modularity**: Features are isolated in separate modules

## 📦 Module Structure

### Core Modules

Core modules provide shared functionality across the application:

#### `core:config`
- Application-wide configuration
- Base URL management
- Build variant-specific settings
- **Dependencies**: None

#### `core:designsystem`
- Shared UI components
- Theme definitions
- Design tokens and styling
- **Dependencies**: Compose UI libraries

#### `core:dispatcher`
- Coroutine dispatcher providers
- Thread management utilities
- **Dependencies**: Coroutines

#### `core:mvi`
- MVI (Model-View-Intent) architecture components
- `UiState`, `UiIntent`, `UiEffect` base classes
- **Dependencies**: None

#### `core:network`
- Retrofit configuration
- OkHttp setup
- Moshi JSON parsing
- Network interceptors
- **Dependencies**: Retrofit, OkHttp, Moshi

### Feature Modules

Features are organized using a multi-module approach:

#### `feature:currency:api`
- Public API contracts for the currency feature
- Domain models (`CurrencyTicker`)
- Repository interface (`CurrencyRepository`)
- Navigation contracts
- **Dependencies**: Navigation Compose

#### `feature:currency:data`
- **Data Layer** - Low-level data access
- Retrofit API interfaces (`CurrencyApi`)
- Data Transfer Objects (DTOs)
- Data models for API responses
- **Dependencies**: 
  - `feature:currency:api`
  - `core:network`

#### `feature:currency:domain`
- **Domain Layer** - Business logic
- Repository implementation (`CurrencyRepositoryImpl`)
- Mappers (DTO → Domain)
- Domain-specific logic
- **Dependencies**:
  - `feature:currency:api`
  - `feature:currency:data`
  - Coroutines

#### `feature:currency:impl`
- **Presentation Layer** - UI and ViewModels
- Compose UI screens
- ViewModels with MVI pattern
- Screen state management
- Dependency injection setup
- **Dependencies**:
  - `feature:currency:api`
  - `feature:currency:domain` (transitively includes `data`)
  - All core modules
  - Compose, Hilt, Navigation

### App Module

#### `app`
- Application entry point
- MainActivity
- Navigation setup
- **Dependencies**: All feature modules

## 🔧 Dependencies

### Core Libraries

- **Kotlin**: 2.0.21
- **Android Gradle Plugin**: 8.13.2
- **Compose BOM**: 2024.09.00
- **Hilt**: 2.51.1 (Dependency Injection)
- **KSP**: 2.0.21-1.0.28 (Kotlin Symbol Processing)

### Key Libraries

- **Jetpack Compose**: Modern declarative UI toolkit
- **Navigation Compose**: Type-safe navigation
- **Hilt Navigation Compose**: Hilt integration for navigation
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with interceptors
- **Moshi**: JSON parsing
- **Coroutines**: Asynchronous programming
- **Lifecycle**: ViewModel and lifecycle-aware components

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK (minSdk: 24, targetSdk: 36)

### Building the Project

1. Clone the repository:
```bash
git clone <repository-url>
cd CurrencyExchange
```

2. Open the project in Android Studio

3. Sync Gradle files (Android Studio will do this automatically)

4. Build the project:
```bash
./gradlew build
```

5. Run the app:
```bash
./gradlew installDebug
```

### Running Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 📁 Project Structure

```
CurrencyExchange/
├── app/                          # Application module
│   └── src/main/java/.../MainActivity.kt
│
├── core/                         # Core modules
│   ├── config/                   # App configuration
│   ├── designsystem/             # UI components & theme
│   ├── dispatcher/               # Coroutine dispatchers
│   ├── mvi/                      # MVI architecture base
│   └── network/                  # Network setup
│
├── feature/                      # Feature modules
│   └── currency/
│       ├── api/                  # Public API contracts
│       │   ├── domain/           # Domain models
│       │   └── repository/       # Repository interfaces
│       ├── data/                 # Data layer
│       │   ├── api/              # Retrofit interfaces
│       │   ├── dto/              # Data Transfer Objects
│       │   └── model/            # Data models
│       ├── domain/               # Domain layer
│       │   ├── mapper/           # DTO → Domain mappers
│       │   └── repository/       # Repository implementations
│       └── impl/                 # Presentation layer
│           ├── di/               # Dependency injection
│           └── presentation/     # UI & ViewModels
│
└── gradle/                       # Gradle configuration
    └── libs.versions.toml        # Version catalog
```

## 🔄 Data Flow

1. **User Interaction** → ViewModel receives `UiIntent`
2. **ViewModel** → Calls repository method
3. **Repository** → Fetches data from API (via data layer)
4. **Data Layer** → Converts API response to DTOs
5. **Domain Layer** → Maps DTOs to domain models
6. **Repository** → Returns domain models as Flow
7. **ViewModel** → Updates `UiState`
8. **UI** → Composes based on new state

## 🧪 Testing

The project includes:
- Unit tests for ViewModels
- Repository tests
- Mapper tests

Test structure mirrors the source structure:
- `src/test/` - Unit tests
- `src/androidTest/` - Instrumented tests

## 📝 Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Document public APIs with KDoc
- Keep functions focused and single-purpose

## 🔐 API Configuration

The app uses the DolarApp API:
- Base URL: `https://api.dolarapp.dev/v1/`
- Endpoints:
  - `GET /tickers?currencies=MXN,ARS` - Get exchange rates
  - `GET /tickers-currencies` - Get available currencies

Configuration is managed in `core:config` module and can be customized per build variant.

## 📄 License

[Add your license here]

## 👥 Contributors

[Add contributors here]
