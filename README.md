# Análise Comparativa de Algoritmos com Uso de Paralelismo

## Resumo
Este trabalho propõe uma análise detalhada do desempenho de diferentes algoritmos de busca em ambientes seriais e paralelos, utilizando a linguagem de programação Java. A busca por eficiência computacional é essencial em diversas aplicações, e entender como diferentes algoritmos se comportam em diferentes cenários de processamento é de suma importância. Neste estudo, serão abordados três algoritmos: serial, paralelo em CPU e paralelo em GPU.

Serão realizadas análises comparativas utilizando textos como conjuntos de dados de entrada para a contagem de busca de uma palavra. Os resultados serão registrados em arquivos CSV, permitindo uma análise visual através de gráficos ou processamento adicional utilizando Java.

## Introdução
Foram implementados três métodos para contagem de ocorrências de uma palavra em arquivos de texto:

### SerialCPU
Versão serial na CPU: Utiliza um loop simples para iterar sobre cada palavra do texto e contar as ocorrências.

### ParallelCPU
Versão paralela na CPU: Utiliza um pool de threads (`ExecutorService`) para dividir o texto em partes e contar as palavras em paralelo, aproveitando todos os núcleos do processador.

### ParallelGPU
Versão paralela na GPU: Utiliza OpenCL (biblioteca jocl-2.0.4) para processar o texto em paralelo na GPU, contando as palavras de forma eficiente com milhares de threads simultâneas.

### Exemplo de saída
```
SerialCPU: 66 ocorrências em 133 ms
ParallelCPU: 66 ocorrências em 82 ms
ParallelGPU: 119 ocorrências em 2705 ms
```

## Passo a passo do desenvolvimento

### 1. SerialCPU.java
Criacao do algoritmo serial que le o arquivo, divide o texto em palavras usando `split("\\W+")` e conta as ocorrencias com um loop simples em uma unica thread. Mede o tempo com `System.nanoTime()` e exibe o resultado.

### 2. ParallelCPU.java
Criacao do algoritmo paralelo para CPU que divide o array de palavras em partes iguais (uma por nucleo do processador) e utiliza `ExecutorService` com `newFixedThreadPool()` para processar cada parte em uma thread separada. Os resultados parciais sao combinados ao final.

### 3. ParallelGPU.java
Criacao do algoritmo paralelo para GPU utilizando OpenCL com a biblioteca jocl-2.0.4 (pacote `org.jocl`). O texto e convertido para bytes e enviado para a memoria da GPU. Um kernel escrito em C e executado por milhares de threads na GPU, onde cada thread verifica se a palavra comeca em uma posicao especifica do texto. O resultado e lido de volta para a CPU.

### 4. Compilacao e teste
Compilacao de todos os arquivos com `javac` incluindo o jocl no classpath. Execucao de teste com o arquivo MobyDick-217452.txt buscando a palavra "the", resultando em 19060 ocorrencias em 0ms na GPU.

### Proximos passos
- Criar TestRunner para executar os 3 metodos com multiplas amostras e gerar CSV
- Criar ChartGenerator para gerar graficos comparativos
- Preencher Resultados e Discussao no README
- Preencher Conclusao

## Metodologia
Implementação de algoritmos de busca sequenciais e paralelos em Java. Desenvolvimento de um framework de teste para executar e registrar os tempos de execução. Os testes foram executados em diferentes arquivos de texto com tamanhos variados, com no mínimo 3 amostras por execução. Os resultados foram armazenados em arquivos CSV para análise estatística e geração de gráficos.

### Análise estatística dos resultados obtidos
Os dados coletados serão analisados estatisticamente para identificar padrões de desempenho e comparar os algoritmos sob diferentes condições, variando o tamanho e a natureza dos conjuntos de dados de entrada.

### Configurações de teste
- **Arquivos de amostra**: DonQuixote (2,2MB), MobyDick (1,2MB), Dracula (890KB)
- **Número de amostras**: mínimo de 3 execuções por algoritmo/arquivo
- **Métrica principal**: tempo de execução em milissegundos
- **Variação de configuração**: número de núcleos no ParallelCPU

## Resultados e Discussão
*Resultados a serem inseridos após a execução dos testes.*

## Conclusão
*Conclusão a ser inserida após a análise dos resultados.*

## Referências
- Documentação Oracle Java
- JOCL - Java Bindings for OpenCL (https://github.com/gpu/JOCL)

## Anexos
Os códigos-fonte estão disponíveis neste repositório.

### Estrutura do projeto
```
/
├── src/
│   ├── SerialCPU.java
│   ├── ParallelCPU.java
│   └── ParallelGPU.java
├── bin/               (arquivos .class compilados)
├── jocl-2.0.4/
│   └── jocl-2.0.4.jar (biblioteca OpenCL)
├── Amostras/
│   ├── DonQuixote-388208.txt
│   ├── Dracula-165307.txt
│   └── MobyDick-217452.txt
└── README.md
```

### Como compilar
```
javac -cp "jocl-2.0.4\jocl-2.0.4.jar" -d bin src\SerialCPU.java src\ParallelCPU.java src\ParallelGPU.java
```

### Como executar
```
SerialCPU:  java -cp bin SerialCPU Amostras\MobyDick-217452.txt palavra
ParallelCPU: java -cp bin ParallelCPU Amostras\MobyDick-217452.txt palavra
ParallelGPU: java -cp "jocl-2.0.4\jocl-2.0.4.jar;bin" ParallelGPU Amostras\MobyDick-217452.txt palavra
```

### Observações sobre a biblioteca jocl-2.0.4
O projeto utiliza a biblioteca **jocl-2.0.4** (pacote `org.jocl.*`), que são bindings Java para a API OpenCL. O arquivo `jocl-2.0.4.jar` deve estar na pasta `jocl-2.0.4/` na raiz do projeto. A biblioteca já inclui a DLL nativa para Windows x86_64, que é extraída automaticamente em tempo de execução. Não é necessário instalar nada adicional além dos drivers OpenCL da GPU (NVIDIA, AMD ou Intel).
