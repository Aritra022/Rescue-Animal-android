require('dotenv').config();
const express = require('express');
const router = express.Router();

const Pet = require('../model/pet');
const User = require('../model/User');
const multer = require('../middleware/multer');
const admin = require('./firebaseAdmin');

function buildImageUrl(req, image) {
    if (!image) return "";

    const img = String(image).trim();

    if (img.startsWith("http://") || img.startsWith("https://")) {
        return img;
    }

    let normalized = img;

    if (normalized.startsWith("Uploads/")) {
        normalized = "/" + normalized;
    } else if (!normalized.startsWith("/Uploads/")) {
        normalized = "/Uploads/" + normalized;
    }

    return `${req.protocol}://${req.get('host')}${normalized}`;
}

function formatPet(req, pet) {
    const obj = pet.toObject ? pet.toObject() : { ...pet };
    obj.image = buildImageUrl(req, obj.image);
    return obj;
}

// Home route
router.get('/', (req, res) => {
    res.send("This is Home Page of Pet Upload Section....!!!");
});

// Upload pet + send notification
router.post('/upload', multer.single('image'), async (req, res) => {
    try {
        const { pet_Type, description, upload_date_time, address, status, userEmail, latitude, longitude } = req.body;

        if (!req.file) {
            return res.status(400).json({ error: 'Image is required' });
        }

        const newPet = new Pet({
            pet_Type,
            description,
            upload_date_time,
            address,
            status: status && status.trim() ? status.trim() : "Pending",
            userEmail,
            latitude,
            longitude,
            image: req.file.path,
            imagePublicId: req.file.filename

        });

        await newPet.save();
        console.log('uploaded', newPet);

        // notification part
        const users = await User.find({
            fcmToken: { $exists: true, $ne: "" }
        });

        const tokens = users
            .map(user => user.fcmToken)
            .filter(token => token && token.trim() !== "");

        console.log("FCM tokens found:", tokens.length);

        if (tokens.length > 0) {
            const message = {
                notification: {
                    title: "New Pet Rescue Request",
                    body: `${pet_Type} uploaded at ${address}`
                },
                data: {
                    pet_Type: pet_Type || "",
                    address: address || "",
                    status: "Pending"
                },
                tokens: tokens
            };

            const response = await admin.messaging().sendEachForMulticast(message);

            console.log("Notification success:", response.successCount);
            console.log("Notification failed:", response.failureCount);

            const invalidTokens = [];

            response.responses.forEach((resp, index) => {
                if (!resp.success) {
                    console.log(
                        "Failed token:",
                        tokens[index],
                        resp.error ? resp.error.message : "Unknown error"
                    );
                    invalidTokens.push(tokens[index]);
                }
            });

            if (invalidTokens.length > 0) {
                await User.updateMany(
                    { fcmToken: { $in: invalidTokens } },
                    { $set: { fcmToken: "" } }
                );
            }
        } else {
            console.log("No FCM tokens found");
        }

        res.status(201).json({
            message: "Pet uploaded successfully!",
            pet: formatPet(req, newPet)
        });

    } catch (error) {
        console.error("Upload error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Get all pets
router.get('/get_all_pets', async (req, res) => {
    try {
        const pets = await Pet.find().sort({ upload_date_time: -1, _id: -1 });
        res.status(200).json(pets.map(pet => formatPet(req, pet)));
    } catch (error) {
        console.error("Fetch all pets error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Get pending pets
router.get('/get_pending_pets', async (req, res) => {
    try {
        const pets = await Pet.find({ status: { $regex: /^Pending$/i } })
            .sort({ upload_date_time: -1, _id: -1 });

        res.status(200).json(pets.map(pet => formatPet(req, pet)));
    } catch (error) {
        console.error("Fetch pending pets error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Get user pets
router.get('/get_user_pets/:email', async (req, res) => {
    try {
        const { email } = req.params;

        const pets = await Pet.find({ userEmail: email })
            .sort({ upload_date_time: -1, _id: -1 });

        res.status(200).json(pets.map(pet => formatPet(req, pet)));
    } catch (error) {
        console.error("Fetch user pets error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Update pet status
// Update pet status + notify user when accepted
router.patch('/update_pets/:id', async (req, res) => {
    try {
        const { id } = req.params;
        let { status } = req.body;

        if (!status) {
            return res.status(400).json({ error: "Status is required" });
        }

        status = status.trim().toLowerCase();

        if (status === "accept" || status === "accepted") status = "Accepted";
        else if (status === "reject" || status === "rejected") status = "Rejected";
        else if (status === "pending") status = "Pending";
        else return res.status(400).json({ error: "Invalid status value" });

        const updatedPet = await Pet.findByIdAndUpdate(
            id,
            { status: status },
            { new: true }
        );

        if (!updatedPet) {
            return res.status(404).json({ error: "Pet not found" });
        }

        // 🔥 SEND NOTIFICATION TO USER WHEN ACCEPTED
        if (status === "Accepted") {

            // Find user using email (since you are storing userEmail)
            const user = await User.findOne({ email: updatedPet.userEmail });

            if (user && user.fcmToken) {

                const message = {
                    notification: {
                        title: "Help is on the way 🚑",
                        body: "A volunteer has accepted your request"
                    },
                    data: {
                        status: "Accepted",
                        petId: updatedPet._id.toString()
                    },
                    token: user.fcmToken
                };

                try {
                    await admin.messaging().send(message);
                    console.log("User notified successfully");
                } catch (err) {
                    console.log("Notification error:", err.message);
                }
            }
        }

        res.status(200).json({
            message: "Pet status updated successfully",
            pet: formatPet(req, updatedPet)
        });

    } catch (error) {
        console.error("Update pet status error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

module.exports = router;