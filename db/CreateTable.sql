CREATE TABLE Ingredients (
    ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, -- 재료 고유 ID
    name TEXT NOT NULL, -- 재료 이름
    category TEXT, -- 재료 분류 (예: 야채, 고기 등)
    expiry_date DATE, -- 유통기한
    UNIQUE(name) -- 재료 이름의 중복 방지
);


CREATE TABLE Allergies (
    allergy_id INTEGER PRIMARY KEY AUTOINCREMENT, -- 알러지 고유 ID
    name TEXT NOT NULL, -- 알러지 이름
    UNIQUE(name) -- 알러지 이름의 중복 방지
);


CREATE TABLE IngredientAllergies (
    ingredient_id INTEGER NOT NULL, -- 재료 ID
    allergy_id INTEGER NOT NULL, -- 알러지 ID
    PRIMARY KEY (ingredient_id, allergy_id), -- 복합 키로 중복 방지
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id) ON DELETE CASCADE,
    FOREIGN KEY (allergy_id) REFERENCES Allergies(allergy_id) ON DELETE CASCADE
);


CREATE TABLE Recipes (
    recipe_id INTEGER PRIMARY KEY AUTOINCREMENT, -- 레시피 고유 ID
    name TEXT NOT NULL, -- 레시피 이름
    instructions TEXT NOT NULL, -- 요리 방법
    UNIQUE(name) -- 레시피 이름의 중복 방지
);


CREATE TABLE RecipeIngredients (
    recipe_id INTEGER NOT NULL, -- 레시피 ID
    ingredient_id INTEGER NOT NULL, -- 재료 ID
    quantity TEXT, -- 사용된 재료의 양 (예: "200g")
    PRIMARY KEY (recipe_id, ingredient_id), -- 복합 키로 중복 방지
    FOREIGN KEY (recipe_id) REFERENCES Recipes(recipe_id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id) ON DELETE CASCADE
);


CREATE TABLE Fridge (
    fridge_id INTEGER PRIMARY KEY AUTOINCREMENT, -- 냉장고 항목 ID
    ingredient_id INTEGER NOT NULL, -- 재료 ID
    quantity TEXT, -- 보관된 재료의 양
    added_date DATE DEFAULT (DATE('now')), -- 등록 날짜
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id) ON DELETE CASCADE
);


