const mongoose = require('mongoose');

const petSchema = new mongoose.Schema({
    pet_Type: String,
    description: String,

    upload_date_time: {
        type: Date,
        default: Date.now,
        expires : 60 * 60 * 12   //12 hours format
    },

    address: String,
    status: String,
    image: String,
    userEmail: String,

    // 🔥 NEW FIELDS (for map)
    latitude: {
        type: Number,
        default: null
    },
    longitude: {
        type: Number,
        default: null
    }

});

module.exports = mongoose.model('Pet', petSchema);