package com.example.yzhuo.finalproject;


import android.Manifest;
import android.app.Activity;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Createalbum_Activity extends AppCompatActivity {
    byte[] mphoto;
    ParseObject album;
    ParseObject photo;
    ProgressDialog dialog;
    Boolean inGrid = false;
    String albumid;
    String privacy;
    RecyclerView recyclerView;
    GridAdapter gridAdapter;
    GridLayoutManager glm;
    List<ParseObject> mPhotosList;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createalbum_);
        mPhotosList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        glm = new GridLayoutManager(Createalbum_Activity.this, 2);
        recyclerView.setLayoutManager(glm);
        albumid = getIntent().getExtras().getString("albumid");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        Log.d("createalbumid", albumid);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Album");
        query.getInBackground(albumid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Log.d("object", object.getObjectId());
                album = object;
                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Photo");
                query1.whereEqualTo("album", album);
                query1.whereEqualTo("verify", true);
//        Log.d("id", album.getObjectId());
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects != null) {
                                if (!((Boolean)album.get("public"))){
                                    fab.setVisibility(View.VISIBLE);
                                }
                                ParseObject thisOwner = (ParseObject) album.get("owner");
                                if(thisOwner.getObjectId() == ParseUser.getCurrentUser().getObjectId()){
                                    fab.setVisibility(View.VISIBLE);
                                }

                                Log.d("arraysize", String.valueOf(objects.size()));
                                mPhotosList = objects;
                            }
                            gridAdapter = new GridAdapter(Createalbum_Activity.this, mPhotosList);
                            recyclerView.setAdapter(gridAdapter);
                        } else {
                            Log.d("parsequery", e.toString());
                        }
                    }
                });
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Createalbum_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(Createalbum_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Createalbum_Activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    ActivityCompat.requestPermissions(Createalbum_Activity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 100);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        //return super.onOptionsItemSelected(item);

        int id = item.getItemId();
        if(id==R.id.deletealbum)
        {
            ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Photo");
            query2.whereEqualTo("album", album);
            query2.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects != null) {
                        Log.d("size before", String.valueOf(objects.size()));
                        ParseObject.deleteAllInBackground(objects, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {

                                album.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(Createalbum_Activity.this, "Deleting the album and photo", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Createalbum_Activity.this, AlbumActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();

                                    }
                                });
                            }
                        });


                    } else {
                        album.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(Createalbum_Activity.this, "Deleting the album", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        });
                    }
                }
            });


        }

        /*if(id == R.id.addnewphoto)
        {
            if (ActivityCompat.checkSelfPermission(Createalbum_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(Createalbum_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Createalbum_Activity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                ActivityCompat.requestPermissions(Createalbum_Activity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }

        }*/






                //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(Createalbum_Activity.this, "Log Out successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Createalbum_Activity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Createalbum_Activity.this, "Try again later", Toast.LENGTH_SHORT).show();
                        Log.d("log out", e.toString());
                    }
                }
            });
        }


        return super.onOptionsItemSelected(item);




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

                new ConvertImagetoFile().execute(bmp);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public class ConvertImagetoFile extends AsyncTask<Bitmap,Void,Void>
    {
        @Override
        protected Void doInBackground(Bitmap... params) {
            int width = params[0].getWidth();
            int height = params[0].getHeight();
            float bitmapratio = (float)width/(float)height;
            if(bitmapratio > 0)
            {
                width = 1000;
                height = (int) (width/bitmapratio);
            }
            else
            {
                height = 1000;
                width = (int) (height/bitmapratio);
            }
            Bitmap bitmap = Bitmap.createScaledBitmap(params[0],width,height,true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
            mphoto = stream.toByteArray();
            savephoto();
            Log.d("mphoto",mphoto.toString());
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Createalbum_Activity.this);
            dialog.setMessage("Loading image");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();

        }
    }
    public void savephoto()
    {
        photo = new ParseObject("Photo");
        photo.put("uploader", ParseUser.getCurrentUser());


        ParseObject thisUser = (ParseObject) album.get("owner");
        if(thisUser.getObjectId() != ParseUser.getCurrentUser().getObjectId()){
            photo.put("verify", false);
        } else {
            photo.put("verify", true);
        }
        final ParseFile image = new ParseFile(mphoto);
        image.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    photo.put("album", album);
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(true);
                    photo.setACL(acl);
                    photo.put("image", image);
                    photo.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mPhotosList.add(photo);
                                gridAdapter.notifyDataSetChanged();
                                InfoPush noti = new InfoPush();

                                if(!(((ParseObject) album.get("owner")).getObjectId()==ParseUser.getCurrentUser().getObjectId()))
                                {
                                    noti.notification(((ParseObject) album.get("owner")).getObjectId(), "New Photo Pending");
                                }
                            }
                        }


                    });
                }
            }


        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Createalbum_Activity.this, AlbumActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
