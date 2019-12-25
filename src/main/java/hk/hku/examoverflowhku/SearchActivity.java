package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class SearchActivity extends AppCompatActivity {

    TextView greetingTextView;
    TextView unlockTextView;
    Button logoutButton;
    TextView courseCodeTextView;
    EditText courseCodeText;
    TextView chooseAcademicYearTextView;
    Spinner chooseAcademicYearSpinner;
    TextView chooseSemesterTextView;
    Spinner chooseSemesterSpinner;
    ImageButton searchButton;

    ProcessingDialog searchingDialog;

    JDBCUtilities jdbcUtilities;

    String name;
    int unlocks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Bundle extras = getIntent().getExtras();
        unlocks = extras.getInt("unlocks");
        SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
        name = sharedPreferences.getString("name", "");

        greetingTextView = findViewById(R.id.greeting_text_view);
        unlockTextView = findViewById(R.id.unlock_remaining);

        greetingTextView.setText(greetingTextView.getText().toString() + name + "!");
        unlockTextView.setText(unlockTextView.getText().toString() + Integer.toString(unlocks) + ".");

        logoutButton = findViewById(R.id.log_out_button);
        courseCodeTextView = findViewById(R.id.enter_course_code);
        courseCodeText = findViewById(R.id.course_code);
        chooseAcademicYearTextView = findViewById(R.id.choose_academic_year);
        chooseAcademicYearSpinner = findViewById(R.id.academic_year_spinner);
        chooseSemesterTextView = findViewById(R.id.choose_semester);
        chooseSemesterSpinner = findViewById(R.id.semester_spinner);
        searchButton = findViewById(R.id.search_button);

        View view = View.inflate(this, R.layout.activity_search, null);
        searchingDialog = new ProcessingDialog(view, R.string.searching);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchingDialog.show();

                findViewById(R.id.search_root_view).post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            String courseCode = courseCodeText.getText().toString();
                            String academicYear = chooseAcademicYearSpinner.getSelectedItem().toString();
                            String semester = chooseSemesterSpinner.getSelectedItem().toString();

                            jdbcUtilities = new JDBCUtilities();
                            jdbcUtilities.openConnection();
                            String courseTitle = jdbcUtilities.getCourseTitleByCourseCode(courseCode);
                            jdbcUtilities.closeConnection();

                            if (courseTitle != null) {
                                Intent myIntent = new Intent(SearchActivity.this, QuestionActivity.class);
                                myIntent.putExtra("courseCode", courseCode);
                                myIntent.putExtra("courseTitle", courseTitle);
                                myIntent.putExtra("unlocks", unlocks);
                                myIntent.putExtra("academicYear", academicYear);
                                myIntent.putExtra("semester", semester);
                                startActivity(myIntent);
                                searchingDialog.dismiss();
                            } else {
                                searchingDialog.dismiss();
                                Toast.makeText(SearchActivity.this, "Course does not exist or server connection failed.", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            searchingDialog.dismiss();
                            Toast.makeText(SearchActivity.this, "Unknown failure. Please connect examoverflow@126.com for help or try again later.", Toast.LENGTH_LONG).show();

                        }
                    }
                });


            }
        });


        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder logoutDialogBuilder = new AlertDialog.Builder(SearchActivity.this);
                logoutDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SharedPreferences sp = getSharedPreferences("config", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent myIntent = new Intent(SearchActivity.this, LoginActivity.class);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
