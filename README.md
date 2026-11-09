# EduPay 💳

> A modern, secure native Android fintech application designed to streamline educational fee payments, tracking, and institutional financial workflows.

---

## 📸 App Showcase

<p align="center">
  <img src="https://github.com/user-attachments/assets/d0e922d7-9300-4549-bc2e-d588c68b6580" width="23%" alt="Login Screen" />
  <img src="https://github.com/user-attachments/assets/84587f90-9652-487e-aeef-d365163d0539" width="23%" alt="Connecting" />
  <img src="https://github.com/user-attachments/assets/d47c04af-0765-4a18-b2fd-389f56d50624" width="23%" alt="EduPay Dashboard" />
  <img src="https://github.com/user-attachments/assets/33307aba-b4d3-49b2-a92a-85805509c9f9" width="23%" alt="Payment Summary & M-Pesa" />
</p>

<p align="center">
  <em>Secure Login, Dashboard overview with student fee breakdowns, and M-Pesa checkout integration.</em>
</p>

---

## Key Features

- **Multi-Child Account Management:** Track outstanding balances, tuition status (`Due soon` / `Overdue`), and fee items across multiple students from a single dashboard.
- **Instant M-Pesa Integration:** Direct mobile money checkout workflow with real-time gateway connection handling (`Connecting to Safaricom...`).
- **Interactive Receipts & History:** Comprehensive digital receipt logs and historical transaction tracking.
- **Secure Authentication:** Robust user login sessions and encrypted local preferences.

---

## Additional Screens & States

<p align="center">
  <img src="https://github.com/user-attachments/assets/7bdcef90-32c0-44ec-96b3-5e2e7caab5ba" width="22%" alt="Welcome Screen" />
  <img src="https://github.com/user-attachments/assets/b7c5c7e6-1af1-4b88-8513-28f682a42d31" width="22%" alt="Loading State" />
  <img src="https://github.com/user-attachments/assets/84587f90-9652-487e-aeef-d365163d0539" width="22%" alt="Connecting" />
  <img src="https://github.com/user-attachments/assets/7f56f9eb-4c45-4f33-97e2-f6b2d2802dfb" width="22%" alt="M-Pesa Input" />
</p>
<p align="center">
  <em>From left to right: Welcome Onboarding, Dashboard Loading State, M-Pesa Payment Input, and Safaricom Gateway Connection.</em>
</p>

---

## Architecture & Data Flow

EduPay is engineered using **Clean Architecture** and **Unidirectional Data Flow (UDF)**:

    [ UI / Jetpack Compose ] ──(Observes UI State)──> [ ViewModel ]
                                                          │
                                               (Invokes Use Cases)
                                                          ▼
                                                  [ Domain Layer ]
                                               (Business Logic / UseCases)
                                                          │
                                               (Accesses Repositories)
                                                          ▼
                                                   [ Data Layer ]
                                          ┌──────────────┴──────────────┐
                                          ▼                             ▼
                                  [ Remote Data ]               [ Local Data ]
                                  (Retrofit/REST API)         (Room / DataStore)

---

## Tech Stack & Engineering Standards

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Declarative UI with Material 3 design principles)
- **Architecture:** MVVM + Clean Architecture + UDF
- **State Management:** `StateFlow`, `SharedFlow`, and Sealed UI State interfaces
- **Asynchronous Operations:** Kotlin Coroutines with dependency-injected dispatchers
- **Dependency Injection:** Hilt / Dagger
- **Networking:** Retrofit2 & OkHttp3 with auth interceptors for M-Pesa API integration

---

## Engineering Challenges & Solutions

### Handling Asynchronous Payment Timeouts
- **Problem:** Mobile payment processing (STK Push) requires seamless user feedback during network or provider delays.
- **Solution:** Implemented exponential backoff polling using Kotlin `Flow` combined with strict UI state machine transitions (`Connecting`, `PendingUserPin`, `Completed`, `Failed`) to ensure responsive, non-blocking feedback.

---

## Testing Strategy

- **Unit Testing:** JUnit5 & MockK for testing ViewModels, Use Cases, and Repository implementations.
- **Coroutines Testing:** `kotlinx-coroutines-test` utilizing `StandardTestDispatcher` and `TestScope`.
- **State Verification:** Unit tests covering deterministic UI state transitions across network loading and error scenarios.

---

## Project Role & Achievements

- **Sole Android Developer:** Took complete end-to-end ownership under the SpaceYaTech program architecting the codebase, designing the declarative UI state machines, and integrating payment gateways.
- **Fintech UX Implementation:** Engineered production-grade mobile screens optimized for handling asynchronous network states, input validation, and secure payment callbacks.

---

## Installation & Setup

1. Clone the repository:
   ```bash
   git clone [https://github.com/dokwako/EduPayApp.git](https://github.com/dokwako/EduPayApp.git)
