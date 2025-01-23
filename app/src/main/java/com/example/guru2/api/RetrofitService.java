package com.example.guru2.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitService {

    // 레시피 요청
    @POST("/api/askRecipe")
    Call<RecipeResData> askRecipe(@Body RecipeReqData data);
}
