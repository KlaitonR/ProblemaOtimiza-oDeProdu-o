package view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javafx.stage.FileChooser;
import util.AlgoritmoGenetico;
import util.Individuo;
import util.Item;

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

	@FXML TextArea txtResultado;
	
	@FXML  TableView <Item> tabItens;
	@FXML  TableColumn < Item , Number > Colid;
	@FXML  TableColumn < Item , Number > colMatProd;
	@FXML  TableColumn < Item , Number > colCustoProd;
	@FXML  TableColumn < Item , Number > colHorasProd;
	@FXML  TableColumn < Item , Number > colLucroProd;
	@FXML  TableColumn < Item , Number > colNumProd;
	
	int pInicial;
	int nGercoes;
	int percentual;
	double penal;
	
	int idItem;
	int numProd;
	double mPrima;
	double custo;
	double horas;
	double lucro;
	
	double limiteCus;
	double limiteMat;
	double limiteTemp;
	
	int geracao  = 0;
	double maior = 0;
	int psMaior = 0;
	
	String txtLim = "";
	String txtResult = "";

	ArrayList<Item> itens = new ArrayList<>(); 
	int nItens;
	
	InputStream scriptI;
	String script;
	
	@FXML
	public void addItem() {
		
		idItem = itens.size();
		
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
			limiteCus = Double.parseDouble(limiteCusto.getText());
			limiteMat = Double.parseDouble(limiteMateria.getText());
			limiteTemp = Double.parseDouble(limiteTempo.getText());
			
		} catch (NumberFormatException e) {
			mostraMensagem("Preencha apenas com números!", AlertType.WARNING);
		}catch (Exception e) {
			mostraMensagem(e.toString(), AlertType.ERROR);
		}
		
		if(tratamentoDadosAlgoritmo())
			return;
		
		if(tratamentoDadosItem())
			return;
			
		percentual = (int)(percentual/100)*pInicial;
			
		if(percentual < 1) {
			percentual = 1;
		}
			
		if(itens.size() > 1) {
			
			txtLim += "Limite de Matéria-Prima: " + limiteMat +
					"\nLimite de Custo: " + limiteCus +
					"\nLimite de Horas: " + limiteTemp + "\n";
			
			nItens = itens.size();
			AlgoritmoGenetico ag = new AlgoritmoGenetico(nItens,pInicial,itens,limiteMat,limiteCus,limiteTemp,penal,percentual);
			ArrayList<Individuo> fitnessPorGeracao= new ArrayList<>();
			
			try {
				
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
				
			} catch (NullPointerException e) {
				mostraMensagem("Errou ao executar", AlertType.ERROR);
			}catch (IndexOutOfBoundsException e) {
				mostraMensagem("Errou ao executar", AlertType.ERROR);
			}catch (Exception e) {
				mostraMensagem(e.toString(), AlertType.ERROR);
			}
			
			txtResult += txtLim + "\n";
			txtResult += "----------- Melhor indivíduo de cada geração -----------\n\n";
					
			for(int i=0; i<fitnessPorGeracao.size(); i++) {
				txtResult += "Geração " + (i+1) + " - Melhor fitness: " + fitnessPorGeracao.get(i).lucroTotal + "\n";
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
	public void limpaParametros() {
		custoProducao.clear();
		horasProducao.clear();
		lucroProducao.clear();
		materiaPrima.clear();
		populacaoInicial.clear();
		penalidade.clear();
		numeroGeracao.clear();
		percentualMutacao.clear();
		limiteMateria.clear();
		limiteCusto.clear();
		limiteTempo.clear();
	}
	
	@FXML
	public void limpaResultado() {
		txtResultado.clear();
		for(int i=0; i < itens.size(); i++) {
			itens.get(i).numProducao = 0;
		}
		
		tabItens.refresh();
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
		
		if(somatorio >= 15) 
			return true;
		
		if(geracao == nGeracoes)
			return true;
		
		return false;
	}
	
	@FXML
	public void abreScript() throws IOException {
		
		if(itens.size() == 0) {
			
			script = leScrip();
			if(script == null) {
				 IllegalArgumentException erro = new IllegalArgumentException();
				 mostraMensagem("Erro ao ler o arquivo" +
				 " \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
				 return;
			}
				
			int cont = 0;
			double parametrosItens[] = new double[4];
			double parametrosaAlgoritmo[] = new double[7];
			int contParametro = 0;
				
			int contId = 1;
			int check = 0;
			boolean acabou;
			String str = "";
			
			while(cont < script.length()) {
					
				if(script.charAt(cont) != ',' &&
				script.charAt(cont) != ' ' &&
				script.charAt(cont) != '|' &&
				script.charAt(cont) != '[' &&
				script.charAt(cont) != ']') {
					str += Character.toString(script.charAt(cont));
					acabou = false;
				}else {
					acabou = true;
				}
					
				if(check == 0 && !str.isEmpty() && acabou) {
					
					try {
						parametrosItens[contParametro] = Double.parseDouble(str);
					}catch (NumberFormatException e) {
						mostraMensagem("Erro ao converter dados do arquivo!", AlertType.ERROR);
						return;
					}catch (Exception e) {
						mostraMensagem(e.toString(), AlertType.ERROR);
					}
						
					if(contParametro == parametrosItens.length - 1) {	
						mPrima = parametrosItens[0];
						custo = parametrosItens[1];
						horas = parametrosItens[2];
						lucro = parametrosItens[3];
						Item item = new Item(custo, horas, lucro, mPrima, contId);
						itens.add(item);
						contParametro = 0;
						contId++;
					}else {
						contParametro++;
					}
					
					str = "";
					
					}
					
					if(script.charAt(cont) == '|')
						check = 1;
					
					if(check == 1 && !str.isEmpty() && acabou) {
						try {
							parametrosaAlgoritmo[contParametro] = Double.parseDouble(str);
						}catch (NumberFormatException e) {
							mostraMensagem("Erro ao converter dados do arquivo!", AlertType.ERROR);
							return;
						}catch (Exception e) {
							mostraMensagem(e.toString(), AlertType.ERROR);
						}
						
						if(contParametro == parametrosaAlgoritmo.length - 1) {
							pInicial = (int)parametrosaAlgoritmo[0];
							penal = parametrosaAlgoritmo[1];
							nGercoes = (int)parametrosaAlgoritmo[2];
							percentual = (int)parametrosaAlgoritmo[3];
							limiteMat = parametrosaAlgoritmo[4];
							limiteCus = parametrosaAlgoritmo[5];
							limiteTemp = parametrosaAlgoritmo[6];
							populacaoInicial.setText(Integer.toString(pInicial));
							penalidade.setText(Double.toString(penal));
							numeroGeracao.setText(Integer.toString(nGercoes));
							percentualMutacao.setText(Integer.toString(percentual));
							limiteMateria.setText(Double.toString(limiteMat));
							limiteCusto.setText(Double.toString(limiteCus));
							limiteTempo.setText(Double.toString(limiteTemp));
							contParametro = 0;
							
						}else {
							contParametro++;
						}
						
						str = "";
					}
					
					if(script.charAt(cont) == ';') 
						return;
					
					cont++;
					
					tableViewItens();
					iniciaTable();
				}
		}else {
			mostraMensagem("Exclua os itens da tabela primeiro para evitar conflitos!", AlertType.WARNING);
		}
	}
	
	 @FXML
	 public void excluiItem() {
		  
		 try {
			Item item = selecionaItem();
			itens.remove(item);
			tableViewItens();
			iniciaTable();
			mostraMensagem("Item deletado com sucesso!", AlertType.CONFIRMATION);
		 }catch (NullPointerException e) {
			 mostraMensagem("Selecione um item para realizar está operação.", AlertType.ERROR);	
			
		}catch (Exception e) {
			mostraMensagem("Erro não identificado:\n " + e.toString(), AlertType.ERROR);
				
		}
	 }
	
	 public Item selecionaItem(){
			
			Item item = null;
			
			try {
				item = tabItens.getSelectionModel().getSelectedItem();
			}catch (Exception e) {
				mostraMensagem("Erro não identificado.\n " + e.toString(), AlertType.ERROR);
			}
				
			return item;
		}
	
	 private String leScrip() throws IOException {
		 
		 try {
			File f = selecionaScript();
			if(f!= null) {
				InputStream is = new FileInputStream(f);
				java.io.InputStreamReader isr = new java.io.InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String script = br.readLine();
					
				while(br.readLine() != null){
					script += br.readLine() + "\n";
				}
		 
				is.close();
				isr.close();
				br.close();
	
				return script;
			}
			
		}catch (NullPointerException e) {
			mostraMensagem("Formato dos dados não esperado, erro ao abrir o aruivo!", AlertType.ERROR);
		}catch (Exception e) {
			mostraMensagem(e.toString(), AlertType.ERROR);
		}
			
			return null;
		}
		
		private File selecionaScript() {
			try {
				FileChooser fileChooser = new FileChooser();
//			   		fileChooser.setInitialDirectory(new File(
//			   				"C:\\Users\\klait\\eclipse-workspace\\ProblemaOtimizacaoDeProducao\\res"));
			   		fileChooser.getExtensionFilters().add(new 
			   				FileChooser.ExtensionFilter("*.txt", "*.TXT")); 	
			   		File txtSelec = fileChooser.showOpenDialog(null);
			   		if (txtSelec != null) {
			   			return txtSelec;
			   		}
			}catch (Exception e) {
				mostraMensagem(e.toString(), AlertType.ERROR);
			}
			
			return null;
		}
		
	public boolean tratamentoDadosItem() {
		
		for(int i=0; i < itens.size(); i++) {
			if(itens.get(i).custoItem <= 0 || itens.get(i).custoItem > limiteCus ||
					itens.get(i).horasProducao <= 0 || itens.get(i).horasProducao > limiteTemp ||
							itens.get(i).materiaItem <= 0 || itens.get(i).materiaItem > limiteMat) {
				IllegalArgumentException erro = new IllegalArgumentException();
				mostraMensagem("Os parâmetro de CUSTO, HORAS e MATÉRIA-PRIMA não podem serem menores ou iguais a 0 (zero)\n" +
				"ou maiores do que seus respectivos limites\n" +
				"ID do item: " + (i+1) +
				 " \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
				return true;
			}
		}
			
		return false;
	}
		
	public boolean tratamentoDadosAlgoritmo() {
			
		//Calculando fatorial
		double f = 1;
		int x = itens.size();
		
		while (x > 1){
			 f += f *(x-1); 
			 x--;
		}
		
		if(pInicial <= 0 || pInicial > f) {
			IllegalArgumentException erro = new IllegalArgumentException();
			mostraMensagem("O parâmetro PENALIDADE não pode ser menor ou igual a 0 (zero)" +
			"e maior do que o fatorial do número de itens" +
			" \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
			return true;
		}
		
	
		if(penal <= 0) {
			IllegalArgumentException erro = new IllegalArgumentException();
			mostraMensagem("O parâmetro PENALIDADE não pode ser menor ou igual a 0 (zero)" +
			" \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
			return true;
		}
		
		if(nGercoes <= 0) {
			IllegalArgumentException erro = new IllegalArgumentException();
			mostraMensagem("O parâmetro NÚMERO DE GERAÇÕES não pode ser menor ou igual a 0 (zero)" +
			" \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
			return true;
		}
			
		if(percentual < 0 || percentual > 99) {
			 IllegalArgumentException erro = new IllegalArgumentException();
			 mostraMensagem("O parâmetro PERCENTUAL DE MUTAÇÃO não pode ser:\n-> 100% ou acima (cem por cento)\n-> Negativo" +
			 " \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
			 return true;
		}
			
		if(limiteCus <= 0 ||
				limiteMat <= 0 ||
				limiteTemp <=0) {
			 IllegalArgumentException erro = new IllegalArgumentException();
			 mostraMensagem("O parâmetros de LIMITE não podem serem menores ou iguais a 0 (zero)" +
			 " \n\nMensagem do erro: " + erro.toString(), AlertType.ERROR);
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
