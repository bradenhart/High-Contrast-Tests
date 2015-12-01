package com.bradenhart.firstparseapp;

import android.app.Application;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by bradenhart on 12/09/15.
 */
public class MyApplication extends Application {

    //public static final String TODO_GROUP_NAME = "ALL_TODOS";

    @Override
    public void onCreate() {
        super.onCreate();

        // add Todo subclass
        //ParseObject.registerSubclass(Todo.class); Would be my Challenge class I think

        // enable the Local Datastore
        Parse.enableLocalDatastore(getApplicationContext());

        /** Parse dashboard for each app shows APP ID and CLIENT KEY */
        // connect this app to my Parse app FirstParseApp
        // sets application id and client key
        Parse.initialize(this, "sMrjZ5qLBuAKwe1nttGfqvbV90NNsQSxucpFsjxo", "MC38jIHOIV6hRn1NkDejqHnFfnah429mUsQnR9g6");
        //ParseUser.enableAutomaticUser();
        //ParseACL defaultACL = new ParseACL();
        //ParseACL.setDefaultACL(defaultACL, true);


        // create a user
        ParseUser user = new ParseUser();
        user.setUsername("Brad Hart");
        user.setPassword("password123");
        user.setEmail("bradendhart@gmail.com");

        // other fields can be set just like with ParseObject
        user.put("phone", "650-555-0000");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Toast.makeText(getApplicationContext(), "Your are now a user of FirstParseApp", Toast.LENGTH_SHORT).show();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(), "Failed to add you as a user of FirstParseApp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
