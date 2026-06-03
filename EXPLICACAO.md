# Explicacao dos Codigos - Analise Comparativa de Algoritmos com Paralelismo

---

## 1. SerialCPU.java

### Importacoes
- `java.io.File` - Manipulacao de arquivos
- `java.nio.charset.StandardCharsets` - Padrao de caracteres UTF-8
- `java.nio.file.Files` - Leitura de arquivos
- `java.nio.file.Path` - Caminho do arquivo

### Funcao: main(String[] args)
Ponto de entrada do programa.

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 9-12 | `if (args.length < 2)` | Verifica se foram passados 2 argumentos (arquivo e palavra). Se nao, mostra mensagem de uso e encerra |
| 14-15 | `caminhoArquivo = args[0]; palavra = args[1]` | Guarda o caminho do arquivo e a palavra em variaveis |
| 17-20 | `if (palavra.isEmpty())` | Verifica se a palavra nao esta vazia |
| 22-26 | `File file = new File(...)` | Verifica se o arquivo existe e e realmente um arquivo |
| 29 | `Files.readString(...)` | Le todo o conteudo do arquivo para uma String |
| 32 | `System.nanoTime()` | Marca o tempo inicial (em nanossegundos) |
| 33 | `contarOcorrenciasPalavra(texto, palavra)` | Chama a funcao que faz a contagem |
| 34 | `System.nanoTime()` | Marca o tempo final |
| 35 | Calculo do tempo | Converte nanossegundos para milissegundos |
| 37 | `System.out.println(...)` | Exibe o resultado |

### Funcao: contarOcorrenciasPalavra(String texto, String palavra)
Conta quantas vezes a palavra aparece no texto.

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 49 | `int count = 0` | Inicializa o contador |
| 50 | `palavra.toLowerCase()` | Normaliza a palavra para minusculo |
| 52 | `texto.split("\\W+")` | Divide o texto em palavras (separadores nao alfanumericos) |
| 53 | `token.toLowerCase().equals(alvo)` | Compara cada token (ignorando maiuscula/minuscula) |
| 55 | `count++` | Incrementa se for igual |
| 59 | `return count` | Retorna o total |

**Funcionamento:** Processa uma palavra por vez, em ordem, usando um loop simples (1 thread apenas).

---

## 2. ParallelCPU.java

### Importacoes
- `java.util.concurrent.*` - Threads e execucao paralela (ExecutorService, Future, Callable)
- `java.util.*` - List, ArrayList

### Funcao: main(String[] args)
Ponto de entrada do programa.

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 12-15 | `if (args.length < 2)` | Valida argumentos |
| 17-20 | `if (args[1].isEmpty())` | Valida palavra vazia |
| 22-23 | Variaveis | Guarda argumentos |
| 26-31 | `File arquivo = new File(...)` | Valida existencia do arquivo |
| 34-35 | `Files.readString(...)` | Le o arquivo |
| 36 | `texto.split("\\W+")` | Divide o texto em um array de palavras |
| 37 | `Runtime.getRuntime().availableProcessors()` | Descobre quantos nucleos a CPU tem (ex: 8 threads) |
| 40 | `Executors.newFixedThreadPool(nThreads)` | Cria um pool com N threads (uma para cada nucleo) |
| 41 | `palavra.toLowerCase()` | Normaliza a palavra alvo |
| 46 | `palavrasPorThread = totalPalavras / nThreads` | Divide o total de palavras pelo numero de threads |
| 47 | `System.nanoTime()` | Marca inicio |
| 53-67 | `for (int i = 0; i < nThreads; i++)` | Cria N tarefas, uma para cada parte do texto |
| 54-55 | `inicio` e `fim` | Define qual parte do array cada thread processa |
| 58-64 | `Callable<Integer> tarefa = () -> { ... }` | Cada tarefa conta palavras no seu pedaco |
| 65 | `executor.submit(tarefa)` | Envia a tarefa para o pool de threads executar |
| 69-71 | `for (Future<Integer> f : tarefasPendentes)` | Aguarda todas as threads terminarem e soma os resultados |
| 73 | `ms = ...` | Calcula o tempo |
| 75 | `executor.shutdown()` | Finaliza o pool |
| 77 | `System.out.println(...)` | Exibe o resultado |

**Funcionamento:** Divide o array de palavras em N partes (uma por nucleo da CPU) e processa cada parte em paralelo usando threads. No final, junta os resultados de todas as threads.

---

## 3. ParallelGPU.java

### Importacoes
- `org.jocl.*` - Biblioteca OpenCL para Java (bindings diretos da API C do OpenCL)
- `static org.jocl.CL.*` - Metodos estaticos do OpenCL

### Funcao: main(String[] args)
Ponto de entrada do programa.

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 11-28 | Validacoes | Valida argumentos, palavra vazia e existencia do arquivo (igual aos outros) |
| 30 | `Files.readString(...)` | Le o arquivo |
| 31-33 | Conversao para bytes | Converte texto e palavra para `byte[]` (a GPU so trabalha com dados brutos) |

### Inicializacao do OpenCL (linhas 35-45)

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 36-38 | `clGetPlatformIDs(...)` | Descobre a plataforma OpenCL (NVIDIA, Intel, AMD) |
| 40-42 | `clGetDeviceIDs(...)` | Escolhe o dispositivo GPU |
| 44 | `clCreateContext(...)` | Cria o contexto OpenCL (conexao com a GPU) |
| 45 | `clCreateCommandQueue(...)` | Cria a fila de comandos (por onde enviamos instrucoes para a GPU) |

### Kernel OpenCL (linhas 47-63)
Codigo em C que roda na GPU:

```
__kernel void contar(...) {
    int id = get_global_id(0);  // ID unico desta thread na GPU
    if (id >= textoLen) return; // Se passou do tamanho do texto, sai
    // Verifica se a palavra comeca nesta posicao 'id'
    for (int i = 0; i < palavraLen; i++) {
        if (texto[id + i] != palavra[i]) { match = 0; break; }
    }
    if (match) {
        atomic_inc(resultado);  // Incrementa o contador com seguranca
    }
}
```

| Linha | Codigo | Explicacao |
|-------|--------|------------|
| 65-67 | `clCreateProgramWithSource`, `clBuildProgram`, `clCreateKernel` | Compila o kernel C e cria o objeto executavel |
| 70-72 | `clCreateBuffer(...)` | Cria buffers na memoria da GPU (texto, palavra, resultado) |
| 75-76 | `clEnqueueWriteBuffer(...)` | Envia o valor 0 para o buffer de resultado na GPU |
| 79-83 | `clSetKernelArg(...)` | Passa os parametros para o kernel (buffer texto, tamanho, buffer palavra, tamanho, buffer resultado) |
| 86-89 | **Execucao** | Marca tempo, executa o kernel com milhares de threads paralelas na GPU (`globalSize = textoBytes.length`), espera finalizar |
| 93-94 | `clEnqueueReadBuffer(...)` | Le o resultado da GPU de volta para a memoria RAM |
| 97 | `System.out.println(...)` | Exibe o resultado |
| 100-106 | `clRelease*()` | Libera os recursos da GPU |

**Funcionamento:** Cria milhares de threads na GPU (uma para cada byte do texto). Cada thread verifica se a palavra buscada comeca na posicao que ela e responsavel. Se encontrar, incrementa um contador atomico.

---

## Resumo das Diferencas

| Caracteristica | SerialCPU | ParallelCPU | ParallelGPU |
|----------------|-----------|-------------|-------------|
| **Onde roda** | 1 nucleo da CPU | Todos os nucleos da CPU | Placa de video (GPU) |
| **Paralelismo** | Nenhum (sequencial) | Threads (1 por nucleo) | Milhares de threads |
| **Metodo de busca** | `split("\\W+")` por token | `split("\\W+")` por token | Comparacao byte a byte |
| **Tecnologia** | Java puro | `ExecutorService` | OpenCL (jocl) |
| **Ideal para** | Textos pequenos | Textos medios/grandes | Textos muito grandes |

---

## Como Compilar e Executar

### Compilar todos
```
javac -cp "jocl-2.0.4\jocl-2.0.4.jar" -d bin src\SerialCPU.java src\ParallelCPU.java src\ParallelGPU.java
```

### Executar SerialCPU
```
java -cp bin SerialCPU Amostras\MobyDick-217452.txt the
```

### Executar ParallelCPU
```
java -cp bin ParallelCPU Amostras\MobyDick-217452.txt the
```

### Executar ParallelGPU
```
java -cp "jocl-2.0.4\jocl-2.0.4.jar;bin" ParallelGPU Amostras\MobyDick-217452.txt the
```
