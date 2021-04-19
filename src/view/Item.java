package view;

public class Item {
	
	int id;
	double custoItem;
	double lucroItem;
	double horasProducao;
	double materiaItem;
	int numProducao;
	
	public Item (double custo, double horasProducao, double lucro, double materia, int id) {
		this.id = id;
		this.custoItem = custo;
		this.horasProducao = horasProducao;
		this.lucroItem = lucro;
		this.materiaItem = materia;
	}

}
