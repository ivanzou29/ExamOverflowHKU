package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.Model.Question;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class QuestionActivity extends AppCompatActivity {
    TextView greetingTextView;
    TextView unlockTextView;

    TextView courseInfoView;
    TextView yearSemView;

    Button addQuestionButton;
    Button backToSearchButton;
    Button logoutButton;


    String name;
    int unlocks;
    String courseCode;
    String courseTitle;
    String academicYear;
    String semester;

    ProcessingDialog addingQuestionDialog;

    JDBCUtilities jdbcUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Bundle extras = getIntent().getExtras();
        unlocks = extras.getInt("unlocks");
        courseCode = extras.getString("courseCode");
        courseTitle = extras.getString("courseTitle");
        academicYear = extras.getString("academicYear");
        semester = extras.getString("semester");

        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        name = sharedPreferences.getString("name", "");


        greetingTextView = findViewById(R.id.greeting_text_view);
        unlockTextView = findViewById(R.id.unlock_remaining);

        greetingTextView.setText(greetingTextView.getText().toString() + name + "!");
        unlockTextView.setText(unlockTextView.getText().toString() + Integer.toString(unlocks) + ".");

        courseInfoView = findViewById(R.id.course_title_view);
        courseInfoView.setText(courseCode + ": " + courseTitle);

        yearSemView = findViewById(R.id.academic_year_and_semester_view);
        yearSemView.setText(academicYear + ", semester" + semester);

        addQuestionButton = findViewById(R.id.add_question_button);
        backToSearchButton = findViewById(R.id.back_to_search_button);
        logoutButton = findViewById(R.id.log_out_button);

        jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();

        View view = View.inflate(this, R.layout.activity_question, null);
        addingQuestionDialog = new ProcessingDialog(view, R.string.adding_question);

        addQuestionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final AlertDialog.Builder addQuestionDialogBuilder = new AlertDialog.Builder(QuestionActivity.this);
                addQuestionDialogBuilder.setTitle("Add an exam question number");
                final EditText inputQuestionNumber = new EditText(QuestionActivity.this);
                inputQuestionNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                addQuestionDialogBuilder.setView(inputQuestionNumber);

                addQuestionDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        addingQuestionDialog.show();
                        findViewById(R.id.question_root_view).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    Question question = new Question();
                                    question.setCourseCode(courseCode);
                                    question.setAcademicYear(academicYear);
                                    question.setQuestionNum(Integer.parseInt(inputQuestionNumber.getText().toString()));
                                    question.setSemester(Integer.parseInt(semester));
                                    question.setQuestionId(UUID.randomUUID().toString());
                                    jdbcUtilities.insertQuestion(question);
                                    addingQuestionDialog.dismiss();
                                    Toast.makeText(QuestionActivity.this, "You have inserted the question successfully", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    addingQuestionDialog.dismiss();
                                    Toast.makeText(QuestionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_LONG).show();

                                } finally {
                                    jdbcUtilities.closeConnection();
                                }
                            }

                    });
                    }
                });


                addQuestionDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                addQuestionDialogBuilder.show();
            }
        });

        backToSearchButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                QuestionActivity.this.finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(QuestionActivity.this);
                logoutDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences sp = getSharedPreferences("config", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent myIntent = new Intent(QuestionActivity.this, LoginActivity.class);
                        myIntent.putExtra("loggingOut", true);
                        startActivity(myIntent);
                    }
                });
                logoutDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                logoutDialogBuilder.setMessage("Are you sure you want to log out?");
                logoutDialogBuilder.show();
            }
        });
    }
}
