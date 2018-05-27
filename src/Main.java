
import java.util.Scanner;

public class Main 
{	
	/*
	 * Dados a serem manipulados
	 */
	public static String tipoProblema;
	public static int quantidadeRestricoes;
	public static int quantidadeVariaveisNaturais;
	public static String[][] restricoes;
	public static Float[][] tableau;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0, variaveisDeExcesso = 0, variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;

	/*
	 * Função principal
	 */
	public static void main(String[] args) 
	{
		lerEntradas();
		imprimirEntradas();
		analisaEntradas();
		imprimirTableau();
	}

	/*
	 * Realiza a leitura do prblema conforme convencionado na
	 * especificação do trabalho. Os dados são lidos da entrada
	 * padrão.
	 */
	private static void lerEntradas()
	{
		Scanner scanner = new Scanner(System.in);
		
		tipoProblema = scanner.nextLine();
		String dimensoes[] = scanner.nextLine().split(" ");
		int quantidadeRestricoes = Integer.parseInt(dimensoes[0]);
		int quantidadeVariaveisNaturais = Integer.parseInt(dimensoes[1]);
		restricoes = new String[quantidadeRestricoes][quantidadeVariaveisNaturais+2];
		funcaoObjetivo = new float[quantidadeVariaveisNaturais];
		
		// Preenche a função objetivo
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			funcaoObjetivo[i] = scanner.nextFloat();
		}
		scanner.nextLine();
		
		// Preenche as restrições
		for(int i = 0; i < quantidadeRestricoes; i++) {
			restricoes[i] = scanner.nextLine().split(" ");
		}
		scanner.close();
	}
	
	/*
	 * Imprime na saída padrão os dados lidos: 
	 * Tipo de problema, quantidade de restrições, quantidade de variáveis e função objetivo
	 */
	private static void imprimirEntradas()
	{
		System.out.println("Tipo do problema: "+tipoProblema);
		System.out.println("Quantidade de Restrições: "+quantidadeRestricoes);
		System.out.println("Quantidade de Variáveis Naturais: "+quantidadeVariaveisNaturais);
		System.out.print("Função Objetivo: ");
		
		// Imprime a função objetivo
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			System.out.print(funcaoObjetivo[i]+"   ");
		}
		System.out.println();
	}
	
	/*
	 * Verifica a necessidade de inserção de variáveis de folga, excesso ou artificiais
	 */
	private static void analisaEntradas() 
	{
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				variaveisDeFolga++;
			} else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				variaveisDeExcesso++;
				variaveisArtificiais++;
			} else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				variaveisArtificiais++;
			}
		}
		
		numeroDeColunasTableau = quantidadeVariaveisNaturais + variaveisArtificiais + variaveisDeExcesso + variaveisDeFolga + 1;
		
		if(variaveisArtificiais == 0) {
			numeroDeLinhasTableau = quantidadeRestricoes+1;
			tableauSemArtificial();
		} else {
			numeroDeLinhasTableau = quantidadeRestricoes+2;
			tableauComArtificial();
		}	
	}
	
	/*
	 * Cria um tableau para a primeira fase
	 */
	private static void tableauComArtificial() 
	{
		// Cria o tableau
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		
		// Preenche o tableau com zeros
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableau[i][j] = (float) 0;
			}
		}
		
		// Insere a função objetivo no tableau
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableau[1][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableau[1][i+1] = funcaoObjetivo[i]*(-1);
		}
		
		// Coloca -1 nas variáveis artificiais da função artificial
		for(int i = 0; i < variaveisArtificiais; i++) {
			System.out.println(i);
			tableau[0][quantidadeVariaveisNaturais+variaveisDeExcesso+variaveisDeFolga+1+i] = (float) -1;
		}
		
		// Insere as restrições no tableau
		for(int i = 0; i < restricoes.length; i++) {
			for(int j = 0; j < restricoes[0].length-2; j++) {
					tableau[i+2][j+1] = Float.valueOf(restricoes[i][j]);
			}	
		}
		
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableau[i+2][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableau[i+2][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableau[i+2][posicaoDaFolga++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableau[i+2][posicaoDaFolga++] = (float) 1;
				tableau[i+2][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableau[i+2][posicaoDaArtificial++] = (float) 1;
			}
		}
	}

	/*
	 * Cria um tableau para a segunda fase
	 */
	private static void tableauSemArtificial() 
	{
		// Cria o tableau
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		
		// Preenche o tableau com zeros
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableau[i][j] = (float) 0;
			}
		}
		
		// Insere a função objetivo no tableau
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableau[0][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableau[0][i+1] = funcaoObjetivo[i]*(-1);
		}
				
		
		for(int i = 1; i < numeroDeLinhasTableau; i++) {
			for(int j = 1; j < numeroDeColunasTableau; j++) {
				if(j < quantidadeVariaveisNaturais)
					tableau[i][j] = Float.valueOf(restricoes[i-1][j-1]);
				else
					tableau[i][j] = (float) 0;
			}	
		}
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableau[i+1][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableau[i+1][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableau[i+1][posicaoDaFolga++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableau[i+1][posicaoDaFolga++] = (float) 1;
				tableau[i+1][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableau[i+1][posicaoDaArtificial++] = (float) 1;
			}
		}
	}
	
	private static void imprimirTableau()
	{
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				System.out.print(tableau[i][j]+"\t");
			}
			System.out.println();
		}		
	}
	
	private static void primeiraFase()
	{
		
	}
}
