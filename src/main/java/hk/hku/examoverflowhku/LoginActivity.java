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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import hk.hku.examoverflowhku.Database.JDBCUtilities;
import hk.hku.examoverflowhku.UI.ProcessingDialog;

public class LoginActivity extends AppCompatActivity {

    EditText uidText;
    EditText passwordText;
    Button loginButton;
    Button signupButton;
    ProcessingDialog loggingInDialog;

    JDBCUtilities jdbcUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean loggingOut = false;
        try {
            Bundle extras = getIntent().getExtras();
            loggingOut = extras.getBoolean("loggingOut");
        } catch (Exception e) {
        }

        uidText = findViewById(R.id.get_uid);
        passwordText = findViewById(R.id.get_password);

        View view = View.inflate(this, R.layout.activity_login, null);
        loggingInDialog = new ProcessingDialog(view, R.string.logging_in);


        loginButton = findViewById(R.id.log_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                loggingInDialog.show();

                findViewById(R.id.login_root_view).post(new Runnable() {
                    String uid = uidText.getText().toString();
                    String password = passwordText.getText().toString();

                    @Override
                    public void run() {
                        try {
                            jdbcUtilities = new JDBCUtilities();
                            jdbcUtilities.openConnection();
                            String realPassword = jdbcUtilities.getPasswordByUid(uid);
                            String name = jdbcUtilities.getNameByUid(uid);
                            jdbcUtilities.closeConnection();
                            if (realPassword.equals(password)) {
                                SharedPreferences sharedPreferences = getSharedPreferences("config", 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.putString("uid", uid);
                                editor.putString("password", password);
                                editor.putString("name", name);
                                editor.apply();
                                loggingInDialog.dismiss();
                                Toast.makeText(LoginActivity.this, name + " logged in successfully!", Toast.LENGTH_LONG).show();
                                // TODO: add new activity after logging in
                                // Intent myIntent = new Intent(v.getContext(), MainPage.class);
                                // myIntent.putExtra("email", eStr);
                                // startActivity(myIntent);
                            } else {
                                loggingInDialog.dismiss();
                                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                alertDialog.setTitle("Authentication failed!");
                                alertDialog.setMessage("The UID and password entered by you do not match. Please sign up if you do not have an account yet.");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                passwordText.getText().clear();
                                alertDialog.show();
                            }
                        } catch (Exception e) {
                            loggingInDialog.dismiss();
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("Server connection failed!");
                            alertDialog.setMessage("Check your network connection or notify \"examoverflow@126.com\" the issue if your network connection has no problem.");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Acknowledged",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }
                });
            }

        });

        signupButton = findViewById(R.id.sign_up_button);
        signupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), SignupActivity.class);
                startActivity(myIntent);
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
