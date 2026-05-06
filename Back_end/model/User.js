const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true
    },
    password: {
        type: String,
        required: true
    },
    email: {
        type: String,
        required: true
    },
    contact: { 
        type: String,
        required: true
    },
    location: {
        type: String,
        required: true
    },
     fcmToken: {
        type: String,
        default: ""
    }
});

module.exports = mongoose.model('User', userSchema);
