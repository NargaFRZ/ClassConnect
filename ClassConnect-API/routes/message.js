const express = require('express');
const { check } = require('express-validator');
const messagesController = require('../controllers/messagesController');

const router = express.Router();

router.post('/sendMessage', messagesController.sendMessage);
router.get('/getMessages/:sender_id/:receiver_id', messagesController.getMessages);
router.get('/getMessageSenders/:receiver_id', messagesController.getMessageSenders);

module.exports = router;
