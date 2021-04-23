package util;

import java.util.ArrayList;
import java.util.Random;

public class AlgoritmoGenetico {
	
	int nItens;
	int nPopulacao;
	ArrayList<Item> itens = new ArrayList<>();
	double penalidade;
	int percentual;
	double limiteMateria;
	double limiteCusto;
	double limiteTempo;
	
	
	Populacao populacao = new Populacao();
	Random roleta = new Random();
	double fitnessTotal;
	double sorteio;
	int nSelecionados = 0;

	ArrayList<Individuo> selecionados = new ArrayList<>();
	ArrayList<Individuo> novaGeracao = new ArrayList<>();
	ArrayList<Individuo> mutacoes = new ArrayList<>();
	
	public AlgoritmoGenetico(int nItens,int nPopulacao, ArrayList<Item> itens, 
			double limiteMateriaPrima, double limiteCusto, double limiteTemp, double penalidade, int percentual) {
		
		this.nItens = nItens;
		this.nPopulacao = nPopulacao;
		this.itens = itens;
		this.penalidade = penalidade - (penalidade*2);
		this.percentual = percentual;
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
	}
	
	public Individuo rodarRoleta() {
		
	sorteio = roleta.nextDouble()*100;
	int i = 0;
	double inicioDaFaixa = 0;
	double fimDaFaixa = 0;
	Individuo ind = null;
		
		while(i < nPopulacao){ 
			
			if(populacao.individuos.get(i).propSelecao > 0) { //Se a porcentagem é 0, não tem espaço na roleta e apenas pula a vez
				
				fimDaFaixa += populacao.individuos.get(i).propSelecao; //Defina o epaço do individuo na roleta
				
				if(sorteio >= inicioDaFaixa && 
						sorteio < fimDaFaixa && 
						!populacao.individuos.get(i).selecionado) {
					populacao.individuos.get(i).selecionado = true;
					ind = populacao.individuos.get(i);
					return ind;
				}
				
				inicioDaFaixa += populacao.individuos.get(i).propSelecao; //Defina os epaços do individuo na roleta
			}
			
			i++;
		}
		
		return null;
	}
	
	public void crossover() {
		
		int pontoDeCorte = 0;
		int sorteio1 = 0;
		int sorteio2 = 0;
	
		while(selecionados.size() > 0) {
			
			pontoDeCorte = roleta.nextInt(nItens-1);
			sorteio1 = roleta.nextInt(selecionados.size());
			sorteio2 = roleta.nextInt(selecionados.size());
		
			while(!confirmaSorteio(sorteio1, sorteio2)) {
				sorteio1 = roleta.nextInt(selecionados.size());
				sorteio2 = roleta.nextInt(selecionados.size());
			}
			
			aplicaCrossover(sorteio1, sorteio2, pontoDeCorte);
		
		}
		
		populacao.individuos.clear();
		populacao.individuos.addAll(novaGeracao);
		selecionados.clear();
		novaGeracao.clear();
		
	}
	
	public boolean confirmaSorteio(int sorteio1, int sorteio2) {
		
		if((sorteio1 == sorteio2)) {
			return false;
		}else {
			return true;
		}
		
	}
	
	public void aplicaCrossover(int sorteio1, int sorteio2, int pontoDeCorte) {
		
		Individuo filho1 = new Individuo(nItens);
		Individuo filho2 = new Individuo(nItens);
		Individuo pai1 = new Individuo(nItens);
		Individuo pai2 = new Individuo(nItens);
		
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
		
		pai1.cromossomos = selecionados.get(sorteio1).cromossomos;
		pai2.cromossomos = selecionados.get(sorteio2).cromossomos;
		novaGeracao.add(pai1);
		novaGeracao.add(pai2);
		
		selecionados.remove(sorteio1); //Quando remove, diminuirá uma posição do index
		
		if(sorteio1 < sorteio2) { //Ajustando index caso o individuo sorteado1 for de index menor que o sorteado2 na lista
			sorteio2--;
		}
		
		selecionados.remove(sorteio2);

		novaGeracao.add(filho1);
		novaGeracao.add(filho2);
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
	
	public void mutacao() {
		
		int geneDeMutacao = 0;
		int indexMutacao = 0;
		int cont = 0;
		
		while(cont < percentual) { //Enquanto o contador não for igual ao de individuos que sofrerá mutação
			
			geneDeMutacao = roleta.nextInt(nItens);
			indexMutacao = roleta.nextInt(populacao.individuos.size());
			
			int valorMutacao = roleta.nextInt(401);
			
			while (valorMutacao == populacao.individuos.get(indexMutacao).cromossomos[geneDeMutacao]) {
				valorMutacao = roleta.nextInt(401);
			}
		
			populacao.individuos.get(indexMutacao).cromossomos[geneDeMutacao] = valorMutacao;
			mutacoes.add(populacao.individuos.get(indexMutacao)); //passa para a lista de individuos já mudificados
			
			cont++;
			
			populacao.individuos.remove(indexMutacao); //Remove da população o individuo selecionado para a mutação
			
		}
		
		populacao.individuos.addAll(mutacoes); //Ao final, adiciona novamente à população todos os individuos que sofreram mutação
		mutacoes.clear(); // limpa a lista de individuos que foram modificados
		
	}
}