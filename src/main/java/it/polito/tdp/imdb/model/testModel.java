package it.polito.tdp.imdb.model;

public class testModel {

	public static void main(String[] args) {
		Model model=new Model();
		model.creaGrafo("Animation");
		System.out.println(model.getNVertici());
		System.out.println(model.getNArchi());

	}

}
