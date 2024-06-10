const express = require('express');
const { check } = require('express-validator');
const accountController = require('../controllers/accountController');

const router = express.Router();

router.post('/register', [
  check('name').not().isEmpty(),
  check('username').not().isEmpty(),
  check('password').not().isEmpty(),
  check('email').not().isEmpty(),
  check('role').isIn(['student', 'teacher', 'admin']),
], accountController.registerUser);

router.post('/login', accountController.loginUser);

module.exports = router;
