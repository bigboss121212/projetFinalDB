package ca.qc.cvm.dba.recettes.entity;

import java.util.List;

public class Recipe {
	private String id;
	private String name;
	private int portion;
	private int prepTime;
	private int cookTime;
	private List<String> steps;
	private List<Ingredient> ingredients;
	private byte[] imageData;
	
	public Recipe() {}
	
	public Recipe(String id, String name, int prepTime, int cookTime, int portion, List<String> steps, List<Ingredient> ingredients, byte[] img) {
		this.id = id;
		this.name = name;
		this.steps = steps;
		this.imageData = img;
		this.ingredients = ingredients;
		this.portion = portion;
		this.cookTime = cookTime;
		this.prepTime = prepTime;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSteps() {
		return steps;
	}

	public void setSteps(List<String> steps) {
		this.steps = steps;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	
	public int getPortion() {
		return portion;
	}

	public void setPortion(int portion) {
		this.portion = portion;
	}
	
	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}

	public int getCookTime() {
		return cookTime;
	}

	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}

	public String toString() {
		return String.format("%-20s%-20s%-20s", name, portion + " portions", ingredients.size() + " ingrédients", steps.size() + " étapes");
	}
}
