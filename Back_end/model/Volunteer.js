const mongoose = require('mongoose');

const dataSchema = new mongoose.Schema({
    name: {
        required: true,
        type: String
    },
    email: {
        required: true,
        type: String
    },

    
    password: {
        required: true,
        type: String
    },
    contact: {
        required: true,
        type: String
    },
    location: {
        required: true,
        type: String
    },

    status: {
    type: String,
    default: "active"
}
   
});

module.exports = mongoose.model('Volunteer', dataSchema);
