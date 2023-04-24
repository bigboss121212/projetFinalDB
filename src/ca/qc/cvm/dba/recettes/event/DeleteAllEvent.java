package ca.qc.cvm.dba.recettes.event;

import ca.qc.cvm.dba.recettes.entity.Recipe;

/**
 * Événement utilisé lorsque l'on veut supprimer toute les données
 */
public class DeleteAllEvent extends CommonEvent {
	
	public DeleteAllEvent() {
		super(CommonEvent.Type.DeleteAll);
	}
}
