import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class Main 
{
	/*
	 * Cores usadas para printar
	 */
	static final String ANSI_RESET = "\u001B[0m";
	static final String ANSI_VERMELHO = "\u001B[31m";
	static final String ANSI_VERDE = "\u001B[32m";
	static final String ANSI_AMARELO = "\u001B[33m";
	static final String ANSI_AZUL = "\u001B[34m";
	static final String ANSI_BRANCO = "\u001B[37m";
	static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
			
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
	public static Float[][] tableau;
	public static float[] funcaoObjetivo;
	public static int variaveisDeFolga = 0;
	public static int variaveisDeExcesso = 0;
	public static int variaveisArtificiais = 0;
	public static int numeroDeLinhasTableau;
	public static int numeroDeColunasTableau;
	public static List<Integer> posicaoDaArtificial = new ArrayList<>();
	public static int resultadoFinal = RESULTADO_SOLUCAO_UNICA;
	public static boolean solucaoDegenerada;
	public static int[] indicesDasVariaveisBasicas;
	public static int[] indicesDasVariaveisArtificais;

	/*
	 * Função principal
	 */
	public static void main(String[] args) 
	{
		lerEntradas();
		imprimirEntradas();
		analisarEntradas();
		criarTableau();
		if(variaveisArtificiais != 0) 
		{
			imprimirTableau("PRIMEIRO TABLEAU", -1, -1, 1);
			minimizarFuncaoArtificial();
			primeiraFase();
			if(resultadoFinal != RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO) 
			{
				atualizarTableau();
				retirarVariaveisArtificiaisDaBase();
				imprimirTableau("", -1, -1, 2);
				segundaFase(); 
			}
		} 
		else 
		{
			imprimirTableau("PRIMEIRO TABLEAU", -1, -1, 2);
			segundaFase(); 
			imprimirTableau("", -1, -1, 2);
		}
		if((resultadoFinal == RESULTADO_SOLUCAO_UNICA || resultadoFinal == RESULTADO_SOLUCAO_MULTIPLA) && solucaoDegenerada == false)
		{ 
			verificarSeSolucaoDegenerada();
		}
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
		scanner.useLocale(Locale.US);
		
		tipoProblema = scanner.nextLine();
		quantidadeRestricoes = scanner.nextInt();
		quantidadeVariaveisNaturais = scanner.nextInt();
		restricoes = new String[quantidadeRestricoes][quantidadeVariaveisNaturais+2];
		funcaoObjetivo = new float[quantidadeVariaveisNaturais];
		
		// Preenche a função objetivo
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) 
		{
			funcaoObjetivo[i] = scanner.nextFloat();
		}
		scanner.nextLine();
		
		// Preenche as restrições
		for(int i = 0; i < quantidadeRestricoes; i++) 
		{
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
		System.out.println("\n\n\n================================ ENTRADAS ================================\n\n\n");
		System.out.println("Tipo do problema: "+tipoProblema);
		System.out.println("Quantidade de Restrições: "+quantidadeRestricoes);
		System.out.println("Quantidade de Variáveis Naturais: "+quantidadeVariaveisNaturais);
		System.out.print("Função Objetivo: ");
		
		// Imprime a função objetivo
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) 
		{
			System.out.print(funcaoObjetivo[i]+"   ");
		}
		System.out.println();
	}
	
	/*
	 * Verifica a necessidade de multiplicar uma restrição por -1. Além disso,
	 * verifica a necessidade de inserção de variáveis de folga, excesso ou artificiais.
	 */
	private static void analisarEntradas() 
	{
		// Verifica se algum valor depois de <=, =, >= é negativo
		for(int i=0; i<quantidadeRestricoes; i++) {
			
			if(Float.valueOf(restricoes[i][restricoes[0].length-1]) < 0) 
			{	
				restricoes[i][restricoes[0].length-1] = String.valueOf((Float.valueOf(restricoes[i][restricoes[0].length-1])*(-1)));
				
				for(int j=0; j<quantidadeVariaveisNaturais; j++) 
				{
					restricoes[i][j] = String.valueOf((Float.valueOf(restricoes[i][j])*(-1)));
				}
				if(restricoes[i][restricoes[0].length-2].equals(">=")) 
				{
					restricoes[i][restricoes[0].length-2] = "<=";
				}
				if(restricoes[i][restricoes[0].length-2].equals("<=")) 
				{
					restricoes[i][restricoes[0].length-2] = ">=";
				}
			}
		}
		
		// Conta quantas variáveis de cada tipo serão necessárias
		for(int i = 0; i < quantidadeRestricoes; i++) 
		{
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) 
			{
				variaveisDeFolga++;
			}
			else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) 
			{
				variaveisDeExcesso++;
				variaveisArtificiais++;
				posicaoDaArtificial.add(i);
			}
			else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) 
			{	
				variaveisArtificiais++;
				posicaoDaArtificial.add(i);
			}
		}
	}
	
	/*
	 * Cria o tableau
	 */
	private static void criarTableau()
	{
		numeroDeColunasTableau = quantidadeVariaveisNaturais + variaveisArtificiais + variaveisDeExcesso + variaveisDeFolga + 1;
		numeroDeLinhasTableau = (variaveisArtificiais == 0) ? quantidadeRestricoes+1 : quantidadeRestricoes+2;
		
		int indiceFuncaoObjetivo = (variaveisArtificiais == 0) ? 0 : 1;
		int indicePrimeiraRestricao = indiceFuncaoObjetivo+1;	
		
		// Cria o tableau
		tableau = new Float[numeroDeLinhasTableau][numeroDeColunasTableau];
		
		// Preenche o tableau com zeros
		for(int i = 0; i < numeroDeLinhasTableau; i++) 
		{
			for(int j = 0; j < numeroDeColunasTableau; j++)	
			{
				tableau[i][j] = (float) 0;
			}
		}
		
		// Insere a função objetivo no tableau
		for(int i = 0; i < quantidadeVariaveisNaturais; i++) 
		{
			tableau[indiceFuncaoObjetivo][i+1] = ((tipoProblema.equals("max")) ? funcaoObjetivo[i] : funcaoObjetivo[i]*(-1));
		}
		
		if(variaveisArtificiais != 0) 
		{
			// Insere a função artificial no tableau (se for o caso)
			for(int i = 0; i < variaveisArtificiais; i ++) 
			{
				tableau[0][quantidadeVariaveisNaturais+variaveisDeExcesso+variaveisDeFolga+1+i] = (float) -1;
			}
			
			// Coloca as restrições no tableau
			for(int i = 0; i < restricoes.length; i++) 
			{
				for(int j = 0; j < restricoes[0].length-2; j++) 
				{
						tableau[i+2][j+1] = Float.valueOf(restricoes[i][j]);
				}	
			}
		}
		else
		{
			// Insere as restrições no tableau
			for(int i = 1; i < numeroDeLinhasTableau; i++) 
			{
				for(int j = 1; j < numeroDeColunasTableau; j++) 
				{
					if(j < quantidadeVariaveisNaturais)
						tableau[i][j] = Float.valueOf(restricoes[i-1][j-1]);
					else
						tableau[i][j] = (float) 0;
				}	
			}
		}
		
		// Insere os valores do vetor do vetor b (respostas) e ...
		for(int i = 0; i < quantidadeRestricoes; i++) 
		{
			tableau[i+indicePrimeiraRestricao][quantidadeVariaveisNaturais] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais-1]);
			tableau[i+indicePrimeiraRestricao][0] = Float.valueOf(restricoes[i][quantidadeVariaveisNaturais+1]);
		}
		
		int posicaoDaFolga = quantidadeVariaveisNaturais+1;
		int posicaoDaArtificial = numeroDeColunasTableau - variaveisArtificiais;
		
		for(int i = 0; i < quantidadeRestricoes; i++) 
		{
			if(restricoes[i][quantidadeVariaveisNaturais].equals("<=")) 
			{
				tableau[i+indicePrimeiraRestricao][posicaoDaFolga++] = (float) 1;
			} 
			else if(restricoes[i][quantidadeVariaveisNaturais].equals(">=")) 
			{
				tableau[i+indicePrimeiraRestricao][posicaoDaFolga++] = (float) -1;
				tableau[i+indicePrimeiraRestricao][posicaoDaArtificial++] = (float) 1;
			}
			else if(restricoes[i][quantidadeVariaveisNaturais].equals("=")) 
			{
				tableau[i+indicePrimeiraRestricao][posicaoDaArtificial++] = (float) 1;
			}
		}

		preencherVetorIndicesVariaveisBasicas(indicePrimeiraRestricao);
		imprimirVetorIndicesVariaveisBasicas();
		preencherVetorIndicesVariaveisArtificiais();
		imprimirVetorIndicesVariaveisArtificiais();
	}
	
	/*
	 * Realiza a primeira fase do algoritmo
	 */
	private static void primeiraFase()
	{
		// Encontra os indices referentes ao pivo
		int indiceColunaPivo = encontrarIndiceColunaPivo();
		int indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo, 1);
		
		imprimirTableau("PRIMEIRA FASE", indiceLinhaPivo, indiceColunaPivo, 1);

		// Enquanto existir uma coluna pivo
		while(indiceColunaPivo != -1)
		{
			// Se existir uma linha pivo
			if(indiceLinhaPivo != -1)
			{
				// Coloca o indice da coluna escolhida no vetor de índices das variaveis básicas
				indicesDasVariaveisBasicas[indiceLinhaPivo-2] = indiceColunaPivo;
				
				// Escalona o tableau
				escalonarTableau(indiceColunaPivo, indiceLinhaPivo);
				
				// Atualiza indices referentes ao pivo
				indiceColunaPivo = encontrarIndiceColunaPivo();
				indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo, 1);

				imprimirTableau("", indiceLinhaPivo, indiceColunaPivo, 1);
			}
			else
			{
				break;
			}
		}
		
		if(tableau[0][0] != 0) {resultadoFinal = RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO; return;}
	}
	
	/*
	 * Realiza a segunda fase do algoritmo
	 */
	private static void segundaFase() 
	{
		// Encontra os indices referentes ao pivo
		int indiceColunaPivo = encontrarIndiceColunaPivo();
		int indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo, 0);
		
		imprimirTableau("SEGUNDA FASE", indiceLinhaPivo, indiceColunaPivo, 2);

		// Enquanto existir uma coluna pivo
		while(indiceColunaPivo != -1)
		{
			// Se existir uma linha pivo
			if(indiceLinhaPivo != -1)
			{
				// Coloca o indice da coluna escolhida no vetor de índices das variaveis básicas
				indicesDasVariaveisBasicas[indiceLinhaPivo-1] = indiceColunaPivo;
				
				// Escalona o tableau
				escalonarTableau(indiceColunaPivo, indiceLinhaPivo);
				
				// Atualiza indices referentes ao pivo
				indiceColunaPivo = encontrarIndiceColunaPivo();
				indiceLinhaPivo = encontrarIndiceLinhaPivo(indiceColunaPivo, 0);
				
				imprimirTableau("", indiceLinhaPivo, indiceColunaPivo, 2);
			}
			else
			{
				resultadoFinal = RESULTADO_SEM_SOLUCAO_VAI_PARA_INFINITO;
				return;
			}
		}
		
		if(verificarSePossuiMultiplasSolucoes()) 
		{
			resultadoFinal = RESULTADO_SOLUCAO_MULTIPLA;
		}
		return;
	}
	
	/*
	 * Soma as linhas das variáveis artificiais à linha da função objetivo
	 */
	private static void minimizarFuncaoArtificial()
	{
		for(int k=0; k<indicesDasVariaveisArtificais.length; k++)
		{
			for(int i=2; i<numeroDeLinhasTableau; i++)
			{
				if(tableau[i][indicesDasVariaveisArtificais[k]] == 1.0) 
				{	
					for(int j=0; j<numeroDeColunasTableau; j++) 
					{
						tableau[0][j] += tableau[i][j];
					}
				}
			}
		}
	}
	
	/*
	 * Retira as variáveis artificiais da base, caso existam
	 */
	private static void retirarVariaveisArtificiaisDaBase()
	{
		List<Integer> indicesDasLinhasAseremRemovidas = new ArrayList<>();
		
		// Percorre as variaveis artificiais
		for(int i=0; i<indicesDasVariaveisArtificais.length; i++)
		{
			// Percorre as variaveis basicas
			for(int j=0; j<indicesDasVariaveisBasicas.length; j++)
			{
				// Se a variável artificial é básica
				if(indicesDasVariaveisArtificais[i] == indicesDasVariaveisBasicas[j])
				{
					solucaoDegenerada = true;
					
					int indiceLinhaPivo = j+1;
					int indiceColunaPivo = -1;
					
					// Escolhe uma coluna pivo
					for(int k=1; k<numeroDeColunasTableau; k++)
					{
						for(int l=0; l<indicesDasVariaveisBasicas.length; l++)
						{
							if(k != indicesDasVariaveisBasicas[l] && tableau[indiceLinhaPivo][k] != 0)
							{
								indiceColunaPivo = k;
								break;
							}
						}
						if(indiceColunaPivo>0) {break;}
					}
					
					// Se foi encontrada uma coluna pivo
					if(indiceColunaPivo > 0)
					{
						// Escalona o tableau
						escalonarTableau(indiceColunaPivo, indiceLinhaPivo);
						
						// Coloca o indice da coluna escolhida no vetor de índices das variaveis básicas
						indicesDasVariaveisBasicas[indiceLinhaPivo-1] = indiceColunaPivo;
					}
					else
					{
						indicesDasLinhasAseremRemovidas.add(j);
					}
				}
			}
		}
		
		// Retira da base
		int[] novoVetorIndicesDasVariaveisBasicas = new int[indicesDasVariaveisBasicas.length - indicesDasLinhasAseremRemovidas.size()];
		int posicao = 0;
		for(int i=0; i<indicesDasVariaveisBasicas.length; i++)
		{
			if(!indicesDasLinhasAseremRemovidas.contains(i))
			{
				novoVetorIndicesDasVariaveisBasicas[posicao] = indicesDasVariaveisBasicas[i];
				posicao++;
			}
		}
		indicesDasVariaveisBasicas = novoVetorIndicesDasVariaveisBasicas;
		
		// Remove as linhas
		for(int indice : indicesDasLinhasAseremRemovidas)
		{
		      removerLinhaTableau(indice+1);
		}
	}
	
	/*
	 * Remove uma linha do tableau
	 */
	private static void removerLinhaTableau(int indiceLinha)
	{
		// Cria uma nova instância do tableau
		Float[][] tableauNovo = new Float[numeroDeLinhasTableau-1][numeroDeColunasTableau];
		int posicao=0;
		
		for(int i = 0; i < numeroDeLinhasTableau; i++) 
		{
			for(int j = 0; j < numeroDeColunasTableau; j++) 
			{
				if(i!=indiceLinha) { tableauNovo[posicao][j] = tableau[i][j]; }
			}
			if(i!=indiceLinha) {posicao++;}
		}
		tableau = tableauNovo;
		numeroDeLinhasTableau--;
		
	}
	
	/*
	 * Percorre a função objetivo (primeira linha do tableau) verificando
	 * se o valor de cada elemento é igual a zero. Se alguma variavel que
	 * não está na base possui valor zero, o problema admite infinitas soluções.
	 */
	private static boolean verificarSePossuiMultiplasSolucoes()
	{
		// Itera sobre a primeira linha, a partir da segunda coluna
		for(int i = 1; i < numeroDeColunasTableau; i++)
		{
			boolean estaNaBase = false;
			
			// Se o valor do elemento for zero
			if(tableau[0][i] == 0)
			{
				// Verifica se este elemento está na base
				for(int j = 0; j < indicesDasVariaveisBasicas.length; j++)
				{
					// Se está na base
					if(indicesDasVariaveisBasicas[j] == i)
					{
						estaNaBase = true;
					}
				}
				
				if(!estaNaBase){ return true; }
			}
		}

		return false;
	}
	
	/*
	 * Percorre o vetor b (resultados) e verifica se alguma variavel básica é nula.
	 */
	private static void verificarSeSolucaoDegenerada() 
	{
		// Itera sobre a primeira coluna, a partir da segunda linha
		for(int i = 1; i < numeroDeLinhasTableau; i++)
		{
			// Se o valor do elemento for zero
			if(tableau[i][0] == 0) { solucaoDegenerada = true; return; }
		}
		solucaoDegenerada = false;
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
			if (tableau[0][i] > max)
			{
				// Atualiza máximo e indice
				max = tableau[0][i];
				indice = i;
			}
		}
		return indice;
	}
	
	/*
	 * Encontra o mínimo da divisão entre o item coluna de 
	 * resultados pelo item referente na coluna pivô
	 */
	private static int encontrarIndiceLinhaPivo(int indiceColunaPivo, int indiceFuncaoObjetivo)
	{
		float min = Float.MAX_VALUE;
		int indice = -1;
		
		// Se não existe coluna pivo
		if(indiceColunaPivo == -1){ return -1;}
		
		// Percorre as linhas do tableau (menos a função objetivo)
		for(int i = indiceFuncaoObjetivo+1; i < numeroDeLinhasTableau; i++)
		{
			if (
				// Se o elemento da linha e coluna pivo for maior que zero e
				tableau[i][indiceColunaPivo] > 0 && 
				
				// o elemento referente na coluna de resultdos for maior ou igual a zero e
				tableau[i][0] >= 0 && 
				
				// a divisão entre eles for menor que o mínimo já encontrado
				tableau[i][0]/tableau[i][indiceColunaPivo] < min
			)
			{
				// Atualiza o mínimo e o indice
				min = tableau[i][0]/tableau[i][indiceColunaPivo];
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
		// Elemento pivo
		float elementoPivo = tableau[indiceLinhaPivo][indiceColunaPivo];
		
		// Divide a linha pivo pelo elemento pivo
		for(int j=0; j<numeroDeColunasTableau; j++)
		{
			tableau[indiceLinhaPivo][j] = tableau[indiceLinhaPivo][j] / elementoPivo;  
		}
		
		// Escalona o tableau
		for(int i=0; i<numeroDeLinhasTableau; i++)
		{
			if(i==indiceLinhaPivo){continue;}
			
			float divisor = tableau[i][indiceColunaPivo];
			
			for(int j=0; j<numeroDeColunasTableau; j++)
			{
				tableau[i][j] = (float) ((-1)*divisor*tableau[indiceLinhaPivo][j] + tableau[i][j]);  
			}
		}
	}
	
	/*
	 * Remove a função artificial e as colunas das variáveis artificiais
	 */
	private static void atualizarTableau()
	{
		// Cria uma nova instância do tableau
		Float[][] tableauNovo = new Float[numeroDeLinhasTableau-1][numeroDeColunasTableau-variaveisArtificiais];
		
		for(int i = 1; i < numeroDeLinhasTableau; i++) 
		{
			for(int j = 0; j < numeroDeColunasTableau-variaveisArtificiais; j++) 
			{
				tableauNovo[i-1][j] = tableau[i][j];
			}
		}
		tableau = tableauNovo;
		numeroDeLinhasTableau--;
		numeroDeColunasTableau -= variaveisArtificiais;
	}
	
	/*
	 * Preenche o vetor com os índices das variáveis básicas
	 */
	private static void preencherVetorIndicesVariaveisBasicas(int indicePrimeiraRestricao)
	{
		indicesDasVariaveisBasicas = new int[quantidadeRestricoes];

		for(int i=indicePrimeiraRestricao; i<numeroDeLinhasTableau; i++) 
		{
			for(int j=1; j<numeroDeColunasTableau; j++) 
			{
				if(tableau[i][j] == 1) 
				{
					boolean pertenceBase = true;
					
					for(int k=indicePrimeiraRestricao; k<numeroDeLinhasTableau; k++)
					{
						if(i==k){continue;}
						if(tableau[k][j] != 0) {pertenceBase = false; break;} 
					}
					
					if(pertenceBase) { indicesDasVariaveisBasicas[i-indicePrimeiraRestricao] = j; }
				}
			}
		}		
	}
	
	/*
	 * Preenche o vetor com os índices das variáveis artificiais
	 */
	private static void preencherVetorIndicesVariaveisArtificiais() 
	{
		indicesDasVariaveisArtificais = new int[variaveisArtificiais];
		int posicaoNoVetor = 0;
		
		for(int j=numeroDeColunasTableau-1; j>=0; j--)
		{
			if(tableau[0][j] == -1) { indicesDasVariaveisArtificais[posicaoNoVetor] = j; posicaoNoVetor++;}
		}
	}
	
	/*
	 * Imprime o tableau na saída padrão
	 */
	private static void imprimirTableau(String titulo, int indiceLinha, int indiceColuna, int fase)
	{	
		// Imprime o título
		System.out.println((titulo != "") ? "\n\n\n================================ "+titulo+" ================================\n\n\n" : "\n\n\n");
		
		// Imprime indicativo de variáveis artificiais
		Arrays.sort(indicesDasVariaveisArtificais);
		System.out.print("\t");
		int cont = 0;
		for(int j=0; j<tableau[0].length && cont<indicesDasVariaveisArtificais.length; j++) {
			if(j == indicesDasVariaveisArtificais[cont]) {
				System.out.print(ANSI_PURPLE_BACKGROUND+ANSI_BRANCO+"ART"+ANSI_RESET+"\t");
				cont++;
			} else {
				System.out.print("\t");
			}
		}
		System.out.println();
		
		// Imprime nome das variáveis
		for(int j=0; j<=tableau[0].length; j++) {
			if(j>1) {
				System.out.print(ANSI_BLUE_BACKGROUND+ANSI_BRANCO+"x"+(j-1)+ANSI_RESET+"\t");
			} else {
				System.out.print("\t");
			}
		}
		System.out.println();
		
		// Imprime o tableau
		int count = 0;
		for(int i = 0; i < tableau.length; i++) 
		{
			// Imprime a primeira coluna
			if(fase == 1) {
				if(i==0) { System.out.print(ANSI_GREEN_BACKGROUND+ANSI_BRANCO+"za*"+ANSI_RESET+"\t"); }
				if(i==1) { System.out.print(ANSI_GREEN_BACKGROUND+ANSI_BRANCO+"z*"+ANSI_RESET+"\t"); }
				if(i>1) {
					System.out.print(ANSI_BLUE_BACKGROUND+ANSI_BRANCO+"x"+indicesDasVariaveisBasicas[count]+ANSI_RESET+"\t");
					count++;
				}
			}
			if(fase == 2) {
				if(i==0) { System.out.print(ANSI_GREEN_BACKGROUND+ANSI_BRANCO+"z*"+ANSI_RESET+"\t"); }
				if(i>0) {
					System.out.print(ANSI_BLUE_BACKGROUND+ANSI_BRANCO+"x"+indicesDasVariaveisBasicas[count]+ANSI_RESET+"\t");
					count++;
				}
			}
			
			// Imprime demais colunas
			for(int j = 0; j < tableau[0].length; j++) 
			{
				System.out.print((i == indiceLinha && j == indiceColuna) ? ANSI_YELLOW_BACKGROUND+ANSI_VERMELHO+String.format("%.3f", tableau[i][j])+ANSI_RESET+"\t" : String.format("%.3f", tableau[i][j])+"\t");
			}
			System.out.println();
		}
	}
	
	/*
	 * Imprime o vetor que contém os índices das variáveis básicas
	 */
	private static void imprimirVetorIndicesVariaveisBasicas()
	{
		System.out.print("Índices das variáveis Básicas: ( ");
		
		for(int i = 0; i < indicesDasVariaveisBasicas.length; i++) 
		{
			System.out.print(indicesDasVariaveisBasicas[i]+" ");
		}
		System.out.println(")");
	}
	
	/*
	 * Imprime o vetor que contém os índices das variáveis artificiais
	 */
	private static void imprimirVetorIndicesVariaveisArtificiais()
	{
		System.out.print("Índices das variáveis Artificiais: ( ");
		
		for(int i = 0; i < indicesDasVariaveisArtificais.length; i++) 
		{
			System.out.print(indicesDasVariaveisArtificais[i]+" ");
		}
		System.out.println(")");
	}

	/*
	 * Imprime na saída padrão o vetor de soluções
	 */	
	private static void imprimirVetorSolucoes()
	{
		float[] vetorSolucoes = new float[numeroDeColunasTableau-1];
		int posicao = 1;
		
		for(int i=0; i<indicesDasVariaveisBasicas.length; i++) 
		{
			vetorSolucoes[indicesDasVariaveisBasicas[i]-1] = tableau[posicao][0]+0;
			posicao++;
		}
		
		System.out.print("( ");
		
		for(int i=0; i<vetorSolucoes.length; i++) 
		{
			System.out.print(String.format("%.3f", vetorSolucoes[i])+" ");
		}
		System.out.println(")");
	}
	
	/*
	 * Imprime na saída padrão o valor da função objetivo
	 */
	private static void imprimirValorFuncaoObjetivo()
	{
		float z = (tipoProblema.equals("max")) ? tableau[0][0]*(-1) : tableau[0][0];
		
		System.out.println(String.format("%.3f", z));
	}
	
	/*
	 * Apresenta o resultado final na saída padrão
	 */ 
	private static void imprimirResultadoFinal()
	{
		System.out.println("\n\n\n================================ RESULTADO ================================\n\n\n");
		
		switch(resultadoFinal) 
		{
			case RESULTADO_SOLUCAO_UNICA : 
				System.out.print(ANSI_GREEN_BACKGROUND+ANSI_BRANCO+"z*"+ANSI_RESET+"="); imprimirValorFuncaoObjetivo();
				System.out.print(ANSI_BLUE_BACKGROUND+ANSI_BRANCO+"x*"+ANSI_RESET+"="); imprimirVetorSolucoes();
				System.out.print("Única");
				if(solucaoDegenerada) { System.out.println(" e Degenerada"); } else { System.out.println();}
				break;
				
			case RESULTADO_SOLUCAO_MULTIPLA : 
				System.out.print(ANSI_GREEN_BACKGROUND+ANSI_BRANCO+"z*"+ANSI_RESET+"="); imprimirValorFuncaoObjetivo();
				System.out.print(ANSI_BLUE_BACKGROUND+ANSI_BRANCO+"x*"+ANSI_RESET+"="); imprimirVetorSolucoes();
				System.out.print("Múltipla"); 
				if(solucaoDegenerada) { System.out.println(" e Degenerada"); } else { System.out.println();}
				break;
				
			case RESULTADO_SEM_SOLUCAO_CONJUNTO_VAZIO : 
				System.out.println("Conjunto vazio"); 
				break;
				
			case RESULTADO_SEM_SOLUCAO_VAI_PARA_INFINITO :
				if(tipoProblema.equals("max")) { System.out.println("z* -> +infinito"); } else { System.out.println("z* -> -infinito"); }
				break;
		}
		System.out.println("\n\n\n");
	}
}

