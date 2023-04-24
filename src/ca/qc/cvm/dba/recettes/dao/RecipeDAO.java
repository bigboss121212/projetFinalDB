package ca.qc.cvm.dba.recettes.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.qc.cvm.dba.recettes.entity.Ingredient;
import ca.qc.cvm.dba.recettes.entity.Recipe;
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

		if (recipe.getId() != null){
			try {
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
					CREATE r-[:Contien{$quantity}]->i
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

				session.run("""
					CREATE (a:Recipe {
						name: $name,
						portion: $portion,
						prepTime: $prepTime,
						cookTime: $cookTime,
						steps: $steps
					})
					
					""", params);



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
		
		return recipeList;
	}

	/**
	 * Suppression d'une recette
	 * 
	 * @param recipe
	 * @return true si succès, false sinon
	 */
	public static boolean delete(Recipe recipe) {
		boolean success = false;
				
		return success;
	}
	
	/**
	 * Suppression totale de toutes les données du système!
	 * 
	 * @return true si succès, false sinon
	 */
	public static boolean deleteAll() {
		boolean success = false;

		return success;
	}
	
	/**
	 * Permet de retourner le nombre d'ingrédients en moyenne dans une recette
	 * 
	 * @return le nombre moyen d'ingrédients
	 */
	public static double getAverageNumberOfIngredients() {
		double num = 0;
		
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
