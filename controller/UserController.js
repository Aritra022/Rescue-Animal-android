require('dotenv').config();
const jwt = require('jsonwebtoken');
const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const multer = require('multer');
const User = require('../model/User');
const nodemailer = require('nodemailer');
const otpStore = new Map();

// Nodemailer Transporter
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

// Verify SMTP connection (optional but recommended)
transporter.verify((error, success) => {
    if (error) {
        console.error("SMTP Error:", error);
    } else {
        console.log("SMTP Server Ready");
    }
});

// Multer setup for file uploads
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + '-' + file.originalname);
    }
});
const upload = multer({ storage: storage });

// Home route
router.get('/', (req, res) => {
    res.send("This is the Home Page of the Pet Rescue Department!");
});

// ✅ Register
router.post('/register', upload.single('profileImage'), async (req, res) => {
    try {
        const { name, email, password, contact, location } = req.body;

        if (!name || !email || !password || !contact || !location) {
            return res.status(400).json({ error: 'Please fill all fields' });
        }

        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(409).json({ error: 'Email already registered' });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const newUser = new User({
            name,
            email,
            password: hashedPassword,
            contact,
            location
        });

        await newUser.save();

        res.status(201).json({ message: "User registered successfully!", user: newUser });
    } catch (error) {
        console.error("Registration error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// ✅ Login
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ message: "Please provide email and password" });
        }

        let user = await User.findOne({ email });

        if (!user) {
            return res.status(400).json({ message: "Invalid email or password" });
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({ message: "Invalid email or password" });
        }

        const token = jwt.sign({ userId: user._id }, process.env.SECRET_KEY, {
            expiresIn: '1d'
        });

        const userResponse = {
            _id: user._id,
            name: user.name,
            email: user.email,
            contact: user.contact,
            location: user.location,
            profileImage: user.profileImage
        };

        res.status(200)
            .cookie("token", token, {
                maxAge: 24 * 60 * 60 * 1000,
                httpOnly: true,
                sameSite: 'strict'
            })
            .json({
                message: `Welcome back ${user.name}`,
                user: userResponse,
                success: true
            });
    } catch (error) {
        console.error("Login error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

router.post('/save-fcm-token', async (req, res) => {
    try {
        const { email, fcmToken } = req.body;

        const user = await User.findOneAndUpdate(
            { email: email },
            { fcmToken: fcmToken },
            { new: true }
        );

        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        res.status(200).json({ message: "FCM token saved successfully" });
    } catch (error) {
        console.error("Save token error:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});

// ✅ Get all users
router.get('/get', async (req, res) => {
    try {
        const users = await User.find();
        res.json(users);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// ✅ Update user
router.patch('/update/:id', upload.single('profileImage'), async (req, res) => {
    try {
        const id = req.params.id;
        const updatedData = req.body;

        if (req.file) {
            updatedData.profileImage = req.file.path;
        }

        const options = { new: true };
        const result = await User.findByIdAndUpdate(id, updatedData, options);

        res.send(result);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// ✅ Delete user
router.delete('/delete/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const data = await User.findByIdAndDelete(id);
        res.send(`User with name ${data.name} has been deleted.`);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// ✅ Forgot Password
router.post('/forgot-password', async (req, res) => {
    try {
        const { email } = req.body;

        const user = await User.findOne({ email });

        if (!user) {
            return res.status(404).json({
                message: "Email not found"
            });
        }

        const now = Date.now();
        const existing = otpStore.get(email);

        if (existing && (now - existing.createdAt) < 2 * 60 * 1000) {
            return res.status(200).json({
                message: "OTP already sent"
            });
        }

        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        otpStore.set(email, {
            otp,
            createdAt: now
        });

        const info = await transporter.sendMail({
            from: process.env.EMAIL_USER,
            to: email,
            subject: "User Password Reset OTP",
            text: `Your OTP is ${otp}`
        });

        console.log("Mail sent:", info.response);

        return res.status(200).json({
            message: "OTP sent successfully"
        });

    } catch (error) {
        console.error("Forgot Password Error:", error);

        return res.status(500).json({
            message: "Failed to send OTP"
        });
    }
});
// VERIFY OTP
router.post('/verify-otp', (req, res) => {
    try {
        const { email, otp } = req.body;

        const saved = otpStore.get(email);

        if (!saved) {
            return res.status(400).json({ message: "OTP not found" });
        }

        if (saved.otp === otp) {
            return res.status(200).json({ message: "OTP verified" });
        }

        return res.status(400).json({ message: "Invalid OTP" });
    } catch (error) {
        console.error("User verify OTP error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});

// RESET PASSWORD
router.post('/reset-password', async (req, res) => {
    try {
        const { email, newPassword } = req.body;

        const user = await User.findOne({ email });
        if (!user) {
            return res.status(404).json({ message: "User not found" });
        }

        user.password = await bcrypt.hash(newPassword, 10);
        await user.save();

        otpStore.delete(email);

        return res.status(200).json({ message: "Password reset successful" });
    } catch (error) {
        console.error("User reset password error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});
module.exports = router;
