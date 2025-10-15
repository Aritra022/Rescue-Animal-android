# 🐾 Rescue Animal Android App


<img width="1024" height="1536" alt="03c2d686-9989-4d87-838d-e83f25b939c5" src="https://github.com/user-attachments/assets/90496b72-751e-4bf8-8795-20d505bcd0ff" />


**Rescue Animal** is a community-driven mobile application designed to help rescue, feed, and rehome animals in need.  
It connects **donors**, **volunteers**, and **adopters** through a single platform, ensuring that no food or animal care resources go to waste.

---

## 📖 About This Project

Every day, countless stray animals struggle to find food and shelter. Many generous people want to help, but lack a proper system to connect with those who can deliver their contributions effectively.  
The **Rescue Animal** app bridges this gap:

- **Donors** can list leftover food or supplies they wish to donate.  
- **Volunteers** receive notifications about available donations and deliver them to the nearest shelters or stray animals.  
- **Adopters** can browse animals available for adoption and request to take them home.  
- **Admin/Organization** can manage all users, volunteers, and rescued pets in the system.

By combining an easy-to-use **Android app frontend** with a secure **Node.js backend**, the project ensures real-time updates, smooth communication, and reliable data storage.

---

## 📱 Features

### **Frontend (Android Studio - Java/XML)**
- User & Volunteer registration/login
- Pet listing with images
- Adoption request system
- Volunteer dashboard to manage food donations
- Smooth and consistent UI

### **Backend (Node.js + Express + MongoDB)**
- JWT-based authentication for users & volunteers
- Pet details storage & retrieval
- Image uploads with Multer
- REST API for mobile app integration

---

## 🛠 Tech Stack
- **Frontend:** Java, XML (Android Studio)
- **Backend:** Node.js, Express.js, MongoDB, Multer
- **Auth:** JWT (JSON Web Token)
- **Tools:** Git, GitHub

---

## 🚀 How to Run

### **Backend**
```bash
cd Back_end
npm install
node server.js
