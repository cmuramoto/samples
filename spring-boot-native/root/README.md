# root

Projeto raiz!

Aqui são declaradas as versões das dependências utilizadas, o plugin-management e os projetos filhos.

## Arquitetura e Árvore de dependências

O projeto atualmente é publicado como um monolito, em que o acesso a dados e serviços é feito diretamente pela api. Contudo optamos por quebrar em componentes para facilitar uma possível migração para arquiteturas distribuídas.

Nesse esquema, a API tem um acoplamento apenas com uma interface opaca [MovieGameService](https://github.com/cmuramoto/letscode/blob/72ffd95594cc454d5ad12d55f8cf1e8b585e0f81/root/domain/movie-domain-frontend/src/main/java/com/nc/domain/frontend/services/v1/MovieGameService.java) que pode ser substituída por um proxy que acessa serviços remotos, não embutidos na aplicação.

A árvore de módulos da aplicação principal tem a seguinte estrutura:

- com.nc:app
  - com.nc:movie-security-jwt
    - com.nc:movie-domain-internal
      - com.nc:movie-utils
  - com.nc:movie-services
    - com.nc:movie-repositories
      - com.nc:movie-domain-frontend 

Este projeto está dividido nos seguintes módulos

1. [libs](https://github.com/cmuramoto/letscode/tree/master/root/libs)
   - Infraestrutura e utilitários.
2. [domain](https://github.com/cmuramoto/letscode/tree/master/root/domain) - 
   - Entidades e interfaces de serviços
3. [data](https://github.com/cmuramoto/letscode/tree/master/root/data) - Repositórios de acesso a dados
   - [CrudRepositories](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html) customizados com paginação, etc
4. [services](https://github.com/cmuramoto/letscode/tree/master/root/services)
   - Implementação das regras de negócio
5. [app](https://github.com/cmuramoto/letscode/tree/master/root/app) 
   - Aplicação web com os endpoints da API e documentação no formato **openapi 3**
6. [tools](https://github.com/cmuramoto/letscode/tree/master/root/tools) 
   - Ferramentas para sincronização de dados de filmes com a [omdb-api](https://www.omdbapi.com/)
7. [assets](https://github.com/cmuramoto/letscode/tree/master/root/assets) - Recursos para o projeto, e.g.
   - [Dados utilizados](https://github.com/cmuramoto/letscode/tree/master/root/assets/db) (filmes omdb + users)
   - [Relatório de Cobertura da API](https://github.com/cmuramoto/letscode/tree/master/root/assets/api-coverage)
   - [Arquivo de Endpoints](https://github.com/cmuramoto/letscode/blob/master/root/assets/insomnia.json) para ser importado no [Insomnia](https://insomnia.rest/download)

## Pré-requisitos

Para compilar e executar o projeto você precisará de

1. jdk-17
2. maven 3.5.4+

## Compilando

```bash
mvn clean install
```

Se tudo der certo (brr...nenhum teste falhar) você terá os seguintes executáveis:

1. Aplicação com api: 
   - app/target/app.jar
   - java -cp app/target/app.jar com.nc.app.boot.Main
   - Após startar, você poderá acessar [a documentação da api](http://localhost:8080/swagger-ui/index.html)
2. Ferramenta para integração com omdb:
   - tools/omdb-integration/target/omdb-tools.jar
   - Para maiores detalhes consultar o [projeto](https://github.com/cmuramoto/letscode/tree/master/root/tools/omdb-integration)

## Executando

Se quiser apenas o executável para testar, disponibilizamos uma imagem do docker

```bash
docker pull cmuramoto/letscode:app-1.0.0
```

```bash
docker container run -p 127.0.0.1:8080:8080/tcp cmuramoto/letscode:app-1.0.0
```

## Gerando relatórios de cobertura

Por padrão o coverage está desabilitado. Para gerar os relatórios basta executar:

```bash
mvn clean install -Pcoverage
```

Deixamos um snapshot do relatório de cobertura da api em [assets](https://github.com/cmuramoto/letscode/tree/master/root/assets/api-coverage).


## Resalvas!

O banco H2 tem alguns probleminhas quando são executadas operações em lote (e.g. deleteAll) seguidas de inserts. Alguns testes forçam um delay sintético para tentar mitigar essas inconsistências, porém não é 100% bullet proof, isto é, um mesmo teste, com os mesmos inputs pode falhar esporadicamente.

O compiler plugin está configurado com **fork=false**, que deixa a compilação mais rápida pois apenas uma vm com javac é startada. Se algo acontecer, mudar para **fork=true** e usar jvms isoladas pode mitigar o problema.

