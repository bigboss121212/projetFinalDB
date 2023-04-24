package ca.qc.cvm.dba.recettes.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ca.qc.cvm.dba.recettes.app.Facade;
import ca.qc.cvm.dba.recettes.entity.Recipe;
import ca.qc.cvm.dba.recettes.event.DeleteEvent;
import ca.qc.cvm.dba.recettes.event.GoToEvent;
import ca.qc.cvm.dba.recettes.view.util.BackgroundPanel;

public class PanelRecipes extends CommonPanel {
	private static final long serialVersionUID = 1L;

	private JTextField searchField;
	private JList<String> recipeList;
	private JButton editButton;
	private JButton recipeGenerator;
	private JButton deleteButton;
	
	private List<Recipe> recipes;

	public PanelRecipes(int width, int height) throws Exception {
		super(width, height, true, "assets/images/background-recipes.jpg");
	}
	
	@Override
	public void jbInit() throws Exception {				
		editButton = this.addButton("Modifier", 600, 150, 100, 20, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Facade.getInstance().processEvent(new GoToEvent(FrameMain.Views.Show, recipes.get(recipeList.getSelectedIndex())));
			}
		});
		
		deleteButton = this.addButton("Supprimer", 750, 150, 100, 20, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(PanelRecipes.this, "Supprimer l'entrée?") == JOptionPane.OK_OPTION) {
					Facade.getInstance().processEvent(new DeleteEvent(recipes.get(recipeList.getSelectedIndex())));
				}
			}
		});
		
		// Search button
		searchField = new JTextField();
		this.addField(searchField, 20, 150, 150, 25);
		searchField.setFont(new Font("Arial", 0, 14));
		this.addButton("Rechercher", 200, 150, 100, 25, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PanelRecipes.this.resetView();
			}
		});

		this.addButton("Ajouter une entrée", 20, 640, 150, 25, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Facade.getInstance().processEvent(new GoToEvent(FrameMain.Views.Show));
			}
		});

		recipeGenerator = this.addButton("Générer une recette", 350, 640, 150, 25, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Facade.getInstance().processEvent(new GoToEvent(FrameMain.Views.Show, Facade.getInstance().generateRandomRecipe()));
			}
		});

		recipeList = new JList<String>();
		recipeList.setFont(new Font("Courier New", 0, 18));
		recipeList.setOpaque(false);
		JScrollPane pane = new JScrollPane(recipeList);
		pane.setOpaque(false);
		pane.getViewport().setOpaque(false);
		super.addField(pane, 20, 190, 830, 440);
		recipeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (recipeList.getSelectedIndex() < 0) {
					deleteButton.setEnabled(false);
					editButton.setEnabled(false);
				}
				else {
					deleteButton.setEnabled(true);
					editButton.setEnabled(true);
				}
			}
		});
	}
	
	/**
	 * Cette méthode est appelée automatiquement à chaque fois qu'un panel est affiché (lorsqu'on arrive sur la page)
	 */
	@Override
	public void resetUI() {
		deleteButton.setEnabled(false);
		editButton.setEnabled(false);
		recipeList.removeAll();
		recipes = Facade.getInstance().getRecipeList(searchField.getText(), 50);
		recipeGenerator.setEnabled(recipes.size() > 0);

		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for (Recipe p : recipes) {
			model.addElement(p.toString());
		}
		
		recipeList.setModel(model);
	}

}
