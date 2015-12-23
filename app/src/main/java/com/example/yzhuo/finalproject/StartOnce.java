package com.example.yzhuo.finalproject;

/**
 * Created by YZHUO on 11/20/2015.
 */


import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;

/**
 * ****************Warning DO NOT CHANGE ANYTHING IN THIS FILE
 * ****************IF ACL DENY THAT MEAN YOU FORGOT TO IMPLEMENT IT
 * ****************BEFORE YOU PUSH THE DATA TO THE PARSE.COM
 */
public class StartOnce extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ty9iEWCtTuS1ZEmbbcfzoW457sq8Ue9ruUdAIHU3", "kzq4H0kfVItpzFpsFe5Jc6cZ7fChwYqueduR7UDj");
        ParseTwitterUtils.initialize("MFNS79RXlvjGTuFJfaY9RJWEY","4vcOIiijHXtvP9yTKLn3PoX9TsJK80FNVdZtCCooB7vBPGCE0U");
        ParseFacebookUtils.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        //ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(false);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
