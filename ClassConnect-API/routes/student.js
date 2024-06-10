const express = require('express');
const { check } = require('express-validator');
const studentController = require('../controllers/studentController');

const router = express.Router();

router.post('/enrollStudent', studentController.enrollStudent);
router.get('/getStudentClasses/:studentId', studentController.getStudentClasses);   
router.get('/getUnsubmittedAssignments/:studentId', studentController.getUnsubmittedAssignments);
router.get('/getSubmittedAssignments/:studentId', studentController.getSubmittedAssignments);
router.get('/getRecentGrades', studentController.getRecentGrades);

module.exports = router;
