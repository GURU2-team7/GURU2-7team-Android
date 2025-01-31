package com.example.guru2.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper_recipe(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RecipeApp.db"
        private const val DATABASE_VERSION = 1

        //레시피 추가
        const val TABLE_RECIPE = "Recipe"
        const val COLUMN_RECIPE_ID = "recipe_id"
        const val COLUMN_RECIPE_NAME = "recipe_name"
        const val COLUMN_RECIPE_ADDED_DATE = "recipe_added_date"

        const val TABLE_RECIPE_DETAILS = "RecipeDetails"
        const val COLUMN_RECIPE_COOKINGTIME = "recipe_cooking_time"
        const val COLUMN_RECIPE_INSTRUCTIONS = "recipe_instructions"
        const val COLUMN_RECIPE_CALORIES = "recipe_calories"
        const val COLUMN_RECIPE_NUTRITION_FACTS = "recipe_nutrition_facts"
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true) // 외래 키 활성화
    }

    override fun onCreate(db: SQLiteDatabase?) {

        //레시피 추가
        val createRecipeTable = """
            CREATE TABLE $TABLE_RECIPE (
            $COLUMN_RECIPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_RECIPE_NAME TEXT NOT NULL,
            $COLUMN_RECIPE_ADDED_DATE DATE DEFAULT (DATE('now'))
            )
        """
        db?.execSQL(createRecipeTable)

        val createRecipeDetailTable = """
            CREATE TABLE $TABLE_RECIPE_DETAILS (
            $COLUMN_RECIPE_ID INTEGER,
            $COLUMN_RECIPE_COOKINGTIME INTEGER,
            $COLUMN_RECIPE_INSTRUCTIONS TEXT,
            $COLUMN_RECIPE_CALORIES INTEGER,
            $COLUMN_RECIPE_NUTRITION_FACTS TEXT,
            FOREIGN KEY($COLUMN_RECIPE_ID) REFERENCES $TABLE_RECIPE($COLUMN_RECIPE_ID)
            )
        """
        db?.execSQL(createRecipeDetailTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE_DETAILS")
        onCreate(db)
    }


    //데이터 추가 확인 코드
    fun addRecipe(recipeName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipeName)
        }
        return db.insert(TABLE_RECIPE, null, values)
    }

    fun addRecipeDetails(recipeId: Long, cookingTime: Int, instructions: String, calories: Int, nutritionFacts: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECIPE_ID, recipeId)
            put(COLUMN_RECIPE_COOKINGTIME, cookingTime)
            put(COLUMN_RECIPE_INSTRUCTIONS, instructions)
            put(COLUMN_RECIPE_CALORIES, calories)
            put(COLUMN_RECIPE_NUTRITION_FACTS, nutritionFacts)
        }
        return db.insert(TABLE_RECIPE_DETAILS, null, values)
    }

}



