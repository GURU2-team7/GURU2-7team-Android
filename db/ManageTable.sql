-- 1. 재료(Ingredients) 테이블 관리
-- 1-1. Insert
-- 새 재료 추가
INSERT INTO Ingredients (name, category, expiry_date)
VALUES ('체다 치즈', '유제품', '2025-06-15');

-- 1-2. Delete
-- 특정 재료 삭제
DELETE FROM Ingredients
WHERE name = '체다 치즈';
-- 관련된 RecipeIngredients에서 해당 재료 삭제
DELETE FROM RecipeIngredients
WHERE ingredient_id = (SELECT ingredient_id FROM Ingredients WHERE name = '체다 치즈');

-- 관련된 IngredientAllergies에서 해당 재료 삭제
DELETE FROM IngredientAllergies
WHERE ingredient_id = (SELECT ingredient_id FROM Ingredients WHERE name = '체다 치즈');

-- 1-3. Update
-- 재료 정보 수정
UPDATE Ingredients
SET expiry_date = '2025-07-01'
WHERE name = '달걀';

-- 1-4. SELECT
-- 모든 재료 조회
SELECT * FROM Ingredients;

-- 특정 재료 조회
SELECT * FROM Ingredients WHERE name = '달걀';



-- 2. 알러지(Allergies)  테이블 관리
-- 2-1. Insert
-- 새로운 알러지 추가
INSERT INTO Allergies (name)
VALUES ('호두 알러지');

-- 특정 알러지를 재료와 연결
INSERT INTO IngredientAllergies (ingredient_id, allergy_id)
VALUES (
    (SELECT ingredient_id FROM Ingredients WHERE name = '호두'),
    (SELECT allergy_id FROM Allergies WHERE name = '호두 알러지')
);

-- 2-2. Delete
-- 특정 알러지 삭제
DELETE FROM Allergies
WHERE name = '호두 알러지';

-- 관련된 IngredientAllergies에서 해당 알러지 삭제
DELETE FROM IngredientAllergies
WHERE allergy_id = (SELECT allergy_id FROM Allergies WHERE name = '호두 알러지');

-- 2-3. Update
-- 알러지 이름 수정
UPDATE Allergies
SET name = '호두 및 견과류 알러지'
WHERE name = '호두 알러지';

-- 2-4. SELECT
-- 모든 알러지 조회
SELECT * FROM Allergies;

-- 특정 알러지 조회
SELECT * FROM Allergies WHERE name = '땅콩 알러지';



-- 3. 레시피(Recipes)   테이블 관리
-- 3-1. Insert
-- 새 레시피 추가
INSERT INTO Recipes (name, instructions)
VALUES ('치즈버거', '1. 빵에 패티와 치즈를 넣습니다. 2. 소스를 바르고 토핑을 추가합니다.');

-- 레시피에 사용된 재료 추가
INSERT INTO RecipeIngredients (recipe_id, ingredient_id, quantity)
VALUES
    ((SELECT recipe_id FROM Recipes WHERE name = '치즈버거'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '체다 치즈'), '1장'),
    ((SELECT recipe_id FROM Recipes WHERE name = '치즈버거'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '소고기'), '150g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '치즈버거'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '토마토'), '1개');

-- 3-2. Delete
-- 특정 레시피 삭제
DELETE FROM Recipes
WHERE name = '치즈버거';

-- 관련된 RecipeIngredients에서 해당 레시피 삭제
DELETE FROM RecipeIngredients
WHERE recipe_id = (SELECT recipe_id FROM Recipes WHERE name = '치즈버거');

-- 3-3. Update
-- 레시피 이름 및 조리법 수정
UPDATE Recipes
SET name = '더블 치즈버거',
    instructions = '1. 빵에 두 개의 패티와 치즈를 넣습니다. 2. 소스를 바르고 토핑을 추가합니다.'
WHERE name = '치즈버거';

-- 3-4. SELECT
-- 모든 레시피 조회
SELECT * FROM Recipes;

-- 특정 레시피 조회
SELECT * FROM Recipes WHERE name = '된장찌개';

-- 특정 레시피에 사용된 재료 조회
SELECT r.name AS recipe_name, i.name AS ingredient_name, ri.quantity
FROM Recipes r
JOIN RecipeIngredients ri ON r.recipe_id = ri.recipe_id
JOIN Ingredients i ON ri.ingredient_id = i.ingredient_id
WHERE r.name = '된장찌개';



-- 4. 냉장고(Fridge)  테이블 관리
-- 4-1. Insert
-- 새 냉장고 항목 추가
INSERT INTO Fridge (ingredient_id, quantity, added_date)
VALUES (
    (SELECT ingredient_id FROM Ingredients WHERE name = '달걀'), 
    '12개', 
    DATE('2025-01-20')
);

-- 4-2. Delete
-- 특정 냉장고 항목 삭제
DELETE FROM Fridge
WHERE ingredient_id = (SELECT ingredient_id FROM Ingredients WHERE name = '달걀');

-- 4-3. Update
-- 냉장고에 보관 중인 재료 수량 수정
UPDATE Fridge
SET quantity = '6개'
WHERE ingredient_id = (SELECT ingredient_id FROM Ingredients WHERE name = '달걀');

-- 4-4. SELECT
-- 냉장고에 보관된 모든 재료 조회
SELECT f.fridge_id, i.name AS ingredient_name, f.quantity, f.added_date
FROM Fridge f
JOIN Ingredients i ON f.ingredient_id = i.ingredient_id;

-- 특정 재료가 냉장고에 보관 중인지 조회
SELECT f.fridge_id, i.name AS ingredient_name, f.quantity, f.added_date
FROM Fridge f
JOIN Ingredients i ON f.ingredient_id = i.ingredient_id
WHERE i.name = '우유';

