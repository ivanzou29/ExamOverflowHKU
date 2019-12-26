package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.Model.Solution;
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
    String questionId;
    String uid;

    ProcessingDialog preparingDialog;
    ProcessingDialog addingSolutionDialog;
    ProcessingDialog inspectSolutionDialog;

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
        questionId = extras.getString("questionId");


        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        name = sharedPreferences.getString("name", "");
        uid = sharedPreferences.getString("uid", "");


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
        inspectSolutionDialog = new ProcessingDialog(view, R.string.unlocking_solution);

        preparingDialog.show();
        findViewById(R.id.solution_root_view).post(new Runnable() {

            @Override
            public void run() {
                prepareScrollView();
                preparingDialog.dismiss();
            }
        });

        addSolutionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final AlertDialog.Builder addQuestionDialogBuilder = new AlertDialog.Builder(SolutionActivity.this);
                addQuestionDialogBuilder.setTitle("Add a solution (You would earn one unlock)");
                final LinearLayout inputSolutionLayout = new LinearLayout(SolutionActivity.this);
                inputSolutionLayout.setOrientation(LinearLayout.VERTICAL);

                final EditText inputSolutionTitle = new EditText(SolutionActivity.this);
                inputSolutionTitle.setHint(R.string.input_solution_title);
                final EditText inputSolutionContent = new EditText(SolutionActivity.this);
                inputSolutionContent.setHint(R.string.input_solution_content);

                inputSolutionLayout.addView(inputSolutionTitle);
                inputSolutionLayout.addView(inputSolutionContent);

                addQuestionDialogBuilder.setView(inputSolutionLayout);

                addQuestionDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        addingSolutionDialog.show();
                        findViewById(R.id.solution_root_view).post(new Runnable() {

                            @Override
                            public void run() {
                                try {

                                    String solutionTitle = inputSolutionTitle.getText().toString();
                                    String solutionContent = inputSolutionContent.getText().toString();

                                    Solution solution = new Solution();
                                    solution.setQuestionId(questionId);
                                    solution.setSolutionContent(solutionContent);
                                    Date timestamp = new Date(System.currentTimeMillis());
                                    solution.setTimestamp(timestamp);
                                    solution.setSolutionTitle(solutionTitle + " " + timestamp.toString());
                                    solution.setStudentName(name);
                                    jdbcUtilities.insertSolution(solution);
                                    jdbcUtilities.increaseUnlock(uid, 1);
                                    unlocks = unlocks + 1;
                                    String unlockText = unlockTextView.getText().toString();
                                    unlockTextView.setText(unlockText.substring(0, unlockText.length() - 2) + Integer.toString(unlocks) + ".");

                                    prepareScrollView();
                                    addingSolutionDialog.dismiss();
                                    Toast.makeText(SolutionActivity.this, "You have inserted the solution successfully and got 1 unlock as reward", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    addingSolutionDialog.dismiss();
                                    Toast.makeText(SolutionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_LONG).show();

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


        backToQuestionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SolutionActivity.this.finish();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(SolutionActivity.this);
                logoutDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences sp = getSharedPreferences("config", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent myIntent = new Intent(SolutionActivity.this, LoginActivity.class);
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
        solutionScrollviewLayout.removeAllViews();
        ArrayList<String> solutionTitles = new ArrayList<String>(jdbcUtilities.getSolutionTitlesByQuestionId(questionId));

        if (solutionTitles.size() == 0) {
            TextView solutionHeader = findViewById(R.id.solution_text_view);
            solutionHeader.setText("Currently no solutions listed. You can create one by clicking the green button.");
            solutionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {
            TextView solutionHeader = findViewById(R.id.solution_text_view);
            solutionHeader.setText(getString(R.string.solutions_header));
            solutionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            for (int i = 0; i < solutionTitles.size(); i++) {
                LinearLayout solutionLayout = new LinearLayout(this);
                solutionLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsLayout.setMargins(5, 20, 5, 20);

                LinearLayout.LayoutParams paramsElement = new LinearLayout.LayoutParams(650, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsElement.setMargins(5, 10, 20, 10);


                solutionLayout.setLayoutParams(paramsLayout);

                final TextView solutionTitleTextView = new TextView(this);
                solutionTitleTextView.setText(solutionTitles.get(i));
                solutionTitleTextView.setTextColor(getColor(R.color.colorTextInput));
                solutionTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                solutionTitleTextView.setLayoutParams(paramsElement);

                Button solutionUnlockButton = new Button(this);
                solutionUnlockButton.setText(R.string.unlock_solution);
                solutionUnlockButton.setBackgroundColor(getColor(R.color.colorAccent));
                solutionUnlockButton.setTextColor(getColor(R.color.inspectButtonText));

                solutionUnlockButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder inspectSolutionDialogBuilder = new AlertDialog.Builder(SolutionActivity.this);
                        inspectSolutionDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                inspectSolutionDialog.show();
                                findViewById(R.id.solution_root_view).post(new Runnable() {

                                    @Override
                                    public void run() {

                                        try {

                                            jdbcUtilities.decreaseUnlock(uid, 1);
                                            unlocks = unlocks - 1;
                                            String unlockText = unlockTextView.getText().toString();
                                            unlockTextView.setText(unlockText.substring(0, unlockText.length() - 2) + Integer.toString(unlocks) + ".");

                                            Intent myIntent = new Intent(SolutionActivity.this, DiscussionActivity.class);
                                            myIntent.putExtra("courseCode", courseCode);
                                            myIntent.putExtra("courseTitle", courseTitle);
                                            myIntent.putExtra("academicYear", academicYear);
                                            myIntent.putExtra("semester", semester);
                                            myIntent.putExtra("questionTitle", questionTitle);

                                            String solutionTitle = solutionTitleTextView.getText().toString();
                                            myIntent.putExtra("solutionTitle", solutionTitle);
                                            String solutionContent = jdbcUtilities.getSolutionContentBySolutionTitle(solutionTitle);
                                            myIntent.putExtra("solutionContent", solutionContent);
                                            String studentName = jdbcUtilities.getStudentNameBySolutionTitle(solutionTitle);
                                            myIntent.putExtra("studentName", studentName);

                                            jdbcUtilities.closeConnection();
                                            startActivity(myIntent);

                                            inspectSolutionDialog.dismiss();


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            inspectSolutionDialog.dismiss();
                                            Toast.makeText(SolutionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                        inspectSolutionDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        inspectSolutionDialogBuilder.setMessage("Unlocking the solution will cost 1 quota, proceed or not?");
                        inspectSolutionDialogBuilder.show();
                    }
                });

                solutionLayout.addView(solutionTitleTextView);
                solutionLayout.addView(solutionUnlockButton);

                solutionScrollviewLayout.addView(solutionLayout);
            }
        }
    }

}
