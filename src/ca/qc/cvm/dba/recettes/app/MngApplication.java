package ca.qc.cvm.dba.recettes.app;

import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import ca.qc.cvm.dba.recettes.dao.BerkeleyConnection;
import ca.qc.cvm.dba.recettes.dao.MongoConnection;
import ca.qc.cvm.dba.recettes.dao.RecipeDAO;
import ca.qc.cvm.dba.recettes.entity.Recipe;
import ca.qc.cvm.dba.recettes.event.SaveEvent;
import ca.qc.cvm.dba.recettes.event.CommonEvent;
import ca.qc.cvm.dba.recettes.event.CorrectionEvent;
import ca.qc.cvm.dba.recettes.event.DeleteEvent;
import ca.qc.cvm.dba.recettes.event.GoToEvent;
import ca.qc.cvm.dba.recettes.event.UIEvent;
import ca.qc.cvm.dba.recettes.view.FrameMain.Views;

public class MngApplication implements Runnable {
    private List<CommonEvent> eventQueue;

    private PropertyChangeSupport support;
    private Recipe currentRecipe;
    
    public MngApplication() {
    	eventQueue = new ArrayList<CommonEvent>();
    	support = new PropertyChangeSupport(this);
    	currentRecipe = null;
    }
    
    public void addEvent(CommonEvent event) {
    	synchronized(MngApplication.class) {
    		eventQueue.add(event);
    	}
    }

	public void run() {
        synchronized (MngApplication.class) {
            CommonEvent event = eventQueue.remove(0);
            processEvent(event);
        }
    }
	
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
	
	/**
	 * Méthode appelée par la Facade. Elle permet de traiter l'événement.
	 * 
	 * @param event
	 */
	private void processEvent(CommonEvent event) {
		if (event.getType() == CommonEvent.Type.GoTo) {
			currentRecipe = ((GoToEvent)event).getRecipe();
			support.firePropertyChange(UIEvent.UIType.GoTo.toString(), null, ((GoToEvent)event).getDestination().toString());
		}
		else if (event.getType() == CommonEvent.Type.Back) {
			support.firePropertyChange( UIEvent.UIType.Back.toString(), null, "back");
		}
		else if (event.getType() == CommonEvent.Type.Save) {
			save(((SaveEvent)event).getPerson());
		}
		else if (event.getType() == CommonEvent.Type.Delete) {
			delete(((DeleteEvent)event).getPerson());
		}
		else if (event.getType() == CommonEvent.Type.DeleteAll) {
			deleteAll();
		}
		else if (event.getType() == CommonEvent.Type.Correction) {
			CorrectionEvent evt = (CorrectionEvent)event;
			CorClient c = new CorClient();
			c.start(evt.getIp(), evt.getName(), evt.getPwd());
		}
		else {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "�v�nement inconnu...");
		}
	}
	
	public Recipe getCurrentRecipe() {
		return this.currentRecipe;
	}
	
	private void delete(Recipe recipe) {
		boolean success = false;
		
		success = RecipeDAO.delete(recipe);
		
		if (success) {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Recette supprimée");
			support.firePropertyChange( UIEvent.UIType.Refresh.toString(), null, "Refresh");
		}
		else {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Désolé, il semble y avoir eu une erreur lors de la suppression");
		}
	}
	
	private void deleteAll() {
		if (RecipeDAO.deleteAll()) {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Base de données supprimées");
		}
		else {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Une erreur est survenue lors de la suppression totale");
		}
	}
	
	private void save(Recipe recipe) {
		boolean success = false;
		
		success = RecipeDAO.save(recipe);
		
		if (success) {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Sauvegarde effectuée");
			support.firePropertyChange(UIEvent.UIType.GoTo.toString(), null, Views.List.toString());
		}
		else {
			support.firePropertyChange(UIEvent.UIType.ShowMessage.toString(), null, "Désolé, veuillez vérifier vos données");
		}
	}
	
	public List<Recipe> getRecipeList(String filter, int limit) {
		List<Recipe> recipeList = RecipeDAO.getRecipeList(filter, limit);
				
		return recipeList;
	}
	
	public long getMaxRecipeTime() {
		return RecipeDAO.getMaxRecipeTime();
	}
	
	public long getPhotoCount() {
		return RecipeDAO.getPhotoCount();
	}
	
	public long getRecipeCount() {
		return RecipeDAO.getRecipeCount();
	}
	
	public Recipe getLastAddedRecipe() {
		return RecipeDAO.getLastAddedRecipe();
	}
	
	public String getProposedIngredient(String recipeId) {
		return RecipeDAO.getProposedIngredient(recipeId);
	}
		
	public double getAverageNumberOfIngredients() {
		return RecipeDAO.getAverageNumberOfIngredients();
	}

	public Recipe getMostIngredientsRecipe() {
		return RecipeDAO.getMostIngredientsRecipe();
	}
	
	public Recipe generateRandomRecipe() {
		return RecipeDAO.generateRandomRecipe();
	}
	
	public void exit() {
		MongoConnection.releaseConnection();
		BerkeleyConnection.releaseConnection();
		System.exit(0);
	}
}
