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
	public static Float[][] tableau;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0, variaveisDeExcesso = 0, variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;

	public static void main(String[] args) 
	{
		lerEntradas();
		imprimirEntradas();
		analisaEntradas();
		imprimirTableau();
	}
	
	private static void lerEntradas()
	{
		Scanner scanner = new Scanner(System.in);
		
		tipoProblema = scanner.nextLine();
		String dimensoes[] = scanner.nextLine().split(" ");
		quantidadeRestricoes = Integer.parseInt(dimensoes[0]);
		quantidadeVariaveisNaturais = Integer.parseInt(dimensoes[1]);
		restricoes = new String[quantidadeRestricoes][quantidadeVariaveisNaturais+2];
		funcaoObjetivo = new float[quantidadeVariaveisNaturais];
		
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			funcaoObjetivo[i] = scanner.nextFloat();
		}
		scanner.nextLine();
		
		for(int i = 0; i < quantidadeRestricoes; i++) {
			restricoes[i] = scanner.nextLine().split(" ");
		}
		scanner.close();
	}
	
	private static void imprimirEntradas()
	{
		System.err.println(tipoProblema + " \n" + quantidadeRestricoes + " " + quantidadeVariaveisNaturais);
		
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			System.out.print(funcaoObjetivo[i]+"   ");
		}
		System.out.println();
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
	
	// A ordem no tableau vai ser: variaveis de folga, de excesso e artificiais
	private static void analisaEntradas() 
	{
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				variaveisDeFolga++;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				variaveisDeExcesso++;
				variaveisArtificiais++;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				variaveisArtificiais++;
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
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableau[i][j] = (float) 0;
			}
		}
		
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableau[1][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableau[1][i+1] = funcaoObjetivo[i]*(-1);
		}
		
		for(int i = 0; i < variaveisArtificiais; i ++) {
			System.out.println(i);
			tableau[0][quantidadeVariaveisNaturais+variaveisDeExcesso+variaveisDeFolga+1+i] = (float) -1;
		}
		
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

	private static void tableauSemArtificial() {
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableau[i][j] = (float) 0;
			}
		}
		
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
}
