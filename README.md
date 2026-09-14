# UniVerse

A Java-based Android communication app built for BAUST Khulna, allowing students and teachers to create profiles, discover each other, exchange real-time messages, and receive live notices from an administrator.

Built in **Android Studio with Java**, using **Firebase Realtime Database** for real-time chat, notices, and cloud-synchronized profile data.

The app appears in the Android app drawer as **UniVerse** with a custom app icon and splash-screen branding.

## 📱 Download

**[Download the latest Android APK](../../releases/latest)**

Download the latest release and install the APK directly on an Android device.

## ✨ Features

### 👨‍💼 Admin

* Admin login and dashboard, credentials synced through Firebase so they work from any device
* Publish notices that appear in real time on users' home screens
* Add and manage teacher profiles
* Add and manage student profiles
* Set department, designation/semester, name, and bio information
* Change the admin password
* Clear the notice board only — student, teacher, and chat data are permanent and are never affected by this action

### 🎓 Students & Teachers

* Create an account and log in from any device, since credentials are stored in the cloud
* Search teachers by department and designation
* Search students by department and semester
* View user profiles
* Start real-time conversations
* Change password or change display name independently, each behind a dedicated screen
* A notification box on the dashboard shows who has texted you

### 💬 Communication

* Real-time one-to-one messaging
* Live Firebase synchronization
* Messages remain available across devices through Firebase and are never deleted by an admin data reset
* Chat updates automatically when new messages arrive

### 🎨 App Experience

* UniVerse-branded splash screen
* Notice board on the main menu shows the 3 most recent notices, newest first
* Global dark-mode toggle available throughout the app
* Custom launcher icon
* Screens resize above the keyboard automatically, so the field being typed into always stays visible
* Developer contact page with social links

## 🛠️ Tech Stack

* **Language:** Java
* **IDE:** Android Studio
* **Database:** Firebase Realtime Database
* **Local Storage:** Android SharedPreferences
* **Build System:** Gradle
* **UI:** Android XML layouts and standard Android UI components

### Data Architecture

Firebase Realtime Database is used for:

* Notices
* Chat messages
* Student profiles
* Teacher profiles
* Admin data

Android `SharedPreferences` provides local caching so previously available application data can remain accessible offline and can be refreshed from Firebase when connectivity is available.

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Md-Manam-Khan/UniVerse.git
cd UniVerse
```

### 2. Configure Firebase

Create your own Firebase project and enable **Firebase Realtime Database**.

Download your project's `google-services.json` file and place it here:

```text
app/google-services.json
```

A template is included in the repository:

```text
app/google-services.json.example
```

The real `google-services.json` file is intentionally excluded from version control.

### 3. Open the project

Open the project in **Android Studio** and allow Gradle to sync.

### 4. Run the application

Run the application on an Android emulator or a physical Android device.

## 🔐 Admin Login

The default admin credentials are seeded automatically into Firebase the first time an admin logs in, and can be found in:

```text
FirebaseManager.java
```

After logging in, the admin password can be changed from the admin dashboard, and the new credentials immediately apply on every device.

## ⚠️ Security Notes

This project was developed as a university coursework project and is **not intended to represent a production-ready secure messaging system**.

The current implementation has known limitations:

* Firebase Realtime Database rules currently allow open read/write access to the project's main data areas
* User and admin passwords are currently stored in plain text
* Authentication is implemented by application logic rather than Firebase Authentication
* Production deployment would require proper Firebase Authentication and authorization rules
* Passwords should be securely hashed rather than stored as plain text
* Database access should be restricted to authenticated and authorized users

The repository therefore focuses on demonstrating Android development, Java, Firebase integration, and real-time communication functionality rather than production security.

## 🏫 Departments

The current application supports:

* CSE
* ME
* EEE
* CE
* English
* BBA

## 🎓 Academic Context

This project was developed as a sessional project for the **Object Oriented Programming Sessional** course at **BAUST**, Level 2, Term 2.

The project was designed and developed as an Android-based communication application for interaction between students and teachers.

AI tools were used during development as supporting tools:

* **Cursor AI** was used to help identify and fix a few Java bugs.
* **Claude** was used to assist with the Firebase Realtime Database integration and the UniVerse rebrand.

The final application structure, features, UI/UX decisions, and project direction were developed as part of the project work.

## 👨‍💻 Author

**Md. Manam Khan**

GitHub: [Md-Manam-Khan](https://github.com/Md-Manam-Khan)

## 📄 License

**Copyright © 2026 Md. Manam Khan. All rights reserved.**

This project was created for educational purposes as part of university coursework. This project is publicly available for viewing and educational reference purposes only.

No permission is granted to copy, modify, distribute, reproduce, publish, sublicense, or use this source code or substantial portions of it for personal, commercial, academic, or other projects without explicit written permission from the author.

The repository's public availability on GitHub does not grant a license to reuse the source code.
