package com.example.recipeapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeDetailActivity extends AppCompatActivity {
    //Connection URL
    public static final String BASE_URL = "http://192.168.8.108:5070/api/recipe/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_item_detail);

        //Load recipe on create
        int recipeId = getIntent().getIntExtra("recipeId", -1);

        if (recipeId != -1) {
            loadRecipe(recipeId);
        }
    }

    //Load recipe by id
    private void loadRecipe(int id) {
        //Create a new load recipe request and queue it
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = BASE_URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        //Get all the json values into variables
                        String name = response.getString("name");
                        String instructions = response.getString("instructions");
                        String imageUrl = response.getString("imageUrl");
                        String category = response.getString("category");
                        String flag = response.getString("area");
                        String time = response.getString("time");

                        TextView title = findViewById(R.id.detailName);
                        TextView instructionsView = findViewById(R.id.detailInstructions);
                        TextView timeView = findViewById(R.id.detailTime);
                        ImageView image = findViewById(R.id.detailImage);
                        ImageView flagImg = findViewById(R.id.detailFlag);
                        ImageView categoryImg = findViewById(R.id.detailCategory);

                        //Text
                        title.setText(name);
                        //Instructions
                        instructionsView.setText(instructions);
                        //Time
                        String timeStr = "⏱ " + time + "m";
                        timeView.setText(timeStr);
                        //Main Image
                        Glide.with(this)
                                .load(imageUrl)
                                .into(image);
                        // Flag Image
                        Glide.with(this)
                                .load(RecipeUtils.getFlagUrl(flag))
                                .into(flagImg);
                        // Category Image
                        Glide.with(this)
                                .load(RecipeUtils.getCategoryImage(category))
                                .into(categoryImg);

                        //Ingredients
                        ArrayList<Ingredient> ingredients = new ArrayList<>();
                        JSONArray arr = response.getJSONArray("ingredients");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            Ingredient ing = new Ingredient();
                            ing.name = obj.getString("ingredient");
                            ing.measure = obj.getString("measure");

                            ingredients.add(ing);
                        }

                        //Setting up the ingredient recycler
                        RecyclerView recycler = findViewById(R.id.ingredientRecycler);
                        recycler.setLayoutManager(new LinearLayoutManager(this));
                        recycler.setAdapter(new IngredientAdapter(ingredients));

                        //Video Tutorial URL + Thumbnail
                        String videoUrl = response.optString("videoUrl", "");
                        ImageView thumbnail = findViewById(R.id.videoThumbnail);

                        //Video Section
                        LinearLayout videoSection = findViewById(R.id.videoContainer);

                        //Validate and extract the preview image of the video URL
                        if (!videoUrl.isEmpty() && !videoUrl.contains("shorts")) {

                            //Extract youtube id of the link - For Example: "dQw4w9WgXcQ"
                            String videoId = extractYoutubeId(videoUrl);

                            if (!videoId.isEmpty()) {
                                // Load thumbnail
                                String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/0.jpg";

                                Glide.with(this)
                                        .load(thumbUrl)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                videoSection.setVisibility(View.GONE);
                                                return true;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                videoSection.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(thumbnail);

                                // Click Listener to open the video in YouTube
                                thumbnail.setOnClickListener(v -> {
                                    Intent intent = new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://www.youtube.com/watch?v=" + videoId)
                                    );
                                    startActivity(intent);
                                });

                            } else {
                                //Do not display thumbnail if couldn't get it
                                thumbnail.setVisibility(View.GONE);
                            }

                        } else {
                            //Do not display the video section if URL is not working
                            videoSection.setVisibility(View.GONE);
                            thumbnail.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //Display any errors
                Throwable::printStackTrace
        );

        queue.add(request);
    }

    //Helper function to extract Youtube ID using regex - - For Example: "dQw4w9WgXcQ"
    public String extractYoutubeId(String url) {
        if (url == null) return "";

        String pattern = "(?:v=|youtu\\.be/)([a-zA-Z0-9_-]{11})";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }
}
