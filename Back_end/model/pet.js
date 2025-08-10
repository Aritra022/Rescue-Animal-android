const mongoose = require('mongoose');

const petSchema = new mongoose.Schema({
    pet_Type: String,
    description: String,
    upload_date_time: {
        type: Date,
        default: Date.now // Automatically sets current date/time
    },
    address: String,
    status: String,
    image: String,
});

module.exports = mongoose.model('Pet', petSchema);
