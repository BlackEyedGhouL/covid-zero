package com.myhealthplusplus.app;

import android.content.Context;
import android.widget.Toast;

import com.myhealthplusplus.app.Models.NewsApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class News_RequestManager {

    Context context;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public void getNewsHeadlines(News_OnFetchDataListener listener, String sort_by){
        CallNewsApi callNewsApi = retrofit.create(CallNewsApi.class);
        Call<NewsApiResponse> call = callNewsApi.callHeadlines("covid" , sort_by,"en", context.getString(R.string.api_key));

        try{
            call.enqueue(new Callback<NewsApiResponse>() {
                @Override
                public void onResponse(Call<NewsApiResponse> call, Response<NewsApiResponse> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(context, "No Response!", Toast.LENGTH_SHORT).show();
                    }

                    listener.onFetchData(response.body().getArticles(), response.message());
                }

                @Override
                public void onFailure(Call<NewsApiResponse> call, Throwable t) {
                    listener.onError("Request Failed!");
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public News_RequestManager(Context context) {
        this.context = context;
    }

    public interface CallNewsApi{
        @GET("top-headlines")
        Call<NewsApiResponse> callHeadlines(
                @Query("q") String query,
                @Query("sortBy") String sort_by,
                @Query("language") String language,
                @Query("apiKey") String api_key
        );
    }
}
