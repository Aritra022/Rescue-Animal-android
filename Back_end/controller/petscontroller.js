require('dotenv').config();
const jwt = require('jsonwebtoken');
const express = require('express');
const router = express.Router();

const Pet = require('../model/pet'); // ✅ Using Pet model
const multer = require('../middleware/multer');
const bcrypt = require('bcrypt');

// Home route
router.get('/', (req, res) => {
    res.send("This is Home Page of Pet Upload Section....!!!");
});

// Upload pet
router.post('/upload', multer.single('image'), async (req, res) => {
    try {
        const { pet_Type, description, upload_date_time, address, status,  } = req.body;

        if (!req.file) {
            return res.status(400).json({ error: 'Image is required' });
        }

        const newPet = new Pet({
            pet_Type,
            description,
            upload_date_time,
            address,
            status,
            image: req.file.filename,
        });

        await newPet.save();

        res.status(201).json({ message: "Pet uploaded successfully!", pet: newPet });
    } catch (error) {
        console.error("Upload error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Update pet info
router.patch('/update_pets/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const updatedData = req.body;
        const options = { new: true };

        const result = await Pet.findByIdAndUpdate(id, updatedData, options);

        res.send(result);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Delete pet info
router.delete('/delete_pet/:id', async (req, res) => {
    try {
        const id = req.params.id;
        const data = await Pet.findByIdAndDelete(id);
        res.send(`Pet with ID ${data._id} has been deleted.`);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
});

// Get pet by ID
router.get('/get/:id', async (req, res) => {
    try {
        const pet = await Pet.findById(req.params.id);

        if (!pet) {
            return res.status(404).json({ error: 'Pet not found' });
        }

        pet._doc.image = `${req.protocol}://${req.get('host')}/uploads/${pet.image}`;
        res.json(pet);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Get all pets
router.get('/get_all_pets', async (req, res) => {
    try {
        const pets = await Pet.find();

        const petsWithImageUrl = pets.map(p => ({
            ...p._doc,
            image: `${req.protocol}://${req.get('host')}/uploads/${p.image}`,
        }));

        res.json(petsWithImageUrl);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Search by location (address)
router.get('/search_by_location/:address', async (req, res) => {
    try {
        const pets = await Pet.find({ address: { $regex: req.params.address, $options: "i" } });

        const petsWithImageUrl = pets.map(p => ({
            ...p._doc,
            image: `${req.protocol}://${req.get('host')}/uploads/${p.image}`,
        }));

        res.json(petsWithImageUrl);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

module.exports = router;
