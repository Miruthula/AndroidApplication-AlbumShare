package com.example.yzhuo.finalproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class PendingPhotoActivity extends AppCompatActivity {

    LinearLayoutManager llm;
    RecyclerView recyclerView;
    PhotoPendingAdapter photoPendingAdapter;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_photo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_pending);
        //setup view
        recyclerView = (RecyclerView) findViewById(R.id.rv_pending);
        llm = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        //end setup view

        progressDialog = new ProgressDialog(PendingPhotoActivity.this);
        progressDialog.setMessage("Loading Photo ...");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        ParseQuery<ParseObject> queryAlbum = new ParseQuery<ParseObject>("Album");
        queryAlbum.whereEqualTo("owner", ParseUser.getCurrentUser());
        ParseQuery<ParseObject> queryPending = new ParseQuery<ParseObject>("Photo");
        queryPending.whereEqualTo("verify", false);
        queryPending.whereMatchesQuery("album", queryAlbum);

        queryPending.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    Log.d("Pending photo", objects.size()+"");
                    photoPendingAdapter = new PhotoPendingAdapter(PendingPhotoActivity.this, objects, fab);
                    photoPendingAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(photoPendingAdapter);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Log.d("find photo", e.toString());
                }
            }
        });

    }

}
