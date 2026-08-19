# 🐾 Rescue Animal

An Android application that helps users report injured or stray animals and allows volunteers to respond quickly. The application uses a Node.js backend with MongoDB for data storage and Firebase Cloud Messaging (FCM) to notify volunteers instantly.

---




## 📱 Features

### User
- Register and Login
- Report injured animals
- Upload animal images
- Share current location
- Provide description and address
- View report status

### Volunteer
- Register and Login
- Receive instant notifications for new rescue requests
- View nearby rescue requests
- Accept or Reject rescue requests
- Update rescue status

### Admin
- Manage users and volunteers
- Monitor rescue requests

---

## 🛠️ Tech Stack

### Android
- Java
- XML
- Android Studio
- Retrofit
- Volley
- Google Maps / Location Services

### Backend
- Node.js
- Express.js
- MongoDB
- Mongoose
- Multer
- Firebase Admin SDK
- Nodemailer

---

## 📂 Project Structure

```
Rescue-Animal-android/
│
├── Android App
│   ├── Activities
│   ├── Adapters
│   ├── Models
│   ├── Retrofit
│   ├── Utilities
│   └── XML Layouts
│
└── Back_end
    ├── Controllers
    ├── Models
    ├── Routes
    ├── Middleware
    ├── Config
    └── Server.js
```

---

## 🚀 How It Works

1. User registers and logs in.
2. User reports an injured animal.
3. User uploads:
   - Animal image
   - Description
   - Address
   - Live location
4. Backend stores the information in MongoDB.
5. Firebase Cloud Messaging sends notifications to volunteers.
6. Volunteers can Accept or Reject the request.
7. Users can track the request status.

---

## 🔔 Notifications

Firebase Cloud Messaging (FCM) is used to notify volunteers instantly whenever a new rescue request is submitted.

---

## 🗄️ Database

MongoDB stores:

- User Details
- Volunteer Details
- Animal Reports
- Rescue Status
- FCM Tokens

---

## 📸 Screenshots

Add screenshots here.

```
screenshots
<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 12" src="https://github.com/user-attachments/assets/f758e05c-8303-42f2-8013-ff6dbb8be289" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 12 (2)" src="https://github.com/user-attachments/assets/d1d2fa87-9072-4048-8d49-
7c212ab61730" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 12 (1)" src="https://github.com/user-attachments/assets/f5023ca7-3ef1-40fd-86b1-9f0b04838a31" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 12 (3)" src="https://github.com/user-attachments/assets/696ea830-402b-4e9d-ac04-972148877cbc" />

<img width="426" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 13" src="https://github.com/user-attachments/assets/bea36aac-ae81-4337-8d8c-98e05cccf7f4" />

<img width="1260" height="2800" alt="WhatsApp Image 2026-08-19 at 14 27 13 (1)" src="https://github.com/user-attachments/assets/8aec9eed-e539-411c-8c3b-6e11da19fa8b" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 13 (2)" src="https://github.com/user-attachments/assets/e0e1fb38-a404-47d5-b5ed-555954206519" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 14" src="https://github.com/user-attachments/assets/302f2948-68c5-41b2-8cc3-46a83607912c" />

<img width="576" height="1280" alt="WhatsApp Image 2026-08-19 at 14 27 14 (1)" src="https://github.com/user-attachments/assets/5d56b6cc-f763-43d3-89d6-e2b2846396da" />



```
## ⚙️ Installation

### Clone Repository

```bash
git clone https://github.com/Aritra022/Rescue-Animal-android.git
```

### Backend

```bash
cd Back_end
npm install
npm start
```

### Android

Open the project in Android Studio and run the application on an emulator or Android device.

---

## 📡 API Examples

### Upload Rescue Request

```
POST /pets/upload
```

### Get Pending Requests

```
GET /pets/get_pending_pets
```

### Update Rescue Status

```
PATCH /pets/update_pets/:id
```

---

## 👨‍💻 Author

**Aritra Bhunia**

GitHub: https://github.com/Aritra022

LinkedIn: https://www.linkedin.com/in/aritra-bhunia-207238266/

---

## 📄 License

This project is developed for educational and academic purposes.
