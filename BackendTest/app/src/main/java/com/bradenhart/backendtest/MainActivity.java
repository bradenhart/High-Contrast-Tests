package com.bradenhart.backendtest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import backend_controller.EndpointsAsyncTask;
import backend_controller.GcmRegistrationAsyncTask;
import backend_controller.ServletPostAsyncTask;

public class MainActivity extends AppCompatActivity {

    private Button clickMeButton;
    private TextView resultTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickMeButton = (Button) findViewById(R.id.clickmebtn);
        resultTextView = (TextView) findViewById(R.id.resultTv);

        clickMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Servlet Call
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, "Dave Smith"));

        // Endpoint Call
        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Dave Smith"));

        //Gcm Registration
        new GcmRegistrationAsyncTask(this).execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
