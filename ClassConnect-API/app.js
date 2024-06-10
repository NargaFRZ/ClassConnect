const express = require('express');
const bodyParser = require('body-parser');
const accountRoutes = require('./routes/account');
const adminRoutes = require('./routes/admin');
const teacherRoutes = require('./routes/teacher');
const fileRoutes = require('./routes/file');
const classRoutes = require('./routes/class');
const studentRoutes = require('./routes/student');
const messageRoutes = require('./routes/message');
const assignmentRoutes = require('./routes/assignment');
const app = express();
require('dotenv').config();

app.use(bodyParser.json());

app.use('/account', accountRoutes);
app.use('/admin', adminRoutes);
app.use('/teacher', teacherRoutes);
app.use('/file', fileRoutes);
app.use('/class', classRoutes);
app.use('/student', studentRoutes);
app.use('/messages', messageRoutes);
app.use('/assignment', assignmentRoutes);

const PORT = process.env.PORT;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
