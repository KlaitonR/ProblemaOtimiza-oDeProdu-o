package util;

public class Individuo {

	public int cromossomos [];
	public double beneficio;
	public double lucroTotal;
	public double custoTotal;
	public double tempoTotal;
	public double materiaTotal;
	public double propSelecao;
	public boolean selecionado;
	public double numItemProd;
	
	public Individuo(int qtItens) {
		this.cromossomos = new int[qtItens];
	}
	
}
