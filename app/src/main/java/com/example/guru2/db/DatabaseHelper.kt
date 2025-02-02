package com.example.guru2.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru2.fridge.Ingredient

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FridgeApp.db"
        // 버전 2 → 4 (예시): 새로 saveDate 컬럼 추가 및 update 메서드 반영
        private const val DATABASE_VERSION = 4

        // 기존 테이블(Ingredients)
        const val TABLE_INGREDIENTS = "Ingredients"
        const val COLUMN_INGREDIENT_ID = "ingredient_id"
        const val COLUMN_INGREDIENT_NAME = "name"

        // 기존 테이블(Fridge)
        const val TABLE_FRIDGE = "Fridge"
        const val COLUMN_FRIDGE_ID = "fridge_id"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_ADDED_DATE = "added_date"

        // 알레르기 테이블
        const val TABLE_ALLERGIES = "Allergies"
        const val COLUMN_ALLERGY_ID = "allergy_id"
        const val COLUMN_ALLERGY_NAME = "allergy_name"

        // ------------------- 북마크 / 저장된 레시피 테이블 -------------------
        const val TABLE_SAVED_RECIPES = "SavedRecipes"
        const val COLUMN_SAVED_ID = "id"
        const val COLUMN_SAVED_NAME = "recipeName"
        const val COLUMN_SAVED_TIME = "cookingTime"
        const val COLUMN_SAVED_INGREDIENTS = "ingredients"
        const val COLUMN_SAVED_TEXT = "recipeText"
        const val COLUMN_SAVED_CALORIE = "calorie"
        const val COLUMN_SAVED_NUTRIENT = "nutrient"
        const val COLUMN_SAVED_DATE = "saveDate" // 새로 추가된 저장 날짜
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // 1) Ingredients
        val createIngredientsTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_INGREDIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INGREDIENT_NAME TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
        db?.execSQL(createIngredientsTable)

        // 2) Fridge
        val createFridgeTable = """
            CREATE TABLE $TABLE_FRIDGE (
                $COLUMN_FRIDGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INGREDIENT_ID INTEGER NOT NULL,
                $COLUMN_QUANTITY TEXT,
                $COLUMN_ADDED_DATE DATE DEFAULT (DATE('now')),
                FOREIGN KEY ($COLUMN_INGREDIENT_ID) REFERENCES $TABLE_INGREDIENTS($COLUMN_INGREDIENT_ID) ON DELETE CASCADE
            )
        """.trimIndent()
        db?.execSQL(createFridgeTable)

        // 3) Allergies
        val createAllergiesTable = """
            CREATE TABLE $TABLE_ALLERGIES (
                $COLUMN_ALLERGY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ALLERGY_NAME TEXT NOT NULL UNIQUE
            )
        """.trimIndent()
        db?.execSQL(createAllergiesTable)

        // 4) SavedRecipes (+ saveDate)
        val createSavedRecipesTable = """
            CREATE TABLE $TABLE_SAVED_RECIPES (
                $COLUMN_SAVED_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SAVED_NAME TEXT,
                $COLUMN_SAVED_TIME TEXT,
                $COLUMN_SAVED_INGREDIENTS TEXT,
                $COLUMN_SAVED_TEXT TEXT,
                $COLUMN_SAVED_CALORIE TEXT,
                $COLUMN_SAVED_NUTRIENT TEXT,
                $COLUMN_SAVED_DATE TEXT
            )
        """.trimIndent()
        db?.execSQL(createSavedRecipesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FRIDGE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALLERGIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SAVED_RECIPES")
        onCreate(db)
    }

    // -------------------------- 냉장고 관련 메서드 --------------------------
    fun insertIngredient(name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_INGREDIENT_NAME, name)
        }
        return db.insertWithOnConflict(TABLE_INGREDIENTS, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun addToFridge(ingredientName: String, quantity: String): Long {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_INGREDIENT_ID FROM $TABLE_INGREDIENTS WHERE $COLUMN_INGREDIENT_NAME = ?",
            arrayOf(ingredientName)
        )
        return if (cursor.moveToFirst()) {
            val ingredientId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_ID))
            cursor.close()

            val values = ContentValues().apply {
                put(COLUMN_INGREDIENT_ID, ingredientId)
                put(COLUMN_QUANTITY, quantity)
            }
            db.insert(TABLE_FRIDGE, null, values)
        } else {
            cursor.close()
            -1
        }
    }

    fun getAllFridgeItems(): List<Ingredient> {
        val db = readableDatabase
        val query = """
            SELECT i.$COLUMN_INGREDIENT_NAME, f.$COLUMN_ADDED_DATE, f.$COLUMN_QUANTITY
            FROM $TABLE_FRIDGE f
            JOIN $TABLE_INGREDIENTS i
            ON f.$COLUMN_INGREDIENT_ID = i.$COLUMN_INGREDIENT_ID
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        val ingredients = mutableListOf<Ingredient>()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDED_DATE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                ingredients.add(Ingredient(name, date, quantity))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return ingredients
    }

    fun searchFridgeItems(query: String): List<Ingredient> {
        val db = readableDatabase
        val sqlQuery = """
            SELECT i.$COLUMN_INGREDIENT_NAME, f.$COLUMN_ADDED_DATE, f.$COLUMN_QUANTITY
            FROM $TABLE_FRIDGE f
            JOIN $TABLE_INGREDIENTS i
            ON f.$COLUMN_INGREDIENT_ID = i.$COLUMN_INGREDIENT_ID
            WHERE i.$COLUMN_INGREDIENT_NAME LIKE ?
        """.trimIndent()
        val cursor = db.rawQuery(sqlQuery, arrayOf("%$query%"))
        val results = mutableListOf<Ingredient>()

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INGREDIENT_NAME))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDED_DATE))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                results.add(Ingredient(name, date, quantity))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return results
    }

    fun deleteFromFridge(ingredientName: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_FRIDGE,
            "$COLUMN_INGREDIENT_ID = (SELECT $COLUMN_INGREDIENT_ID FROM $TABLE_INGREDIENTS WHERE $COLUMN_INGREDIENT_NAME = ?)",
            arrayOf(ingredientName)
        )
    }

    fun updateFridgeQuantity(ingredientName: String, quantity: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_QUANTITY, quantity.toString())
        }
        return db.update(
            TABLE_FRIDGE,
            values,
            "$COLUMN_INGREDIENT_ID = (SELECT $COLUMN_INGREDIENT_ID FROM $TABLE_INGREDIENTS WHERE $COLUMN_INGREDIENT_NAME = ?)",
            arrayOf(ingredientName)
        )
    }

    // -------------------------- 알레르기 관련 메서드 --------------------------
    fun insertAllergy(allergyName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALLERGY_NAME, allergyName)
        }
        return db.insertWithOnConflict(TABLE_ALLERGIES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun getAllAllergies(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ALLERGY_NAME FROM $TABLE_ALLERGIES", null)
        val allergies = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            do {
                val allergyName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALLERGY_NAME))
                allergies.add(allergyName)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return allergies
    }

    fun deleteAllergy(allergyName: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_ALLERGIES,
            "$COLUMN_ALLERGY_NAME = ?",
            arrayOf(allergyName)
        )
    }

    // -------------------------- SavedRecipes (북마크) 관련 메서드 --------------------------
    /**
     * 레시피를 SavedRecipes 테이블에 저장 (날짜 포함)
     */
    fun insertSavedRecipe(
        recipeName: String,
        cookingTime: String,
        ingredients: String,
        recipeText: String,
        calorie: String,
        nutrient: String,
        saveDate: String  // 새로 추가
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SAVED_NAME, recipeName)
            put(COLUMN_SAVED_TIME, cookingTime)
            put(COLUMN_SAVED_INGREDIENTS, ingredients)
            put(COLUMN_SAVED_TEXT, recipeText)
            put(COLUMN_SAVED_CALORIE, calorie)
            put(COLUMN_SAVED_NUTRIENT, nutrient)
            put(COLUMN_SAVED_DATE, saveDate)
        }
        return db.insert(TABLE_SAVED_RECIPES, null, values)
    }

    /**
     * 이미 북마크된 레시피인지 확인 (이름 기준)
     */
    fun isRecipeBookmarked(recipeName: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_SAVED_ID FROM $TABLE_SAVED_RECIPES WHERE $COLUMN_SAVED_NAME = ?",
            arrayOf(recipeName)
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    /**
     * DB에서 레시피 삭제 (북마크 해제)
     */
    fun deleteSavedRecipe(recipeName: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_SAVED_RECIPES,
            "$COLUMN_SAVED_NAME = ?",
            arrayOf(recipeName)
        )
    }

    /**
     * 레시피 정보 수정 (제목, 재료, etc.)
     */
    fun updateSavedRecipe(
        originalName: String,
        newName: String,
        cookingTime: String,
        ingredients: String,
        recipeText: String,
        calorie: String,
        nutrient: String,
        newSaveDate: String
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SAVED_NAME, newName)
            put(COLUMN_SAVED_TIME, cookingTime)
            put(COLUMN_SAVED_INGREDIENTS, ingredients)
            put(COLUMN_SAVED_TEXT, recipeText)
            put(COLUMN_SAVED_CALORIE, calorie)
            put(COLUMN_SAVED_NUTRIENT, nutrient)
            put(COLUMN_SAVED_DATE, newSaveDate)
        }
        return db.update(
            TABLE_SAVED_RECIPES,
            values,
            "$COLUMN_SAVED_NAME = ?",
            arrayOf(originalName)
        )
    }

    /**
     * (추가) ID로 레시피 조회해서 모든 필드 반환
     * -> Bookmark 페이지에서 id만 넘기고, 상세 페이지에서 이 메서드로 전체 호출
     */
    fun getSavedRecipeById(recipeId: Int): SavedRecipe? {
        val db = readableDatabase
        val query = """
            SELECT 
                $COLUMN_SAVED_NAME,
                $COLUMN_SAVED_TIME,
                $COLUMN_SAVED_INGREDIENTS,
                $COLUMN_SAVED_TEXT,
                $COLUMN_SAVED_CALORIE,
                $COLUMN_SAVED_NUTRIENT,
                $COLUMN_SAVED_DATE
            FROM $TABLE_SAVED_RECIPES
            WHERE $COLUMN_SAVED_ID = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(recipeId.toString()))
        var result: SavedRecipe? = null
        if (cursor.moveToFirst()) {
            val name     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_NAME))
            val time     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_TIME))
            val ing      = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_INGREDIENTS))
            val text     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_TEXT))
            val cal      = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_CALORIE))
            val nut      = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_NUTRIENT))
            val date     = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SAVED_DATE))

            result = SavedRecipe(
                id           = recipeId,
                recipeName   = name,
                cookingTime  = time,
                ingredients  = ing,
                recipeText   = text,
                calorie      = cal,
                nutrient     = nut,
                saveDate     = date
            )
        }
        cursor.close()
        return result
    }
}

/**
 * 데이터 클래스로 저장된 레시피를 모델링
 */
data class SavedRecipe(
    val id: Int,
    val recipeName: String,
    val cookingTime: String,
    val ingredients: String,
    val recipeText: String,
    val calorie: String,
    val nutrient: String,
    val saveDate: String
)
