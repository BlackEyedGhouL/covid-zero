package com.myhealthplusplus.app;

import com.myhealthplusplus.app.Models.NewsHeadlines;

import java.util.List;

public interface News_OnFetchDataListener<NewsApiResponse> {

    void onFetchData(List<NewsHeadlines> list, String message);
    void onError(String message);
}
