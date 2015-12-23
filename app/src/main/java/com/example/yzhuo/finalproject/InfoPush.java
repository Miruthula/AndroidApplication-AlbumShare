package com.example.yzhuo.finalproject;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzhuo on 11/21/2015.
 */
public class InfoPush {

    List<ParseUser> users;
    List<ParseObject> publicAlbum, sharedAlbum, ownAlbum, photoList;

    public List<ParseUser> getUserList(){
        users = new ArrayList<>();
        ParseQuery<ParseObject> userList = ParseQuery.getQuery("Privacy");
        userList.whereEqualTo("listing", true);
        userList.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(int x = 0; x<objects.size(); x++){
                        users.add(((ParseUser)objects.get(0).get("user")));
                    }
                }
            }
        });
        return users;
    }

    public void startPrivacy(ParseUser thisUser){
        //ParseObject privacy = new ParseObject("Privacy");
        //thisUser.put("user", thisUser);
        thisUser.put("listing", true);
        thisUser.put("notification", true);
        thisUser.saveInBackground();
    }

    public void notification(String reciever, String Message){

        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("objectId", reciever);
        userQuery.whereEqualTo("notification", true);
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", userQuery);


        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(Message);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("push", e.toString());
                }
            }
        });
    }

    public void notiAll(String Message){

        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("notification", true);
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", userQuery);


        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(Message);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("push", e.toString());
                }
            }
        });
    }




    public List<ParseObject> getPhotoList(ParseObject album){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Photo");
        query.whereEqualTo("album", album);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                photoList = new ArrayList<ParseObject>(objects);
            }
        });
        return photoList;
    }
}
