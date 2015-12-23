package com.example.yzhuo.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;


public class PhotoDetailActivity extends AppCompatActivity {

    String imageUrl, objectId, date, ownId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button delete = (Button) findViewById(R.id.button_photo_delete);

        imageUrl = getIntent().getExtras().getString("imageUrl");
        objectId = getIntent().getExtras().getString("objectId");
        ownId = getIntent().getExtras().getString("ownerId");
        date = getIntent().getExtras().getString("date");

        Log.d("Id", ownId + " "+ ParseUser.getCurrentUser().getObjectId().toString());
        ImageView image = (ImageView) findViewById(R.id.imageView_photo_icon);

        delete.setVisibility(View.VISIBLE);

        if(!(ownId.equals(ParseUser.getCurrentUser().getObjectId().toString()))){
            delete.setVisibility(View.GONE);
        }


        ParseQuery<ParseObject> thisOwner = new ParseQuery<ParseObject>("_User");
        thisOwner.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    String name = objects.get(0).get("name").toString();
                    ((TextView)findViewById(R.id.textView_photo_author)).setText(name);
                }
            }
        });

        Picasso.with(PhotoDetailActivity.this)
                .load(imageUrl)
                .error(R.drawable.ic_action_default_icon)
                .into(image);
        ((TextView)findViewById(R.id.textView_photo_create)).setText(date);

        findViewById(R.id.button_photo_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseObject thisObject = ParseObject.createWithoutData("Photo", objectId);
                thisObject.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(PhotoDetailActivity.this, "Image Delete", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.d("delete photo", e.toString());
                        }
                    }
                });
            }
        });

        (findViewById(R.id.button_photo_cancle)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

}
