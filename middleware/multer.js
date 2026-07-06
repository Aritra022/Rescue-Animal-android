const multer = require("multer");
const { CloudinaryStorage } = require("multer-storage-cloudinary");
const cloudinary = require("../config/cloudinary");

const storage = new CloudinaryStorage({
    cloudinary,
    params: {
        folder: "PetRescue", // Folder name in Cloudinary

        allowed_formats: ["jpg", "jpeg", "png"],

        public_id: (req, file) => {
            return Date.now().toString();
        }
    }
});

const fileFilter = (req, file, cb) => {
    const allowedTypes = /jpg|jpeg|png/;

    const extname = allowedTypes.test(
        require("path").extname(file.originalname).toLowerCase()
    );

    const mimetype = allowedTypes.test(file.mimetype);

    if (extname && mimetype) {
        cb(null, true);
    } else {
        cb(new Error("Only JPG, JPEG, and PNG images are allowed."));
    }
};

module.exports = multer({
    storage,
    fileFilter,
    limits: {
        fileSize: 5 * 1024 * 1024
    }
});