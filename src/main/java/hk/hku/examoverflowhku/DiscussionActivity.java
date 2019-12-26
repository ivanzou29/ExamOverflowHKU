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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.Model.Discussion;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class DiscussionActivity extends AppCompatActivity {
    TextView courseInfoView;
    TextView yearSemQNumView;
    TextView solutionTitleView;
    TextView authorTimestampView;
    TextView solutionContentView;

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
        authorTimestampView.setText(authorName + ", " + solutionTitle.substring(solutionTitle.length() - 10));

        solutionContentView = findViewById(R.id.solution_content_view);
        solutionContentView.setText(solutionContent);

        addDiscussionButton = findViewById(R.id.add_discussion_button);
        backToSolutionsButton = findViewById(R.id.back_to_solution_button);
        logoutButton = findViewById(R.id.log_out_button);

        solutionContentScrollview = findViewById(R.id.solution_content_scroll_view);
        discussionScrollViewLayout = findViewById(R.id.discussion_scrollview_layout);

        jdbcUtilities = new JDBCUtilities();
        jdbcUtilities.openConnection();

        View view = View.inflate(this, R.layout.activity_discussion, null);
        addingDiscussionDialog = new ProcessingDialog(view, R.string.adding_discussion);
        preparingDialog = new ProcessingDialog(view, R.string.preparing_discussion_layout);

        preparingDialog.show();
        findViewById(R.id.discussion_root_view).post(new Runnable() {

            @Override
            public void run() {
                prepareScrollView();
                preparingDialog.dismiss();
            }
        });

        addDiscussionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final AlertDialog.Builder addDiscussionDialogBuilder = new AlertDialog.Builder(DiscussionActivity.this);
                addDiscussionDialogBuilder.setTitle("Add a discussion");
                final EditText inputDiscussion = new EditText(DiscussionActivity.this);
                inputDiscussion.setHint(R.string.input_discussion_content);
                addDiscussionDialogBuilder.setView(inputDiscussion);


                addDiscussionDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        addingDiscussionDialog.show();
                        findViewById(R.id.discussion_root_view).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    String discussionContent = inputDiscussion.getText().toString();
                                    Discussion discussion = new Discussion();
                                    discussion.setSolutionTitle(solutionTitle);
                                    discussion.setDiscussionContent(discussionContent);
                                    discussion.setDiscussionId(UUID.randomUUID().toString());

                                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                    discussion.setTimestamp(timestamp);
                                    discussion.setStudentName(name);

                                    jdbcUtilities.insertDiscussion(discussion);

                                    prepareScrollView();
                                    addingDiscussionDialog.dismiss();
                                    Toast.makeText(DiscussionActivity.this, "You have added one discussion", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    addingDiscussionDialog.dismiss();
                                    Toast.makeText(DiscussionActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_LONG).show();

                                }
                            }

                        });
                    }
                });

                addDiscussionDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                addDiscussionDialogBuilder.show();

            }
        });


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
                jdbcUtilities.closeConnection();
                DiscussionActivity.this.finish();
            }
        });


    }


    public void prepareScrollView() {
        discussionScrollViewLayout.removeAllViews();
        ArrayList<Discussion> discussions = new ArrayList<>(jdbcUtilities.getDiscussionsBySolutionTitle(solutionTitle));
        if (discussions.size() == 0) {
            TextView discussionHeader = findViewById(R.id.discussion_title_view);
            discussionHeader.setText("Currently no discussions on this solution. You can create one by clicking the green button.");
            discussionHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {

            for (int i = 0; i < discussions.size(); i++) {
                Discussion discussion = discussions.get(i);
                LinearLayout discussionLayout = new LinearLayout(this);
                discussionLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsLayout.setMargins(5, 10, 5, 10);
                discussionLayout.setLayoutParams(paramsLayout);

                TextView authorAndTimestampView = new TextView(this);
                authorAndTimestampView.setText(discussion.getStudentName() + ", " + discussion.getTimestamp() + ": ");
                authorAndTimestampView.setTextColor(getColor(R.color.colorTextInput));
                authorAndTimestampView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);

                TextView discussionContentView = new TextView(this);
                discussionContentView.setText(discussion.getDiscussionContent());
                discussionContentView.setTextColor(getColor(R.color.colorTextInput));
                discussionContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

                discussionLayout.addView(authorAndTimestampView);
                discussionLayout.addView(discussionContentView);

                discussionScrollViewLayout.addView(discussionLayout);
            }
        }
    }
}
