package com.project.loco;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DatabaseViewer extends FragmentActivity {
    private static final String TAG = "DatabaseViewer";
    private LocationViewModel locationViewModel;

    public static final int NEW_LOCATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_viewer);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final LocationsAdapter adapter = new LocationsAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
        locationViewModel.getAllLocations().observe(this, new Observer<List<LocoLocation>>() {
            @Override
            public void onChanged(@Nullable List<LocoLocation> locoLocations) {
                adapter.setWords(locoLocations);
            }
        });
    }


}
