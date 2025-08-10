const express = require('express');
const router = express.Router();
const Admin = require('../model/Admin');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const multer = require('../middleware/multer');

// 1️⃣ Register
router.post('/register', multer.single('image'), async (req, res) => {
    try {
        const { email, name, password, contact, location } = req.body;
        const image = req.file?.filename;

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

// 2️⃣ Login
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

// 3️⃣ Update
router.patch('/update/:id', async (req, res) => {
    try {
        const updatedAdmin = await Admin.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.status(200).json(updatedAdmin);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// 4️⃣ Delete
router.delete('/delete/:id', async (req, res) => {
    try {
        const deletedAdmin = await Admin.findByIdAndDelete(req.params.id);
        res.status(200).json({ message: `${deletedAdmin.name} has been deleted.` });
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// 5️⃣ Get Admin by ID
router.get('/get/:id', async (req, res) => {
    try {
        const admin = await Admin.findById(req.params.id);
        if (!admin) return res.status(404).json({ message: "Admin not found." });

        const withImageUrl = {
            ...admin._doc,
            image: `${req.protocol}://${req.get('host')}/uploads/${admin.image}`,
        };

        res.status(200).json(withImageUrl);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// 6️⃣ Get All Admins
router.get('/getAll', async (req, res) => {
    try {
        const admins = await Admin.find();

        const withImages = admins.map(a => ({
            ...a._doc,
            image: `${req.protocol}://${req.get('host')}/uploads/${a.image}`
        }));

        res.status(200).json(withImages);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

module.exports = router;
