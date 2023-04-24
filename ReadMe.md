

Index pour s'assurer que le nom de la recette est unique:

CREATE CONSTRAINT unique_name_recette
FOR (r:Recipe) REQUIRE r.name IS UNIQUE

Index pour s'assurer que le nom de l'ingredient est unique:

CREATE CONSTRAINT unique_name_ingredient
FOR (i:Ingredient) REQUIRE i.name IS UNIQUE