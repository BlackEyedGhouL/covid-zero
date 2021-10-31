package com.myhealthplusplus.app.Status;

import com.myhealthplusplus.app.R;

import java.util.ArrayList;
import java.util.List;

public class Data {

    public static List<Status> getStatusList(){

        List<Status> statusList = new ArrayList<>();

        Status not_set = new Status();
        not_set.setName("Not Set");
        not_set.setImage(R.drawable.not_set);
        statusList.add(not_set);

        Status healthy = new Status();
        healthy.setName("I've been tested NEGATIVE for COVID-19");
        healthy.setImage(R.drawable.healthy);
        statusList.add(healthy);

        Status not_healthy = new Status();
        not_healthy.setName("I've been tested POSITIVE for COVID-19");
        not_healthy.setImage(R.drawable.not_healthy);
        statusList.add(not_healthy);

        return statusList;
    }
}
