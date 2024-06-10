const bcrypt = require("bcryptjs");
const { validationResult } = require("express-validator");
const pool = require("../db");

const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(String(email).toLowerCase());
};

const registerUser = async (req, res) => {
  const { name, username, password, email, role } = req.body;
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, message: "Validation errors", payload: errors.array() });
  }

  if (!validateEmail(email)) {
    return res.status(400).json({ success: false, message: "Invalid email format", payload: null });
  }

  const validRoles = ["student", "teacher", "admin"];
  if (!validRoles.includes(role)) {
    return res.status(400).json({ success: false, message: "Invalid role", payload: null });
  }

  try {
    const usernameCheck = await pool.query(
      "SELECT * FROM Users WHERE username = $1",
      [username]
    );
    if (usernameCheck.rowCount > 0) {
      return res.status(400).json({ success: false, message: "Username already exists", payload: null });
    }

    const emailCheck = await pool.query(
      "SELECT * FROM Users WHERE email = $1",
      [email]
    );
    if (emailCheck.rowCount > 0) {
      return res.status(400).json({ success: false, message: "Email already exists", payload: null });
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const result = await pool.query(
      "INSERT INTO Users (name, username, password, email, role) VALUES ($1, $2, $3, $4, $5::Roles) RETURNING *",
      [name, username, hashedPassword, email, role]
    );
    res.status(201).json({ success: true, message: "User registered successfully", payload: result.rows[0] });
  } catch (err) {
    console.error("Error creating user:", err.message);
    res.status(500).json({ success: false, message: "Error creating user", payload: null });
  }
};

const loginUser = async (req, res) => {
  const { username, password } = req.body;
  try {
    const result = await pool.query("SELECT * FROM Users WHERE username = $1", [username]);
    const user = result.rows[0];
    if (!user) {
      return res.status(400).json({ success: false, message: "Invalid credentials", payload: null });
    }

    if (!user.approved) {
      return res.status(400).json({ success: false, message: "Account not approved yet", payload: null });
    }

    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({ success: false, message: "Invalid credentials", payload: null });
    }

    res.status(200).json({ success: true, message: "Login successful", payload: user });
  } catch (err) {
    res.status(500).json({ success: false, message: "Error logging in", payload: null });
  }
};

module.exports = {
  registerUser,
  loginUser,
};
