package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables to be used for rendering data in app
    RecyclerView recyclerView;
    RecipeAdapter recipeAdapter;
    ArrayList<Recipe> recipes = new ArrayList<>();


    //Declaring variables for user selections
    int selectedCountryId = 0;
    int selectedCategoryId = 0;
    int maxTime = 180;
    int maxIngredients = 30;
    int selectedLanguage = 0;
    private boolean isSpinnerSetting = false;

    String[] languages = {
            "English", "Spanish", "French", "German", "Chinese", "Hindi"
    };

    String[] countryValues = {
            "",
            "Algerian",
            "American",
            "Argentinian",
            "Australian",
            "British",
            "Canadian",
            "Chinese",
            "Croatian",
            "Dutch",
            "Egyptian",
            "Filipino",
            "French",
            "Greek",
            "Indian",
            "Irish",
            "Italian",
            "Jamaican",
            "Japanese",
            "Kenyan",
            "Malaysian",
            "Mexican",
            "Moroccan",
            "Norwegian",
            "Polish",
            "Portuguese",
            "Russian",
            "Saudi Arabian",
            "Slovakian",
            "Spanish",
            "Syrian",
            "Thai",
            "Tunisian",
            "Turkish",
            "Ukrainian",
            "Uruguayan",
            "Venezuelan",
            "Vietnamese"
    };

    String[] categoryValues = {
            "",
            "Beef",
            "Chicken",
            "Dessert",
            "Vegetarian",
            "Vegan",
            "Lamb",
            "Goat",
            "Pasta",
            "Pork",
            "Seafood",
            "Side"
    };

    //Connection & preference storage constants
    public static final String BASE_URL = "https://mobilerecipeapp-production.up.railway.app/api/recipe/";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PREF_LANG_CODE = "language_code";
    private static final String PREF_LANG_POS = "language_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply saved preferences
        applySavedLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Getting language spinner and setting the adapter to it
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, languages);
        languageSpinner.setAdapter(adapter);

        // Load saved language preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        selectedLanguage = prefs.getInt(PREF_LANG_POS, 0);

        // Set spinner without triggering listener
        isSpinnerSetting = true;
        languageSpinner.setSelection(selectedLanguage);
        isSpinnerSetting = false;

        //Getting the recipe recycler and setting the adapter to it
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recipeAdapter = new RecipeAdapter(recipes, recipe -> {
            Intent intent = new Intent(MainActivity.this, RecipeDetailActivity.class);
            intent.putExtra("recipeId", recipe.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(recipeAdapter);

        //Load all recipes (no filters)
        loadRecipes("");

        //Getting search and filter components
        TextInputEditText searchInput = findViewById(R.id.searchInput);
        ImageButton filterBtn = findViewById(R.id.filterButton);

        //Adding listeners
        filterBtn.setOnClickListener(v -> showFilterDialog());

        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadRecipes(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerSetting) return;
                if (position == selectedLanguage) return;

                String code;
                switch (position) {
                    case 1: code = "es"; break;
                    case 2: code = "fr"; break;
                    case 3: code = "de"; break;
                    case 4: code = "zh"; break;
                    case 5: code = "hi"; break;
                    default: code = "en";
                }

                selectedLanguage = position;
                setLocale(code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //Load and apply preferences (Language)
    private void applySavedLocale() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String langCode = prefs.getString(PREF_LANG_CODE, "en");
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    //Save preferences
    private void setLocale(String languageCode) {
        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit()
                .putString(PREF_LANG_CODE, languageCode)
                .putInt(PREF_LANG_POS, selectedLanguage)
                .apply();

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Refresh UI
        recreate();
    }

    //Load recipes from api based on query
    private void loadRecipes(String query) {
        //Create and queue import request for API
        RequestQueue queue = Volley.newRequestQueue(this);
        String url;
        if(query != null && !query.isEmpty()) {
            JsonArrayRequest importRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL + "import/" + Uri.encode(query), null,
                    response -> {
                    },
                    error -> {
                        //Show error if somethings wrong
                        error.printStackTrace();
                        android.util.Log.e("API_ERROR", error.toString());
                    }
            );
            queue.add(importRequest);
        }

        //Assemble the search request URL
        url = BASE_URL + "search/";
        if (query != null && !query.trim().isEmpty()) {
            url += (Uri.encode(query) + "/");
        } else {
            url += "a";
        }

        url += "?country=" + Uri.encode(countryValues[selectedCountryId])
                + "&category=" + Uri.encode(categoryValues[selectedCategoryId])
                + "&maxTime=" + maxTime
                + "&maxIngredients=" + maxIngredients;

        Log.d("URL", url);

        //Create and queue a search request for API
        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    //Clear the displayed recipes
                    recipes.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            //Process and add all the returned recipes
                            JSONObject obj = response.getJSONObject(i);
                            Recipe r = new Recipe();
                            r.id = obj.getInt("id");
                            r.name = obj.getString("name");
                            r.imageUrl = obj.getString("imageUrl");
                            r.area = obj.getString("area");
                            r.time = obj.getString("time");
                            r.ingredientCount = obj.getInt("ingredientCount");
                            r.category = obj.getString("category");
                            recipes.add(r);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    recipeAdapter.notifyDataSetChanged();
                },
                error -> {
                    //Show error if somethings wrong
                    error.printStackTrace();
                    android.util.Log.e("API_ERROR", error.toString());
                }
        );
        queue.add(request);
    }

    //Display filters dialog
    private void showFilterDialog() {
        //Create alert popup dialogue
        View view = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setView(view)
                        .create();

        //Get all the components used in filtering
        Spinner countrySpinner = view.findViewById(R.id.countrySpinner);
        Spinner categorySpinner = view.findViewById(R.id.categorySpinner);
        Slider timeSlider = view.findViewById(R.id.timeSlider);
        Slider ingredientSlider = view.findViewById(R.id.ingredientSlider);
        Button apply = view.findViewById(R.id.applyFilters);
        Button clear = view.findViewById(R.id.clearFilters);

        //Define selection countries & categories from the selected language file
        String[] countries = getResources().getStringArray(R.array.countries);
        //Values for api - localization can break it

        String[] categories = getResources().getStringArray(R.array.categories);

        //Setting the adapters
        countrySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countries));
        categorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));

        //Re-selecting previously selected values
        countrySpinner.setSelection(selectedCountryId);
        categorySpinner.setSelection(selectedCategoryId);
        timeSlider.setValue(maxTime);
        ingredientSlider.setValue(maxIngredients);

        //Apply click listener
        apply.setOnClickListener(v -> {
            //Get user selected values
            selectedCountryId = countrySpinner.getSelectedItemPosition();
            selectedCategoryId = categorySpinner.getSelectedItemPosition();

            maxTime = (int) timeSlider.getValue();
            maxIngredients = (int) ingredientSlider.getValue();

            //Load recipes based on the selected filters
            loadRecipes(Objects.requireNonNull(((TextInputEditText) findViewById(R.id.searchInput)).getText()).toString().trim());
            //Close Filter Dialogue
            dialog.dismiss();
        });

        //Clear click listener
        clear.setOnClickListener(v -> {
            //Clear all the selections
            selectedCountryId = 0;
            selectedCategoryId = 0;
            maxTime = 180;
            maxIngredients = 30;
            countrySpinner.setSelection(0);
            categorySpinner.setSelection(0);
            timeSlider.setValue(timeSlider.getValueTo());
            ingredientSlider.setValue(ingredientSlider.getValueTo());
            //Load recipes with no filters
            loadRecipes(Objects.requireNonNull(((TextInputEditText) findViewById(R.id.searchInput)).getText()).toString().trim());
            //Close Filter Dialogue
            dialog.dismiss();
        });

        //Display Filter Dialogue
        dialog.show();
    }

    //Helper get index - Finds which index the value occupies in spinner
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }
}