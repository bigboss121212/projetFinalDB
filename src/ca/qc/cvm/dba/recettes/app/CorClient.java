package ca.qc.cvm.dba.recettes.app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import ca.qc.cvm.dba.correctionserver.lib.BaseCorClient;
import ca.qc.cvm.dba.recettes.dao.RecipeDAO;
import ca.qc.cvm.dba.recettes.entity.Ingredient;
import ca.qc.cvm.dba.recettes.entity.Recipe;

public class CorClient extends BaseCorClient  {
	
	public CorClient() {
	}
	
	@Override
	protected void executeTests(final List<String> res, List<String> info) {
		long count = -1;
		try {
			count = RecipeDAO.getRecipeCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb fiches : " + count);
		
		count = -1;
		try {
			count = RecipeDAO.getPhotoCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb photos : " + count);
		
		boolean s1 = false;
		try {
			s1 = RecipeDAO.deleteAll();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Suppression totale : " + s1);

		try {
			count = RecipeDAO.getRecipeCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb fiches (après supp. totale) : " + count);
		
		count = -1;
		try {
			count = RecipeDAO.getPhotoCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb photos (après supp. totale) : " + count);
		
		boolean isFirstLine = true;
		String f1 = "";
		String f2 = "";
		
		for (String line : info) {
			if (line.startsWith("RUN;")) {
				line = line.replace("RUN;", "");
				
				if (isFirstLine) {
					StringTokenizer tokenizer = new StringTokenizer(line, ":");
					f1 = tokenizer.nextElement().toString();
					f2 = tokenizer.nextElement().toString();
					isFirstLine = false;
				}
				else {
					StringTokenizer tokenizer = new StringTokenizer(line, ":");
					String p1 = tokenizer.nextElement().toString();
					int p2 = Integer.parseInt(tokenizer.nextElement().toString());
					int p3 = Integer.parseInt(tokenizer.nextElement().toString());
					int p4 = Integer.parseInt(tokenizer.nextElement().toString());
					String p5 = tokenizer.nextElement().toString();
					String p6 = tokenizer.nextElement().toString();
					String p7 = tokenizer.nextElement().toString();
	
					
					byte[] imageInByte = null;
					BufferedImage image;
					try {
						File f = new File(p5);
						image = ImageIO.read(f);
						ByteArrayOutputStream b =new ByteArrayOutputStream();
						ImageIO.write(image, "jpg", b );
						imageInByte = b.toByteArray();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					StringTokenizer tk = new StringTokenizer(p6, "-");
					List<String> s = new ArrayList<String>();
					while (tk.hasMoreElements())  {
						s.add(tk.nextElement().toString());
					}
					
					tk = new StringTokenizer(p7, "-");
					List<Ingredient> i = new ArrayList<Ingredient>();
					while (tk.hasMoreElements())  {
						i.add(new Ingredient(i.size() + "qty", tk.nextElement().toString()));
					}
					
					Recipe p = new Recipe(null, p1, p2, p3, p4, s, i, imageInByte);
					RecipeDAO.save(p);
				}
			}
		}
		
		count = -1;
		try {
			count = RecipeDAO.getRecipeCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb fiches (ap insertion) : " + count);
		
		count = -1;
		try {
			count = RecipeDAO.getPhotoCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb photos (ap insertion) : " + count);
		
		try {
			List<Recipe> p = RecipeDAO.getRecipeList("rH", 100);
			if (p.size() > 0) {
				RecipeDAO.delete(p.get(p.size() - 1));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		count = -1;
		try {
			count = RecipeDAO.getRecipeCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb fiches (ap suppression) : " + count);
		
		count = -1;
		try {
			count = RecipeDAO.getPhotoCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb photos (ap suppression) : " + count);		
		
		String l = "";
		try {
			List<Recipe> p = RecipeDAO.getRecipeList("", 3);
			
			for (Recipe person : p) {
				l += person.toString() + "\n";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("===== Fiches (limite) ======\n" + l + "-----------------------------");

		l = "";
		try {
			List<Recipe> p = RecipeDAO.getRecipeList("", 100);
			
			for (Recipe person : p) {
				l += person.toString() + "\n";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("===== Fiches (avant modification) ======\n" + l + "-----------------------------");
		
		l = "";
		try {
			List<Recipe> p = RecipeDAO.getRecipeList("", 100);
			if (p.size() > 0) {
				Recipe tmp = p.get(0);
				tmp.setName("rAAA");
				tmp.setPortion(5);
				tmp.setPrepTime(5);
				tmp.setCookTime(5);
				tmp.getIngredients().remove(0);
				RecipeDAO.save(tmp);
			}
			
			p = RecipeDAO.getRecipeList("", 200);
			
			for (Recipe person : p) {
				l += person.toString() + "\n";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("===== Fiches (après modification) ======\n" + l + "-----------------------------");
		
		l = "";
		try {
			List<Recipe> p = RecipeDAO.getRecipeList(f1, 100);
			
			for (Recipe recipe : p) {
				l += recipe.toString() + "\n";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("===== Fiches (" + f1 + ") ======\n" + l + "-----------------------------");
		
		l = "";
		try {
			List<Recipe> p = RecipeDAO.getRecipeList(f2, 100);
			
			for (Recipe recipe : p) {
				l += recipe.toString() + "\n";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("===== Fiches (" + f2 + ") ======\n" + l + "-----------------------------");
		
		count = -1;
		try {
			count = RecipeDAO.getMaxRecipeTime();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Recette temps Max : " + count);
		
		double db = -1;
		try {
			db = RecipeDAO.getAverageNumberOfIngredients();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Moyenne nb ingrédients : " + db);
		
		Recipe r = null;
		try {
			r = RecipeDAO.getLastAddedRecipe();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Dernière recette ajoutée: " + (r == null ? "-" : r.getName()));

		r = null;
		try {
			r = RecipeDAO.getMostIngredientsRecipe();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Recette ayant le plus d'ingrédients: " + (r == null ? "-" : r.getName()));

		if (r != null) {
			String ing = "--";
			try {
				ing = RecipeDAO.getProposedIngredient(r.getId());
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			res.add("Ingrédient proposé: " + ing);
		}
		
		r = null;
		try {
			r = RecipeDAO.generateRandomRecipe();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (r != null) {
			res.add("Générateur : ");
			res.add("- nom : " + r.getName());
			res.add("- preparation: " + r.getPrepTime());
			res.add("- cuisson : " + r.getCookTime());
			res.add("- portion :" + r.getPortion());

			for (Ingredient i : r.getIngredients()) {
				res.add("- ingrédient : " + i.getQuantity() + " " + i.getName());
			}
			
			for (String e : r.getSteps()) {
				res.add("- étape : " + e);
			}
		}
		else {
			res.add("Générateur : --");
		}
		
		count = -1;
		try {
			count = RecipeDAO.getRecipeCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb fiches : " + count);
		
		count = -1;
		try {
			count = RecipeDAO.getPhotoCount();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		res.add("Nb photos : " + count);
		
	}
}
