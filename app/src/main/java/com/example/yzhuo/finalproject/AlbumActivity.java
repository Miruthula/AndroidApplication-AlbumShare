package com.example.yzhuo.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        ParseUser thisUser = ParseUser.getCurrentUser();
        ParseInstallation thisInstallation = ParseInstallation.getCurrentInstallation();
        thisInstallation.put("user", thisUser);
        thisInstallation.saveInBackground();

        //***************toolbar*********************
        //*********getting photo file from parse.com and converted into drawable
        ParseFile file = (ParseFile) ParseUser.getCurrentUser().get("photo");
        try {
            data = file.getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Bitmap bitmapIcon = BitmapFactory.decodeByteArray(data, 0, data.length);
        bitmapIcon = Bitmap.createScaledBitmap(bitmapIcon, 100, 100, true);
        Drawable icon = new BitmapDrawable(getResources(),bitmapIcon);
        toolbar.setLogo(icon);
        //**********end icon
        String screenTitle = "  Album - " + ParseUser.getCurrentUser().get("name");
        toolbar.setTitle(screenTitle);
        setSupportActionBar(toolbar);
        //**********************end toolbar*********************




        //setup view
        recyclerView = (RecyclerView) findViewById(R.id.rv_album);
        llm = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        //end setup view


        //image view setup for alert dialog
        image = new ImageView(this);
        image.setMinimumHeight(500);
        image.setMaxWidth(500);
        image.setImageResource(R.drawable.ic_action_default_icon);


        //end image view

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumActivity.this, InboxActivity.class);
                startActivity(intent);
            }
        });

        //drop down menu
        spinner = (Spinner) findViewById(R.id.spinner_album_albumType);
        setUpSpinner();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (id == 0) {
                    //start progress dialog
                    progressDialog = new ProgressDialog(AlbumActivity.this);
                    progressDialog.setMessage("Loading Album ...");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                    //end progress dialog
                    loadingMyAlbum();
                    Toast.makeText(AlbumActivity.this, "My Album Seleteced", Toast.LENGTH_SHORT).show();
                } else if (id == 1) {
                    //TODO get all shared album to user from parse.com
                    //start progress dialog
                    progressDialog = new ProgressDialog(AlbumActivity.this);
                    progressDialog.setMessage("Loading Album ...");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                    //end progress dialog
                    loadingSharedAlbum();
                    Toast.makeText(AlbumActivity.this, "Shared Album Seleteced", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO get all public album from parse.com
                    //start progress dialog
                    progressDialog = new ProgressDialog(AlbumActivity.this);
                    progressDialog.setMessage("Loading Album ...");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                    //end progress dialog
                    loadingPublicAlbum();
                    Toast.makeText(AlbumActivity.this, "Public Album Seleteced", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //end drop down menu




    }

    private void loadingPublicAlbum() {
        ParseQuery<ParseObject> publicAlbumQuery = ParseQuery.getQuery("Album");
        //this will result in all public + shared
        publicAlbumQuery.whereNotEqualTo("owner", ParseUser.getCurrentUser());
        publicAlbumQuery.whereEqualTo("public", true);

        publicAlbumQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    albumList = new ArrayList<ParseObject>(objects);
                    albumAdapter = new AlbumAdapter(AlbumActivity.this, albumList);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(albumAdapter);
                    Log.d("publicAlbum size", objects.size()+"");
                    progressDialog.dismiss();
                } else {
                    Log.d("publicAlbum", e.toString());
                }
            }
        });
    }

    private void loadingSharedAlbum() {

        ParseQuery<ParseObject> sharedAlbumQuery = ParseQuery.getQuery("Album");
        //this will result in all public + shared
        sharedAlbumQuery.whereNotEqualTo("owner", ParseUser.getCurrentUser());
        sharedAlbumQuery.whereEqualTo("public", false);

        sharedAlbumQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    albumList = new ArrayList<ParseObject>(objects);
                    albumAdapter = new AlbumAdapter(AlbumActivity.this, albumList);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(albumAdapter);
                    Log.d("publicAlbum size", objects.size()+"");
                    progressDialog.dismiss();
                } else {
                    Log.d("publicAlbum", e.toString());
                }
            }
        });
    }

    private void loadingMyAlbum() {
        ParseQuery<ParseObject> myAlbumQuery = ParseQuery.getQuery("Album");
        myAlbumQuery.whereEqualTo("owner", ParseUser.getCurrentUser());
        myAlbumQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    albumList = new ArrayList<ParseObject>(objects);
                    albumAdapter = new AlbumAdapter(AlbumActivity.this, albumList);
                    recyclerView.setLayoutManager(llm);
                    recyclerView.setAdapter(albumAdapter);
                    Log.d("myAlbum size", objects.size() + "");
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Log.d("myAlbum", e.toString());
                }
            }
        });
    }

    private void setUpSpinner() {
        //spinner items
        List<String> categories = new ArrayList<>();
        categories.add("My Albums");
        categories.add("Shared Albums");
        categories.add("Public Albums");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER_VERTICAL);
                ((TextView)v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView)v).setGravity(Gravity.CENTER_VERTICAL);
                ((TextView)v).setTextColor(Color.WHITE);
                return v;
            }
        };

        spinner.setAdapter(spinnerAdapter);
        //Spinner Done
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.createnewalbum)
        {
            Intent intent = new Intent(AlbumActivity.this, creatingnewalbumActivity.class);
            startActivity(intent);
        }
        //grid or list
        //TODO load different layout for RecycleView
        if(id == R.id.gridOrList){
            if(!inGrid) {
                inGrid = true;
                item.setIcon(R.drawable.ic_stat_list);
                //grid view
                Toast.makeText(AlbumActivity.this, "Change to Grid View", Toast.LENGTH_SHORT).show();
                glm = new GridLayoutManager(this, 2);
                albumGridAdapter = new AlbumGridAdapter(AlbumActivity.this,albumList);
                recyclerView.setLayoutManager(glm);
                recyclerView.setAdapter(albumGridAdapter);

            } else {
                inGrid = false;
                item.setIcon(R.drawable.ic_stat_grid);
                //list view
                Toast.makeText(AlbumActivity.this, "Change to List View", Toast.LENGTH_SHORT).show();
                albumAdapter = new AlbumAdapter(AlbumActivity.this, albumList);
                recyclerView.setLayoutManager(llm);
                recyclerView.setAdapter(albumAdapter);

            }
        } else if(id == R.id.action_settings){
            //account setting
            Intent intent = new Intent(AlbumActivity.this, AccountSettingActivity.class);
            startActivity(intent);
        } else  if (id == R.id.action_pending){
            Intent intent = new Intent(AlbumActivity.this, PendingPhotoActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_refresh){
            finish();
            startActivity(getIntent());
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e==null){
                        Toast.makeText(AlbumActivity.this, "Log Out successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AlbumActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(AlbumActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                        Log.d("log out", e.toString());
                    }
                }
            });
        }




        return super.onOptionsItemSelected(item);
    }



}
