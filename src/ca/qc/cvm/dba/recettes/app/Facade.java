package ca.qc.cvm.dba.recettes.app;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Observer;

import ca.qc.cvm.dba.recettes.entity.Recipe;
import ca.qc.cvm.dba.recettes.event.CommonEvent;

/**
 * Cette classe est l'interm�diaire entre la logique et la vue
 * Entre les panel et le MngApplication. C'est le point d'entr�e de la vue
 * vers la logique
 */
public class Facade {
	private static Facade instance;
	
	private MngApplication app;
	
	private Facade() {
		app = new MngApplication();
	}
	
	public static Facade getInstance() {
		if (instance == null) {
			instance = new Facade();
		}
		
		return instance;
	}
	
	public void processEvent(CommonEvent event) {
		app.addEvent(event);
        new Thread(app).start();
	}
	
	public void addObserverClass( PropertyChangeListener pcl) {
		app.addPropertyChangeListener(pcl);
	}
	
	public Recipe getCurrentRecipe() {
		return app.getCurrentRecipe();
	}
	
	public String getProposedIngredient(String recipeId) {
		return app.getProposedIngredient(recipeId);
	}
	
	public List<Recipe> getRecipeList(String filter, int limit) {
		return app.getRecipeList(filter, limit);
	}
		
	public long getMaxRecipeTime() {
		return app.getMaxRecipeTime();
	}
	
	public long getPhotoCount() {
		return app.getPhotoCount();
	}
	
	public long getRecipeCount() {
		return app.getRecipeCount();
	}
	
	public Recipe getLastAddedRecipe() {
		return app.getLastAddedRecipe();
	}
	
	public Recipe generateRandomRecipe() {
		return app.generateRandomRecipe();
	}
	
	public double getAverageNumberOfIngredients() {
		return app.getAverageNumberOfIngredients();
	}

	public Recipe getMostIngredientsRecipe() {
		return app.getMostIngredientsRecipe();
	}
		
	public void exit() {
		app.exit();
	}
}
