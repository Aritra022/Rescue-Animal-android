require('dotenv').config();
const jwt = require('jsonwebtoken');
const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const multer = require('multer');
const Volunteer = require('../model/Volunteer');
const nodemailer = require('nodemailer');
const otpStore = new Map();

// 🔧 Multer setup for file uploads
const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        cb(null, Date.now() + '-' + file.originalname);
    }
});
const upload = multer({ storage: storage });

// 🏠 Home route
router.get('/', (req, res) => {
    res.send("This is Home Page of Volunteer Department....!!!");
});

// ✅ Register volunteer (with image upload)
router.post('/register', upload.single('profileImage'), async (req, res) => {
    try {
        const { name, email, password, contact, location } = req.body;

        if (!name || !email || !password || !contact || !location) {
            return res.status(400).json({ error: 'Please fill all fields' });
        }

        const existingUser = await Volunteer.findOne({ email });
        if (existingUser) {
            return res.status(409).json({ error: 'Email already registered' });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const newUser = new Volunteer({
            name,
            email,
            password: hashedPassword,
            contact,
            location,
            profileImage: req.file ? req.file.path : undefined
        });

        await newUser.save();

        res.status(201).json({ message: "Volunteer registered successfully!", user: newUser });
    } catch (error) {
        console.error("Registration error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// ✅ Login volunteer
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({
                message: "Please provide email and password",
                success: false
            });
        }

        let user = await Volunteer.findOne({ email });
        if (!user) {
            return res.status(400).json({
                message: "Invalid email or password.",
                success: false
            });
        }

        // ✅ block check
        if (user.status === "blocked") {
            return res.status(403).json({
                message: "Your account has been blocked by admin.",
                success: false
            });
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(400).json({
                message: "Invalid email or password.",
                success: false
            });
        }

        const token = jwt.sign(
            { userId: user._id },
            process.env.SECRET_KEY,
            { expiresIn: '1d' }
        );

        const userResponse = {
            _id: user._id,
            name: user.name,
            email: user.email,
            contact: user.contact,
            location: user.location,
            profileImage: user.profileImage,
            status: user.status
        };

        return res.status(200)
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

// ✅ Get all volunteers
router.get('/get', async (req, res) => {
    try {
        const users = await Volunteer.find();
        res.json(users);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// ✅ Update volunteer (with image)
router.patch('/update/:id', upload.single('profileImage'), async (req, res) => {
    try {
        const id = req.params.id;
        const updatedData = req.body;

        if (req.file) {
            updatedData.profileImage = req.file.path;
        }

        const options = { new: true };
        const result = await Volunteer.findByIdAndUpdate(id, updatedData, options);
        res.send(result);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// ✅ Delete volunteer
router.delete('/delete/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const data = await Volunteer.findByIdAndDelete(id);
        res.send(`Volunteer with name ${data.name} has been deleted.`);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

router.post('/forgot-password', async (req, res) => {
    try {
        const { email } = req.body;

        const volunteer = await Volunteer.findOne({ email });
        if (!volunteer) {
            return res.status(404).json({ message: "Email not found" });
        }

        const now = Date.now();
        const existing = otpStore[email];

        if (existing && (now - existing.createdAt) < 2 * 60 * 1000) {
            return res.status(200).json({ message: "OTP already sent" });
        }

        const otp = Math.floor(100000 + Math.random() * 900000).toString();

        otpStore[email] = {
            otp: otp,
            createdAt: now
        };

        const transporter = nodemailer.createTransport({
            service: 'gmail',
            auth: {
                user: process.env.EMAIL_USER,
                pass: process.env.EMAIL_PASS
            }
        });

        await transporter.sendMail({
            from: process.env.EMAIL_USER,
            to: email,
            subject: 'Volunteer Password Reset OTP',
            text: `Your OTP is ${otp}`
        });

        return res.status(200).json({ message: "OTP sent successfully" });
    } catch (error) {
        console.error("Volunteer forgot password error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});

router.post('/verify-otp', (req, res) => {
    try {
        const { email, otp } = req.body;

        const saved = otpStore[email];

        if (!saved) {
            return res.status(400).json({ message: "OTP not found" });
        }

        if (saved.otp === otp) {
            return res.status(200).json({ message: "OTP verified" });
        }

        return res.status(400).json({ message: "Invalid OTP" });
    } catch (error) {
        console.error("Volunteer verify OTP error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});

router.post('/reset-password', async (req, res) => {
    try {
        const { email, newPassword } = req.body;

        const volunteer = await Volunteer.findOne({ email });
        if (!volunteer) {
            return res.status(404).json({ message: "Volunteer not found" });
        }

        volunteer.password = await bcrypt.hash(newPassword, 10);
        await volunteer.save();

        delete otpStore[email];

        return res.status(200).json({ message: "Password reset successful" });
    } catch (error) {
        console.error("Volunteer reset password error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});


module.exports = router;
