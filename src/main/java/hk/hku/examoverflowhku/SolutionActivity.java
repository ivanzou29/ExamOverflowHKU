package hk.hku.examoverflowhku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class SolutionActivity extends AppCompatActivity {
    TextView greetingTextView;
    TextView unlockTextView;

    TextView courseInfoView;
    TextView yearSemQNumView;

    LinearLayout solutionScrollviewLayout;

    Button addSolutionButton;
    Button backToQuestionsButton;
    Button logoutButton;


    String name;
    int unlocks;
    String courseCode;
    String courseTitle;
    String academicYear;
    String semester;
    String questionTitle;

    ProcessingDialog preparingDialog;
    ProcessingDialog addingSolutionDialog;

    JDBCUtilities jdbcUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution);

        Bundle extras = getIntent().getExtras();
        unlocks = extras.getInt("unlocks");
        courseCode = extras.getString("courseCode");
        courseTitle = extras.getString("courseTitle");
        academicYear = extras.getString("academicYear");
        semester = extras.getString("semester");
        questionTitle = extras.getString("questionTitle");


        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        name = sharedPreferences.getString("name", "");


        greetingTextView = findViewById(R.id.greeting_text_view);
        unlockTextView = findViewById(R.id.unlock_remaining);

        greetingTextView.setText(greetingTextView.getText().toString() + name + "!");
        unlockTextView.setText(unlockTextView.getText().toString() + Integer.toString(unlocks) + ".");

        courseInfoView = findViewById(R.id.course_title_view);
        courseInfoView.setText(courseCode + ": " + courseTitle);

        yearSemQNumView = findViewById(R.id.academic_year_and_semester_and_question_view);
        yearSemQNumView.setText(academicYear + ", semester" + semester + ": " + questionTitle);

        addSolutionButton = findViewById(R.id.add_solution_button);
        backToQuestionsButton = findViewById(R.id.back_to_question_button);
        logoutButton = findViewById(R.id.log_out_button);

        solutionScrollviewLayout = findViewById(R.id.solution_scrollview_layout);

        jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();


        View view = View.inflate(this, R.layout.activity_question, null);
        addingSolutionDialog = new ProcessingDialog(view, R.string.adding_question);
        preparingDialog = new ProcessingDialog(view, R.string.preparing_solution_layout);


    }

}
