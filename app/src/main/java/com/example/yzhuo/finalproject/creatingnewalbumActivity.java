package com.example.yzhuo.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class creatingnewalbumActivity extends AppCompatActivity {
    AlertDialog.Builder alert;
    Bitmap bt;
    int privacy;
    Spinner spinner;
    Boolean inGrid = false;
    byte[] data;
    byte[] mcoverphoto;
    Toolbar toolbar;
    ParseObject newalbum;
    String Albumid;
    Intent intent;
    List<ParseObject> albumList;
    LinearLayoutManager llm;
    GridLayoutManager glm;
    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    AlbumGridAdapter albumGridAdapter;
    ProgressDialog progressDialog;
    ProgressDialog dialog;
    ImageView image;
    EditText edittext;
    Button button;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatingnewalbum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = new Intent(creatingnewalbumActivity.this,Createalbum_Activity.class);
        edittext = (EditText) findViewById(R.id.editText_album_title);
        image = (ImageView) findViewById(R.id.imageViewcover);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(creatingnewalbumActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(creatingnewalbumActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(creatingnewalbumActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    ActivityCompat.requestPermissions(creatingnewalbumActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 100);
                }
            }
        });
        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

        final RadioButton pri = (RadioButton) findViewById(R.id.radioButtonprivate);
        final RadioButton pub = (RadioButton) findViewById(R.id.radioButtonpublic);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButtonprivate){
                    privacy =1;
                    pri.setChecked(true);
                    pub.setChecked(false);
                } else {
                    privacy = 0;
                    pri.setChecked(false);
                    pub.setChecked(true);
                }

                Log.d("set public", privacy+"");
            }
        });

        button1 = (Button) findViewById(R.id.buttoncancelalbum);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button = (Button) findViewById(R.id.buttonaddalbum);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(creatingnewalbumActivity.this);
                progressDialog.setMessage("Adding new album...");
                progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (edittext.getText().toString().equals(null)) {
                    Toast.makeText(creatingnewalbumActivity.this, "Please enter a title for the album", Toast.LENGTH_SHORT).show();
                } else {
                    newalbum = new ParseObject("Album");
                    newalbum.put("title", edittext.getText().toString());
                    newalbum.put("owner", ParseUser.getCurrentUser());
                    newalbum.put("ownerName", ParseUser.getCurrentUser().get("name"));
                    ParseACL acl = new ParseACL();
                    if (privacy == 0) {
                        acl.setPublicReadAccess(true);
                        acl.setReadAccess(ParseUser.getCurrentUser(), true);
                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                        newalbum.put("public", true);

                    } else if (privacy == 1) {
                        acl.setPublicReadAccess(false);
                        newalbum.put("public", false);
                        acl.setReadAccess(ParseUser.getCurrentUser(), true);
                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);

                    }
                    newalbum.setACL(acl);
                    final ParseFile image1 = new ParseFile(mcoverphoto);
                    image1.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                newalbum.put("cover", image1);
                                newalbum.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            progressDialog.dismiss();
                                            Albumid = newalbum.getObjectId();
                                            Log.d("albumid", Albumid);
                                            Intent intent = new Intent(creatingnewalbumActivity.this, Createalbum_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("albumid", Albumid);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d("album save", e.toString());
                                            progressDialog.dismiss();
                                            Toast.makeText(creatingnewalbumActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Log.d("album cover", e.toString());
                                progressDialog.dismiss();
                                Toast.makeText(creatingnewalbumActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                            }
                        }


                    });


                }


            }
        });


        //alert.setMessage("Select the cover photo of the Album");


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode==100 && resultCode == Activity.RESULT_OK && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap bmp = BitmapFactory.decodeFile(imgDecodableString);
                Log.d("bitmpap", String.valueOf(bmp.getDensity()));
                bt = bmp;
                Log.d("bitmpap", String.valueOf(bt.getDensity()));
                Picasso.with(this)
                        .load(selectedImage)
                        .error(R.drawable.ic_action_default_icon)
                        .into(image);
                //image.setImageBitmap(bt);
                new ConvertImagetoFile().execute(bmp);

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public class ConvertImagetoFile extends AsyncTask<Bitmap, Void, Void>
    {
        @Override
        protected Void doInBackground(Bitmap... params) {
            int width = params[0].getWidth();
            int height = params[0].getHeight();
            float bitmapratio = (float)width/(float)height;
            if(bitmapratio > 0)
            {
                width = 500;
                height = (int) (width/bitmapratio);
            }
            else
            {
                height = 500;
                width = (int) (height/bitmapratio);
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(params[0],width,height,true);
            // image.setImageBitmap(bitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            mcoverphoto = stream.toByteArray();
            Log.d("mcoverphoto", mcoverphoto.toString());
            bt=bitmap;
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(creatingnewalbumActivity.this);
            dialog.setMessage("Loading image");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
//            image.setImageBitmap(bt);



        }
    }

}
