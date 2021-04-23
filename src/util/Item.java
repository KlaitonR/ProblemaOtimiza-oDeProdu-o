package util;

public class Item {
	
	public int id;
	public double custoItem;
	public double lucroItem;
	public double horasProducao;
	public double materiaItem;
	public int numProducao;
	
	public Item (double custo, double horasProducao, double lucro, double materia, int id) {
		this.id = id;
		this.custoItem = custo;
		this.horasProducao = horasProducao;
		this.lucroItem = lucro;
		this.materiaItem = materia;
	}

}
