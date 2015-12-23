package com.example.yzhuo.finalproject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

public class SignupActivity extends AppCompatActivity {

    String gender="Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle(R.string.title_activity_signup);
        setSupportActionBar(toolbar);

        ((Switch)findViewById(R.id.switch_signUp_gender)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "Female";
                    Log.d("Gender", gender);
                } else {
                    gender = "Male";
                    Log.d("Gender", gender);
                }
            }
        });


        (findViewById(R.id.button_signUp_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        (findViewById(R.id.button_signUp_signUp)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText)findViewById(R.id.editText_signUp_name)).getText().toString();
                String email = ((EditText)findViewById(R.id.editText_signUp_email)).getText().toString();
                String pw = ((EditText)findViewById(R.id.editText_signUp_pw)).getText().toString();
                String confPW = ((EditText)findViewById(R.id.editText_signUp_confPW)).getText().toString();
                gender = "Male";


                if(name.equals("") || email.equals("") || pw.equals("") || confPW.equals("")){
                    Toast.makeText(SignupActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                } else if(!email.matches(LoginActivity.EMAIL_REGEX)){
                    Toast.makeText(SignupActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                } else if (!pw.equals(confPW)){
                    Toast.makeText(SignupActivity.this, "Password and Confirm Password did not Match", Toast.LENGTH_SHORT).show();
                } else {
                    final ParseUser user = new ParseUser();
                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(pw);
                    user.put("name",name);
                    user.put("gender", gender);

                    //set ACL
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    user.setACL(acl);
                    //end set ACL
                    //save image
                    Resources res = getResources();
                    Drawable drawable = res.getDrawable(R.drawable.ic_action_default_icon);
                    Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
                    byte[] data = stream.toByteArray();
                    final ParseFile file = new ParseFile(data);
                    //end

                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                user.put("photo", file);
                                user.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            InfoPush privacy = new InfoPush();
                                            privacy.startPrivacy(user);
                                            //test
                                            Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                            InfoPush push = new InfoPush();
                                            push.notiAll("New User SignUp");
                                            finish();
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Email already been used", Toast.LENGTH_SHORT).show();
                                            Log.d("signUp", e.toString());
                                        }
                                    }
                                });
                            } else {
                                Log.d("file upload", e.toString());
                            }
                        }
                    });




                }
            }
        });

    }

}
