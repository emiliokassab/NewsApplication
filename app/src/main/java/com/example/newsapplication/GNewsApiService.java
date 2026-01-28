package com.example.newsapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GNewsApiService
{
    @GET("top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("apikey") String apiKey,
            @Query("lang") String language,
            @Query("max") int maxArticles
    );
    @GET("search")
    Call<NewsResponse> searchArticles(
            @Query("q") String query,
            @Query("lang") String language,
            @Query("max") int maxArticles,
            @Query("apikey") String apiKey
    );
}
