package ca.qc.cvm.dba.recettes.event;

import ca.qc.cvm.dba.recettes.entity.Recipe;
import ca.qc.cvm.dba.recettes.view.FrameMain.Views;

/**
 * Événement pour passer d'un panel à un autre
 */
public class GoToEvent extends CommonEvent {
	private Views destination;
	private Recipe currentRecipe;
	
	public GoToEvent(Views destination) {
		super(CommonEvent.Type.GoTo);
		
		this.destination = destination;
	}
	
	public GoToEvent(Views destination, Recipe recipe) {
		super(CommonEvent.Type.GoTo);
		
		this.destination = destination;
		this.currentRecipe = recipe;
	}
	
	public Recipe getRecipe() {
		return currentRecipe;
	}
	
	public Views getDestination() {
		return destination;
	}
}
