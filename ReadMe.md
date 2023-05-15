# list des index et des contrainte (index) unique

Index pour s'assurer que le nom de la recette est unique:

CREATE CONSTRAINT unique_name_recette
FOR (r:Recipe) REQUIRE r.name_lower IS UNIQUE

Index pour s'assurer que le nom de l'ingredient est unique:

CREATE CONSTRAINT unique_name_ingredient
FOR (i:Ingredient) REQUIRE i.name IS UNIQUE

CREATE INDEX recipe_totalTime FOR (r:Recipe)
ON (r.totalTime);


CREATE INDEX recipe_insert_time FOR (r:Recipe)
ON (r.time);