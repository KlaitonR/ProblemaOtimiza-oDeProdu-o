package view;

import java.util.ArrayList;
import java.util.Random;

public class AlgoritmoGenetico {
	
	int nItens;
	int nPopulacao;
	ArrayList<Item> itens = new ArrayList<>();
	double penalidade;
	int nMutacoes;
	double limiteMateria;
	double limiteCusto;
	double limiteTempo;
	
	
	Populacao populacao = new Populacao();
	Random roleta = new Random();
	double fitnessTotal;
	double sorteio;
	int nSelecionados = 0;
	String faixa = "";
	
	ArrayList<Individuo> selecionados = new ArrayList<>();
	ArrayList<Individuo> novaGeracao = new ArrayList<>();
	ArrayList<Individuo> mutacoes = new ArrayList<>();
	
	public AlgoritmoGenetico(int nItens,int nPopulacao, ArrayList<Item> itens, 
			double limiteMateriaPrima, double limiteCusto, double limiteTemp, double penalidade, int percentual) {
		
		this.nItens = nItens;
		this.nPopulacao = nPopulacao;
		this.itens = itens;
		this.penalidade = penalidade - (penalidade*2);
		this.nMutacoes = percentual;
		this.limiteMateria = limiteMateriaPrima;
		this.limiteCusto = limiteCusto;
		this.limiteTempo = limiteTemp;
		
	}
	
	public void primeiraPopulacao() {
		
		int i=0;
		
		while(i < nPopulacao) { //Criando os Individuos
			Individuo individuo = new Individuo(nItens);
			for(int j=0; j<nItens; j++) { //Definindo os cromossomos
				individuo.cromossomos[j] = roleta.nextInt((int)(limiteMateria/itens.get(j).materiaItem) + 1);
			}
			
			if(confirmaIndividuo( individuo)){
				populacao.individuos.add(individuo); //Adicionando o individuo a populacao
				i++;
			}
		}
		
		System.out.println("Gerou primeira população");
		
	}
	
	public boolean confirmaIndividuo( Individuo ind) {
		
			for(int j=0; j<nItens; j++) { 
				ind.custoTotal += (int)(ind.cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).custoItem;
				ind.tempoTotal += (int)(ind.cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).horasProducao;
				ind.materiaTotal += ind.cromossomos[j];
			}
			
		
		if(ind.materiaTotal <= limiteMateria && ind.custoTotal <= limiteCusto && ind.tempoTotal <= limiteTempo) {
			
			int check = 0;
			
			for(int i=0; i<populacao.individuos.size(); i++) {
				check = 0;
				for(int j=0; j<nItens; j++) {
					if(ind.cromossomos[j] == populacao.individuos.get(i).cromossomos[j]) {
						check++;
					}
				}
				
				if(check == nItens)
					return false;
			}
			
		}else {
			return false;
		}
		
		return true;
		
	}
	
	public Individuo fitness(){
		
		for(int i=0; i<nPopulacao;i++) {
			for(int j=0; j<nItens; j++) { 
				populacao.individuos.get(i).lucroTotal += (int)(populacao.individuos.get(i).cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).lucroItem;
				populacao.individuos.get(i).beneficio += (int)(populacao.individuos.get(i).cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).lucroItem;
				populacao.individuos.get(i).custoTotal += (int)(populacao.individuos.get(i).cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).custoItem;
				populacao.individuos.get(i).tempoTotal += (int)(populacao.individuos.get(i).cromossomos[j]/itens.get(j).materiaItem) * itens.get(j).horasProducao;
				populacao.individuos.get(i).materiaTotal += populacao.individuos.get(i).cromossomos[j];
			}
			
			if(populacao.individuos.get(i).custoTotal > limiteCusto ||
					populacao.individuos.get(i).materiaTotal > limiteMateria ||
					populacao.individuos.get(i).tempoTotal > limiteTempo) { //aplica penalidade
				populacao.individuos.get(i).beneficio += penalidade;
			}
			
		}
		
		double menorBeneficio = 999999999;
		double maior = 0;
		fitnessTotal = 0;
		Individuo ind = null;
		
		for(int i=0; i<nPopulacao; i++) { // Verificando o menor beneficio
			if(populacao.individuos.get(i).beneficio < menorBeneficio) {
				menorBeneficio = populacao.individuos.get(i).beneficio;
			}
		}
		
		for(int i=0; i<nPopulacao; i++) { //Eliminando valores negativos
			populacao.individuos.get(i).beneficio -= menorBeneficio;
		}
		
		for(int i=0; i<nPopulacao; i++) {
			if(populacao.individuos.get(i).beneficio > maior) { //Verifica o maior fitness dessa geração
				maior = populacao.individuos.get(i).beneficio;
				ind = populacao.individuos.get(i);
			}
		}
		
		for(int i=0; i<nPopulacao; i++) {// Calculando Fitness Total
			fitnessTotal += populacao.individuos.get(i).beneficio;
		}
		
		for(int i=0; i<nPopulacao; i++) { //Calculando probabilidade de ser selecionado
			populacao.individuos.get(i).propSelecao = (populacao.individuos.get(i).beneficio/fitnessTotal) * 100;              
		}
		
		
		System.out.println("Calculou Fitness");
		
		return ind;
		
	}
	
	public void selecao() {
		
		nSelecionados = 0;
		
		Individuo individuo = null;
		
		while(nSelecionados < nPopulacao/2) {
			individuo = rodarRoleta();
			
			if(individuo != null) {
				selecionados.add(individuo);
				nSelecionados++;
				individuo = null;
			}
		}
		
		System.out.println("Selecionou");
		
	}
	
	public Individuo rodarRoleta() {
		
	sorteio = roleta.nextDouble()*100;
	int i = 0;
	double inicioDaFaixa = 0;
	double fimDaFaixa = 0;
	Individuo ind = null;
	
	faixa += "Número sorteado: " + sorteio + "\n \n";
		
		while(i < nPopulacao){ 
			
			if(populacao.individuos.get(i).propSelecao > 0) { //Se a porcentagem é 0, não tem espaço na roleta e apenas pula a vez
				
				fimDaFaixa += populacao.individuos.get(i).propSelecao; //Defina o epaço do individuo na roleta
				faixa += "Faixa do individuo " + (i+1)+ " - De " + inicioDaFaixa + " ";
				
				if(sorteio >= inicioDaFaixa && 
						sorteio < fimDaFaixa && 
						!populacao.individuos.get(i).selecionado) {
					populacao.individuos.get(i).selecionado = true;
					ind = populacao.individuos.get(i);
					faixa += "até " + fimDaFaixa + "\n";
					faixa += "\n Individuo selecionado: " + (i+1)+ "\n\n";
					return ind;
				}
				
				inicioDaFaixa += populacao.individuos.get(i).propSelecao; //Defina os epaços do individuo na roleta
				faixa += "até " + fimDaFaixa + "\n";
			}
			
			i++;
		}
		
		faixa += "\n";
		
		return null;
	}
	
	public String crossover() {
		
		int pontoDeCorte = 0;
		int sorteio1 = 0;
		int sorteio2 = 0;
		String result = "";
	
		while(selecionados.size() > 0) {
			
			pontoDeCorte = roleta.nextInt(nItens-1);
			sorteio1 = roleta.nextInt(selecionados.size());
			sorteio2 = roleta.nextInt(selecionados.size());
		
			while(!confirmaSorteio(sorteio1, sorteio2)) {
				sorteio1 = roleta.nextInt(selecionados.size());
				sorteio2 = roleta.nextInt(selecionados.size());
			}
			
			result += aplicaCrossover(sorteio1, sorteio2, pontoDeCorte);
		
		}
		
		populacao.individuos.clear();
		populacao.individuos.addAll(novaGeracao);
		selecionados.clear();
		novaGeracao.clear();
		
		System.out.println("Fez crossover");
		
		return result;
		
	}
	
	public boolean confirmaSorteio(int sorteio1, int sorteio2) {
		
		if((sorteio1 == sorteio2)) {
			return false;
		}else {
			return true;
		}
		
	}
	
	public String aplicaCrossover(int sorteio1, int sorteio2, int pontoDeCorte) {
		
		String cros = "\n----------- INDIVIDUOS SELECIONADOS PARA O CROSSOVER --------------\n";
		Individuo filho1 = new Individuo(nItens);
		Individuo filho2 = new Individuo(nItens);
		Individuo pai1 = new Individuo(nItens);
		Individuo pai2 = new Individuo(nItens);
		
		cros+= "\nPonto de Corte " + (pontoDeCorte+1)+"\n";
		
		cros+= "\nIndividuo 1: ";
		for(int i=0; i<nItens; i++) {
			cros +=  selecionados.get(sorteio1).cromossomos[i];
			if(i==pontoDeCorte)
				cros += "|";
		}
		
		cros+= "\nIndividuo 2: ";
		for(int i=0; i<nItens; i++) {
			cros +=  selecionados.get(sorteio2).cromossomos[i];
			if(i==pontoDeCorte)
				cros += "|";
		}
		
		cros+="\n";
		
		cros+= "\n--- APLICANDO CROSSOVER ---\n";
		
		//Faz o crossover
		for(int i=0; i<nItens; i++) {
			
			if(i <= pontoDeCorte) {
				filho1.cromossomos[i] = selecionados.get(sorteio1).cromossomos[i];
				filho2.cromossomos[i] = selecionados.get(sorteio2).cromossomos[i];
			}else if(i>pontoDeCorte) {
				filho1.cromossomos[i] = selecionados.get(sorteio2).cromossomos[i];
				filho2.cromossomos[i] = selecionados.get(sorteio1).cromossomos[i];
			}
			
		}
		
		//Verifica se os filhos são iguais os pais
		//Casso seja, inverte a outra parte dos genes
		if(!confirmaCrossover(filho1, selecionados.get(sorteio1))) { 
			
			for(int i=0; i<nItens; i++) {
				
				if(i <= pontoDeCorte) {
					filho1.cromossomos[i] = selecionados.get(sorteio2).cromossomos[i];
					filho2.cromossomos[i] = selecionados.get(sorteio1).cromossomos[i];
				}else if(i>pontoDeCorte) {
					filho1.cromossomos[i] = selecionados.get(sorteio1).cromossomos[i];
					filho2.cromossomos[i] = selecionados.get(sorteio2).cromossomos[i];
				}
				
			}
		}
		
		//String para resultado
		cros+= "\nFilho 1: ";
		for(int i=0; i<nItens; i++) {
			cros +=  filho1.cromossomos[i];
			if(i==pontoDeCorte)
				cros += "|";
		}
	
		cros+= "\nFilho 2: ";
		for(int i=0; i<nItens; i++) {
			cros +=  filho2.cromossomos[i];
			if(i==pontoDeCorte)
				cros += "|";
		}
		
		cros+="\n";
		
		pai1.cromossomos = selecionados.get(sorteio1).cromossomos;
		pai2.cromossomos = selecionados.get(sorteio2).cromossomos;
		novaGeracao.add(pai1);
		novaGeracao.add(pai2);
		
		selecionados.remove(sorteio1); //Quando remove, diminuirá uma posição do index
		
		if(sorteio1 < sorteio2) { //Ajustando index caso o individuo sorteado1 for de index menor que o sorteado2 na lista
			sorteio2--;
		}
		
		selecionados.remove(sorteio2);
		
		cros += "\nNúmero de indv ainda restantes para aplicar cross: " + selecionados.size()+"\n";
		
		novaGeracao.add(filho1);
		novaGeracao.add(filho2);
		
		cros += "\nNúmero de indv da nova geração: " + novaGeracao.size()+"\n";
		
		return "\n" + cros + "\n";
		
	}
	
	public boolean confirmaCrossover(Individuo f, Individuo p) {
		
		int check = 0;
		
		for(int i=0; i<nItens; i++) {
			if(f.cromossomos[i] == p.cromossomos[i]) {
				check++;
			}
		}
		
		if(check == nItens)
			return false;
		else {
			return true;
		}
		
	}
	
	public String mutacao() {
		
		String mut = "\n----------- APLICA MUTAÇÃO ------------\n";
		int geneDeMutacao = 0;
		int indexMutacao = 0;
		int cont = 0;
		
		while(cont < nMutacoes) { //Enquanto o contador não for igual ao de individuos que sofrerá mutação
			
			geneDeMutacao = roleta.nextInt(nItens);
			indexMutacao = roleta.nextInt(populacao.individuos.size());
			
			String antesDaMutacao = "\nIndividuo: ";
			
			for(int i=0; i<nItens; i++) { // String para guardar resultado
				antesDaMutacao += populacao.individuos.get(indexMutacao).cromossomos[i];
			}
			
			int valorMutacao = roleta.nextInt(401);
			
			while (valorMutacao == populacao.individuos.get(indexMutacao).cromossomos[geneDeMutacao]) {
				valorMutacao = roleta.nextInt(401);
			}
		
			populacao.individuos.get(indexMutacao).cromossomos[geneDeMutacao] = valorMutacao;
			
			mutacoes.add(populacao.individuos.get(indexMutacao)); //passa para a lista de individuos já mudificados
			System.out.println("\nMutação no individuo " + (indexMutacao+1) + " no gene " + (geneDeMutacao+1));
			
			String aposMutacao = "\nIndividio após mutação: ";
			
			for(int i=0; i<nItens; i++) { // String para guardar resultado
				 
				if(geneDeMutacao == i) { 
					aposMutacao += "|"+populacao.individuos.get(indexMutacao).cromossomos[i]+"|";
				}else {
					aposMutacao += populacao.individuos.get(indexMutacao).cromossomos[i];
				}
			}
			
			mut += antesDaMutacao + "\n";
			mut += aposMutacao+"\n";
			
			cont++;
			
			populacao.individuos.remove(indexMutacao); //Remove da população o individuo selecionado para a mutação
			
		}
		
		populacao.individuos.addAll(mutacoes); //Ao final, adiciona novamente à população todos os individuos que sofreram mutação
		mutacoes.clear(); // limpa a lista de individuos que foram modificados
		
		
		System.out.println("Aplicou mutação");
		
		return mut;
	}
	
	public String mostraResultado(ArrayList<Item> itens, int nItens) {
		
		
		String selecionados = "";
		String Cromossomo = "";
		selecionados = "\nIndividuos Selecionados: ";
		String result = "";
		
		result += "/n";
		
		for(int i=0; i<nItens;i++) {
			result += "Item " + (i+1) + " - Custo: "+ itens.get(i).custoItem + "  Lucro: " + itens.get(i).lucroItem; 
		}
		
		result += "\n------ POPULAÇÃO ------\n";
		
		for(int i=0; i<nPopulacao; i++) { 
			
			result += "\nIndividuo " + (i+1); 
			Cromossomo = "";
			if(populacao.individuos.get(i).selecionado)
				selecionados += (i+1) + "  ";
			
			for(int j=0; j<nItens; j++) { 
				Cromossomo += populacao.individuos.get(i).cromossomos[j] + " ";
			
			}
			
			result += "Cromossomos: " + Cromossomo;
			result += "Lucro: " + populacao.individuos.get(i).lucroTotal; 
			result += "Custo: " + populacao.individuos.get(i).custoTotal; 
			result += "Probabilidade de seleção: " + populacao.individuos.get(i).propSelecao;
			result += "\n";
			
		}
		
		result += "---------------------------------------------------------------------------";
		result += "\nFitness total: " + fitnessTotal + "\n"; 
		result += faixa;
		result += selecionados;
		
		return result;
		
	}
	
}
