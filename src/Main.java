import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main 
{	
	public static String tipoProblema;
	public static int quantidadeRestricoes;
	public static int quantidadeVariaveisNaturais;

	public static void main(String[] args) 
	{
		lerArquivo(args[0]);

	}
	
	private static void lerArquivo(String nomeArquivo)
	{
		try 
		{
			File arquivo = new File(nomeArquivo);
			FileReader leitorArquivo = new FileReader(arquivo);
			BufferedReader bufferLeitor = new BufferedReader(leitorArquivo);
			
			tipoProblema = bufferLeitor.readLine();
			String dimensoes[] = bufferLeitor.readLine().split(" ");
			quantidadeRestricoes = Integer.parseInt(dimensoes[0]);
			quantidadeVariaveisNaturais = Integer.parseInt(dimensoes[1]);
			
			System.err.println(tipoProblema + quantidadeRestricoes + quantidadeVariaveisNaturais);
			
			leitorArquivo.close();
		} 
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
