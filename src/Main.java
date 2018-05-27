/*import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;*/
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main 
{	
	public static String tipoProblema;
	public static int quantidadeRestricoes;
	public static int quantidadeVariaveisNaturais;
	public static String[][] restricoes;
	public static Float[][] tableauPrimeiraFase;
	public static Float[][] tableauSegundaFase;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0, variaveisDeExcesso = 0, variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;
	public static List<Integer> posicaoDaArtificial = new ArrayList<>();

	public static void main(String[] args) 
	{
		lerEntradas();
		analisaEntradas();
		imprimirTableau();
	}
	
	
	
	//A ordem no tableau vai ser variaveis de folga, de excesso e artificiais
	private static void analisaEntradas() {
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				variaveisDeFolga++;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				variaveisDeExcesso++;
				variaveisArtificiais++;
				posicaoDaArtificial.add(i);
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				variaveisArtificiais++;
				posicaoDaArtificial.add(i);
			}
		}
		
		numeroDeColunasTableau = quantidadeVariaveisNaturais + variaveisArtificiais + variaveisDeExcesso + variaveisDeFolga + 1;
		
		if(variaveisArtificiais == 0) {
			numeroDeLinhasTableau = quantidadeRestricoes+1;
			tableauSemArtificial();
		}else {
			numeroDeLinhasTableau = quantidadeRestricoes+2;
			tableauComArtificial();
		}
		
	}
		
		
	
	private static void tableauComArtificial() {
		tableauPrimeiraFase = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauPrimeiraFase[i][j] = (float) 0;
			}
		}
		
		
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableauPrimeiraFase[1][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableauPrimeiraFase[1][i+1] = funcaoObjetivo[i]*(-1);
		}
		
		
		for(int i = 0; i < variaveisArtificiais; i ++) {
			tableauPrimeiraFase[0][quantidadeVariaveisNaturais+variaveisDeExcesso+variaveisDeFolga+1+i] = (float) -1;
		}
		
		
		for(int i = 0; i < restricoes.length; i++) {
			for(int j = 0; j < restricoes[0].length-2; j++) {
					tableauPrimeiraFase[i+2][j+1] = Float.valueOf(restricoes[i][j]);
			}	
		}
		
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableauPrimeiraFase[i+2][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableauPrimeiraFase[i+2][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
			
		
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableauPrimeiraFase[i+2][posicaoDaFolga++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableauPrimeiraFase[i+2][posicaoDaFolga++] = (float) 1;
				tableauPrimeiraFase[i+2][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableauPrimeiraFase[i+2][posicaoDaArtificial++] = (float) 1;
			}
		}
		
		primeiraFase();
		
	}


	
	private static void primeiraFase() {
		for(int i = 0; i < posicaoDaArtificial.size(); i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauPrimeiraFase[0][j] += tableauPrimeiraFase[posicaoDaArtificial.get(i)+2][j];
			} 
		}
		boolean funcaoArtificialNula = false;
		int numeroMaximoDeIteracoes = 10;
		
		while(!funcaoArtificialNula && numeroMaximoDeIteracoes != 0) {
			float maximo = 0;
			float minimo = 99999;
			int posicaoDoMaximo = 0;
			int posicaoDoMinimo = 0;
			for(int i = 1; i < numeroDeColunasTableau; i++) {
				if(tableauPrimeiraFase[0][i]>maximo) {
					maximo = tableauPrimeiraFase[0][i];
					posicaoDoMaximo = i;
				}
			}
			
			for(int i = 2; i <numeroDeLinhasTableau; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] > 0 && tableauPrimeiraFase[i][0]/tableauPrimeiraFase[i][posicaoDoMaximo] < minimo) {
					minimo = tableauPrimeiraFase[i][0]/tableauPrimeiraFase[i][posicaoDoMaximo];
					posicaoDoMinimo = i;			
				}
			}
			
			for(int i = 0; i < numeroDeColunasTableau; i++) {
				if(tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo] != 1) {
					tableauPrimeiraFase[posicaoDoMinimo][i] /= tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
				}
			}
			
			for(int i = 0; i < posicaoDoMinimo; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] != 0) {
					float multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase = tableauPrimeiraFase[i][posicaoDoMaximo]/tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
					for(int j = 0; j < numeroDeColunasTableau; j++) {
						tableauPrimeiraFase[i][j] -= multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase*tableauPrimeiraFase[posicaoDoMinimo][j];
					}
				}
			}
			
			for(int i = posicaoDoMinimo+1; i < numeroDeLinhasTableau; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] != 0) {
					float multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase = tableauPrimeiraFase[i][posicaoDoMaximo]/tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
					
					for(int j = 0; j < numeroDeColunasTableau; j++) {
						tableauPrimeiraFase[i][j] -= multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase*tableauPrimeiraFase[posicaoDoMinimo][j];
					}
				}
			}
			
			float maiorValorDaPrimeiraLinha = 0;
			for(int i = 0; i < numeroDeColunasTableau; i++) {
				
				if(tableauPrimeiraFase[0][i] > maiorValorDaPrimeiraLinha) {
					maiorValorDaPrimeiraLinha = tableauPrimeiraFase[0][i];
				}
			}
			
			if(maiorValorDaPrimeiraLinha > 0) {
				funcaoArtificialNula = false;
			}else {
				funcaoArtificialNula = true;
			}
			numeroMaximoDeIteracoes--;
		}
		tableauSegundaFase = new Float[numeroDeLinhasTableau-1][numeroDeColunasTableau-variaveisArtificiais];
		
		if(numeroMaximoDeIteracoes == 0) {
			System.err.println("O Tableau não Possui nenhuma solução viavel!");
		}else {
			for(int i = 0; i < numeroDeLinhasTableau-1; i++) {
				for(int j = 0; j < numeroDeColunasTableau-variaveisArtificiais; j++) {
					tableauSegundaFase[i][j] = tableauPrimeiraFase[i+1][j];
				}
			}
			
			numeroDeLinhasTableau = numeroDeLinhasTableau-1;
			numeroDeColunasTableau = numeroDeColunasTableau-variaveisArtificiais;
		}
		
	}		
	



	private static void tableauSemArtificial() {
		tableauSegundaFase = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauSegundaFase[i][j] = (float) 0;
			}
		}
		
		
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableauSegundaFase[0][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableauSegundaFase[0][i+1] = funcaoObjetivo[i]*(-1);
		}
		
				
		for(int i = 1; i < numeroDeLinhasTableau; i++) {
			for(int j = 1; j < numeroDeColunasTableau; j++) {
				if(j < quantidadeVariaveisNaturais)
					tableauSegundaFase[i][j] = Float.valueOf(restricoes[i-1][j-1]);
				else
					tableauSegundaFase[i][j] = (float) 0;
			}	
		}
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableauSegundaFase[i+1][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableauSegundaFase[i+1][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
			
		
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableauSegundaFase[i+1][posicaoDaFolga++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableauSegundaFase[i+1][posicaoDaFolga++] = (float) 1;
				tableauSegundaFase[i+1][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableauSegundaFase[i+1][posicaoDaArtificial++] = (float) 1;
			}
		}
		
		
	}



	private static void lerEntradas()
	{
		try 
		{
			//File arquivo = new File(nomeArquivo);
			//FileReader leitorArquivo = new FileReader(arquivo);
			//BufferedReader bufferLeitor = new BufferedReader(leitorArquivo);
			Scanner scanner = new Scanner(System.in);
			
			tipoProblema = scanner.nextLine();//bufferLeitor.readLine();
			String dimensoes[] = scanner.nextLine().split(" ");//bufferLeitor.readLine().split(" ");
			quantidadeRestricoes = Integer.parseInt(dimensoes[0]);
			quantidadeVariaveisNaturais = Integer.parseInt(dimensoes[1]);
			restricoes = new String[quantidadeRestricoes][quantidadeVariaveisNaturais+2];
			funcaoObjetivo = new float[quantidadeVariaveisNaturais];
			
			System.err.println(tipoProblema + " \n" + quantidadeRestricoes + " " + quantidadeVariaveisNaturais);
			
			for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
				funcaoObjetivo[i] = scanner.nextFloat();
				System.out.print(funcaoObjetivo[i]+"   ");
			}
			System.out.println();
			
			//Scanner aleatório porque ele ta pegando uma linha vazia sabe-se la de onde
			scanner.nextLine();
			
			for(int i = 0; i < quantidadeRestricoes; i++) {
				restricoes[i] = scanner.nextLine().split(" ");
			}
			
			scanner.close();
					
			//leitorArquivo.close();
		} 
		catch(/*IO*/Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void imprimirTableau()
	{
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				System.out.print(tableauSegundaFase[i][j]+"\t");
			}
			System.out.println();
		}		
	}
}
