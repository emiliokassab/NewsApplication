package com.example.newsapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<Article> articles;
    private Context context;

    public NewsAdapter(Context context) {
        this.context = context;
        this.articles = new ArrayList<>();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new NewsViewHolder(view);
        //Creates the article card layout
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = articles.get(position);

        holder.textViewTitle.setText(article.getTitle());
        holder.textViewSource.setText(article.getSource() != null ? article.getSource().getName() : "Unknown");
        holder.textViewDescription.setText(article.getDescription());
        holder.textViewDate.setText(article.getPublishedAt());
        //filling the card with article data

// Load image using Glide
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .into(holder.imageViewArticle);
        }

// Click to open article in browser
        holder.itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
            context.startActivity(browserIntent);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
        //update list when we have new articles
    }

    public List<Article> getArticles() {
        return articles;
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewArticle;
        TextView textViewTitle;
        TextView textViewSource;
        TextView textViewDescription;
        TextView textViewDate;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewArticle = itemView.findViewById(R.id.imageViewArticle);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSource = itemView.findViewById(R.id.textViewSource);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
