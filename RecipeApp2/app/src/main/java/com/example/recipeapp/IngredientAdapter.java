package com.example.recipeapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//Ingredient adapter for displaying the ingredients in the recipes in an image + name + + quantity format
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final ArrayList<Ingredient> ingredients;

    public IngredientAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.ingredientImage);
            text = view.findViewById(R.id.ingredientText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ing = ingredients.get(position);

        // Show text in format: "Chicken (200g)"
        String ingredientStr = ing.name + " (" + ing.measure + ")";
        holder.text.setText(ingredientStr);

        // Show Image via Link
        String imageUrl = "https://www.themealdb.com/images/ingredients/"
                + ing.name.replace(" ", "_") + "-Small.png";

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}