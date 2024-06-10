const pool = require("../db");

const getAllClasses = async (req, res) => {
    try {
        const query = "SELECT * FROM Classes";
        const result = await pool.query(query);
        res.status(200).json({ success: true, message: "Classes retrieved successfully", payload: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, message: "Error retrieving classes", payload: null });
    }
};

const getClassById = async (req, res) => {
    const { classId } = req.params;
    try {
        const query = "SELECT * FROM Classes WHERE class_id = $1";
        const result = await pool.query(query, [classId]);
        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, message: "Class not found", payload: null });
        }
        res.status(200).json({ success: true, message: "Class retrieved successfully", payload: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, message: "Error retrieving class", payload: null });
    }
};

const getClassMembers = async (req, res) => {
    const { classId } = req.params;

    try {
        const client = await pool.connect();

        const query = `
            SELECT u.role, u.user_id AS member_id, u.name
            FROM Users u
            INNER JOIN TeacherClass t ON u.user_id = t.teacher_id
            WHERE t.class_id = $1

            UNION

            SELECT u.role, u.user_id AS member_id, u.name
            FROM Users u
            INNER JOIN StudentClass s ON u.user_id = s.student_id
            WHERE s.class_id = $1
        `;

        const result = await client.query(query, [classId]);

        client.release();

        res.status(200).json({ success: true, message: 'Class members retrieved successfully', payload: result.rows });
    } catch (error) {
        console.error('Error fetching class members:', error);
        res.status(500).json({ success: false, message: 'Error fetching class members', payload: null });
    };
};

module.exports = {
    getAllClasses,
    getClassById,
    getClassMembers,
};