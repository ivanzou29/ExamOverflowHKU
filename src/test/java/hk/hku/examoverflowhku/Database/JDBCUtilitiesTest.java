package hk.hku.examoverflowhku.Database;

import org.junit.Test;

import java.sql.Connection;

import hk.hku.examoverflowhku.Model.Student;

public class JDBCUtilitiesTest {

    private Connection conn;

    @Test
    public void openConnection_ShouldNotReturnNullIfValid() {
        conn = JDBCUtilities.openConnection();
        System.out.println(conn);
    }

    @Test
    public void insertStudent_ShouldInsertStudentSuccessfully() {
        JDBCUtilities jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();
        Student student = new Student();
        student.setEmail("123456@hku.hk");
        student.setUid("3035123456");
        student.setName("Zou Yunfan");
        student.setPassword("12345678");
        jdbcUtilities.insertStudent(student);
        jdbcUtilities.closeConnection();
    }

    @Test
    public void getPasswordByUid_ShouldReturnCorrectPassword() {
        JDBCUtilities jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();
        String password = jdbcUtilities.getPasswordByUid("3035123456");
        System.out.println(password);
    }

}
