package com.example.recipeapp;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

//Recipe adapter for displaying the recipes in a 2 column grid form
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final ArrayList<Recipe> recipes;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, flag, category;
        TextView name, time, ingredientCount;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.recipeImage);
            flag = view.findViewById(R.id.flag);
            name = view.findViewById(R.id.recipeName);
            ingredientCount = view.findViewById(R.id.ingredientCount);
            time = view.findViewById(R.id.time);
            category = view.findViewById(R.id.category);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        // Name
        holder.name.setText(recipe.name);

        // Image
        Glide.with(holder.itemView.getContext())
                .load(recipe.imageUrl)
                .into(holder.image);

        //Ingredient Count
        String ingredientCountStr = "\uD83E\uDD51 " + recipe.ingredientCount;
        holder.ingredientCount.setText(ingredientCountStr);

        //Time
        String timeStr = "⏱ " + recipe.time + "m";
        holder.time.setText(timeStr);

        // Flag Image
        Glide.with(holder.itemView.getContext())
                .load(RecipeUtils.getFlagUrl(recipe.area))
                .into(holder.flag);

        holder.flag.setContentDescription(recipe.area);

        // Category Image
        Glide.with(holder.itemView.getContext())
                .load(RecipeUtils.getCategoryImage(recipe.category))
                .into(holder.category);

        holder.category.setContentDescription(recipe.category);

        //Click Listener to open the detailed recipe description when clicked
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final OnRecipeClickListener listener;

    public RecipeAdapter(ArrayList<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }
}