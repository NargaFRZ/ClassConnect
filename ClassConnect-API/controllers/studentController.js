const pool = require("../db");
const { get } = require("../routes/assignment");

const enrollStudent = async (req, res) => {
    const { classId, studentId, enrollmentKey } = req.body;

    try {
        const client = await pool.connect();

        const verifyQuery = `
            SELECT enrollment_key
            FROM Classes
            WHERE class_id = $1
        `;

        const verifyResult = await client.query(verifyQuery, [classId]);

        if (verifyResult.rows.length === 0) {
            client.release();
            return res.status(404).json({ error: 'Class not found' });
        }

        const correctEnrollmentKey = verifyResult.rows[0].enrollment_key;

        if (correctEnrollmentKey !== enrollmentKey) {
            client.release();
            return res.status(403).json({ error: 'Incorrect enrollment key' });
        }

        const enrollQuery = `
            INSERT INTO StudentClass (class_id, student_id)
            VALUES ($1, $2)
        `;
        const enrollValues = [classId, studentId];

        await client.query(enrollQuery, enrollValues);

        client.release();

        res.status(200).json({ success: true, message: 'Student enrolled successfully', payload: { classId, studentId }});
    } catch (error) {
        console.error('Error enrolling student:', error);
        res.status(500).json({ success: false, message: 'Error enrolling student', payload: null });
    }
};

const getStudentClasses = async (req, res) => {
    const { studentId } = req.params;

    try {
        const query = `
            SELECT c.class_id, c.name, c.description
            FROM Classes c
            INNER JOIN StudentClass s ON c.class_id = s.class_id
            WHERE s.student_id = $1
        `;

        const result = await pool.query(query, [studentId]);

        res.status(200).json({ success: true, message: 'Classes retrieved successfully', payload: result.rows });
    } catch (error) {
        console.error('Error fetching student classes:', error);
        res.status(500).json({ success: false, message: 'Error fetching student classes', payload: null });
    }
};

const getUnsubmittedAssignments = async (req, res) => {
    const { studentId } = req.params;

    try {
        const query = `
            SELECT a.assignment_id, a.title, a.description, a.due_date,
                   CASE 
                       WHEN a.due_date > NOW() THEN DATE_PART('day', a.due_date - NOW())
                       ELSE 0 
                   END AS days_until_due
            FROM Assignments a
            LEFT JOIN Submission s ON a.assignment_id = s.assignment_id AND s.student_id = $1
            WHERE s.submission_id IS NULL
        `;
        const result = await pool.query(query, [studentId]);

        res.status(200).json({ success: true, message: 'Unsubmitted assignments retrieved successfully', payload: result.rows });
    } catch (error) {
        console.error('Error fetching unsubmitted assignments:', error);
        res.status(500).json({ success: false, message: 'Error fetching unsubmitted assignments', payload: null });
    }
};

const getSubmittedAssignments = async (req, res) => {
    const { studentId } = req.params;

    try {
        const query = `
            SELECT a.assignment_id, a.title, a.description, a.due_date, s.submitted_date
            FROM Assignments a
            INNER JOIN Submission s ON a.assignment_id = s.assignment_id
            WHERE s.student_id = $1
        `;
        const result = await pool.query(query, [studentId]);

        res.status(200).json({ success: true, message: 'Submitted assignments retrieved successfully', payload: result.rows });
    } catch (error) {
        console.error('Error fetching submitted assignments:', error);
        res.status(500).json({ success: false, message: 'Error fetching submitted assignments', payload: null });
    }
};

const getRecentGrades = async (req, res) => {
    const { student_id } = req.query;

    try {
        const query = `
            SELECT g.grade_id, g.submission_id, g.score, g.feedback, s.assignment_id
            FROM Grades g
            JOIN Submission s ON g.submission_id = s.submission_id
            WHERE s.student_id = $1
            ORDER BY g.grade_id DESC
            LIMIT 5
        `;
        const values = [student_id];
        const result = await pool.query(query, values);
        const grades = result.rows;

        res.status(200).json({ success: true, payload: grades });
    } catch (error) {
        console.error("Error fetching recent grades:", error);
        res.status(500).json({ success: false, message: "Error fetching recent grades", payload: null });
    }
};

module.exports = {
    enrollStudent,
    getStudentClasses,
    getUnsubmittedAssignments,
    getSubmittedAssignments,
    getRecentGrades,
};