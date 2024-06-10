const pool = require("../db");

const sendMessage = async (req, res) => {
    const { sender_id, receiver_id, content } = req.body;
    try {
        const query = `
            INSERT INTO Messages (sender_id, receiver_id, content)
            VALUES ($1, $2, $3)
            RETURNING *
        `;
        const values = [sender_id, receiver_id, content];
        const result = await pool.query(query, values);
        res.status(201).json({ success: true, message: "Message sent successfully", payload: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, message: "Error sending message", payload: null });
    }
};

const getMessages = async (req, res) => {
    const { sender_id, receiver_id } = req.params;
    try {
        const query = `
            SELECT m.*, u.name AS sender_name
            FROM Messages m
            JOIN Users u ON m.sender_id = u.user_id
            WHERE (m.sender_id = $1 AND m.receiver_id = $2)
            OR (m.sender_id = $2 AND m.receiver_id = $1)
            ORDER BY m.message_id ASC
        `;
        const values = [sender_id, receiver_id];
        const result = await pool.query(query, values);
        res.status(200).json({ success: true, message: "Messages retrieved successfully", payload: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, message: "Error retrieving messages", payload: null });
    }
};

const getMessageSenders = async (req, res) => {
    const { receiver_id } = req.params;
    try {
        const query = `
            SELECT DISTINCT sender_id
            FROM Messages
            WHERE receiver_id = $1
        `;
        const values = [receiver_id];
        const result = await pool.query(query, values);
        const senderIds = result.rows.map(row => row.sender_id);

        if (senderIds.length === 0) {
            return res.status(200).json({ success: true, message: "No senders found", payload: [] });
        }

        const usersQuery = `
            SELECT user_id, name, username
            FROM Users
            WHERE user_id = ANY($1::uuid[])
        `;
        const usersResult = await pool.query(usersQuery, [senderIds]);

        res.status(200).json({ success: true, message: "Senders retrieved successfully", payload: usersResult.rows });
    } catch (error) {
        res.status(500).json({ success: false, message: "Error retrieving senders", payload: null });
    }
};

module.exports = {
    sendMessage,
    getMessages,
    getMessageSenders
};
