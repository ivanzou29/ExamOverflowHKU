package hk.hku.examoverflowhku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.Model.Student;
import hk.hku.examoverflowhku.Token.SendEmailTask;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class SignupActivity extends AppCompatActivity {
    EditText emailText;
    EditText uidText;
    EditText nameText;
    EditText firstPasswordText;
    EditText secondPasswordText;
    EditText tokenText;

    JDBCUtilities jdbcUtilities;

    Button getToken;
    Button signupButton;

    ProcessingDialog signingUpDialog;

    String tokenSent;

    static final int INITIAL_UNLOCKS = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailText = findViewById(R.id.email_address);
        uidText = findViewById(R.id.uid);
        nameText = findViewById(R.id.name);
        firstPasswordText = findViewById(R.id.first_password);
        secondPasswordText = findViewById(R.id.second_password);
        tokenText = findViewById(R.id.token);

        getToken = findViewById(R.id.get_token_button);
        signupButton = findViewById(R.id.sign_up_button);

        View view = View.inflate(this, R.layout.activity_signup, null);
        signingUpDialog = new ProcessingDialog(view, R.string.signing_up);

        getToken.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                try {
                    String email = emailText.getText().toString();
                    if (email.substring(email.length() - 6).equals("hku.hk")) {
                        Random random = new Random();
                        tokenSent = Integer.toString(1000 + random.nextInt(9000));
                        new SendEmailTask(email, tokenSent).execute();
                        Toast.makeText(SignupActivity.this, "Token has been sent!", Toast.LENGTH_LONG).show();
                    } else {
                        emailText.getText().clear();
                        Toast.makeText(SignupActivity.this, "Please only use an HKU email to register!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                    alertDialog.setTitle("Sending email token failed!");
                    alertDialog.setMessage("Check your network connection or contact \"examoverflow@126.com\".");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                signingUpDialog.show();

                findViewById(R.id.signup_root_view).post(new Runnable() {
                    String email = emailText.getText().toString();
                    String uid = uidText.getText().toString();
                    String name = nameText.getText().toString();
                    String firstPassword = firstPasswordText.getText().toString();
                    String secondPassword = secondPasswordText.getText().toString();
                    String token = tokenText.getText().toString();

                    @Override
                    public void run() {
                        if (token.equals(tokenSent) && firstPassword.equals(secondPassword)) {
                            Student student = new Student();
                            student.setEmail(email);
                            student.setUid(uid);
                            student.setName(name);
                            student.setPassword(firstPassword);
                            try {
                                jdbcUtilities = new JDBCUtilities();
                                jdbcUtilities.openConnection();
                                jdbcUtilities.insertStudent(student);
                                jdbcUtilities.insertUnlocks(uid, INITIAL_UNLOCKS);
                                jdbcUtilities.closeConnection();
                                SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putString("uid", uid);
                                editor.putString("password", firstPassword);
                                editor.putString("name", name);
                                editor.apply();
                                Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(v.getContext(), SearchActivity.class);
                                myIntent.putExtra("unlocks", INITIAL_UNLOCKS);
                                startActivity(myIntent);
                            } catch (SQLException e) {
                                AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                                alertDialog.setTitle("Duplication registration!");
                                alertDialog.setMessage("It seems the UID or the email have been used for registration before.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            } catch (NullPointerException e) {
                                AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                                alertDialog.setTitle("Server failure!");
                                alertDialog.setMessage("Check your network connection or contact \"examoverflow@126.com\".");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                            }
                        } else {
                            AlertDialog alertDialog = new AlertDialog.Builder(SignupActivity.this).create();
                            alertDialog.setTitle("Sign up failed!");
                            alertDialog.setMessage("The two lines of password entered by you do not match, or your have entered the incorrect token.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            firstPasswordText.getText().clear();
                            secondPasswordText.getText().clear();
                            tokenText.getText().clear();
                        }

                    }

                });
            }
        });
    }
}
