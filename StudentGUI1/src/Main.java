import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class StudentGUI extends JFrame implements ActionListener {
    private final JLabel emailLabel, passwordLabel;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JButton loginButton, registerButton;
    private final Connection connection;
    private ResultSet studentInfo;

    public StudentGUI() {
        super("Student Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialize database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/xyz_database", "root", "123");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // initialize components
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // add action listeners
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        // add components to panel
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(new JLabel());
        panel.add(new JLabel());

        // add panel to frame
        add(panel);

        setVisible(true);
    }

    // method to retrieve student information from database
    private void getStudentInfo(int studentID) throws SQLException {
        Statement statement = connection.createStatement();
        studentInfo = statement.executeQuery("SELECT * FROM Students WHERE id = " + studentID);
        studentInfo.next();
    }

    // method to display student information
    private void displayStudentInfo() {
        JFrame studentInfoFrame = new JFrame("Student Information");
        studentInfoFrame.setSize(400, 400);

        JPanel panel = new JPanel(new GridLayout(9, 2));
        panel.add(new JLabel("First Name:"));
        try {
            panel.add(new JLabel(studentInfo.getString("first_name")));
            panel.add(new JLabel("Last Name:"));
            panel.add(new JLabel(studentInfo.getString("last_name")));
            panel.add(new JLabel("Email:"));
            panel.add(new JLabel(studentInfo.getString("email")));
            panel.add(new JLabel("Phone Number:"));
            panel.add(new JLabel(studentInfo.getString("phone_number")));
            panel.add(new JLabel("Date of Birth:"));
            panel.add(new JLabel(studentInfo.getString("date_of_birth")));
            panel.add(new JLabel("Address:"));
            panel.add(new JLabel(studentInfo.getString("address")));

            // add update button
            JButton updateButton = new JButton("Update Information");
            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // open a new frame to update student information
                    UpdateStudentInfoFrame updateFrame = new UpdateStudentInfoFrame(studentInfo);
                }
            });
            panel.add(updateButton);
            panel.add(new JLabel());

            // add select courses button
            JButton selectCoursesButton = new JButton("Select Courses");
            selectCoursesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        CourseSelectionFrame coursesFrame = new CourseSelectionFrame(connection, studentInfo.getInt(1));
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            panel.add(selectCoursesButton);
            panel.add(new JLabel());

            // add drop courses button
            JButton dropCoursesButton = new JButton("Drop Courses");
            dropCoursesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        CourseDropFrame coursesFrame = new CourseDropFrame(connection, studentInfo.getInt(1));
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            panel.add(dropCoursesButton);
            panel.add(new JLabel());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        studentInfoFrame.add(panel);
        studentInfoFrame.setVisible(true);
    }

    // handle button clicks
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            try {
                // retrieve student info from database
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery("SELECT id FROM Students WHERE email = '" + email + "' AND password = '" + password + "'");
                if (result.next()) {
                    int studentID = result.getInt("id");
                    getStudentInfo(studentID);
                    displayStudentInfo();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex){
                JOptionPane.showMessageDialog(this, "Error retrieving student information from database", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();}
        } else if (e.getSource() == registerButton) {
            // create new registration frame
            JFrame registrationFrame = new JFrame("Student Registration");
            registrationFrame.setSize(400, 400);
            // create form panel
            JPanel panel = new JPanel(new GridLayout(8, 2));
            panel.add(new JLabel("First Name:"));
            panel.add(new JTextField());
            panel.add(new JLabel("Last Name:"));
            panel.add(new JTextField());
            panel.add(new JLabel("Email:"));
            panel.add(new JTextField());
            panel.add(new JLabel("Password:"));
            panel.add(new JPasswordField());
            panel.add(new JLabel("Phone Number:"));
            panel.add(new JTextField());
            panel.add(new JLabel("Date of Birth:"));
            panel.add(new JTextField());
            panel.add(new JLabel("Address:"));
            panel.add(new JTextField());
            panel.add(new JLabel());
            JButton registerButton = new JButton("Register");
            panel.add(registerButton);

            // add action listener to register button
            registerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // insert new student into database
                    try {
                        String firstName = ((JTextField)panel.getComponent(1)).getText();
                        String lastName = ((JTextField)panel.getComponent(3)).getText();
                        String email = ((JTextField)panel.getComponent(5)).getText();
                        String password = new String(((JPasswordField)panel.getComponent(7)).getPassword());
                        String phoneNumber = ((JTextField)panel.getComponent(9)).getText();
                        String dateOfBirth = ((JTextField)panel.getComponent(11)).getText();
                        String address = ((JTextField)panel.getComponent(13)).getText();

                        Statement statement = connection.createStatement();
                        String query = "INSERT INTO Students (first_name, last_name, email, phone_number, date_of_birth, address, password) " +
                                "VALUES ('" + firstName + "', '" + lastName + "', '" + email + "', '" + phoneNumber + "', '" + dateOfBirth + "', '" + address + "', '" + password + "')";
                        statement.executeUpdate(query);
                        JOptionPane.showMessageDialog(registrationFrame, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                        registrationFrame.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(registrationFrame, "Error registering student", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });

            // add panel to frame
            registrationFrame.add(panel);
            registrationFrame.setVisible(true);
        }
    }

    class CourseDropFrame extends JFrame implements ActionListener {
        private final Connection connection;
        private final int studentID;
        private final JList<String> coursesList;

        public CourseDropFrame(Connection connection, int studentID) throws SQLException {
            super("Drop Courses");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // initialize database connection
            this.connection = connection;
            this.studentID = studentID;

            // get list of courses that the student is registered for
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Courses.course_name FROM Courses INNER JOIN Enrollment ON Courses.id = Enrollment.course_id WHERE Enrollment.student_id = " + studentID);
            DefaultListModel<String> coursesModel = new DefaultListModel<String>();
            while (resultSet.next()) {
                coursesModel.addElement(resultSet.getString(1));
            }

            // initialize components
            JLabel coursesLabel = new JLabel("Select a course to drop:");
            coursesList = new JList<String>(coursesModel);
            JButton dropButton = new JButton("Drop Course");
            dropButton.addActionListener(this);

            // add components to panel
            JPanel panel = new JPanel(new GridLayout(3, 1));
            panel.add(coursesLabel);
            panel.add(new JScrollPane(coursesList));
            panel.add(dropButton);

            // add panel to frame
            add(panel);

            setVisible(true);
        }

        // method to drop selected course
        private void dropCourse() throws SQLException {
            String selectedCourse = coursesList.getSelectedValue();
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Please select a course to drop.");
                return;
            }
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to drop the course?");
            if (confirmation == JOptionPane.YES_OPTION) {
                // get course ID from database
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT Courses.id FROM Courses INNER JOIN Enrollment ON Courses.id = Enrollment.course_id WHERE Enrollment.student_id = " + studentID + " AND Courses.course_name = '" + selectedCourse + "'");
                resultSet.next();
                int courseID = resultSet.getInt(1);

                // delete enrollment record from database
                statement.executeUpdate("DELETE FROM Enrollment WHERE student_id = " + studentID + " AND course_id = " + courseID);

                JOptionPane.showMessageDialog(this, "Course dropped successfully.");
                dispose();
            }
        }

        // handle button click events
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Drop Course")) {
                try {
                    dropCourse();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }


        class UpdateStudentInfoFrame extends JFrame implements ActionListener {
        private final JLabel firstNameLabel, lastNameLabel, emailLabel, phoneNumberLabel, dateOfBirthLabel, addressLabel;
        private final JTextField firstNameField, lastNameField, emailField, phoneNumberField, dateOfBirthField, addressField;
        private final JButton saveButton;
        private final ResultSet studentInfo;

        public UpdateStudentInfoFrame(ResultSet studentInfo) {
            super("Update Student Information");
            setSize(300, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // initialize components
            this.studentInfo = studentInfo;
            firstNameLabel = new JLabel("First Name:");
            lastNameLabel = new JLabel("Last Name:");
            emailLabel = new JLabel("Email:");
            phoneNumberLabel = new JLabel("Phone Number:");
            dateOfBirthLabel = new JLabel("Date of Birth:");
            addressLabel = new JLabel("Address:");
            try {
                firstNameField = new JTextField(studentInfo.getString("first_name"), 20);
                lastNameField = new JTextField(studentInfo.getString("last_name"), 20);
                emailField = new JTextField(studentInfo.getString("email"), 20);
                phoneNumberField = new JTextField(studentInfo.getString("phone_number"), 20);
                dateOfBirthField = new JTextField(studentInfo.getString("date_of_birth"), 20);
                addressField = new JTextField(studentInfo.getString("address"), 20);
                saveButton = new JButton("Save Changes");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            // add action listener to save button
            saveButton.addActionListener(this);

            // add components to panel
            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(firstNameLabel);
            panel.add(firstNameField);
            panel.add(lastNameLabel);
            panel.add(lastNameField);
            panel.add(emailLabel);
            panel.add(emailField);
            panel.add(phoneNumberLabel);
            panel.add(phoneNumberField);
            panel.add(dateOfBirthLabel);
            panel.add(dateOfBirthField);
            panel.add(addressLabel);
            panel.add(addressField);
            panel.add(saveButton);
            // add panel to frame
            add(panel);

            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == saveButton) {
                try {
                    // update student information in the database
                    String updateQuery = "UPDATE students SET first_name = ?, last_name = ?, email = ?, phone_number = ?, date_of_birth = ?, address = ? WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, firstNameField.getText());
                    preparedStatement.setString(2, lastNameField.getText());
                    preparedStatement.setString(3, emailField.getText());
                    preparedStatement.setString(4, phoneNumberField.getText());
                    preparedStatement.setString(5, dateOfBirthField.getText());
                    preparedStatement.setString(6, addressField.getText());
                    preparedStatement.setInt(7, studentInfo.getInt("id"));
                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Student information updated successfully. Log in again to see changes.");
                    dispose();
                } catch (SQLException exception) {
                    JOptionPane.showMessageDialog(this, "Error updating student information: " + exception.getMessage());
                }
            }
        }
    }


        public class CourseSelectionFrame extends JFrame implements ActionListener {
        private final Connection connection;
        private final int studentID;
        private final JComboBox<String> courseComboBox;
        private final JButton addButton;

        public CourseSelectionFrame(Connection connection, int studentID) throws SQLException {
            super("Course Selection");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            this.connection = connection;
            this.studentID = studentID;

            // create course selection components
            JLabel courseLabel = new JLabel("Select a course:");
            courseComboBox = new JComboBox<>();
            addButton = new JButton("Add");

            // add action listeners
            addButton.addActionListener(this);

            // add components to panel
            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(courseLabel);
            panel.add(courseComboBox);
            panel.add(new JLabel());
            panel.add(addButton);

            // add panel to frame
            add(panel);

            // populate course selection
            populateCourseSelection();

            setVisible(true);
        }

        // method to populate course selection
        private void populateCourseSelection() throws SQLException {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM Courses WHERE id NOT IN (SELECT course_id FROM Enrollment WHERE student_id = " + studentID + ")");
            while (result.next()) {
                courseComboBox.addItem(result.getString("course_name"));
            }
        }

        // handle button clicks
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == addButton) {
                try {
                    // add selected course to student's enrollment
                    String courseName = (String) courseComboBox.getSelectedItem();
                    Statement statement = connection.createStatement();
                    ResultSet result = statement.executeQuery("SELECT id FROM Courses WHERE course_name = '" + courseName + "'");
                    result.next();
                    int courseID = result.getInt("id");
                    statement.executeUpdate("INSERT INTO Enrollment (semester, year, program_of_study, degree, status, student_id, course_id) VALUES ('Spring', 2023, 'Computer Science', 'Bachelor', 'Enrolled', " + studentID + ", " + courseID + ")");
                    JOptionPane.showMessageDialog(this, "Course added successfully!");
                    dispose(); // close course selection frame
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error adding course: " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        new StudentGUI();
    }
}