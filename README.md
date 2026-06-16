# Al-Mizan Mobile Application

Al-Mizan is a modern Android application designed for economic operators to manage public tender submissions (Appels d'Offres) in a secure and streamlined environment.

## 🚀 Key Features

- **Dashboard**: Real-time statistics on active tenders, user submissions, and pending appeals.
- **Tender Management**: Browse, search, and filter public tenders by status (Published, Closed, Under Review).
- **Secure Submission System**: 
    - Managed multi-envelope submission process (Administrative, Technical, Financial).
    - Document attachment management with PDF support.
    - Ability to withdraw or review submissions.
- **AI-Powered Scanning**: Built-in document scanner with OCR (ML Kit) for administrative verification.
- **2FA Security**: Secure login with Email/Password and OTP verification.
- **Real-time Tracking**: Monitor the status of your bids and view detailed scoring/observations from commissions.
- **Appeals (Recours)**: Formal process for lodging appeals against tender outcomes.
- **Notifications**: Stay updated with alerts on new tenders, question responses, and results.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Architecture**: MVVM (Model-View-ViewModel) with ViewBinding
- **Design System**: Material 3 (M3) with a custom Sovereign Design palette
- **Networking**: Retrofit 2 + OkHttp 3 + GSON
- **Local Persistence**: Jetpack DataStore (Preferences) for session management
- **Navigation**: Jetpack Navigation Component
- **Media & Scanning**: 
    - CameraX for document capture
    - Google ML Kit for Text Recognition
    - Glide for image loading
- **UI Enhancement**: Lottie Animations, Facebook Shimmer effects

## 📂 Project Structure

- `com.almizan.mobile.data`: API service definitions, network clients, and data models (`Marche`, `Soumission`, `User`, etc.).
- `com.almizan.mobile.front`: UI layer organized by feature modules (Auth, Dashboard, Marches, Soumission, Suivi, Profil, Recours).
- `com.almizan.mobile.utils`: Helper classes for session handling (`SessionManager`) and generic resources (`Resource` wrapper).
- `res/values`: Centralized styling with `Theme.AlMizan`, Sovereign color palette, and standardized dimension systems.

## 🌐 API Endpoints (v1)

The application communicates with the Al-Mizan backend via the following REST endpoints:

### Auth
- `POST /auth/login`: Authenticate user.
- `POST /auth/verify-otp`: Validate 2FA code.
- `POST /auth/register`: Create a new account.
- `POST /auth/logout`: End session.

### Tenders (Marchés)
- `GET /tenders`: List all tenders (with pagination, search, and filters).
- `GET /tenders/public`: Publicly accessible tenders.
- `GET /tenders/{id}`: Detailed tender information.
- `GET /tenders/{id}/cdc/download`: Download Terms of Reference (CDC).
- `GET /tenders/{id}/questions`: List Q&A for a tender.
- `POST /tenders/{id}/questions`: Ask a new question.

### Bids (Soumissions)
- `POST /soumissions`: Create a draft bid.
- `GET /soumissions`: List user's bids.
- `GET /soumissions/{id}`: View bid details and scoring.
- `POST /soumissions/{id}/submit`: Confirm and lock the submission.
- `POST /soumissions/{id}/withdraw`: Withdraw a submitted bid.
- `POST /soumissions/{id}/admin/attachement`: Upload administrative documents (Multipart).

### Support & Notifications
- `POST /recours`: Submit a formal appeal.
- `GET /notifications`: List user notifications.
- `POST /notifications/{id}/read`: Mark notification as read.

## ⚙️ Development Setup

1. **Prerequisites**: Android Studio Jellyfish or newer.
2. **Base URL**: The application currently targets `http://10.0.2.2:3000/api/v1/` for local development (standard Android Emulator bridge to host machine). This can be configured in `app/build.gradle.kts`.
3. **Build**:
   ```bash
   ./gradlew assembleDebug
   ```

## 📜 License

Copyright © 2024 Al-Mizan. All rights reserved.
