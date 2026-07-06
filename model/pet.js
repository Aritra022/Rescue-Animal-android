const mongoose = require('mongoose');

const petSchema = new mongoose.Schema({
    pet_Type: String,
    description: String,

    upload_date_time: {
        type: Date,
        default: Date.now,
        expires: 60 * 60 * 12 // 12 hours
    },

    address: String,
    status: String,

    // Cloudinary image URL
    image: {
        type: String,
        required: true
    },

    // Cloudinary public_id (used for deleting the image later)
    imagePublicId: {
        type: String
    },

    userEmail: String,

    latitude: {
        type: Number,
        default: null
    },

    longitude: {
        type: Number,
        default: null
    }
});

module.exports = mongoose.model("Pet", petSchema);