import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main 
{
	/*
	 * Constantes
	 */
	static final int RESULTADO_SOLUCAO_UNICA = 0;
	static final int RESULTADO_SOLUCAO_MULTIPLA = 1;
	static final int RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO = 2;
	static final int RESULTADO_SEM_SOLUCAO_VAI_PARA_INFINITO = 3;
	
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
	public static int variaveisDeFolga = 0;
	public static int variaveisDeExcesso = 0;
	public static int variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;
	public static List<Integer> posicaoDaArtificial = new ArrayList<>();
	public static int resultadoFinal = RESULTADO_SOLUCAO_UNICA;
	public static boolean solucaoDegenerada;

	/*
	 * Função principal
	 */
	public static void main(String[] args) 
	{
		lerEntradas();
		imprimirEntradas();
		analisaEntradas();
		imprimirResultadoFinal();
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
		quantidadeRestricoes = scanner.nextInt();
		quantidadeVariaveisNaturais = scanner.nextInt();
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
		
		//numero de colunas que o tableau precisará
		numeroDeColunasTableau = quantidadeVariaveisNaturais + variaveisArtificiais + variaveisDeExcesso + variaveisDeFolga + 1;
		
		//verificação se haverá primeira fase ou não
		if(variaveisArtificiais == 0) {
			//numero de linhas do tableau sem variaveis artificiais
			numeroDeLinhasTableau = quantidadeRestricoes+1;
			tableauSemArtificial();
		}else {
			//numero de linhas do tableau sem variaveis artificiais
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
		
		//preenche o tableau com as restrições
		for(int i = 0; i < restricoes.length; i++) {
			for(int j = 0; j < restricoes[0].length-2; j++) {
					tableauPrimeiraFase[i+2][j+1] = Float.valueOf(restricoes[i][j]);
			}	
		}
		
		//preenche o tableau com os valores que estão na coluna da ultima variavel natural(não me pergunte o porquê)
		//preenche o tableau com os valores das variaveis da base na primeira coluna
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableauPrimeiraFase[i+2][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableauPrimeiraFase[i+2][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
		
		//variaveis criadas para auxiliar a colocar os valores das variaveis de folga e artificiais no tableau
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		//preenchimento do tableau com as variaveis de folga e artificiais
		for(int i = 0; i < quantidadeRestricoes; i++) {
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableauPrimeiraFase[i+2][posicaoDaFolga++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableauPrimeiraFase[i+2][posicaoDaFolga++] = (float) -1;
				tableauPrimeiraFase[i+2][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableauPrimeiraFase[i+2][posicaoDaArtificial++] = (float) 1;
			}
		}
		//chamada da primeira fase após a montagem do tableau
		primeiraFase();
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
				
		// preenchimento do tableau com as restrições
		for(int i = 1; i < numeroDeLinhasTableau; i++) {
			for(int j = 1; j < numeroDeColunasTableau; j++) {
				if(j < quantidadeVariaveisNaturais)
					tableauSegundaFase[i][j] = Float.valueOf(restricoes[i-1][j-1]);
			}	
		}
		
		//preenche o tableau com os valores que estão na coluna da ultima variavel natural(de novo não me pergunte o porquê)
		//preenche o tableau com os valores das variaveis da base na primeira coluna
		for(int i = 0; i < quantidadeRestricoes; i++) {
			tableauSegundaFase[i+1][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableauSegundaFase[i+1][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
		
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		//int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		
		//preenchimento do tableau com as variaveis de folga
		//eu reparei q eu tava tratando das artificiais na função que se chama tableau"""SEM"""artificiais ¯\_(ツ)_/¯
		for(int i = 0; i < quantidadeRestricoes; i++) {
			//if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) {
				tableauSegundaFase[i+1][posicaoDaFolga++] = (float) 1;
				/*}else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) {
				tableauSegundaFase[i+1][posicaoDaFolga++] = (float) 1;
				tableauSegundaFase[i+1][posicaoDaArtificial++] = (float) 1;
			}else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) {
				tableauSegundaFase[i+1][posicaoDaArtificial++] = (float) 1;
			}
	*/	}
		
		segundaFase();
	}
	
	/*
	 * Executa a primeira fase do algoritmo
	 */
	private static void primeiraFase() {
		//for que soma as linhas onde existem variaveis artificiais a linha da função artificial
		for(int i = 0; i < posicaoDaArtificial.size(); i++) {
			for(int j = 0; j < numeroDeColunasTableau; j++) {
				tableauPrimeiraFase[0][j] += tableauPrimeiraFase[posicaoDaArtificial.get(i)+2][j];
			} 
		}
		//variavel que só se torna verdadeira quando todas as colunas da primeira linha se tornam inferiores a zero
		boolean primeiraLinhaComValoresPositivos = false;
		
		//loop que só se encerra quando todas as colunas da primeira linha se tornam inferiores a zero
		while(!primeiraLinhaComValoresPositivos) {
			float maximo = 0;
			float minimo = Float.MAX_VALUE;
			int posicaoDoMaximo = 0;
			int posicaoDoMinimo = 0;
			//for para identificação da candidata que deve entrar, seu valor e sua posição
			//desempate é menor índice
			for(int i = 1; i < numeroDeColunasTableau; i++) {
				if(tableauPrimeiraFase[0][i]>maximo) {
					maximo = tableauPrimeiraFase[0][i];
					posicaoDoMaximo = i;
				}
			}
			
			//for para identificação da variavel que vai sair da base com o valor do quociente de menor valor e sua posição
			//desempate é menor índice
			for(int i = 2; i <numeroDeLinhasTableau; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] > 0 && tableauPrimeiraFase[i][0]/tableauPrimeiraFase[i][posicaoDoMaximo] < minimo) {
					minimo = tableauPrimeiraFase[i][0]/tableauPrimeiraFase[i][posicaoDoMaximo];
					posicaoDoMinimo = i;			
				}
			}
			
			//for para tornar o pivô em '1' dividindo toda a linha pelo pivô se necessario
			for(int i = 0; i < numeroDeColunasTableau; i++) {
				if(tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo] != 1) {
					tableauPrimeiraFase[posicaoDoMinimo][i] /= tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
				}
			}
			
			//for para zerar a coluna do pivô acima do pivô se já não for '0'
			for(int i = 0; i < posicaoDoMinimo; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] != 0) {
					float multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase = tableauPrimeiraFase[i][posicaoDoMaximo]/tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
					for(int j = 0; j < numeroDeColunasTableau; j++) {
						tableauPrimeiraFase[i][j] -= multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase*tableauPrimeiraFase[posicaoDoMinimo][j];
					}
				}
			}
			
			//for para zerar a coluna do pivô abaixo do pivô se já não for '0'
			for(int i = posicaoDoMinimo+1; i < numeroDeLinhasTableau; i++) {
				if(tableauPrimeiraFase[i][posicaoDoMaximo] != 0) {
					float multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase = tableauPrimeiraFase[i][posicaoDoMaximo]/tableauPrimeiraFase[posicaoDoMinimo][posicaoDoMaximo];
					
					for(int j = 0; j < numeroDeColunasTableau; j++) {
						tableauPrimeiraFase[i][j] -= multiplicadorDaLinhaParaZerarAColunaDaVariavelQueVaiEntrarNaBase*tableauPrimeiraFase[posicaoDoMinimo][j];
					}
				}
			}
			
			float maiorValorDaPrimeiraLinha = 0;
			//for para verificar qual o maior valor na primeira linha
			for(int i = 0; i < numeroDeColunasTableau; i++) {
				
				if(tableauPrimeiraFase[0][i] > maiorValorDaPrimeiraLinha) {
					maiorValorDaPrimeiraLinha = tableauPrimeiraFase[0][i];
				}
			}
			
			//se o maior valor for maior que '0' a primeira linha não esta nula e mais uma iteração será necessária
			if(maiorValorDaPrimeiraLinha > 0) {
				primeiraLinhaComValoresPositivos = false;
			//caso contrario o a primeira fase se encerra e não ocorrerá mais uma iteração
			}else {
				primeiraLinhaComValoresPositivos = true;
			}
		}
		tableauSegundaFase = new Float[numeroDeLinhasTableau-1][numeroDeColunasTableau-variaveisArtificiais];
		
		//caso a função artificial não tenha sido zerada temos que o conjunto solução é vazio
		if(tableauPrimeiraFase[0][0] != 0) {
			resultadoFinal = RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO;
		//caso contrario trascrevemos o tableauprimeirafase no tableausegundafase sem a linha da função artificial e as colunas das variaveis artificiais
		}else {
			for(int i = 0; i < numeroDeLinhasTableau-1; i++) {
				for(int j = 0; j < numeroDeColunasTableau-variaveisArtificiais; j++) {
					tableauSegundaFase[i][j] = tableauPrimeiraFase[i+1][j];
				}
			}
			//além de adequar o numero de linhas e colunas do tableau
			numeroDeLinhasTableau = numeroDeLinhasTableau-1;
			numeroDeColunasTableau = numeroDeColunasTableau-variaveisArtificiais;
			//e chamar a segunda fase
			segundaFase();
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
				
				resultadoFinal = RESULTADO_SEM_SOLUCAO_VAI_PARA_INFINITO;
				return;
			}
			
			imprimirTableau();
		}
	}
	
	/*
	 * Percorre a função objetivo (primeira linha do tableau) verificando
	 * se o valor de cada elemento é igual a zero. Se alguma variavel que
	 * não está na base possui valor zero, o problema admite infinitas soluções.
	 */
	private static boolean verificarSePossuiMultiplasSolucoes()
	{
		return true;
	}
	
	/*
	 * Encontra o valor máximo da primeira linha do tableau
	 */
	private static int encontrarIndiceColunaPivo()
	{
		float max = 0;
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
		
		// Percorre as linhas do tableau (menos a função objetivo)
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
	
	/*
	 * Apresenta o resultado final na saída padrão
	 */
	private static void imprimirResultadoFinal()
	{
		switch(resultadoFinal) {
			case RESULTADO_SOLUCAO_UNICA : System.out.println("Solução única"); break;
			case RESULTADO_SOLUCAO_MULTIPLA : System.out.println("Multiplas soluções"); break;
			case RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO : System.out.println("O tableau não possui nenhuma solução viavel"); break;
			case RESULTADO_SEM_SOLUCAO_VAI_PARA_INFINITO : System.out.println("Sem solução (z = -inf)"); break;
		}
	}
}
