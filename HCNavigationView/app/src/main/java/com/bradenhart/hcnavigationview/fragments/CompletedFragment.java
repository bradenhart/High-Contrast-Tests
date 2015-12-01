package com.bradenhart.hcnavigationview.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bradenhart.hclib.domain.Challenge;
import com.bradenhart.hcnavigationview.R;
import com.bradenhart.hcnavigationview.databases.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by bradenhart on 25/06/15.
 */
public class CompletedFragment extends Fragment {

    private final String LOGTAG = "CompletedActivity";
    private Context context;
    private TextView viewAllTv;
    private DatabaseHandler dbHandler;

    public CompletedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
        context = getActivity();
        dbHandler = DatabaseHandler.getInstance(context);
        viewAllTv = (TextView) view.findViewById(R.id.view_all_challenges_textview);

        startLocalDownloadThread();

        return view;
    }

    private void startLocalDownloadThread() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dialog = new ProgressDialog(context);
                dialog.setTitle("Loading");
                dialog.setMessage("Please wait while the challenges are loaded...");
                dialog.setCancelable(false);
                dialog.show();
                String chStr = "";
                ArrayList<Challenge> challenges = dbHandler.getChallengesFromDb();
                for (Challenge c : challenges) {
                    chStr += "\nbatch: " + c.getId() + " " +  c.getInfo();
                }
                dialog.cancel();
                viewAllTv.setText(chStr);
            }
        });
    }

}
