const fs = require('fs');
const path = require('path');

const uploadFile = (req, res) => {
    const file = req.file;
    if (!file) {
        return res.status(400).send({ error: 'No file uploaded' });
    }
    res.status(201).send({ message: 'File uploaded successfully', file: file });
};

const deleteFile = (req, res) => {
    const { fileName } = req.body;
    const filePath = path.join(__dirname, '../uploads', fileName);

    fs.unlink(filePath, (err) => {
        if (err) {
            return res.status(500).send({ error: 'Failed to delete file' });
        }
        res.status(200).send({ message: 'File deleted successfully' });
    });
};

module.exports = {
    uploadFile,
    deleteFile,
};
