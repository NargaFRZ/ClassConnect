const express = require('express');
const multer = require('multer');
const path = require('path');
const assignmentController = require('../controllers/assignmentController');

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

router.get('/getAssignmentById/:assignmentId', assignmentController.getAssignmentById);
router.delete('/deleteAssignment/:assignmentId', assignmentController.deleteAssignment);
router.put('/updateAssignment/:assignmentId', assignmentController.updateAssignment);
router.post('/createSubmission', upload.single('submission'), assignmentController.createSubmission);
router.delete('/deleteSubmission/:submissionId', assignmentController.deleteSubmission);
router.put('/updateSubmission/:submissionId', upload.single('submission'), assignmentController.updateSubmission);
router.get('/getSubmission/', assignmentController.getSubmissionByAssignmentAndStudent);
router.get('/submission/:assignmentId', assignmentController.getSubmissionsByAssignment);
router.get('/getSubmissionByClass', assignmentController.getSubmissionByClass);
router.get('/getAssignmentByClass', assignmentController.getAssignmentByClass);
router.get('/getSubmissionById/:submissionId', assignmentController.getSubmissionById);

module.exports = router;
