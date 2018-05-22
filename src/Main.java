import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import com.sun.xml.internal.bind.v2.runtime.output.StAXExStreamWriterOutput;

public class Main 
{	
	public static String tipoProblema;
	public static int quantidadeRestricoes;
	public static int quantidadeVariaveisNaturais;
	public static String[][] restricoes;
	public static Float[][] tableau;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0, variaveisDeExcesso = 0, vairaveisArtificiais = 0;

	public static void main(String[] args) 
	{
		lerEntradas();
		analisaEntradas();

	}
	
	
	
	//A ordem no tableau vai ser variaveis de folga, de excesso e artificiais
	private static void analisaEntradas() {
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				variaveisDeFolga++;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				variaveisDeExcesso++;
				vairaveisArtificiais++;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				vairaveisArtificiais++;
			}
		}
		
		int numeroDeLinhasTableau = quantidadeRestricoes+1;
		int numeroDeColunasTableau = quantidadeVariaveisNaturais + vairaveisArtificiais + variaveisDeExcesso+variaveisDeFolga;
		System.out.println("linhas = "+ numeroDeLinhasTableau + ", colunas = "+ numeroDeColunasTableau);
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
	
		for(int i = 0; i < numeroDeColunasTableau; i++) {
			if(i<quantidadeVariaveisNaturais)
				tableau[0][i] = funcaoObjetivo[i];
			else
				tableau[0][i] = (float) 0;
		}
	
		for(int i = 1; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				if(j < quantidadeVariaveisNaturais)
					tableau[i][j] = Float.valueOf(restricoes[i-1][j]);
				else
					tableau[i][j] = (float) 0;
			}	
		}
		
		
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
			System.out.print(tableau[i][j]+"\t");
			}
			System.out.println();
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
					
			//leitorArquivo.close();
		} 
		catch(/*IO*/Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static void imprimirTableau()
	{
		
	}
}
