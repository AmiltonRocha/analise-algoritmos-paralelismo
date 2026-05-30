import java.io.File;                       
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;                
import java.nio.file.Path;                 

class SerialCPU {                         

    public static void main(String[] args) {  // Ponto de entrada do programa.
        if (args.length < 2) {                // Verifica se vieram 2 argumentos: arquivo e palavra.
            System.err.println("Uso: java SerialCPU <arquivo.txt> <palavra>"); // Mensagem de uso.
            return;                            // Encerra o programa se faltou argumento.
        }

        String caminhoArquivo = args[0];       // Primeiro argumento: caminho do arquivo de texto.
        String palavra = args[1];              // Segundo argumento: palavra que será contada.

        if (palavra.isEmpty()) {               // Evita pesquisa com palavra vazia.
            System.err.println("A palavra não pode ser vazia."); // Mensagem de erro.
            return;                            // Encerra se a palavra estiver vazia.
        }

        File file = new File(caminhoArquivo);  // Cria objeto File para o caminho informado.
        if (!file.exists() || !file.isFile()) { // Checa se existe e se é realmente arquivo.
            System.err.println("Arquivo não encontrado ou não é um arquivo: " + caminhoArquivo);
            return;                            // Encerra se o caminho for inválido.
        }

        try {                                  // Bloco para tratar possíveis erros de leitura.
            String texto = Files.readString(Path.of(caminhoArquivo), StandardCharsets.UTF_8);
            // Lê TODO o conteúdo do arquivo como String usando UTF-8.

            long inicioNs = System.nanoTime(); // Marca o instante inicial (em nanossegundos).
            int ocorrencias = contarOcorrenciasPalavra(texto, palavra); // Faz a contagem serial.
            long fimNs = System.nanoTime();    // Marca o instante final (em nanossegundos).
            long ms = (fimNs - inicioNs) / 1_000_000L; // Converte duração para milissegundos.

            System.out.println("SerialCPU: " + ocorrencias + " ocorrências em " + ms + " ms");
            // Mostra o resultado no formato do trabalho.
        } catch (Exception e) {                // Captura erro de leitura ou processamento.
            System.err.println("Erro ao ler o arquivo: " + e.getMessage()); // Mostra erro.
        }
    }

    /**
     * Conta quantas vezes a palavra aparece no texto.
     * Regra usada: compara por token (palavra inteira), ignorando maiúsculas/minúsculas.
     */
    private static int contarOcorrenciasPalavra(String texto, String palavra) {
        int count = 0;                         // Acumulador de ocorrências.
        String alvo = palavra.toLowerCase();   // Normaliza palavra buscada para minúsculo.

        for (String token : texto.split("\\W+")) { // Quebra texto por separadores não alfanuméricos.
            if (!token.isEmpty() && token.toLowerCase().equals(alvo)) {
                // Se token não vazio e igual ao alvo (ignorando caixa), conta.
                count++;
            }
        }

        return count;                          // Retorna total encontrado.
    }
}