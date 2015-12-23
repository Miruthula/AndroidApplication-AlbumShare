package com.example.yzhuo.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageDetailActivity extends AppCompatActivity {

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        pd = new ProgressDialog(MessageDetailActivity.this);
        TextView tv = (TextView)findViewById(R.id.textView_fullMessage);
        /**TODO include sender **/
        tv.setText(getIntent().getStringExtra("message"));

        findViewById(R.id.button_delete_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MessageDetailActivity.this)
                        .setMessage("Delete this message?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //delete message
                                pd.setMessage("deleting message...");
                                pd.setCancelable(false);
                                pd.show();

                                ParseQuery query = new ParseQuery("Message");
                                query.whereEqualTo("objectId", getIntent().getStringExtra("messageId"));

                                query.findInBackground(new FindCallback() {
                                    @Override
                                    public void done(List objects, ParseException e) {

                                    }

                                    @Override
                                    public void done(Object o, Throwable throwable) {
                                        if(throwable == null) {
                                            List<ParseObject> messages = (List<ParseObject>) o;
                                            for(ParseObject message : messages) {
                                                message.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if(e == null) {
                                                            //pd.dismiss();
                                                            Toast.makeText(MessageDetailActivity.this,"success",Toast.LENGTH_SHORT).show();
                                                            finish();
                                                            startActivity(new Intent(MessageDetailActivity.this, InboxActivity.class));
                                                        } else {
                                                            Log.d("messageDetail delete", e.toString());
                                                           // pd.dismiss();
                                                            Toast.makeText(MessageDetailActivity.this,"Could not delete message...",Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });

                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy, kk:mm");
                                Log.d("date",date.format(new Date()));
                            }
                        })
                        .show();
            }
        });

        findViewById(R.id.button_reply_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageDetailActivity.this, MessageActivity.class);
                intent.putExtra("receiverId", getIntent().getStringExtra("owner"));

                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MessageDetailActivity.this, InboxActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
