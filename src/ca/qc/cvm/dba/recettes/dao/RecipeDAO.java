package ca.qc.cvm.dba.recettes.dao;

import java.nio.charset.StandardCharsets;
import java.util.*;

import ca.qc.cvm.dba.recettes.entity.Ingredient;
import ca.qc.cvm.dba.recettes.entity.Recipe;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.StatementResult;

public class RecipeDAO {

	/**
	 * Méthode permettant de sauvegarder une recette
	 * 
	 * Notes importantes:
	 * - Si le champ "id" n'est pas null, alors c'est une mise à jour, autrement c'est une insertion
	 * - Le nom de la recette doit être unique
	 * - Regarder comment est fait la classe Recette et Ingredient pour avoir une idée des données à sauvegarder
	 *
	 * @param recipe recette à sauvegarder
	 * @return true si succès, false sinon
	 */
	public static boolean save(Recipe recipe) {
		boolean success = false;

		Database connection = BerkeleyConnection.getConnection();

		if (recipe.getId() != null){
			try {

				DatabaseEntry theKey = new DatabaseEntry(recipe.getId().getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry(recipe.getImageData());
				connection.put(null, theKey, theData);

				Session session = Neo4jConnection.getConnection();
				HashMap<String, Object> params = new HashMap<>();

				createMissingIngredient(recipe.getIngredients(), session);

				params.put("id", recipe.getId());
				params.put("name", recipe.getName());
				params.put("portion", recipe.getPortion());
				params.put("prepTime", recipe.getPrepTime());
				params.put("cookTime", recipe.getCookTime());
				params.put("steps", recipe.getSteps());

				session.run("""
					MATCH (a:Recipe {
						id: $id
					})
					SET a = {
						name: $name,
						name_lower: toLower($name),
						portion: $portion,
						prepTime: $prepTime,
						cookTime: $cookTime,
						steps: $steps
					}
					""", params);

				for (Ingredient ingredient: recipe.getIngredients()) {
					params = new HashMap<>();

					params.put("nameI", ingredient.getName());
					params.put("nameR", recipe.getName());
					params.put("quantity", ingredient.getQuantity());

					session.run("""
					MATCH (r:Recipe {
						name: $nameR
					})
					MATCH (i:Ingredient {
						name: $nameI
					})
					CREATE (r)-[:Contient{quantity: $quantity}]->(i)
					""", params);
				}

				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			try {

				Session session = Neo4jConnection.getConnection();
				HashMap<String, Object> params = new HashMap<>();

				createMissingIngredient(recipe.getIngredients(), session);

				params.put("name", recipe.getName());
				params.put("portion", recipe.getPortion());
				params.put("prepTime", recipe.getPrepTime());
				params.put("cookTime", recipe.getCookTime());
				params.put("steps", recipe.getSteps());

				StatementResult result = session.run("""
					CREATE (a:Recipe {
						name: $name,
						name_lower: toLower($name),
						portion: $portion,
						prepTime: $prepTime,
						cookTime: $cookTime,
						steps: $steps
					})
					RETURN elementId(a)
					""", params);

				String id = result.next().get(0).asString();

				DatabaseEntry theKey = new DatabaseEntry(id.getBytes("UTF-8"));
				DatabaseEntry theData = new DatabaseEntry(recipe.getImageData());
				connection.put(null, theKey, theData);

				for (Ingredient ingredient: recipe.getIngredients()) {
					params = new HashMap<>();

					params.put("nameI", ingredient.getName());
					params.put("nameR", recipe.getName());
					params.put("quantity", ingredient.getQuantity());

					session.run("""
					MATCH (r:Recipe {
						name: $nameR
					}),
					(i:Ingredient {
						name: $nameI
					})
					CREATE (r)-[:Contient {quantity: $quantity}]->(i)
					""", params);
				}

				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return success;
	}

	private static void createMissingIngredient(List<Ingredient> listIngredient, Session session){

		for (Ingredient ingredient: listIngredient) {

			HashMap<String, Object> params = new HashMap<>();
			params.put("name", ingredient.getName());

			session.run("""
					MERGE (a:Ingredient {
						name: $name
					})
					""", params);
		}
	}

	/**
	 * Méthode permettant de retourner la liste des recettes de la base de données.
	 * 
	 * Notes importantes:
	 * - N'oubliez pas de limiter les résultats en fonction du paramètre limit
	 * - La liste doit être triées en ordre croissant, selon le nom des recettes
	 * - Le champ filtre doit permettre de filtrer selon le préfixe du nom (insensible à la casse)
	 * - N'oubliez pas de mettre l'ID dans la recette
	 * - Il pourrait ne pas y avoir de filtre (champ filtre vide)
	 * 	 * 
	 * @param filter champ filtre, peut être vide ou null
	 * @param limit permet de restreindre les résultats
	 * @return la liste des recettes, selon le filtre si nécessaire
	 */
	public static List<Recipe> getRecipeList(String filter, int limit) {
		List<Recipe> recipeList = new ArrayList<Recipe>();

		Session session = Neo4jConnection.getConnection();
		HashMap<String, Object> param = new HashMap<>();


		param.put("filtre", filter);
		param.put("limit", limit);

		StatementResult result = session.run("""
    			MATCH (a:Recipe)
  				WHERE a.name_lower STARTS WITH toLower($filtre)
    			RETURN
					elementId(a) as id,
					a.name as name,
					a.portion as por, 
					a.prepTime as prep, 
					a.cookTime as cook,
					a.steps as steps
				LIMIT $limit""", param);
		int i = 1;
		System.out.print("limit=");
		System.out.println(limit);
		System.out.print("filter=\"");
		System.out.print(filter);
		System.out.println("\"");
		while (result.hasNext()){
			System.out.println(i);
			i++;
			Record record = result.next();
			String id = record.get("id").asString();
			String name = record.get("name").asString();
			int portion = record.get("por").asInt();
			int prepTime = record.get("prep").asInt();
			int cookTime = record.get("cook").asInt();
			Vector<String> steps = new Vector<>();

			List<Object> dammit_java =  record.get("steps").asList();
			for (Object step: dammit_java) {
				steps.add((String) step);
			}

			param.put("id", id);
			param.put("limit", limit);

			StatementResult result2 = session.run("""
    			MATCH (a:Recipe) -[b: Contient]- (c: Ingredient)
    			WHERE elementId(a) = $id
    			RETURN
					b.quantity as qu,
					c.name as name
				LIMIT $limit""", param);

			Vector<Ingredient> ingredients = new Vector<>();
			while (result2.hasNext()) {
				Record record2 = result2.next();
				String quantity = record2.get(0).asString();
				String ingredient_name = record2.get(1).asString();

				Ingredient ingredient = new Ingredient(quantity, ingredient_name);
				ingredients.add(ingredient);
			}

			Database c = BerkeleyConnection.getConnection();

			DatabaseEntry theKey = new DatabaseEntry(id.getBytes(StandardCharsets.UTF_8));
			DatabaseEntry theData = new DatabaseEntry();
			byte[] img = null;
			if (c.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				img = theData.getData();

			}
			recipeList.add(new Recipe(id, name, prepTime, cookTime, portion, steps, ingredients, img));
		}

		return recipeList;
	}

	/**
	 * Suppression d'une recette
	 * 
	 * @param recipe
	 * @return true si succès, false sinon
	 */
	public static boolean delete(Recipe recipe) {
		HashMap<String, Object> param = new HashMap<>();

		try{
			Session session = Neo4jConnection.getConnection();
			param.put("id", recipe.getId());

			session.run("""
    			MATCH (a:Recipe)
    			WHERE elementId(a) = $id
    			DELETE a
 				""", param);

			Database c = BerkeleyConnection.getConnection();
			DatabaseEntry theKey = new DatabaseEntry(recipe.getId().getBytes(StandardCharsets.UTF_8));
			c.delete(null, theKey);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Suppression totale de toutes les données du système!
	 * 
	 * @return true si succès, false sinon
	 */
	public static boolean deleteAll() {
		try {
			Session session = Neo4jConnection.getConnection();
			StatementResult result2 = session.run("MATCH RETURN count(*) as count");
			int total = result2.next().get("count").asInt();

			List<Recipe> recipeList = getRecipeList("", total);

			for (Recipe recette: recipeList) {
				delete(recette);
			}

			session.run("MATCH (a) detach delete a");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Permet de retourner le nombre d'ingrédients en moyenne dans une recette
	 * 
	 * @return le nombre moyen d'ingrédients
	 */
	public static double getAverageNumberOfIngredients() {
		double num = 0;

		try {
			Session session = Neo4jConnection.getConnection();
			StatementResult result2 = session.run("""
					MATCH (r:Recipe)-[c:Contient]-()
					WITH r, COUNT(c) AS nbRelations
					RETURN AVG(nbRelations) AS nombreMoyenRelations
					""");
			num = result2.next().get(0).asDouble();

		}
		catch (Exception e) {
			e.printStackTrace();

		}
		
		return num;
	}

	/**
	 * Permet d'obtenir la recette ayant le plus d'ingrédients
	 * (s'il y a deux recettes ayant le nombre maximum d'ingrédients, retourner la première)
	 *
	 * @return la recette ayant le plus d'ingrédients
	 */
	public static Recipe getMostIngredientsRecipe() {
		Recipe r = null;

		return r;
	}
	
	/**
	 * Permet d'obtenir le temps de la recette la plus longue à faire.
	 * 
	 * La recette la plus longue est calculée selon son temps de cuisson plus son temps de préparation
	 * 
	 * @return le temps maximal
	 */
	public static long getMaxRecipeTime() {
		long num = 0;
		
		return num;
	}
	
	/**
	 * Permet d'obtenir le nombre de photos dans la base de données BerkeleyDB
	 * 
	 * @return nombre de photos dans BerkeleyDB
	 */
	public static long getPhotoCount() {
		long num = 0;
		return num;
	}

	/**
	 * Permet d'obtenir le nombre de recettes dans votre base de données
	 * 
	 * @return nombre de recettes
	 */
	public static long getRecipeCount() {
		long num = 0;
		
		return num;
	}
	
	/**
	 * Permet d'obtenir la dernière recette ajoutée dans le système
	 * 
	 * @return la dernière recette
	 */
	public static Recipe getLastAddedRecipe() {
		Recipe recipe = null;
		
		return recipe;
	}
	
	/**
	 * Cette fonctionnalité permet de générer une recette en se basant sur celles existantes
	 * dans le système. Voici l'algorithme générale à utiliser :
	 * 
	 * 1- Allez chercher tous les ingrédients dans votre base de données
	 * 2- Construisez une liste aléatoire d'ingrédients selon les ingrédients obtenus à l'étape précédente
	 * 3- Créez une liste aléatoire de quelques étapes basée sur une liste prédéfinie(ex : "Mélangez tous les ingr�dients", "cuire au four 20 minutes", etc)
	 * 4- Faites un temps de cuisson, de préparation et de nombre de portions aléatoires entre
	 * 5- Copiez une image d'une autre recette
	 * 6- Construisez un nom en utilisant cette logique :
	 *    - un préfixe aléatoire parmi une liste prédéfinie (ex: ["Giblotte à", "Mélangé de", "Crastillon de"]
	 *    - un suffixe basé sur un des ingrédients de la recette (ex: "farine").
	 *    - Résultat fictif : Giblotte à farine
	 * 
	 * Laissez l'ID de le recette vide, et ne l'ajoutez pas dans la base de données.
	 * 
	 * @return une recette générée
	 */
	public static Recipe generateRandomRecipe() {
		Recipe r = new Recipe();
		
		return r;
	}
	
	/**
	 * Permet d'obtenir une proposition d'ingrédient à ajouter à la recette en cours de modification
	 * 
	 * - L'idée est de comparer la recette en cours avec une autre recette existante en fonction de leurs ingrédients.
	 * - Si une recette possède au moins 2 ingrédients identiques avec la recette en cours de modification,
	 *   alors retourner un autre ingrédient de cette recette
	 * - La recette en cours ne doit pas déjà avoir cet ingrédient
	 * 
	 * - Exemple :
	 *     Recette 1 - Pesto : Basilic + parmesan + huile d'olive
	 *     Recette 2 - Salade tomates au basilic : Tomates + Basilic + huile d'olive
	 *     
	 *     Le système pourrait proposer à Recette 2 d'y ajouter du parmesan, puisque la recette de Pesto et de salade
	 *     possèdent 2 mêmes ingrédients (basilic et huile d'olive)
	 * 
	 * @param recipeId id de la recette
	 * @return une proposition d'un ingrédient à ajouter à la recette
	 */
	public static String getProposedIngredient(String recipeId) {
		String proposedIngredient = "--";
		
		return proposedIngredient;
	}
}
