package com.myhealthplusplus.app.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myhealthplusplus.app.Adapters.VaccineCentersAdapter;
import com.myhealthplusplus.app.Models.VaccineCenter;
import com.myhealthplusplus.app.R;
import com.myhealthplusplus.app.SQLite.DatabaseAdapter;
import com.myhealthplusplus.app.SQLite.PreCreateDB;

import java.util.ArrayList;
import java.util.List;

public class FragmentVaccineCentersList extends Fragment {

    Context mContext;
    RecyclerView recyclerView;
    DatabaseAdapter databaseAdapter;
    VaccineCentersAdapter vaccineCentersAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<VaccineCenter> vaccineCenterList = new ArrayList<>();
    View rootView;

    public FragmentVaccineCentersList() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_fragment_vaccine_centers_list, container, false);

        recyclerView = rootView.findViewById(R.id.vcl_recycler_view);

        PreCreateDB.copyDB(mContext);
        databaseAdapter = new DatabaseAdapter(mContext);
        vaccineCenterList = databaseAdapter.getAllCenters();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        vaccineCentersAdapter = new VaccineCentersAdapter(mContext, vaccineCenterList, recyclerView);
        recyclerView.setAdapter(vaccineCentersAdapter);

        return rootView;
    }
}