package com.example.yzhuo.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    final static String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_login);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Logging....");

        //check if already login
        if(ParseUser.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, AlbumActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        //start the signup activity page
        ((Button)findViewById(R.id.button_login_signUp))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                        startActivity(intent);
                    }
                });

        //start the login procedure
        ((Button)findViewById(R.id.button_login_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((EditText) findViewById(R.id.editText_login_email)).getText().toString();
                String pw = ((EditText) findViewById(R.id.editText_login_pw)).getText().toString();

                if (id.equals("") || pw.equals("")) {
                    Toast.makeText(LoginActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!id.matches(EMAIL_REGEX)) {
                    Toast.makeText(LoginActivity.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                } else {
                    ParseUser.logInInBackground(id, pw, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AlbumActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Email or Password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        //twitter login
        (findViewById(R.id.imageButton_login_twitter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d("twitter login", e.toString());
                        } else if (user.isNew()) {
                            progressDialog.dismiss();
                            InfoPush privacy = new InfoPush();
                            privacy.startPrivacy(user);
                            InfoPush push = new InfoPush();
                            push.notiAll("New User SignUp");
                            Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AlbumActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });



        //facebook login
        final List<String> permissions = Arrays.asList("public_profile","email");
        (findViewById(R.id.imageButton_login_facebook)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            Log.d("facebook login", "login failed");
                        } else if (user.isNew()) {
                            progressDialog.dismiss();
                            InfoPush privacy = new InfoPush();
                            privacy.startPrivacy(user);
                            InfoPush push = new InfoPush();
                            push.notiAll("New User SignUp");
                            Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AlbumActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

}
