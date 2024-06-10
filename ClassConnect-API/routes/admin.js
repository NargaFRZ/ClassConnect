const express = require('express');
const { check } = require('express-validator');
const adminController = require('../controllers/adminController');

const router = express.Router();

router.put('/approve', adminController.approveUser);
router.delete('/delete', adminController.deleteUser);
router.get('/unapproved', adminController.getUnapprovedUsers);

module.exports = router;
