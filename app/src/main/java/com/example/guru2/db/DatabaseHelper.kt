package com.example.guru2.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.guru2.fridge.Ingredient

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FridgeApp.db"

        // 새 테이블 추가로 DB 구조가 변경되었으므로 1 → 2로 버전 업
        private const val DATABASE_VERSION = 3

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

        //레시피 추가 테이블
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

    override fun onCreate(db: SQLiteDatabase?) {
        val createIngredientsTable = """
            CREATE TABLE $TABLE_INGREDIENTS (
                $COLUMN_INGREDIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INGREDIENT_NAME TEXT NOT NULL UNIQUE
            )
        """
        db?.execSQL(createIngredientsTable)

        val createFridgeTable = """
            CREATE TABLE $TABLE_FRIDGE (
                $COLUMN_FRIDGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_INGREDIENT_ID INTEGER NOT NULL,
                $COLUMN_QUANTITY TEXT,
                $COLUMN_ADDED_DATE DATE DEFAULT (DATE('now')),
                FOREIGN KEY ($COLUMN_INGREDIENT_ID) REFERENCES $TABLE_INGREDIENTS($COLUMN_INGREDIENT_ID) ON DELETE CASCADE
            )
        """
        db?.execSQL(createFridgeTable)

        // 알레르기 테이블 생성
        val createAllergiesTable = """
            CREATE TABLE $TABLE_ALLERGIES (
                $COLUMN_ALLERGY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ALLERGY_NAME TEXT NOT NULL UNIQUE
            )
        """
        db?.execSQL(createAllergiesTable)

        //레시피 추가
        val createRecipeTable = """
            CREATE TABLE ${TABLE_RECIPE} (
            ${COLUMN_RECIPE_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${COLUMN_RECIPE_NAME} TEXT NOT NULL,
            ${COLUMN_RECIPE_ADDED_DATE} DATE DEFAULT (DATE('now'))
            )
        """
        db?.execSQL(createRecipeTable)

        val createRecipeDetailTable = """
            CREATE TABLE ${TABLE_RECIPE_DETAILS} (
            ${COLUMN_RECIPE_ID} INTEGER,
            ${COLUMN_RECIPE_COOKINGTIME} INTEGER,
            ${COLUMN_RECIPE_INSTRUCTIONS} TEXT,
            ${COLUMN_RECIPE_CALORIES} INTEGER,
            ${COLUMN_RECIPE_NUTRITION_FACTS} TEXT,
            FOREIGN KEY(${COLUMN_RECIPE_ID}) REFERENCES ${TABLE_RECIPE}(${COLUMN_RECIPE_ID})
            )
        """
        db?.execSQL(createRecipeDetailTable)

        // 예시 데이터 삽입 ---> 여기 꼭빼기!!!!!!
        val insertRecipe = """
        INSERT INTO $TABLE_RECIPE ($COLUMN_RECIPE_NAME)
        VALUES ('Tomato Salad'), ('Cucumber Soup')
    """
        db?.execSQL(insertRecipe)

        val insertRecipeDetails = """
        INSERT INTO $TABLE_RECIPE_DETAILS ($COLUMN_RECIPE_ID, $COLUMN_RECIPE_COOKINGTIME, $COLUMN_RECIPE_INSTRUCTIONS, $COLUMN_RECIPE_CALORIES, $COLUMN_RECIPE_NUTRITION_FACTS)
        VALUES (1, 15, 'Mix all ingredients and serve', 200, 'Vitamins, Antioxidants'),
               (2, 20, 'Boil and blend cucumber', 150, 'Vitamin C, Potassium')
    """
        db?.execSQL(insertRecipeDetails)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 버전이 올라가면 기존 테이블 삭제 후 재생성
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FRIDGE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_INGREDIENTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALLERGIES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_RECIPE_DETAILS")
        onCreate(db)
    }


    // -------------------------- 냉장고 관련 메서드 --------------------------
    fun insertIngredient(name: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_INGREDIENT_NAME, name)
        }
        return db.insertWithOnConflict(
            TABLE_INGREDIENTS,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
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
        """
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
        """
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
    /**
     * 알레르기 테이블에 새로운 항목 추가
     * 이미 등록된 알레르기인 경우, UNIQUE 제약으로 Insert가 무시될 수 있으므로 CONFLICT_IGNORE 사용
     */
    fun insertAllergy(allergyName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALLERGY_NAME, allergyName)
        }
        return db.insertWithOnConflict(
            TABLE_ALLERGIES,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    /**
     * 저장된 모든 알레르기 목록 조회
     */
    fun getAllAllergies(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_ALLERGY_NAME FROM $TABLE_ALLERGIES", null)
        val allergies = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            do {
                val allergyName =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALLERGY_NAME))
                allergies.add(allergyName)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return allergies
    }

    /**
     * 특정 알레르기 항목 삭제
     */
    fun deleteAllergy(allergyName: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_ALLERGIES,
            "$COLUMN_ALLERGY_NAME = ?",
            arrayOf(allergyName)
        )
    }


    // -------------------------- 레시피 상세 등록 관련 메서드 --------------------------
    //데이터 추가 확인 코드
    fun addRecipe(recipeName: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECIPE_NAME, recipeName)
        }
        return db.insert(TABLE_RECIPE, null, values)
    }

    fun addRecipeDetails(
        recipeId: Long,
        cookingTime: Int,
        instructions: String,
        calories: Int,
        nutritionFacts: String
    ): Long {
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
