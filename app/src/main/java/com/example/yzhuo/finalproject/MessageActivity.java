package com.example.yzhuo.finalproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String messageText = ((EditText)(findViewById(R.id.compose_message_editText))).getText().toString();
                if(!messageText.equals("")) {
                    /** Todo add photo **/
                    final String receiverId = getIntent().getStringExtra("receiverId");
                    ParseObject message = new ParseObject("Message");

                    message.put("owner", ParseUser.getCurrentUser());
                    message.put("receiver",receiverId);
                    message.put("isRead", false);
                    message.put("message", messageText);
                    message.put("sender",ParseUser.getCurrentUser().get("name"));

                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setPublicWriteAccess(true);
                    message.setACL(acl);

                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                new InfoPush().notification(receiverId,"You've got a message");
                                Toast.makeText(MessageActivity.this, "message has been sent", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Log.d("save message", e.toString());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }
}
