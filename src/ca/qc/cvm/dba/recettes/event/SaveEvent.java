package ca.qc.cvm.dba.recettes.event;

import ca.qc.cvm.dba.recettes.entity.Recipe;

/**
 * Événement utilisé lorsque l'on veut sauvegarder une nouvelle
 * entrée 
 */
public class SaveEvent extends CommonEvent {
	private Recipe person;
	
	public SaveEvent(Recipe person) {
		super(CommonEvent.Type.Save);
		
		this.person = person;
	}
	
	public Recipe getPerson() {
		return person;
	}
}
