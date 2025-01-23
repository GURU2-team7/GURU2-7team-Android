package com.example.guru2.api;

import com.google.gson.annotations.SerializedName;


public class RecipeResData {
    @SerializedName("nameOfDish")
    private String nameOfDish;
    @SerializedName("ingredients")
    private String ingredients;
    @SerializedName("recipe")
    private String recipe;
    @SerializedName("calorie")
    private String calorie;
    @SerializedName("nutrient")
    private String nutrient;
    @SerializedName("cookingTime")
    private String cookingTime;

    public String getNameOfDish() {
        return nameOfDish;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getRecipe() {
        return recipe;
    }

    public String getCalorie() {
        return calorie;
    }

    public String getNutrient() {
        return nutrient;
    }

    public String getCookingTime() {
        return cookingTime;
    }
}