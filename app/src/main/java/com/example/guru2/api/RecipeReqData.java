package com.example.guru2.api;

import com.google.gson.annotations.SerializedName;

public class RecipeReqData {

    @SerializedName("allergy")
    private String allergy;
    @SerializedName("typeOfCooking")
    private String typeOfCooking;
    @SerializedName("ingredients")
    private String ingredients;
    @SerializedName("cookingMethod")
    private String cookingMethod;
    @SerializedName("cookingTime")
    private String cookingTime;

    public RecipeReqData(String allergy, String typeOfCooking, String ingredients, String cookingMethod, String cookingTime) {
        this.allergy = allergy;
        this.typeOfCooking = typeOfCooking;
        this.ingredients = ingredients;
        this.cookingMethod = cookingMethod;
        this.cookingTime = cookingTime;
    }
}
