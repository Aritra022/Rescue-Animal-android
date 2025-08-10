require('dotenv').config();
const jwt = require('jsonwebtoken');
const express = require('express');
const router = express.Router();
const bcrypt = require('bcrypt');
const multer = require('multer');
const User = require('../model/User');

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

module.exports = router;
