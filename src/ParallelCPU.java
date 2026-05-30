import java.io.File; // ira verificar se o arquivo existe e se é um arquivo
import java.nio.charset.StandardCharsets; //garantir encoding UTF-8 
import java.nio.file.Files; //leitura do arquivo
import java.nio.file.Path; //caminho do arquivo
import java.util.concurrent.*; //threads e execução paralela
import java.util.*;

class ParallelCPU {
 
 public static void main(String[] args) {
   // caso não digite 2 argumento vai retornar um erro.
 if (args.length <2){
     System.err.println("Uso: java ParallelCPU <arquivo.txt> <palavra>");
     return;
 }
  //validação de palavra vazia
  if (args[1].isEmpty()){
    System.err.println("A palavra não pode ser vazia");
    return;
  }
 // Nesses Strings passei o caminho do arquivo e a palavra para buscar
 String caminhoArquivo = args[0];
 String palavra = args[1];

//Verificar se o arquivo existe
  File arquivo = new File(caminhoArquivo);
  if (!arquivo.exists()|| !arquivo.isFile()){
    System.err.println("Arquivo não encontrado ou não é um arquivo: " + caminhoArquivo);
    return;

  }
    // Tratamento de erro 
  try {
    String texto = Files.readString(Path.of(caminhoArquivo), 
    StandardCharsets.UTF_8);
    String[] palavras = texto.split("\\W+"); // quebra o texto
    int nThreads = Runtime.getRuntime().availableProcessors(); // vai pegar o nucleo da CPU para criar threads

     // vou criar um pool para as threads atraves da função newFixedThreadPool
     ExecutorService executor = Executors.newFixedThreadPool(nThreads);
     String alvo = palavra.toLowerCase(); // palavra para buscar

     // Quebra as palavras e iniciar o tempo
     int totalPalavras = palavras.length;
     // Calculo de quantas palavras vai ser calculadas por threads
     int palavrasPorThread = (int) Math.ceil((double) totalPalavras / nThreads);
      long inicioConometro = System.nanoTime();

      List<Future<Integer>> tarefasPendentes = new ArrayList<>();

       // Criação das tarefas paralelas

  for (int i = 0; i < nThreads; i++) {
            final int inicio = i * palavrasPorThread;
            final int fim = Math.min(inicio + palavrasPorThread, totalPalavras);

            //Criação das tarefas para as thread
          Callable<Integer> tarefa = () -> {
                int count = 0;
                for (int j = inicio; j < fim; j++) {
                    if (palavras[j].equalsIgnoreCase(alvo)) count++;
                }
                return count;
            };
           tarefasPendentes.add(executor.submit(tarefa));

        }
        int total = 0;
        for (Future<Integer> f : tarefasPendentes) {
            total += f.get();
        }
         long fimConometro = System.nanoTime();
        long ms = (fimConometro - inicioConometro) / 1_000_000L;

        executor.shutdown();

        System.out.println("ParallelCPU: " + total + " ocorrências em " + ms + " ms");



 // pega o numero de threads disponiveis
  }
  catch (Exception e){
    System.err.println("Erro ao ler o arquivo: " + e.getMessage());
    return;
  } 
}

}
