import java.util.ArrayList;
import java.util.List;
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
	public static Float[][] tableauPrimeiraFase;
	public static Float[][] tableauSegundaFase;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0, variaveisDeExcesso = 0, variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;
	public static List<Integer> posicaoDaArtificial = new ArrayList<>();

	/*
	 * Função principal
	 */
	public static void main(String[] args) 
	{
		lerEntradas();
		imprimirEntradas();
		analisaEntradas();
		primeiraFase();
		imprimirTableau();
		segundaFase();
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
		quantidadeRestricoes = Integer.parseInt(dimensoes[0]);
		quantidadeVariaveisNaturais = Integer.parseInt(dimensoes[1]);
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
	 * Verifica a necessidade de inserção de variáveis de folga, excesso ou artificiais.
	 */
	private static void analisaEntradas() 
	{
		// Conta quantas variáveis de cada tipo serão necessárias
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
		
	/*
	 * Cria um tableau para a primeira fase
	 */
	private static void tableauComArtificial() 
	{
		// Cria o tableau
		tableauPrimeiraFase = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		
		// Preenche o tableau com zeros
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauPrimeiraFase[i][j] = (float) 0;
			}
		}
		
		// Insere a função objetivo no tableau
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableauPrimeiraFase[1][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableauPrimeiraFase[1][i+1] = funcaoObjetivo[i]*(-1);
		}
		
		// Coloca -1 nas variáveis artificiais da função artificial
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
	}
	
	/*
	 * Cria um tableau para a segunda fase
	 */
	private static void tableauSemArtificial() 
	{
		// Cria o tableau
		tableauSegundaFase = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		
		// Preenche o tableau com zeros
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauSegundaFase[i][j] = (float) 0;
			}
		}
		
		// Insere a função objetivo no tableau
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) {
			if(tipoProblema.equals("max")) {
				tableauSegundaFase[0][i+1] = funcaoObjetivo[i];
			}
			else if(tipoProblema.equals("min")) 
				tableauSegundaFase[0][i+1] = funcaoObjetivo[i]*(-1);
		}
				
		// Insere as restrições no tableau
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
	
	/*
	 * Executa a primeira fase do algoritmo
	 */
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
	
	/*
	 * Realiza a segunda fase do algoritmo
	 */
	private static void segundaFase() 
	{
		// Encontra os indices referentes ao pivo
		int indiceColunaPivo = encontrarIndiceColunaPivo();
		int indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo);
		
		// Imprime a linha e coluna do pivo
		System.out.printf("Indice Col: %d\n",indiceColunaPivo);
		System.out.printf("Indice Lin: %d\n",indiceLinhaPivo);
		
		// Enquanto existir uma coluna pivo
		while(indiceColunaPivo != -1)
		{
			// Se existir uma linha pivo
			if(indiceLinhaPivo != -1)
			{
				// Escalona o tableau
				escalonarTableau(indiceColunaPivo, indiceLinhaPivo);

				// Atualiza indices referentes ao pivo
				indiceColunaPivo = encontrarIndiceColunaPivo();
				indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo);
			}
			else
			{
				break;
			}
		}
	}
	
	/*
	 * Encontra o valor máximo da primeira linha do tableau
	 */
	private static int encontrarIndiceColunaPivo()
	{
		double max = 0;
		int indice = -1;
		
		// Itera sobre a primeira linha, a partir da segunda coluna
		for(int i = 1; i < numeroDeColunasTableau; i++)
		{
			// Se o valor é maior que o máximo já encontrado
			if (tableauSegundaFase[0][i] > max)
			{
				// Atualiza máximo e indice
				max = tableauSegundaFase[0][i];
				indice = i;
			}
		}
		return indice;
	}
	
	/*
	 * Encontra o mínimo da divisão entre o item coluna de 
	 * resultados pelo item referente na coluna pivô
	 */
	private static int encontrarIndiceLinhaPivo(int indiceColunaPivo)
	{
		float min = Float.MAX_VALUE;
		int indice = -1;
		
		// Se não existe coluna pivo
		if(indiceColunaPivo == -1){ return -1;}
		
		// Percorre as linhas do tableau (menos a fo)
		for(int i = 1; i < numeroDeLinhasTableau; i++)
		{
			 
			if (
				// Se o elemento da linha e coluna pivo for maior que zero e
				tableauSegundaFase[i][indiceColunaPivo] > 0 && 
				
				// o elemento referente na coluna de resultdos for maior ou igual a zero e
				tableauSegundaFase[i][0] >= 0 && 
				
				// a divisão entre eles for menor que o mínimo já encontrado
				tableauSegundaFase[i][0]/tableauSegundaFase[i][indiceColunaPivo] < min
			)
			{
				// Atualiza o mínimo e o indice
				min = tableauSegundaFase[i][numeroDeColunasTableau-1]/tableauSegundaFase[i][indiceColunaPivo];
				indice = i;
			}
		}
		return indice;
	}
	
	/*
	 * Escalona o tableau
	 */
	private static void escalonarTableau(int indiceColunaPivo, int indiceLinhaPivo)
	{
		for(int i=0; i<numeroDeLinhasTableau; i++)
		{
			if(i==indiceLinhaPivo){continue;}
			float divisor = tableauSegundaFase[i][indiceColunaPivo];
			for(int j=0; j<numeroDeColunasTableau; j++)
			{
				tableauSegundaFase[i][j] = (float) ((-1)*divisor*tableauSegundaFase[indiceLinhaPivo][j] + tableauSegundaFase[i][j]);  
			}
		}
	}
	
	/*
	 * Imprime o tableau na saída padrão
	 */
	private static void imprimirTableau()
	{
		System.out.println();
		for(int i = 0; i < numeroDeLinhasTableau; i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				System.out.print(tableauSegundaFase[i][j]+"\t");
			}
			System.out.println();
		}		
	}
}
