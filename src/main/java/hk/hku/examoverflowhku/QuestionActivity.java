package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
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

    LinearLayout questionScrollviewLayout;

    Button addQuestionButton;
    Button backToSearchButton;
    Button logoutButton;


    String name;
    int unlocks;
    String courseCode;
    String courseTitle;
    String academicYear;
    String semester;

    ProcessingDialog preparingDialog;
    ProcessingDialog addingQuestionDialog;
    ProcessingDialog inspectQuestionDialog;

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

        questionScrollviewLayout = findViewById(R.id.question_scrollview_layout);

        jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();


        View view = View.inflate(this, R.layout.activity_question, null);
        addingQuestionDialog = new ProcessingDialog(view, R.string.adding_question);
        preparingDialog = new ProcessingDialog(view, R.string.preparing_question_layout);
        inspectQuestionDialog = new ProcessingDialog(view, R.string.inspecting_question);

        preparingDialog.show();
        findViewById(R.id.question_root_view).post(new Runnable() {

            @Override
            public void run() {
                prepareScrollView();
                preparingDialog.dismiss();
            }
        });

        addQuestionButton.setOnClickListener(new View.OnClickListener() {

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
                                    prepareScrollView();
                                    addingQuestionDialog.dismiss();
                                    Toast.makeText(QuestionActivity.this, "You have inserted the question successfully", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    addingQuestionDialog.dismiss();
                                    Toast.makeText(QuestionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_LONG).show();

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

        backToSearchButton.setOnClickListener(new View.OnClickListener() {

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

    public void prepareScrollView() {
        questionScrollviewLayout.removeAllViews();
        ArrayList<Integer> questionNums = new ArrayList<Integer>(jdbcUtilities.getQuestionNums(courseCode, academicYear, Integer.parseInt(semester)));
        Collections.sort(questionNums);

        if (questionNums.size() == 0) {
            TextView questionHeader = findViewById(R.id.question_title_view);
            questionHeader.setText("Currently no questions listed. Please create one by clicking the green button.");
            questionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {
            TextView questionHeader = findViewById(R.id.question_title_view);
            questionHeader.setText(getString(R.string.questions_header));
            questionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            for (int i = 0; i < questionNums.size(); i++) {
                LinearLayout questionLayout = new LinearLayout(this);
                questionLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsLayout.setMargins(5, 20, 5, 20);

                LinearLayout.LayoutParams paramsElement = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsElement.setMargins(100, 10, 100, 10);


                questionLayout.setLayoutParams(paramsLayout);

                final TextView questionNumberTextView = new TextView(this);
                questionNumberTextView.setText("Question " + Integer.toString(questionNums.get(i)));
                questionNumberTextView.setTextColor(getColor(R.color.colorTextInput));
                questionNumberTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                questionNumberTextView.setGravity(Gravity.CENTER);
                questionNumberTextView.setLayoutParams(paramsElement);

                Button questionInspectButton = new Button(this);
                questionInspectButton.setText(R.string.inspect_question);
                questionInspectButton.setBackgroundColor(getColor(R.color.colorAccent));
                questionInspectButton.setTextColor(getColor(R.color.inspectButtonText));
                questionInspectButton.setGravity(Gravity.CENTER);

                final String questionTitle = questionNumberTextView.getText().toString();
                final int questionNum = Integer.parseInt(questionTitle.substring(9));

                questionInspectButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        inspectQuestionDialog.show();
                        findViewById(R.id.question_root_view).post(new Runnable(){

                            @Override
                            public void run() {
                                try {
                                    String questionId = jdbcUtilities.getQuestionId(courseCode, academicYear, Integer.parseInt(semester), questionNum);

                                    Intent myIntent = new Intent(QuestionActivity.this, SolutionActivity.class);
                                    myIntent.putExtra("courseCode", courseCode);
                                    myIntent.putExtra("courseTitle", courseTitle);
                                    myIntent.putExtra("unlocks", unlocks);
                                    myIntent.putExtra("academicYear", academicYear);
                                    myIntent.putExtra("semester", semester);
                                    myIntent.putExtra("questionId", questionId);
                                    myIntent.putExtra("questionTitle", questionTitle);
                                    startActivity(myIntent);

                                    inspectQuestionDialog.dismiss();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    inspectQuestionDialog.dismiss();
                                    Toast.makeText(QuestionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                questionLayout.addView(questionNumberTextView);
                questionLayout.addView(questionInspectButton);

                questionScrollviewLayout.addView(questionLayout);
            }
        }
    }


}
