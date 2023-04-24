package ca.qc.cvm.dba.recettes.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import ca.qc.cvm.dba.recettes.app.Facade;
import ca.qc.cvm.dba.recettes.dao.RecipeDAO;
import ca.qc.cvm.dba.recettes.entity.Ingredient;
import ca.qc.cvm.dba.recettes.entity.Recipe;
import ca.qc.cvm.dba.recettes.event.SaveEvent;
import ca.qc.cvm.dba.recettes.view.util.BackgroundPanel;

public class PanelRecipeSave extends CommonPanel {
	private static final long serialVersionUID = 1L;
	private JTextField nameField;
	private JSpinner prepTimeField;
	private JSpinner cookTimeField;
	private JSpinner portionCount;
	private ImageIcon photoImg;
	
	private List<Ingredient> ingredients;
	private JList<String> ingredientsField;
	private JButton deleteIngredientsBtn;

	private List<String> steps;
	private JList<String> stepsField;
	private JButton deleteStepsBtn;
	
	private Recipe currentRecipe;	
	private JLabel photoImage;
	
	private JTextField extraIngredientField;
	
	public PanelRecipeSave(int width, int height) throws Exception {
		super(width, height, true, "assets/images/background-recipe-add.jpg");
	}
	
	@Override
	public void jbInit() throws Exception {
		super.addLabel("Nom: ", 20, 20, 150, 30);
		nameField = new JTextField();
		super.addField(nameField, 200, 20, 300, 30);
		
		super.addLabel("Temps de prép.: ", 20, 70, 150, 30);
		prepTimeField = new JSpinner(new SpinnerNumberModel(20,5,180,1));
		super.addField(prepTimeField, 200, 70, 100, 30);

		super.addLabel("Temps de cuisson: ", 20, 120, 150, 30);
		cookTimeField = new JSpinner();	
		cookTimeField = new JSpinner(new SpinnerNumberModel(10,0,600,1));
		super.addField(cookTimeField, 200, 120, 100, 30);

		super.addLabel("Nb de portions: ", 20, 170, 150, 30);
		portionCount = new JSpinner();
		portionCount = new JSpinner(new SpinnerNumberModel(1,1,20,1));
		super.addField(portionCount, 200, 170, 100, 30);
		
		super.addLabel("Photo: ", 20, 220, 150, 30);
		photoImage = super.addLabel("", 550, 20, 300, 300);
		photoImage.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		super.addButton("Choisir", 200, 220, 100, 30, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
		        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        fileChooser.setFileFilter(new FileFilter(){

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.toString().endsWith(".jpg") || f.toString().endsWith(".png");
					}

					@Override
					public String getDescription() {
						return null;
					}
		        	
		        });
		        int resultat = fileChooser.showSaveDialog(PanelRecipeSave.this);

	            if (resultat != JFileChooser.CANCEL_OPTION) {
	            	try {
	            		BufferedImage inputImage = ImageIO.read(fileChooser.getSelectedFile());
	            		BufferedImage outputImage = new BufferedImage(300, 300, inputImage.getType());
		                Graphics2D g2d = outputImage.createGraphics();
		                g2d.drawImage(inputImage, 0, 0, 300, 300, null);
		                g2d.dispose();
		                
		                
		            	photoImg = new ImageIcon(outputImage);		            	
		            	photoImage.setIcon(photoImg);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	            }
			}
		});
		
		super.addLabel("Ingrédients: ", 20, 270, 150, 30);
		ingredientsField = new JList<String>();
		ingredientsField.setFont(bigFont);
		super.addField(new JScrollPane(ingredientsField), 200, 270, 300, 150);
		ingredientsField.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (ingredientsField.getSelectedIndex() < 0) {
					deleteIngredientsBtn.setEnabled(false);
				}
				else {
					deleteIngredientsBtn.setEnabled(true);
				}
			}
		});
		
		super.addButton("+ ingrédient", 200, 430, 100, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel qtyLabel = new JLabel("Quantité :");
				qtyLabel.setBounds(15, 5, 100, 20);
		        final JTextField qtyTxt = new JTextField();
		        qtyTxt.setBounds(105, 5, 100, 20);
		        
		        JLabel nameLabel = new JLabel("Nom :");
		        nameLabel.setBounds(15, 35, 100, 20);
		        final JTextField nameTxt = new JTextField();
		        nameTxt.setBounds(105, 35, 100, 20);
		        final JDialog diag = new JDialog();		
		        JButton btn = new JButton("Ajouter");
				btn.setBounds(85, 65, 100, 20);
		        
		        btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ingredients.add(new Ingredient(qtyTxt.getText(), nameTxt.getText()));						
						updateIngredientList(ingredientsField, ingredients);						
						diag.setVisible(false);
					}
		        });

		        Object[] options = new Object[] {};
		        JOptionPane jop = new JOptionPane("",
		                                        JOptionPane.QUESTION_MESSAGE,
		                                        JOptionPane.DEFAULT_OPTION,
		                                        null,options, null);
		        jop.setLayout(null);
		        jop.add(qtyLabel);	
		        jop.add(qtyTxt);
		        jop.add(nameLabel);
		        jop.add(nameTxt);		        
		        jop.add(btn);		        
 
		        diag.setLocationRelativeTo(PanelRecipeSave.this);
		        diag.getContentPane().add(jop);
		        diag.pack();
		        diag.setVisible(true);
			}			
		});
		
		deleteIngredientsBtn = super.addButton("Supprimer", 400, 430, 100, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ingredients.remove(ingredientsField.getSelectedIndex());				
				updateIngredientList(ingredientsField, ingredients);
			}			
		});		

		super.addLabel("étapes: ", 20, 470, 150, 30);
		stepsField = new JList<String>();
		stepsField.setFont(bigFont);
		super.addField(new JScrollPane(stepsField), 200, 470, 300, 130);
		stepsField.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (stepsField.getSelectedIndex() < 0) {
					deleteStepsBtn.setEnabled(false);
				}
				else {
					deleteStepsBtn.setEnabled(true);
				}
			}
		});
		
		super.addButton("+ étape", 200, 610, 100, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {				
				JButton btn = new JButton("Ajouter");
		        btn.setSize(new Dimension(100, 20));
		        final JTextField stepTxt = new JTextField();
		        final JDialog diag = new JDialog();
		        
		        btn.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						steps.add(stepTxt.getText());						
						updateList(stepsField, steps);						
						diag.setVisible(false);
					}
		        });

		        Object[] options = new Object[] {};
		        JOptionPane jop = new JOptionPane("",
		                                        JOptionPane.QUESTION_MESSAGE,
		                                        JOptionPane.DEFAULT_OPTION,
		                                        null,options, null);
		        jop.setLayout(new BorderLayout());
		        jop.add(stepTxt, BorderLayout.NORTH);		        
		        jop.add(btn, BorderLayout.SOUTH);		        
 
		        diag.setLocationRelativeTo(PanelRecipeSave.this);
		        diag.getContentPane().add(jop);
		        diag.pack();
		        diag.setVisible(true);
			}			
		});
		
		deleteStepsBtn = super.addButton("Supprimer", 400, 610, 100, 20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				steps.remove(stepsField.getSelectedValue());				
				updateList(stepsField, steps);
			}			
		});

		extraIngredientField = new JTextField();
		extraIngredientField.setEditable(false);
		super.addLabel("Ingrédient proposé", 550, 350, 150, 30);
		super.addField(extraIngredientField, 550, 380, 300, 30);
		
		super.addButton("Sauvegarder", 200, 650, 100, 30, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String errorMsg = "";
				
				if (nameField.getText().trim().length() == 0) {
					errorMsg += "\n - Le nom ne peut pas être vide";
				}
				
				if (photoImg == null) {
					errorMsg += "\n - La photo ne peut pas être vide";
				}
				
				if (steps.size() == 0) {
					errorMsg += "\n - Il doit y avoir au moins 1 êtape";
				}
				
				if (ingredients.size() == 0) {
					errorMsg += "\n - Il doit y avoir au moins 1 ingrédient";
				}
								
				if (errorMsg.length() == 0) {
					Recipe r = new Recipe();
					r.setId(currentRecipe != null ? currentRecipe.getId() : null);
					r.setName(nameField.getText());
					
					r.setPortion((int)portionCount.getValue());
					r.setPrepTime((int)prepTimeField.getValue());
					r.setCookTime((int)cookTimeField.getValue());
					r.setSteps(steps);
					r.setIngredients(ingredients);
					
					BufferedImage bi = new BufferedImage(photoImg.getIconWidth(),photoImg.getIconHeight(), BufferedImage.TYPE_INT_RGB);
					Graphics g = bi.createGraphics();
					photoImg.paintIcon(null, g, 0,0);
					g.dispose();
					
				    try {
				    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    	ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
				        ImageIO.write(bi, "png", ios);
				        ios.close();
				        r.setImageData(baos.toByteArray());
				    } catch (Exception ee) {
				    	ee.printStackTrace();
				    }
				    
					Facade.getInstance().processEvent(new SaveEvent(r));
				}
				else {
					JOptionPane.showMessageDialog(PanelRecipeSave.this, "Erreur : " + errorMsg);
				}
			}
		});
	}
	
	private void updateIngredientList(JList field, List<Ingredient> list) {		
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for (Ingredient c : list) {
			model.addElement(c.getQuantity() + " " + c.getName());
		}
		
		field.setModel(model);
	}
	
	private void updateList(JList field, List<String> list) {		
		DefaultListModel<String> model = new DefaultListModel<String>();
		
		for (String c : list) {
			model.addElement(c);
		}
		
		field.setModel(model);
	}
	
	/**
	 * Cette méthode est appelée automatiquement à chaque fois qu'un panel est affiché (lorsqu'on arrive sur la page).
	 * Elle peut donc servir à préparer l'interface graphique (vider les champs, remplir les combobox, etc)
	 */
	@Override
	public void resetUI() {
		extraIngredientField.setText("");
		steps = new ArrayList<String>();
		ingredients = new ArrayList<Ingredient>();
		deleteStepsBtn.setEnabled(false);
		deleteIngredientsBtn.setEnabled(false);
		
		currentRecipe = Facade.getInstance().getCurrentRecipe();
		
		if (currentRecipe != null) {
			if (currentRecipe.getId() != null) {
				extraIngredientField.setText(Facade.getInstance().getProposedIngredient(currentRecipe.getId()));
			}
			nameField.setText(currentRecipe.getName());
			prepTimeField.setValue(currentRecipe.getPrepTime());
			cookTimeField.setValue(currentRecipe.getCookTime());
			portionCount.setValue(currentRecipe.getPortion());
			
			if (currentRecipe.getImageData() != null) {
				photoImg = new ImageIcon(currentRecipe.getImageData());
				photoImage.setIcon(photoImg);
				photoImage.repaint();
			}
			else {
				photoImg = null;
				photoImage.setIcon(null);	
			}
			
			if (currentRecipe.getSteps() != null) {
				for (String c : currentRecipe.getSteps()) {
					steps.add(c);
				}
			}
			
			if (currentRecipe.getIngredients() != null) {
				for (Ingredient i : currentRecipe.getIngredients()) {
					ingredients.add(i);
				}
			}
		}
		else {
			nameField.setText("");
			prepTimeField.setValue(10);
			cookTimeField.setValue(0);
			portionCount.setValue(1);
			
			photoImg = null;
			photoImage.setIcon(null);
		}

		updateList(stepsField, steps);
		updateIngredientList(ingredientsField, ingredients);
	}

}
