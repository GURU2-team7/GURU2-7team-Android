INSERT INTO Ingredients (name, category, expiry_date)
VALUES
    ('달걀', '유제품 및 계란', '2025-01-25'),
    ('우유', '유제품 및 계란', '2025-02-10'),
    ('버터', '유제품 및 계란', '2025-03-01'),
    ('감자', '채소', '2025-02-15'),
    ('양파', '채소', '2025-02-20'),
    ('마늘', '채소', '2025-03-05'),
    ('당근', '채소', '2025-02-25'),
    ('고구마', '채소', '2025-03-10'),
    ('파', '채소', '2025-02-18'),
    ('토마토', '채소', '2025-02-22'),
    ('소고기', '육류', '2025-01-30'),
    ('돼지고기', '육류', '2025-01-28'),
    ('닭고기', '육류', '2025-01-27'),
    ('대구', '수산물', '2025-01-29'),
    ('고등어', '수산물', '2025-01-26'),
    ('김치', '발효식품', '2025-03-31'),
    ('두부', '대두 제품', '2025-01-25'),
    ('된장', '발효식품', '2025-04-15'),
    ('고추장', '발효식품', '2025-05-10'),
    ('설탕', '조미료', '2026-01-01'),
    ('밀가루', '곡류 및 가공품', '2025-06-30'),
    ('땅콩', '견과류', '2025-04-15'),
    ('새우', '수산물', '2025-02-10'),
    ('스파게티 면', '곡류 및 가공품', '2025-06-30'),
    ('올리브 오일', '조미료', '2026-01-01'),
    ('토마토 소스', '소스 및 양념', '2025-12-01'),
    ('파르메산 치즈', '유제품', '2025-05-15'),
    ('스테이크용 소고기', '육류', '2025-01-30'),
    ('로즈마리', '향신료', '2025-12-31'),
    ('피자 도우', '곡류 및 가공품', '2025-04-15'),
    ('모짜렐라 치즈', '유제품', '2025-06-10'),
    ('페퍼로니', '육류', '2025-03-20'),
    ('타코 쉘', '곡류 및 가공품', '2025-05-01'),
    ('다진 소고기', '육류', '2025-02-15'),
    ('살사 소스', '소스 및 양념', '2025-07-01'),
    ('아보카도', '과일', '2025-01-28'),
    ('크로와상 반죽', '곡류 및 가공품', '2025-03-30'),
    ('크림 치즈', '유제품', '2025-05-05'),
    ('잼', '소스 및 양념', '2026-01-01'),
    ('밥', '곡류 및 가공품', '2025-02-15'), 
    ('소금', '조미료', '2026-01-01'); 



INSERT INTO Allergies (name)
VALUES
    ('땅콩 알러지'),
    ('갑각류 알러지'),
    ('우유 알러지'),
    ('글루텐 알러지'),
    ('계란 알러지');


INSERT INTO IngredientAllergies (ingredient_id, allergy_id)
VALUES
    ((SELECT ingredient_id FROM Ingredients WHERE name = '밀가루'),
     (SELECT allergy_id FROM Allergies WHERE name = '글루텐 알러지')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '땅콩'),
     (SELECT allergy_id FROM Allergies WHERE name = '땅콩 알러지')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '새우'),
     (SELECT allergy_id FROM Allergies WHERE name = '갑각류 알러지')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '우유'),
     (SELECT allergy_id FROM Allergies WHERE name = '우유 알러지')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '달걀'),
     (SELECT allergy_id FROM Allergies WHERE name = '계란 알러지'));


INSERT INTO Recipes (name, instructions)
VALUES
    ('김치볶음밥', '1. 프라이팬에 기름을 두르고 김치와 밥을 볶습니다. 2. 간장을 약간 넣고 섞어줍니다.'),
    ('감자튀김', '1. 감자를 얇게 썰어 소금물에 담가둡니다. 2. 기름에 튀긴 후 소금을 뿌립니다.'),
    ('계란말이', '1. 달걀을 풀고 소금으로 간합니다. 2. 프라이팬에 얇게 펴서 돌돌 말아줍니다.'),
    ('된장찌개', '1. 냄비에 된장과 물을 넣고 끓입니다. 2. 양파, 두부, 감자를 넣고 조리합니다.'),
    ('고등어구이', '1. 고등어를 손질하고 소금을 뿌립니다. 2. 프라이팬에 노릇노릇하게 굽습니다.'),
    ('스파게티', '1. 스파게티 면을 삶습니다. 2. 올리브 오일을 두르고 토마토 소스와 섞어줍니다. 3. 파르메산 치즈를 뿌립니다.'),
    ('스테이크', '1. 소고기에 소금과 후추를 뿌립니다. 2. 팬에 버터와 로즈마리를 넣고 고기를 굽습니다.'),
    ('피자', '1. 피자 도우 위에 토마토 소스를 펴 바릅니다. 2. 모짜렐라 치즈와 페퍼로니를 올려 오븐에서 굽습니다.'),
    ('타코', '1. 다진 소고기를 볶습니다. 2. 타코 쉘에 고기, 살사 소스, 아보카도를 넣습니다.'),
    ('크로와상', '1. 크로와상 반죽을 오븐에서 구워줍니다. 2. 크림 치즈나 잼을 곁들여 먹습니다.');


INSERT INTO RecipeIngredients (recipe_id, ingredient_id, quantity)
VALUES
    -- 김치볶음밥
    ((SELECT recipe_id FROM Recipes WHERE name = '김치볶음밥'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '김치'), '100g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '김치볶음밥'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '밥'), '1공기'),
    ((SELECT recipe_id FROM Recipes WHERE name = '김치볶음밥'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '양파'), '50g'),

    -- 감자튀김
    ((SELECT recipe_id FROM Recipes WHERE name = '감자튀김'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '감자'), '200g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '감자튀김'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '소금'), '10g'),

    -- 계란말이
    ((SELECT recipe_id FROM Recipes WHERE name = '계란말이'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '달걀'), '3개'),
    ((SELECT recipe_id FROM Recipes WHERE name = '계란말이'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '소금'), '5g'),

    -- 된장찌개
    ((SELECT recipe_id FROM Recipes WHERE name = '된장찌개'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '된장'), '50g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '된장찌개'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '두부'), '100g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '된장찌개'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '양파'), '50g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '된장찌개'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '감자'), '100g'),

    -- 고등어구이
    ((SELECT recipe_id FROM Recipes WHERE name = '고등어구이'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '고등어'), '1마리'),
    ((SELECT recipe_id FROM Recipes WHERE name = '고등어구이'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '소금'), '10g'),

    -- 스파게티
    ((SELECT recipe_id FROM Recipes WHERE name = '스파게티'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '스파게티 면'), '200g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '스파게티'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '올리브 오일'), '2큰술'),
    ((SELECT recipe_id FROM Recipes WHERE name = '스파게티'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '토마토 소스'), '150g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '스파게티'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '파르메산 치즈'), '30g'),

    -- 스테이크
    ((SELECT recipe_id FROM Recipes WHERE name = '스테이크'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '스테이크용 소고기'), '300g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '스테이크'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '버터'), '20g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '스테이크'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '로즈마리'), '2줄기'),

    -- 피자
    ((SELECT recipe_id FROM Recipes WHERE name = '피자'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '피자 도우'), '1개'),
    ((SELECT recipe_id FROM Recipes WHERE name = '피자'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '토마토 소스'), '100g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '피자'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '모짜렐라 치즈'), '100g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '피자'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '페퍼로니'), '50g'),

    -- 타코
    ((SELECT recipe_id FROM Recipes WHERE name = '타코'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '타코 쉘'), '2개'),
    ((SELECT recipe_id FROM Recipes WHERE name = '타코'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '다진 소고기'), '100g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '타코'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '살사 소스'), '50g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '타코'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '아보카도'), '1개'),

    -- 크로와상
    ((SELECT recipe_id FROM Recipes WHERE name = '크로와상'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '크로와상 반죽'), '1개'),
    ((SELECT recipe_id FROM Recipes WHERE name = '크로와상'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '크림 치즈'), '50g'),
    ((SELECT recipe_id FROM Recipes WHERE name = '크로와상'),
     (SELECT ingredient_id FROM Ingredients WHERE name = '잼'), '30g');


INSERT INTO Fridge (ingredient_id, quantity, added_date)
VALUES
    ((SELECT ingredient_id FROM Ingredients WHERE name = '달걀'), '10개', DATE('2025-01-15')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '우유'), '1리터', DATE('2025-01-14')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '양파'), '5개', DATE('2025-01-13')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '돼지고기'), '500g', DATE('2025-01-14')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '두부'), '2모', DATE('2025-01-13')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '새우'), '300g', DATE('2025-01-15')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '크림 치즈'), '200g', DATE('2025-01-12')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '잼'), '150g', DATE('2025-01-10')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '땅콩'), '100g', DATE('2025-01-11')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '모짜렐라 치즈'), '250g', DATE('2025-01-12')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '토마토 소스'), '1병', DATE('2025-01-14')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '밀가루'), '1kg', DATE('2025-01-12')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '고추장'), '500g', DATE('2025-01-11')),
    ((SELECT ingredient_id FROM Ingredients WHERE name = '설탕'), '1kg', DATE('2025-01-10'));

