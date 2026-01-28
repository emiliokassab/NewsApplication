package com.example.newsapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "2009c37b938d11f166c813c951fbd926";
    private static final String BASE_URL = "https://gnews.io/api/v4/";

    private EditText editTextNumberOfArticles;
    private EditText editTextSearchKeyword;
    private EditText editTextFilterTitle;
    private EditText editTextFilterAuthor;
    private Button buttonFetchArticles;
    private Button buttonSearch;
    private Button buttonFilter;
    private RecyclerView recyclerViewArticles;

    private NewsAdapter newsAdapter;
    private GNewsApiService apiService;
    private List<Article> allArticles; // Store all fetched articles for filtering

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Initialize views
        initializeViews();

// Setup Retrofit
        setupRetrofit();

// Setup RecyclerView
        setupRecyclerView();

// Setup button clicks
        setupButtonListeners();
    }

    private void initializeViews() {
        editTextNumberOfArticles = findViewById(R.id.editTextNumberOfArticles);
        editTextSearchKeyword = findViewById(R.id.editTextSearchKeyword);
        editTextFilterTitle = findViewById(R.id.editTextFilterTitle);
        editTextFilterAuthor = findViewById(R.id.editTextFilterAuthor);
        buttonFetchArticles = findViewById(R.id.buttonFetchArticles);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonFilter = findViewById(R.id.buttonFilter);
        recyclerViewArticles = findViewById(R.id.recyclerViewArticles);

        allArticles = new ArrayList<>();
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(GNewsApiService.class);
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(this);
        recyclerViewArticles.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewArticles.setAdapter(newsAdapter);
    }

    private void setupButtonListeners() {
// Fetch N articles
        buttonFetchArticles.setOnClickListener(v -> fetchArticles());

// Search by keyword
        buttonSearch.setOnClickListener(v -> searchArticles());

// Filter by title/author
        buttonFilter.setOnClickListener(v -> filterArticles());
    }

    // Feature 1: Fetch N news articles
    private void fetchArticles() {
        String numText = editTextNumberOfArticles.getText().toString();
        int numArticles = 10; // default

        try {
            numArticles = Integer.parseInt(numText);
            if (numArticles < 1 || numArticles > 10) {
                Toast.makeText(this, "Please enter a number between 1 and 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<NewsResponse> call = apiService.getTopHeadlines("2009c37b938d11f166c813c951fbd926","en", numArticles);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allArticles = response.body().getArticles();
                    newsAdapter.setArticles(allArticles);
                    Toast.makeText(MainActivity.this,
                            "Fetched " + allArticles.size() + " articles",
                            Toast.LENGTH_SHORT).show();
                } else
                {
                    String errorMsg="Failed:code"+response.code();
                    try
                    {
                        if(response.errorBody()!=null)
                        {
                            errorMsg+=" - "+response.errorBody().string();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    android.util.Log.e("NewsApp",errorMsg);
                    Toast.makeText(MainActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                android.util.Log.e("NewsApp","Error fetching articles",t);
                Toast.makeText(MainActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Feature 2: Search by keyword
    private void searchArticles() {
        String keyword = editTextSearchKeyword.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(this, "Please enter a search keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<NewsResponse> call = apiService.searchArticles(keyword, "en", 10, "2009c37b938d11f166c813c951fbd926");

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allArticles = response.body().getArticles();
                    newsAdapter.setArticles(allArticles);
                    Toast.makeText(MainActivity.this,
                            "Found " + allArticles.size() + " articles",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "No articles found",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Feature 3: Filter by title or author
    private void filterArticles() {
        String titleFilter = editTextFilterTitle.getText().toString().trim().toLowerCase();
        String authorFilter = editTextFilterAuthor.getText().toString().trim().toLowerCase();

        if (titleFilter.isEmpty() && authorFilter.isEmpty()) {
            Toast.makeText(this, "Please enter a title or author to filter", Toast.LENGTH_SHORT).show();
            return;
        }

        if (allArticles.isEmpty()) {
            Toast.makeText(this, "Please fetch or search articles first", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Article> filteredArticles = new ArrayList<>();

        for (Article article : allArticles) {
            boolean matchesTitle = titleFilter.isEmpty() ||
                    article.getTitle().toLowerCase().contains(titleFilter);

            boolean matchesAuthor = authorFilter.isEmpty() ||
                    (article.getSource() != null &&
                            article.getSource().getName().toLowerCase().contains(authorFilter));

            if (matchesTitle && matchesAuthor) {
                filteredArticles.add(article);
            }
        }

        newsAdapter.setArticles(filteredArticles);
        Toast.makeText(this,
                "Filtered: " + filteredArticles.size() + " articles found",
                Toast.LENGTH_SHORT).show();
    }
}