const express = require('express');
const multer = require('multer');
const path = require('path');
const teacherController = require('../controllers/teacherController');

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

router.post('/createClass', teacherController.createClass);
router.post('/registerAsClassTeacher', teacherController.registerAsClassTeacher);
router.post('/createAssignment', upload.single('file'), teacherController.createAssignment);
router.post('/gradeAssignment', teacherController.gradeAssignment);
router.get('/getTeachedClasses/:teacherId', teacherController.getTeachedClasses);

module.exports = router;
