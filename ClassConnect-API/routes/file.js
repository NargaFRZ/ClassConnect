const express = require('express');
const multer = require('multer');
const path = require('path');
const fileController = require('../controllers/fileController');

const router = express.Router();

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
      cb(null, path.join(__dirname, '../uploads'));
    },
    filename: function (req, file, cb) {
      cb(null, file.originalname);
    },
});

const upload = multer({ storage: storage });

router.post('/uploadFile', upload.single('file'), fileController.uploadFile);
router.delete('/deleteFile', fileController.deleteFile);

module.exports = router;
