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
<![Uploading WhatsApp Image 2026-08-19 at 14.27.12.jpeg…]()



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
