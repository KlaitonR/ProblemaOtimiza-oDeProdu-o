package view;

import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrincipalController {
	
	@FXML TextField populacaoInicial;
	@FXML TextField penalidade;
	@FXML TextField numeroGeracao;
	@FXML TextField percentualMutacao;
	@FXML TextField materiaPrima;
	@FXML TextField custoProducao;
	@FXML TextField horasProducao;
	@FXML TextField limiteCusto;
	@FXML TextField limiteMateria;
	@FXML TextField limiteTempo;
	@FXML TextField lucroProducao;
	
	@FXML Button addItem;
	@FXML Button gerarSolução;
	
	@FXML TextArea txtItens;
	@FXML TextArea txtLimites;
	@FXML TextArea txtResultado;
	
	
	int pInicial;
	double penal;
	int nGercoes;
	int percentual;
	double mPrima;
	double custo;
	double horas;
	double limitecus;
	double limiteMat;
	double limiteTemp;
	double lucro;
	
	int geracao  = 0;
	double maior = 0;
	int psMaior = 0;
	
	String txtLim = "";
	String txtIte = "";
	String txtResult = "";
	
	ArrayList<Item> itens = new ArrayList<>(); 
	int nItens;
	
	@FXML
	public void addItem() {
		
		custo = Double.parseDouble(custoProducao.getText());
		horas = Double.parseDouble(horasProducao.getText());
		mPrima = Double.parseDouble(materiaPrima.getText());
		lucro = Double.parseDouble(lucroProducao.getText());
		
		Item item = new Item(custo, horas, lucro, mPrima);
		itens.add(item);
		
		txtIte += "Item "+ itens.size() + "\nMatéria-Prima para produção: " + mPrima +
				"\nCusto de produção: " + custo + "\nHoras de produção: " + horas +
				"\nLucro de produção: " + lucro + "\n\n";
		
		txtItens.setText(txtIte);
		
		custoProducao.clear();
		horasProducao.clear();
		lucroProducao.clear();
		materiaPrima.clear();
		
	}
	
	@FXML
	public void gerarSolucao() {
		
		if(itens.size() > 1) {
			
			pInicial = Integer.parseInt(populacaoInicial.getText());
			penal = Double.parseDouble(penalidade.getText());
			nGercoes = Integer.parseInt(numeroGeracao.getText());
			percentual = Integer.parseInt(percentualMutacao.getText());
			limitecus = Double.parseDouble(limiteCusto.getText());
			limiteMat = Double.parseDouble(limiteMateria.getText());
			limiteTemp = Double.parseDouble(limiteTempo.getText());
			
			txtLim += "\nLimite de Matéria-Prima: " + limiteMat +
					"\nLimite de Custo: " + limitecus +
					"\nLimite de Horas: " + limiteTemp + "\n\n";
			
			txtLimites.setText(txtLim);
			
			nItens = itens.size();
			AlgoritmoGenetico ag = new AlgoritmoGenetico(nItens,pInicial,itens,limiteMat,limitecus,limiteTemp,penal,percentual);
			ArrayList<Individuo> fitnessPorGeracao= new ArrayList<>();
			
			ag.primeiraPopulacao();
			
			fitnessPorGeracao.add(ag.fitness());
			if(fitnessPorGeracao.get(geracao).lucroTotal > maior) {
				maior = fitnessPorGeracao.get(geracao).lucroTotal;
				psMaior = geracao;
			}
			
			while(!criterioDeParada(fitnessPorGeracao, geracao, maior, psMaior, nGercoes)) {
					
				ag.selecao();
				
//				txtResult += ag.mostraResultado(itens, nItens);
				
//				txtResult += ag.crossover();
				ag.crossover();
				
//				txtResult += ag.mutacao(); 
				ag.mutacao(); 
				
				geracao++;
				
				fitnessPorGeracao.add(ag.fitness());
				
				if(fitnessPorGeracao.get(geracao).lucroTotal > maior) {
					maior = fitnessPorGeracao.get(geracao).lucroTotal;
					psMaior = geracao;
				}
			
			}	
		
//			txtResult += "\n";

			for(int i=0; i<fitnessPorGeracao.size(); i++) {
				txtResult += "\nFitnes geração " + (i+1) + " - Melhor fitness: " + fitnessPorGeracao.get(i).lucroTotal;
			}
			
			txtResult += "\n\nMELHOR FITNESS: " + fitnessPorGeracao.get(psMaior).lucroTotal + " - GERAÇÃO: " + (psMaior+1) + "\n";
			
			String qtItens = "";
			for(int i=0; i< nItens; i++) {
				qtItens += fitnessPorGeracao.get(psMaior).cromossomos[i] + " unidades do Item " + (i+1) + "\n ";
			}
		
			txtResult += "\nNúmero de itens para produzir: " + qtItens;
			txtResult += "\nLucro Total: " + fitnessPorGeracao.get(psMaior).lucroTotal + " R$";
			txtResult += "\nCusto Total: " + fitnessPorGeracao.get(psMaior).custoTotal + " R$";
			txtResult += "\nMatéria-Prima Total: " + (int)fitnessPorGeracao.get(psMaior).materiaTotal + " unidades";
			txtResult += "\nHoras Totais: " + (int)fitnessPorGeracao.get(psMaior).tempoTotal + " horas";
			
			txtResultado.setText(txtResult);
			System.out.println(txtResult);
			
			fitnessPorGeracao.clear();
			txtResult = "";
			txtIte = "";
			txtLim = "";
			maior = 0;
			psMaior = 0;
			geracao = 0;
			
		}
	
	}
	
	static public boolean criterioDeParada (ArrayList<Individuo> fitnessPorGeracao, int geracao, double maior, int psMaior, int nGeracoes) {
		
		int somatorio = 0;
		
		for(int i = psMaior; i<geracao; i++) {
				if(fitnessPorGeracao.get(i).lucroTotal > maior) {
					maior = fitnessPorGeracao.get(i).lucroTotal;
					psMaior = i;
				}else {
					somatorio++;
				}
		}
		
		if(somatorio >= 15) {
			return true;
		}
		
		if(geracao == nGeracoes) {
			return true;
		}
		
		return false;
	}

	
	
}
