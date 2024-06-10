const pool = require("../db");

const approveUser = async (req, res) => {
  const { user_id } = req.body;
  try {
    const result = await pool.query(
      "UPDATE Users SET approved = TRUE WHERE user_id = $1 RETURNING *",
      [user_id]
    );
    if (result.rowCount === 0) {
      return res.status(404).json({ success: false, message: "User not found", payload: null });
    }
    res.status(200).json({ success: true, message: "User approved successfully", payload: result.rows[0] });
  } catch (err) {
    res.status(500).json({ success: false, message: "Error approving user", payload: null });
  }
};

const deleteUser = async (req, res) => {
  const { user_id } = req.query;
  try {
    const result = await pool.query(
      "DELETE FROM Users WHERE user_id = $1 RETURNING *",
      [user_id]
    );
    if (result.rowCount === 0) {
      return res.status(404).json({ success: false, message: "User not found", payload: null });
    }
    res.status(200).json({ success: true, message: "User deleted successfully", payload: result.rows[0] });
  } catch (err) {
    res.status(500).json({ success: false, message: "Error deleting user", payload: null });
  }
};

const getUnapprovedUsers = async (req, res) => {
  try {
    const result = await pool.query(
      "SELECT * FROM Users WHERE approved = FALSE"
    );
    res.status(200).json({ success: true, message: "Unapproved users retrieved successfully", payload: result.rows });
  } catch (err) {
    res.status(500).json({ success: false, message: "Error retrieving unapproved users", payload: null });
  }
};

module.exports = {
  approveUser,
  deleteUser,
  getUnapprovedUsers,
};
