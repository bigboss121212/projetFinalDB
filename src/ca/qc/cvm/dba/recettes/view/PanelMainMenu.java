package ca.qc.cvm.dba.recettes.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import ca.qc.cvm.dba.correctionserver.lib.CorrectionDialog;
import ca.qc.cvm.dba.recettes.app.Facade;
import ca.qc.cvm.dba.recettes.event.CorrectionEvent;
import ca.qc.cvm.dba.recettes.event.DeleteAllEvent;
import ca.qc.cvm.dba.recettes.event.GoToEvent;
import ca.qc.cvm.dba.recettes.view.util.BackgroundPanel;

public class PanelMainMenu extends CommonPanel {
	private static final long serialVersionUID = 1L;

	public PanelMainMenu(int width, int height) throws Exception {
		super(width, height, false, "assets/images/background-main.jpg");
	}
	
	@Override
	public void jbInit() throws Exception {
		
		this.addButton("Les recettes", 20, 20, 150, 40, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Facade.getInstance().processEvent(new GoToEvent(FrameMain.Views.List));
			}
		});
		
		this.addButton("Statistiques", 20, 100, 150, 40, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Facade.getInstance().processEvent(new GoToEvent(FrameMain.Views.Stats));
			}
		});
		
		this.addButton("Quitter", 20, 180, 150, 40, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(PanelMainMenu.this, "Voulez-vous vraiment quitter?");
				
				if (result == JOptionPane.OK_OPTION) {
					Facade.getInstance().exit();
				}
			}
		});

		
		this.addButton("Tout supprimer", 20, 620, 150, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(PanelMainMenu.this, "Voulez-vous vraiment supprimer toute la base de données?");
				
				if (result == JOptionPane.OK_OPTION) {
					Facade.getInstance().processEvent(new DeleteAllEvent());
				}
			}
		});

		
		this.addButton("Correction", 20, 650, 150, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String[] data = CorrectionDialog.getData(null);
				
				if (data != null) {
					Facade.getInstance().processEvent(new CorrectionEvent(data[0], data[1], data[2]));
				}
			}
		});
	}
	
	/**
	 * Cette méthode est appelée automatiquement à chaque fois qu'un panel est affiché (lorsqu'on arrive sur la page)
	 */
	@Override
	public void resetUI() {
	}

}
