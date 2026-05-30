# Análise Comparativa de Algoritmos com Uso de Paralelismo

## Resumo
Este trabalho propõe uma análise detalhada do desempenho de diferentes algoritmos de busca em ambientes seriais e paralelos, utilizando a linguagem de programação Java. A busca por eficiência computacional é essencial em diversas aplicações, e entender como diferentes algoritmos se comportam em diferentes cenários de processamento é de suma importância. Neste estudo, serão abordados três algoritmos: serial, paralelo em CPU e paralelo em GPU.

## Introdução
Foram implementados três métodos para contagem de ocorrências de uma palavra em arquivos de texto:
- **SerialCPU**: versão sequencial na CPU utilizando um loop simples.
- **ParallelCPU**: versão paralela na CPU utilizando `ExecutorService` com pool de threads.
- **ParallelGPU**: versão paralela na GPU utilizando OpenCL (biblioteca jocl-2.0.4).

## Metodologia
Implementação de algoritmos de busca sequenciais e paralelos em Java. Desenvolvimento de um framework de teste para executar e registrar os tempos de execução. Os testes foram executados em diferentes arquivos de texto com tamanhos variados, com no mínimo 3 amostras por execução. Os resultados foram armazenados em arquivos CSV para análise estatística e geração de gráficos.

## Resultados e Discussão
*Resultados a serem inseridos após a execução dos testes.*

## Conclusão
*Conclusão a ser inserida após a análise dos resultados.*

## Referências
- Documentação Oracle Java
- JOCL - Java Bindings for OpenCL (http://jogamp.org/jocl/)

## Anexos
Os códigos-fonte estão disponíveis neste repositório.
