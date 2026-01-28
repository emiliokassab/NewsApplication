package com.example.newsapplication;

import java.util.List;

public class NewsResponse
{
    private int totalArticles;
    public int getTotalArticles()
    {
        return totalArticles;
    }
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
    //this class is created to capture the total number of articles and the list of articles
}
