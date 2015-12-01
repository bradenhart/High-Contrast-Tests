package com.bradenhart.backendtest;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by bradenhart on 6/09/15.
 */
public class UploadFormActivity extends AppCompatActivity {

    private EditText nameETv;
    private EditText descETv;
    private Spinner diffSpinner;
    private EditText minETv;
    private EditText maxETv;
    private Button uploadBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_form);

        nameETv = (EditText) findViewById(R.id.name_etv);
        descETv = (EditText) findViewById(R.id.description_etv);
        diffSpinner = (Spinner) findViewById(R.id.difficulty_spinner);
        minETv = (EditText) findViewById(R.id.min_size_etv);
        maxETv = (EditText) findViewById(R.id.max_size_etv);
        uploadBtn = (Button) findViewById(R.id.upload_button);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void uploadChallenge() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = nameETv.getText().toString();
                    String description = descETv.getText().toString();
                    String difficulty = diffSpinner.getSelectedItem().toString();
                    Integer min = new Integer(minETv.getText().toString());
                    Integer max = new Integer(maxETv.getText().toString());
                    Challenge challenge = new Challenge(null, name, description, difficulty, min, max, false);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
