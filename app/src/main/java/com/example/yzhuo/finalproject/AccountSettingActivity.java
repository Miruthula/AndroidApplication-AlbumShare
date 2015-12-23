package com.example.yzhuo.finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class AccountSettingActivity extends AppCompatActivity {

    String name, gender, imageUrl;
    Boolean notifi, list;
    ParseUser thisUser;
    ParseUser thisPrivacy;
    private Bitmap selectedImage;
    ProgressDialog progressDialog;

    final int RESULT_LOAD_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(AccountSettingActivity.this);
        progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading....");
        progressDialog.show();



        //get data from parse.com and set the screen
        ParseQuery<ParseUser> privacy = ParseQuery.getQuery("_User");
        privacy.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        privacy.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    thisPrivacy = objects.get(0);
                    thisUser = objects.get(0);
                    name = thisUser.get("name").toString();
                    gender = thisUser.get("gender").toString();
                    ParseFile thisPhoto = (ParseFile) thisUser.get("photo");
                    imageUrl = thisPhoto.getUrl();
                    notifi = objects.get(0).getBoolean("notification");
                    list = objects.get(0).getBoolean("listing");

                    Log.d("privacy", name + " " + gender + " " + notifi.toString() + " " + list.toString());

                    ((EditText) findViewById(R.id.editText_acccountSetting_name)).setText(name);
                    if (gender.equalsIgnoreCase("male")) {
                        ((Switch) findViewById(R.id.switch_accountSetting_gender)).setChecked(false);
                    } else {
                        ((Switch) findViewById(R.id.switch_accountSetting_gender)).setChecked(true);
                    }

                    ((Switch) findViewById(R.id.switch_accountSetting_listing)).setChecked(list);
                    ((Switch) findViewById(R.id.switch_accountSetting_notification)).setChecked(notifi);

                    Picasso.with(AccountSettingActivity.this)
                            .load(imageUrl)
                            .error(R.drawable.ic_action_default_icon)
                            .into(((ImageView) findViewById(R.id.imageView_accountSetting_icon)));

                } else {
                    Log.d("privacy", e.toString());
                }
                progressDialog.dismiss();
            }
        });

        //switch listener
                ((Switch) findViewById(R.id.switch_accountSetting_gender)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

        ((Switch)findViewById(R.id.switch_accountSetting_notification)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifi = true;
                    Log.d("Notif", notifi.toString());
                } else {
                    notifi = false;
                    Log.d("Notif", notifi.toString());
                }
            }
        });

        ((Switch)findViewById(R.id.switch_accountSetting_listing)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    list = true;
                    Log.d("List", notifi.toString());
                } else {
                    list = false;
                    Log.d("List", notifi.toString());
                }
            }
        });

        //end switch listener

        //get image from photo
        findViewById(R.id.imageView_accountSetting_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        //end get image


        //uploading the data to parse.com
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Saving...");
                progressDialog.show();
                name = ((EditText) findViewById(R.id.editText_acccountSetting_name)).getText().toString();
                //save image
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageView imageTemp = (ImageView) findViewById(R.id.imageView_accountSetting_icon);
                selectedImage = ((BitmapDrawable)(imageTemp.getDrawable())).getBitmap();
                selectedImage.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] data = stream.toByteArray();
                final ParseFile fileImage = new ParseFile(data);
                //end
                fileImage.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        //Toast.makeText(AccountSettingActivity.this, "Saving...", Toast.LENGTH_SHORT).show();
                        if (e == null) {
                            thisUser.put("name", name);
                            thisUser.put("photo", fileImage);
                            thisUser.put("gender", gender);
                            thisUser.put("listing", list);
                            thisUser.put("notification", notifi);

                            thisUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AccountSettingActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AccountSettingActivity.this, AlbumActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {

                                        Log.d("user save", e.toString());
                                    }
                                }
                            });


                        } else {

                            Log.d("image upload", e.toString());
                        }
                    }
                });

            }
        });
        //end uploading
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //imagePath = picturePath;


            Picasso.with(AccountSettingActivity.this)
                    .load(imageUri)
                    .error(R.drawable.ic_action_default_icon)
                    .resize(1000, 1000)
                    .into(((ImageView) findViewById(R.id.imageView_accountSetting_icon)), new Callback() {
                        @Override
                        public void onSuccess() {

                            ImageView imageTemp = (ImageView) findViewById(R.id.imageView_accountSetting_icon);
                            selectedImage = ((BitmapDrawable)(imageTemp.getDrawable())).getBitmap();
                        }

                        @Override
                        public void onError() {

                        }
                    });

        }
    }
}
