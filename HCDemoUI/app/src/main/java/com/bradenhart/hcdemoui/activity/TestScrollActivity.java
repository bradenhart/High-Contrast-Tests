package com.bradenhart.hcdemoui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.bradenhart.hcdemoui.R;
import com.bradenhart.hcdemoui.adapter.RecyclerAdapter;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradenhart on 12/12/15.
 */
public class TestScrollActivity extends TestBaseActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scroll);

        recyclerView = (RecyclerView) findViewById(R.id.test_recyclerview);
        setupRecyclerView(recyclerView);

        fab = (FloatingActionButton) findViewById(R.id.test_fab);
        fab.attachToRecyclerView(recyclerView);

    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList());
//        recyclerAdapter.setExpandListener(this);
            recyclerView.setAdapter(recyclerAdapter);
        }
    }

    private List<String> createItemList() {
        List<String> itemList = new ArrayList<>();

        int itemsCount = 20;
        for (int i = 1; i <= itemsCount; i++) {
            itemList.add("Item " + i);
        }

        return itemList;
    }

    @Override
    protected boolean useHeaderBar() {
        return true;
    }

    @Override
    protected boolean useFab() {
        return true;
    }

    @Override
    protected int setFabIcon() {
        return R.drawable.ic_filter_list_white_24dp;
    }
}
