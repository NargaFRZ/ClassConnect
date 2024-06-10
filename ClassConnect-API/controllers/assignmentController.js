const pool = require("../db");

const getAssignmentById = async (req, res) => {
    try {
        const query = "SELECT * FROM Assignments WHERE assignment_id = $1";
        const result = await pool.query(query, [req.params.assignmentId]);
        res.status(200).json({ success: true, message: "Assignment retrieved successfully", payload: result.rows });
    } catch (error) {
        console.error("Error retrieving assignment:", error);
        res.status(500).json({ success: false, message: "Error retrieving assignment", payload: null });
    }
};

const deleteAssignment = async (req, res) => {
    try {
        const { assignmentId } = req.params;
        const query = "DELETE FROM Assignments WHERE assignment_id = $1";
        await pool.query(query, [assignmentId]);
        res.status(200).json({ success: true, message: "Assignment deleted successfully", payload: null });
    } catch (error) {
        console.error("Error deleting assignment:", error);
        res.status(500).json({ success: false, message: "Error deleting assignment", payload: null });
    }
};

const updateAssignment = async (req, res) => {
    try {
        const { assignmentId } = req.params;
        const { title, description, due_date } = req.body;
        const query = `
            UPDATE Assignments
            SET title = $1, description = $2, due_date = $3
            WHERE assignment_id = $4
        `;
        await pool.query(query, [title, description, due_date, assignmentId]);
        res.status(200).json({ success: true, message: "Assignment updated successfully", payload: null });
    } catch (error) {
        console.error("Error updating assignment:", error);
        res.status(500).json({ success: false, message: "Error updating assignment", payload: null });
    }
};

const createSubmission = async (req, res) => {
    const { assignment_id, student_id } = req.body;
    const submitted_date = new Date();
    const submission = req.file.path;

    try {
        const query = `
            INSERT INTO Submission (assignment_id, student_id, submission, submitted_date)
            VALUES ($1, $2, $3, $4)
            RETURNING *
        `;
        const values = [assignment_id, student_id, submission, submitted_date];
        const result = await pool.query(query, values);
        const newSubmission = result.rows[0];

        res.status(201).json({ success: true, message: "Submission created successfully", payload: newSubmission });
    } catch (error) {
        console.error("Error creating submission:", error);
        res.status(500).json({ success: false, message: "Error creating submission", payload: null });
    }
};

const deleteSubmission = async (req, res) => {
    try {
        const { submissionId } = req.params;
        console.log(req.params);
        const query = "DELETE FROM Submission WHERE submission_id = $1";
        await pool.query(query, [submissionId]);
        res.status(200).json({ success: true, message: "Submission deleted successfully", payload: null });
    } catch (error) {
        console.error("Error deleting submission:", error);
        res.status(500).json({ success: false, message: "Error deleting submission", payload: null });
    }
};

const updateSubmission = async (req, res) => {
    const { submissionId } = req.params;
    const { assignment_id, student_id } = req.body;
    const submitted_date = new Date();
    const submission = req.file.path;

    try {
        const query = `
            UPDATE Submission
            SET assignment_id = $1, student_id = $2, submission = $3, submitted_date = $4
            WHERE submission_id = $5
            RETURNING *
        `;
        const values = [assignment_id, student_id, submission, submitted_date, submissionId];
        const result = await pool.query(query, values);
        const updatedSubmission = result.rows[0];

        res.status(200).json({ success: true, message: "Submission updated successfully", payload: updatedSubmission });
    } catch (error) {
        console.error("Error updating submission:", error);
        res.status(500).json({ success: false, message: "Error updating submission", payload: null });
    }
};

const getSubmissionByAssignmentAndStudent = async (req, res) => {
    const { assignment_id, student_id } = req.query;

    try {
        const query = `SELECT * FROM Submission WHERE assignment_id = $1 AND student_id = $2`;
        const values = [assignment_id, student_id];
        const result = await pool.query(query, values);

        if (result.rows.length > 0) {
            res.status(200).json({ success: true, payload: result.rows[0] });
        } else {
            res.status(404).json({ success: false, message: "No submission found" });
        }
    } catch (error) {
        console.error("Error fetching submission:", error);
        res.status(500).json({ success: false, message: "Error fetching submission" });
    }
};

const getSubmissionsByAssignment = async (req, res) => {
    const { assignment_id } = req.params;

    try {
        const query = `
            SELECT s.submission_id, s.assignment_id, s.student_id, s.submission, s.submitted_date, u.name AS student_name
            FROM Submission s
            JOIN Users u ON s.student_id = u.user_id
            WHERE s.assignment_id = $1
        `;
        const values = [assignment_id];
        const result = await pool.query(query, values);
        const submissions = result.rows;

        res.status(200).json({ success: true, payload: submissions });
    } catch (error) {
        console.error("Error fetching submissions:", error);
        res.status(500).json({ success: false, message: "Error fetching submissions", payload: null });
    }
};

const getSubmissionByClass = async (req, res) => {
    const { class_id } = req.query;

    try {
        const query = `
            SELECT s.submission_id, s.assignment_id, s.student_id, s.submission, s.submitted_date, u.name AS student_name
            FROM Submission s
            JOIN Users u ON s.student_id = u.user_id
            JOIN StudentClass c ON s.student_id = c.student_id
            WHERE c.class_id = $1
        `;
        const values = [class_id];
        const result = await pool.query(query, values);
        const submissions = result.rows;

        res.status(200).json({ success: true, payload: submissions });
    } catch (error) {
        console.error("Error fetching submissions:", error);
        res.status(500).json({ success: false, message: "Error fetching submissions", payload: null });
    }
};

const getAssignmentByClass = async (req, res) => {
    const { class_id } = req.query;

    try {
        const query = `
            SELECT a.assignment_id, a.title, a.description, a.due_date
            FROM Assignments a
            JOIN Classes c ON a.class_id = c.class_id
            WHERE c.class_id = $1
        `;
        const values = [class_id];
        const result = await pool.query(query, values);
        const assignments = result.rows;

        res.status(200).json({ success: true, payload: assignments });
    } catch (error) {
        console.error("Error fetching assignments:", error);
        res.status(500).json({ success: false, message: "Error fetching assignments", payload: null });
    }
};

const getSubmissionById = async (req, res) => {
    try {
        const query = "SELECT * FROM Submission WHERE submission_id = $1";
        const result = await pool.query(query, [req.params.submissionId]);
        const submission = result.rows[0]; 
        res.status(200).json({ success: true, message: "Submission retrieved successfully", payload: submission });
    } catch (error) {
        console.error("Error retrieving submission:", error);
        res.status(500).json({ success: false, message: "Error retrieving submission", payload: null });
    }
};


module.exports = {
    getAssignmentById,
    deleteAssignment,
    updateAssignment,
    createSubmission,
    deleteSubmission,
    updateSubmission,
    getSubmissionByAssignmentAndStudent,
    getSubmissionsByAssignment,
    getSubmissionByClass,
    getAssignmentByClass,
    getSubmissionById,
};
