package com.example.newsapplication;

import javax.xml.transform.Source;

public class Article
{
    private String title;
    private String description;
    private String url;
    private String urlToImage;

    private String publishedAt;

    private Source source;

    public static class Source
    {
        private String name;
        public String getName()
        {
            return name;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public Source getSource() {
        return source;
    }
    //no need for setters in this case because the API that we have we only need to read
}
