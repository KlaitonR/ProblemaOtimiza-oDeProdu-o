package view;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

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

	@FXML TextArea txtLimites;
	@FXML TextArea txtResultado;
	
	@FXML  TableView <Item> tabItens;
	@FXML  TableColumn < Item , Number > Colid;
	@FXML  TableColumn < Item , Number > colMatProd;
	@FXML  TableColumn < Item , Number > colCustoProd;
	@FXML  TableColumn < Item , Number > colHorasProd;
	@FXML  TableColumn < Item , Number > colLucroProd;
	@FXML  TableColumn < Item , Number > colNumProd;
	
	int pInicial;
	double penal;
	int nGercoes;
	int percentual;
	double mPrima;
	
	int idItem;
	double custo;
	double horas;
	double lucro;
	int numProd;
	
	int numItem1;
	int numItem2;
	int numItem3;
	int numItem4;
	
	double limitecus;
	double limiteMat;
	double limiteTemp;
	
	int geracao  = 0;
	double maior = 0;
	int psMaior = 0;
	
	String txtLim = "";
	String txtResult = "";

	ArrayList<Item> itens = new ArrayList<>(); 
	int nItens;
	
	@FXML
	public void addItem() {
		
		try {
			custo = Double.parseDouble(custoProducao.getText());
			horas = Double.parseDouble(horasProducao.getText());
			mPrima = Double.parseDouble(materiaPrima.getText());
			lucro = Double.parseDouble(lucroProducao.getText());
		
			idItem++;
			
			Item item = new Item(custo, horas, lucro, mPrima, idItem);
			itens.add(item);
			
			tableViewItens();
			iniciaTable();
			
			custoProducao.clear();
			horasProducao.clear();
			lucroProducao.clear();
			materiaPrima.clear();
			
			mostraMensagem("Item inserido com sucesso!", AlertType.CONFIRMATION);
		
		} catch (NumberFormatException e) {
			mostraMensagem("Preencha apenas com números!", AlertType.WARNING);
		}catch (Exception e) {
			mostraMensagem(e.toString(), AlertType.ERROR);
		}
		
	}
	
	@FXML
	public void gerarSolucao() {
		
			
			try {
				pInicial = Integer.parseInt(populacaoInicial.getText());
				penal = Double.parseDouble(penalidade.getText());
				nGercoes = Integer.parseInt(numeroGeracao.getText());
				percentual = Integer.parseInt(percentualMutacao.getText());
				limitecus = Double.parseDouble(limiteCusto.getText());
				limiteMat = Double.parseDouble(limiteMateria.getText());
				limiteTemp = Double.parseDouble(limiteTempo.getText());
			} catch (NumberFormatException e) {
				mostraMensagem("Preencha apenas com números!", AlertType.WARNING);
			}catch (Exception e) {
				mostraMensagem(e.toString(), AlertType.ERROR);
			}
			
			percentual = (int)(percentual/100)*pInicial;
			
			if(percentual < 1) {
				percentual = 1;
			}
			
		if(itens.size() > 1) {
			
			txtLim += "Limite de Matéria-Prima: " + limiteMat +
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
				
				ag.crossover();

				ag.mutacao(); 
				
				geracao++;
				
				fitnessPorGeracao.add(ag.fitness());
				
				if(fitnessPorGeracao.get(geracao).lucroTotal > maior) {
					maior = fitnessPorGeracao.get(geracao).lucroTotal;
					psMaior = geracao;
				}
				
			}	

			for(int i=0; i<fitnessPorGeracao.size(); i++) {
				txtResult += "Fitnes geração " + (i+1) + " - Melhor fitness: " + fitnessPorGeracao.get(i).lucroTotal + "\n";
			}
			
			txtResult += "\n\nMELHOR FITNESS: " + fitnessPorGeracao.get(psMaior).lucroTotal + " - GERAÇÃO: " + (psMaior+1) + "\n";
			
			String qtItens = "";
			for(int i=0; i< nItens; i++) {
				itens.get(i).numProducao = fitnessPorGeracao.get(psMaior).cromossomos[i];
				qtItens += fitnessPorGeracao.get(psMaior).cromossomos[i] + " unidades do Item " + (i+1) + "\n ";
			}
			
			tabItens.refresh();
		
			txtResult += "\nNúmero de itens para produzir: " + qtItens;
			txtResult += "\nLucro Total: " + fitnessPorGeracao.get(psMaior).lucroTotal + " R$";
			txtResult += "\nCusto Total: " + fitnessPorGeracao.get(psMaior).custoTotal + " R$";
			txtResult += "\nMatéria-Prima Total: " + (int)fitnessPorGeracao.get(psMaior).materiaTotal + " unidades";
			txtResult += "\nHoras Totais: " + (int)fitnessPorGeracao.get(psMaior).tempoTotal + " horas";
			
			txtResultado.setText(txtResult);
			
			fitnessPorGeracao.clear();
			txtResult = "";
			txtLim = "";
			maior = 0;
			psMaior = 0;
			geracao = 0;
			
		}else {
			mostraMensagem("Insira pelo menos 2 itens!", AlertType.WARNING);
		}
	
	}
	
	@FXML
	public void limparItens() {
		itens.clear();
		tableViewItens();
	}
	
	@FXML
	 private void tableViewItens(){  
		tabItens.setItems(FXCollections.observableArrayList(itens));
	 }
	
	@FXML
	public void iniciaTable() {
		Colid.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().id));
		colMatProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().materiaItem));
		colCustoProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().custoItem));
		colHorasProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().horasProducao));
		colLucroProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().lucroItem));
		colNumProd.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().numProducao));

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
	
	private void mostraMensagem (String msg, AlertType tipo) { // recebe uma String por paremetro
		
		Alert a = new Alert (tipo);
		
		a.setHeaderText(null); // modificar mensagem
		a.setContentText(msg);
		a.show();
	}
}
