# domain-internal

Modelo Relacional e definição de interfaces de Serviços.

As principais entidades (Match e Round) funcionam como máquinas de estado e transições inválidas 

## Considerações sobre Exceptions

O projeto define uma única exception [DomainException](https://github.com/cmuramoto/letscode/blob/47f1234b540e383ddebd375da17126027b72c09f/root/domain/movie-domain-internal/src/main/java/com/nc/domain/internal/DomainException.java#L20), que sempre é lançada
quando um fluxo não pode prosseguir devido à pré-condições não atingidas.

Essa exception é tratada em um ponto central no [Controller](https://github.com/cmuramoto/letscode/blob/47f1234b540e383ddebd375da17126027b72c09f/root/app/src/main/java/com/nc/app/api/v1/GameController.java#L141).

Além de uma *mensagem de sistema*, essa exceção encapsula o objeto [FrontEndTranslatedError](https://github.com/cmuramoto/letscode/blob/47f1234b540e383ddebd375da17126027b72c09f/root/domain/movie-domain-internal/src/main/java/com/nc/domain/base/FrontEndTranslatedError.java#L6), 
que tem a *intenção* de propagar uma mensagem traduzida para API.

Todos exceções são lançados na forma [Errors.get()](https://github.com/cmuramoto/letscode/blob/47f1234b540e383ddebd375da17126027b72c09f/root/domain/movie-domain-internal/src/main/java/com/nc/domain/internal/Errors.java#L158).<some_method>(...args).

Na implementação atual, a mensagem de erro propagada para a API é a própria "mensagem de sistema", porém essa centralização permite evolução das mensagens para formatos mais amigáveis em um ponto único do sistema.

Além disso, essa exception não captura StackTraces! Como o intuito é capturar e proteger o sistema de transições inválidas, o overhead de lançar essa exception é minimizado com essa estratégia.

Finalmente, essa exception é do tipo RuntimeException. Além de manter as assinaturas dos métodos menos verbosas sem throws DomainException espalhado por todo canto, obtemos Rollback automático de serviços transacionais pelo fato da exception ser do tipo Runtime :) 

## Considerações sobre Regras de Negócio e Mapeamentos

### Entidades Principais

A class Match descreve uma "partida" com N Rounds (rodadas do quiz). Como o número de Rounds é pequeno, optamos por fazer um mapeamento bidirecional 1:N.

Esta classe é uma máquina de estado que internamente valida transições, e.g., para finalizar ou iniciar um novo Round.

O Round último round criado é acessado pelo método [Match::tail()](https://github.com/cmuramoto/letscode/blob/92061a997eb74a5a4ee896a68de2cdc1c0b2e77d/root/domain/movie-domain-internal/src/main/java/com/nc/domain/internal/Match.java#L193).

Rounds também são máquinas de estado que controlam as transições. Por exemplo, não é possível encerrar uma rodada selecionando um filme não associado à mesma.

### Estratégia de Geração de rodadas

A estratégia de definição de rodadas é definida na interface [MovieSamplingStrategy](https://github.com/cmuramoto/letscode/blob/92061a997eb74a5a4ee896a68de2cdc1c0b2e77d/root/domain/movie-domain-internal/src/main/java/com/nc/domain/internal/MovieSamplingStrategy.java)

As rodadas são geradas de forma lazy, isto é, a medida que o jogador avança uma nova rodada é criada. Essa interface recebe como parâmetro o conjunto de pares já vistos nas rodadas anteriores (null na primeira) e utiliza essa informação para gerar pares subsequentes.

A implementação desta interface, [RandomSamplingStrategy](https://github.com/cmuramoto/letscode/blob/92061a997eb74a5a4ee896a68de2cdc1c0b2e77d/root/services/movie-services/src/main/java/com/nc/services/internal/RandomSamplingStrategy.java), utiliza um algoritmo simples que permite que poucas queries sejam realizadas no banco.

Essencialmente a ideia do algoritmo é:

1. Conta o número de filmes disponíveis 
2. Seleciona um número aleatório entre <span>[0,count)</span>
3. Executa uma query paginada, com tamanho 1, ordenada por id, filtrando ids já vistos e iniciando na página do número aleatório
4. Assim obtemos o primeiro filme do par e o adicionamos ao conjunto de ids já vistos
5. Repetimos 2,3 e obtemos o segundo filme do par
