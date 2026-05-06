const express = require('express');
const router = express.Router();
const Admin = require('../model/Admin');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const multer = require('../middleware/multer');
const User = require('../model/User');
const Volunteer = require('../model/Volunteer');
const Pet = require('../model/pet');
const nodemailer = require('nodemailer');

// ✅ FIXED OTP STORE
const otpStore = new Map();


// ================= ADMIN REGISTER =================
router.post('/register', multer.single('image'), async (req, res) => {
    try {
        const { email, name, password, contact, location } = req.body;

        if (!email || !name || !password || !contact || !location) {
            return res.status(400).json({ message: "All fields are required." });
        }

        const existingAdmin = await Admin.findOne({ email });
        if (existingAdmin) {
            return res.status(400).json({ message: "Email already registered." });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const newAdmin = new Admin({
            email,
            name,
            password: hashedPassword,
            contact,
            location,
        });

        await newAdmin.save();
        res.status(201).json(newAdmin);

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


// ================= ADMIN LOGIN =================
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        const admin = await Admin.findOne({ email });
        if (!admin) return res.status(400).json({ message: "Invalid email or password." });

        const isMatch = await bcrypt.compare(password, admin.password);
        if (!isMatch) return res.status(400).json({ message: "Invalid email or password." });

        const token = jwt.sign({ adminId: admin._id }, process.env.SECRET_KEY, { expiresIn: '1d' });

        res.status(200).json({
            message: "Login successful.",
            token,
            admin: {
                id: admin._id,
                email: admin.email,
                name: admin.name,
                contact: admin.contact,
                location: admin.location,
            }
        });

    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


// ================= ADMIN UPDATE =================
router.patch('/update/:id', async (req, res) => {
    try {
        const updatedAdmin = await Admin.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.status(200).json(updatedAdmin);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});


// ================= ADMIN DELETE =================
router.delete('/delete/:id', async (req, res) => {
    try {
        const deletedAdmin = await Admin.findByIdAndDelete(req.params.id);
        res.status(200).json({ message: `${deletedAdmin.name} has been deleted.` });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});


// ================= ADMIN DATA =================
router.get('/get/:id', async (req, res) => {
    try {
        const admin = await Admin.findById(req.params.id);
        if (!admin) return res.status(404).json({ message: "Admin not found." });

        res.status(200).json(admin);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

router.get('/getAll', async (req, res) => {
    try {
        const admins = await Admin.find();
        res.status(200).json(admins);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});


// ================= ALL USERS =================
router.get('/all-users', async (req, res) => {
    const users = await User.find();
    res.json(users);
});


// ================= ALL VOLUNTEERS =================
router.get('/all-volunteers', async (req, res) => {
    const volunteers = await Volunteer.find();
    res.json(volunteers);
});


// ================= ALL REQUESTS =================
router.get('/all-requests', async (req, res) => {
    const requests = await Pet.find();
    res.json(requests);
});


// ================= DELETE =================
router.delete('/delete-user/:id', async (req, res) => {
    await User.findByIdAndDelete(req.params.id);
    res.json({ message: "User deleted" });
});

router.delete('/delete-volunteer/:id', async (req, res) => {
    await Volunteer.findByIdAndDelete(req.params.id);
    res.json({ message: "Volunteer deleted" });
});

router.delete('/delete-request/:id', async (req, res) => {
    await Pet.findByIdAndDelete(req.params.id);
    res.json({ message: "Request deleted" });
});


// ================= BLOCK / ACTIVATE =================
router.patch('/block-volunteer/:id', async (req, res) => {
    await Volunteer.findByIdAndUpdate(req.params.id, { status: "blocked" });
    res.json({ message: "Volunteer blocked" });
});

router.patch('/activate-volunteer/:id', async (req, res) => {
    await Volunteer.findByIdAndUpdate(req.params.id, { status: "active" });
    res.json({ message: "Volunteer activated" });
});


// ================= FORGOT PASSWORD =================

// SEND OTP
router.post('/forgot-password', async (req, res) => {
    try {
        const { email } = req.body;

        const admin = await Admin.findOne({ email });
        if (!admin) {
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
            subject: 'Admin Password Reset OTP',
            text: `Your OTP is ${otp}`
        });

        return res.status(200).json({ message: "OTP sent successfully" });
    } catch (error) {
        console.error("Admin forgot password error:", error);
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
        console.error("Admin verify OTP error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});
// RESET PASSWORD
router.post('/reset-password', async (req, res) => {
    try {
        const { email, newPassword } = req.body;

        const admin = await Admin.findOne({ email });
        if (!admin) {
            return res.status(404).json({ message: "Admin not found" });
        }

        admin.password = await bcrypt.hash(newPassword, 10);
        await admin.save();

        delete otpStore[email];

        return res.status(200).json({ message: "Password reset successful" });
    } catch (error) {
        console.error("Admin reset password error:", error);
        return res.status(500).json({ message: "Internal Server Error" });
    }
});

module.exports = router;