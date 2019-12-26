package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class DiscussionActivity extends AppCompatActivity {
    TextView courseInfoView;
    TextView yearSemQNumView;
    TextView solutionTitleView;
    TextView authorTimestampView;

    ScrollView solutionContentScrollview;
    LinearLayout discussionScrollViewLayout;

    Button addDiscussionButton;
    Button backToSolutionsButton;
    Button logoutButton;


    String name;
    String courseCode;
    String courseTitle;
    String academicYear;
    String semester;
    String questionTitle;
    String uid;
    String solutionTitle;
    String solutionContent;
    String authorName;

    ProcessingDialog preparingDialog;
    ProcessingDialog addingDiscussionDialog;

    JDBCUtilities jdbcUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);


        Bundle extras = getIntent().getExtras();
        courseCode = extras.getString("courseCode");
        courseTitle = extras.getString("courseTitle");
        academicYear = extras.getString("academicYear");
        semester = extras.getString("semester");
        questionTitle = extras.getString("questionTitle");
        solutionTitle = extras.getString("solutionTitle");
        solutionContent = extras.getString("solutionContent");
        authorName = extras.getString("studentName");

        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        name = sharedPreferences.getString("name", "");
        uid = sharedPreferences.getString("uid", "");


        courseInfoView = findViewById(R.id.course_title_view);
        courseInfoView.setText(courseCode + ": " + courseTitle);

        yearSemQNumView = findViewById(R.id.academic_year_and_semester_and_question_view);
        yearSemQNumView.setText(academicYear + ", semester" + semester + ": " + questionTitle);

        solutionTitleView = findViewById(R.id.solution_title_view);
        solutionTitleView.setText(solutionTitle.substring(0, solutionTitle.length() - 11));

        authorTimestampView = findViewById(R.id.author_timestamp_view);
        authorTimestampView.setText(authorName + ", " + solutionTitle.substring(solutionTitle.length()-10));

        addDiscussionButton = findViewById(R.id.add_discussion_button);
        backToSolutionsButton = findViewById(R.id.back_to_solution_button);
        logoutButton = findViewById(R.id.log_out_button);

        solutionContentScrollview = findViewById(R.id.solution_content_scroll_view);
        discussionScrollViewLayout = findViewById(R.id.discussion_scrollview_layout);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(DiscussionActivity.this);
                logoutDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences sp = getSharedPreferences("config", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent myIntent = new Intent(DiscussionActivity.this, LoginActivity.class);
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

        backToSolutionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DiscussionActivity.this.finish();
            }
        });


    }

    public void prepareSolutionContent() {
        solutionContentScrollview.removeAllViews();

    }
}
