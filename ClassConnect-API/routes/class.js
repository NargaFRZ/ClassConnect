const express = require('express');
const { check } = require('express-validator');
const classController = require('../controllers/classController');

const router = express.Router();

router.get('/getAllClasses', classController.getAllClasses);
router.get('/getClassById/:classId', classController.getClassById);
router.get('/getClassMembers/:classId', classController.getClassMembers);

module.exports = router;