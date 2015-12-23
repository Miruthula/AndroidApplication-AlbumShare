package com.example.yzhuo.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ProgressDialog loadingMessageDialog;
    List<ParseObject> messageList;
    InboxAdapter inboxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectReceiver();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadingMessageDialog = new ProgressDialog(InboxActivity.this);
        loadingMessageDialog.setMessage("loading messages...");
        loadingMessageDialog.setCancelable(false);
        loadingMessageDialog.show();

        loadMessages();

    }

    public void loadMessages() {
        final ParseQuery<ParseObject> messages = ParseQuery.getQuery("Message");
        messages.whereEqualTo("receiver", ParseUser.getCurrentUser().getObjectId().toString());
        messages.orderByDescending("createdAt");
        messages.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null) {

                    //messageList = new ArrayList<ParseObject>(objects);
                    inboxAdapter = new InboxAdapter(InboxActivity.this, objects);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(inboxAdapter);

                    loadingMessageDialog.dismiss();
                } else {
                    loadingMessageDialog.dismiss();
                }
            }
        });
    }

    public void selectReceiver(){
        final List<ParseUser> users = new ArrayList<>();
        ParseQuery<ParseUser> userList = ParseQuery.getQuery("_User");
        userList.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        userList.whereEqualTo("listing", true);
        userList.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    final String[] names = new String[objects.size()];
                    for (int x = 0; x < objects.size(); x++) {
                        ParseUser u = objects.get(x);
                        users.add(u);
                        Log.d("get name",u.get("name").toString());
                        names[x] = (u.get("name").toString());
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(InboxActivity.this);
                    builder.setTitle("Users");
                    builder.setItems(names, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Intent intent = new Intent(InboxActivity.this, MessageActivity.class);
                            for (ParseUser user : users) {
                                if (user.get("name").toString().equals(names[item])) {
                                    intent.putExtra("receiverId",user.getObjectId());
                                    continue;
                                }
                            }
                            startActivity(intent);
                            finish();
                        }
                    }).show();
                }
            }
        });
    }

}
