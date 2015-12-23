package com.example.yzhuo.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class settingsalbumActivity extends AppCompatActivity {
    String albumid;
    ParseObject album;
    String title;
    ParseFile editimage;
    boolean privacy;
    EditText edittitle;
    String editedtitle;
    ImageView editviewimage;
    RadioGroup editrg;
    ProgressDialog dialog;
    byte[] mcovereditphoto;
    int editprivacy;
    Button save;
    Button cancel;
    TextView viewsharedusers;
    TextView removeuser;
    ImageView share;
    String sharedusersarray="";
    String[] viewusers;
    ArrayList<String> names1;
    String[] names;
    ParseACL acl;

    List<ParseObject> sharedList;
    List<ParseObject> removeList;
    List<ParseObject> availableList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingsalbum);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        albumid = getIntent().getExtras().getString("albumid");
        acl = new ParseACL();
        names1= new ArrayList<String>();

        sharedList = new ArrayList<>();
        edittitle = (EditText) findViewById(R.id.editText_settings_album_title);
        editviewimage = (ImageView) findViewById(R.id.imageVieweditcover);
        editrg = (RadioGroup) findViewById(R.id.radioGroupedit);

        dialog = new ProgressDialog(settingsalbumActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

        editviewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(settingsalbumActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(settingsalbumActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(settingsalbumActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    ActivityCompat.requestPermissions(settingsalbumActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 100);
                }


            }
        });

        Log.d("id", albumid);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Album");
        query.getInBackground(albumid, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                dialog.dismiss();
                Log.d("object", object.getObjectId());
                album = object;
                title = album.get("title").toString();
                editimage = album.getParseFile("cover");
                privacy = album.getBoolean("public");
                if(album.get("shared") == null){
                    sharedList = new ArrayList<ParseObject>();
                }else {
                    sharedList = new ArrayList<>((ArrayList) album.get("shared"));
                }
                edittitle.setText(title);
                acl = album.getACL();
                String imageUrl = editimage.getUrl();//live url
                Uri imageUri = Uri.parse(imageUrl);
                Picasso.with(settingsalbumActivity.this).load(imageUri.toString()).into(editviewimage);
                if (privacy == true) {
                    editprivacy = 0;
                    editrg.check(R.id.radioButtoneditpublic);
                } else {
                    editprivacy = 1;
                    editrg.check(R.id.radioButtoneditprivate);
                }
                //dialog.dismiss();

            }
        });
        final List<ParseObject> users = new ArrayList<>();
        ParseQuery<ParseObject> userList = ParseQuery.getQuery("_User");
        userList.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        userList.whereEqualTo("listing", true);
        userList.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    availableList = new ArrayList<ParseObject>(objects);
                    //Log.d("name", availableList.get(0).get("name").toString());
                    names = new String[objects.size()];
                    for (int x = 0; x < objects.size(); x++) {

                        ParseObject u = objects.get(x);
                        users.add(u);
                        Log.d("get name", u.get("name").toString());
                        names[x] = (u.get("name").toString());
                    }
                }
            }
        });

        share = (ImageView) findViewById(R.id.imageViewshareusers);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsalbumActivity.this);
                builder.setTitle("Users");
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Intent intent = new Intent(settingsalbumActivity.this, MessageActivity.class);
                        if(!sharedList.contains(users.get(item))) {
                            sharedList.add(users.get(item));
                        }
                        album.addAllUnique("shared", Arrays.asList(users.get(item)));
                        acl.setWriteAccess(users.get(item).getObjectId(), true);
                        acl.setReadAccess(users.get(item).getObjectId(), true);
                        album.setACL(acl);

                    }
                }).show();
            }
        });
        viewsharedusers = (TextView) findViewById(R.id.textViewviewsharedusers);
        viewsharedusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                names1 = new ArrayList<>();
                int size =  sharedList.size();
                if(size == 0){
                    names1.add("Empty List");
                } else {
                    for(int x = 0; x < size; x++){
                        names1.add((String) sharedList.get(x).get("name"));
                    }
                }
                viewusers = names1.toArray(new String[names1.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsalbumActivity.this);
                                builder.setTitle("Users");
                                builder.setItems(viewusers, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }

                                });
                                builder.create().show();
                            }

                        });


        removeuser = (TextView) findViewById(R.id.textViewremoveusers);
        removeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                names1 = new ArrayList<>();
                int size =  sharedList.size();
                if(size == 0){
                    names1.add("Empty List");
                } else {

                    for(int x = 0; x < size; x++){
                        names1.add((String) sharedList.get(x).get("name"));
                    }
                }
                viewusers = names1.toArray(new String[names1.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsalbumActivity.this);
                builder.setTitle("Users");
                builder.setItems(viewusers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        album.remove("shared");
                        sharedList.remove(item);
                        album.addAllUnique("shared", Arrays.asList(sharedList));
                        ParseACL acl = album.getACL();
                        //acl.setWriteAccess(availableList.get(item).getObjectId(), false);
                        acl.setReadAccess(availableList.get(item).getObjectId(), false);
                        album.setACL(acl);

                    }

                });
                builder.create().show();

            }
        });

        save = (Button) findViewById(R.id.buttonsavechanges);
        cancel = (Button) findViewById(R.id.buttoncancelalbum);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedList.size() == 0){
                    album.remove("shared");
                }
                saveeditedcover();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editrg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtoneditprivate) {
                    editprivacy = 1;
                    Log.d("privacy", editprivacy+"");
                }else {
                    editprivacy = 0;
                    Log.d("privacy", editprivacy+"");
                }
            }
        });

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
                /*Picasso.with(this)
                        .load(selectedImage)
                        .error(R.drawable.ic_action_default_icon)
                        .into(editviewimage);*/
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
            mcovereditphoto = stream.toByteArray();

            final ParseFile image = new ParseFile(mcovereditphoto);
            image.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        title = edittitle.getText().toString();
                        edittitle.setText(title);
                        album.put("cover",image);
                        String imageUrl = image.getUrl();//live url
                        Uri imageUri = Uri.parse(imageUrl);
                        Picasso.with(settingsalbumActivity.this).load(imageUri.toString()).into(editviewimage);
                    }
                }
            });

            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(settingsalbumActivity.this);
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
    public void saveeditedcover()
    {
        title = edittitle.getText().toString();
        Log.d("title", title);
        album.put("title", title);
       // ParseACL acl = new ParseACL();
        if (editprivacy == 0) {
            ParseACL newACL = new ParseACL();
            newACL.setPublicReadAccess(true);
            newACL.setPublicWriteAccess(false);
            newACL.setReadAccess(ParseUser.getCurrentUser(), true);
            newACL.setWriteAccess(ParseUser.getCurrentUser(), true);
            album.setACL(newACL);
            album.remove("shared");
            album.put("public", true);

        } else if (editprivacy == 1) {
            acl.setPublicReadAccess(false);
            album.put("public", false);
            album.setACL(acl);
        }

        album.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(settingsalbumActivity.this, "Your changes are saved", Toast.LENGTH_SHORT).show();
                    Log.d("saved", "changes Saved");


                }
            }


        });


                }
            }


