require('dotenv').config();
const admin = require('firebase-admin');
const serviceAccount =process.env.SERVIECE_ACCOUNT_KEY;

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
});

module.exports = admin;