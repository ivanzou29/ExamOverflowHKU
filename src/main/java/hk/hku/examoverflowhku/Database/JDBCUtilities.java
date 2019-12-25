package hk.hku.examoverflowhku.Database;


import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import hk.hku.examoverflowhku.Model.Question;
import hk.hku.examoverflowhku.Model.Student;

public class JDBCUtilities {
    private static final String URL = "jdbc:mysql://cdb-rhgud8sn.gz.tencentcdb.com:10029/ExamOverflow?useSSL=true";
    private static final String USER = "root";
    private static final String PASSWORD = "COMP3330@hku";
    private static Connection conn = null;

    public static Connection openConnection() {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public void insertStudent(Student student) {
        String sql = "INSERT INTO Student (email, uid, password, name) " +
                "VALUES (?,?,?,?)";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, student.getEmail());
            ptmt.setString(2, student.getUid());
            ptmt.setString(3, student.getPassword());
            ptmt.setString(4, student.getName());
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPasswordByUid(String uid) {
        String sql = "SELECT password FROM Student WHERE uid = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, uid);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                ptmt.close();
                return password;
            } else {
                return "Invalid Query for the password...";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Invalid Query for the password...";
        }
    }

    public String getNameByUid(String uid) {
        String sql = "SELECT name FROM Student WHERE uid = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, uid);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                ptmt.close();
                return name;
            } else {
                return "Anonymous";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Anonymous";
        }
    }

    public void insertUnlocks(String uid, int unlocks) {
        String sql = "INSERT INTO Unlocks (uid, unlocks) " +
                "VALUES (?,?)";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, uid);
            ptmt.setInt(2, unlocks);
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUnlocksByUid(String uid) {
        String sql = "SELECT Unlocks FROM Unlocks WHERE uid = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, uid);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                int unlocks = rs.getInt("unlocks");
                ptmt.close();
                return unlocks;
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getCourseTitleByCourseCode(String courseCode) {
        String sql = "SELECT course_title FROM Course WHERE course_code = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, courseCode);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                String courseTitle = rs.getString("course_title");
                ptmt.close();
                return courseTitle;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertQuestion(Question question) {
        String sql = "INSERT INTO Question (course_code, academic_year, semester, question_num, question_id) " +
                "VALUES (?,?,?,?,?)";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, question.getCourseCode());
            ptmt.setString(2, question.getAcademicYear());
            ptmt.setInt(3, question.getSemester());
            ptmt.setInt(4, question.getQuestionNum());
            ptmt.setString(5, question.getQuestionId());
            ptmt.execute();
            ptmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getQuestionId(String courseCode, String academicYear, int semester, int question_num) {
        String sql = "SELECT question_id FROM Question WHERE course_code = ? AND academic_year = ? AND semester = ? AND question_num = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, courseCode);
            ptmt.setString(2, academicYear);
            ptmt.setInt(3, semester);
            ptmt.setInt(4, question_num);
            ResultSet rs = ptmt.executeQuery();
            if (rs.next()) {
                String questionId = rs.getString("question_id");
                ptmt.close();
                return questionId;
            } else {
                return "System error";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "System error";
        }
    }

    public Collection<Integer> getQuestionNums(String courseCode, String academicYear, int semester) {
        String sql = "SELECT question_num FROM Question WHERE course_code = ? AND academic_year = ? AND semester = ?";
        try {
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, courseCode);
            ptmt.setString(2, academicYear);
            ptmt.setInt(3, semester);
            ResultSet rs = ptmt.executeQuery();

            ArrayList<Integer> questionNums = new ArrayList<Integer>();

            while (rs.next()) {
                questionNums.add(rs.getInt("question_num"));
            }

            return questionNums;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<Integer>();
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
