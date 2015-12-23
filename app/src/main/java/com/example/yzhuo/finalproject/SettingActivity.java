package com.example.yzhuo.finalproject;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.io.ByteArrayOutputStream;

public class SettingActivity extends AppCompatActivity {

    String gender, name, pw, confPW;
    ParseUser thisUser;
    ParseFile userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        gender = "Male";
        thisUser = ParseUser.getCurrentUser();

        ((Switch)findViewById(R.id.switch_setting_gender)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        (findViewById(R.id.imageView_setting_icon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO get the image data to save
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this, "Saving", Toast.LENGTH_SHORT).show();
                //TODO saving the data from the UI
                name = ((EditText)findViewById(R.id.editText_setting_name)).getText().toString();
                thisUser.put("gender", gender);
                thisUser.put("name",name);
                Log.d("name", name);

                ParseACL acl = new ParseACL();
                acl.setPublicReadAccess(true);
                thisUser.setACL(acl);


                //get image
                Resources res = getResources();
                Drawable drawable = res.getDrawable(R.drawable.ic_action_default_icon);
                Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] data = stream.toByteArray();
                userIcon = new ParseFile(data);
                //end

                userIcon.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            thisUser.put("photo", userIcon);
                            thisUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {

                                    Intent intent = new Intent(SettingActivity.this, AlbumActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
