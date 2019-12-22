package hk.hku.examoverflowhku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    TextView greetingTextView;
    TextView unlockTextView;
    Button logoutButton;
    TextView courseCodeTextView;
    EditText courseCodeText;
    TextView chooseAcademicYearTextView;
    Spinner chooseAcademicYear;
    TextView chooseSemesterTextView;
    Spinner chooseSemester;
    ImageButton searchButton;

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




    }
}
