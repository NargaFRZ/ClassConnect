const pool = require("../db");
const fs = require('fs');
const path = require('path');

const createClass = async (req, res) => {
    try {
        const { name, description, enrollment_key, user_id } = req.body; 

        const roleCheckQuery = 'SELECT role FROM Users WHERE user_id = $1';
        const roleCheckResult = await pool.query(roleCheckQuery, [user_id]);

        if (roleCheckResult.rows[0].role !== 'teacher') {
            return res.status(403).json({ success: false, message: "Only teachers can create classes", payload: null });
        }

        const insertClassQuery = `
            INSERT INTO Classes (name, description, enrollment_key)
            VALUES ($1, $2, $3)
            RETURNING *
        `;
        const insertClassValues = [name, description, enrollment_key];
        const classResult = await pool.query(insertClassQuery, insertClassValues);
        const newClass = classResult.rows[0];

        const insertTeacherClassQuery = `
            INSERT INTO TeacherClass (class_id, teacher_id)
            VALUES ($1, $2)
        `;
        const insertTeacherClassValues = [newClass.class_id, user_id];
        await pool.query(insertTeacherClassQuery, insertTeacherClassValues);

        res.status(201).json({ success: true, message: "Class created successfully", payload: newClass });
    } catch (error) {
        console.error("Error creating class:", error);
        res.status(500).json({ success: false, message: "Error creating class", payload: null });
    }
}

const registerAsClassTeacher = async (req, res) => {
    const { class_id, teacher_id } = req.body;
    try {
        const query = `
            INSERT INTO TeacherClass (class_id, teacher_id)
            VALUES ($1, $2)
            RETURNING *
        `;
        const values = [class_id, teacher_id];
        const result = await pool.query(query, values);
        const newTeacherClass = result.rows[0];

        res.status(201).json({ success: true, message: "Teacher registered for class successfully", payload: newTeacherClass });
    } catch (error) {
        console.error("Error registering teacher for class:", error);
        res.status(500).json({ success: false, message: "Error registering teacher for class", payload: null });
    }
};

const createAssignment = async (req, res) => {
    const { class_id, title, description, due_date } = req.body;
    const file = req.file;

    if (!file) {
        return res.status(400).send({ success: false, message: 'No file uploaded' });
    }

    try {
        const filePath = path.join(__dirname, '../uploads', file.originalname);
        const query = `
            INSERT INTO Assignments (class_id, title, assignment, description, due_date)
            VALUES ($1, $2, $3, $4, $5)
            RETURNING *
        `;
        const values = [class_id, title, filePath, description, due_date];
        const result = await pool.query(query, values);
        const newAssignment = result.rows[0];

        res.status(201).json({ success: true, message: 'Assignment created successfully', payload: newAssignment });
    } catch (error) {
        console.error('Error creating assignment:', error);
        res.status(500).json({ success: false, message: 'Error creating assignment', payload: null });
    }
};

const gradeAssignment = async (req, res) => {
    const { submission_id, score, feedback } = req.body;

    try {
        const query = `
            INSERT INTO Grades (submission_id, score, feedback)
            VALUES ($1, $2, $3)
            RETURNING *
        `;
        const values = [submission_id, score, feedback];
        const result = await pool.query(query, values);
        const newGrade = result.rows[0];

        res.status(201).json({ success: true, message: 'Assignment graded successfully', payload: newGrade });
    } catch (error) {
        console.error('Error grading assignment:', error);
        res.status(500).json({ success: false, message: 'Error grading assignment', payload: null });
    }
};


const getTeachedClasses = async (req, res) => {
    const teacherId = req.params.teacherId;

    try {
        const result = await db.query('SELECT * FROM classes WHERE teacher_id = $1', [teacherId]);
        res.json(result.rows);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch teached classes' });
    }
};


module.exports = {
    createClass,
    registerAsClassTeacher,
    createAssignment,
    gradeAssignment,
    getTeachedClasses,
};
