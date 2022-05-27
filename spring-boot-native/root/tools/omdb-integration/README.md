# Ferramenta para integração com omdb

Para a ferramenta funcionar é necessário primeiramente solicitar e ativar uma [Api Key](https://www.omdbapi.com/apikey.aspx).

A Api tem 2 modos de consulta

1. Por id ou título: https://www.omdbapi.com?apiKey=<my_key>&i=<id_imdb>
   - Essa consulta retorna objetos **inflados**, que contém a informação de rating e total votes, além de outras como uma url para imagem do filme, plot, etc
2. Por consulta paginada: https://www.omdbapi.com?apiKey=<my_key>&s=<some_key_word>&page=
   - Essa consulta retorna uma **lista** de objetos **flat** sem todas as informações que precisamos, porém a resposta contém
     - imdbId
     - totalResults
   - A api de busca sempre retorna **10** dados por página
   - De posse de totalResults, podemos fazer calcular o número de requisições que precisamos fazer para pegar todos os dados:

```java
   totalPages = totalResults/10 + ((totalResults%10)==0?0:1)
```

O fluxo de streaming fica:

1. Executa primeira consulta com page=0 para pegar o total de páginas
2. Repete a consulta https://www.omdbapi.com?apiKey=<my_key>&s=<some_key_word>&page=<current>, com current [1..totalPages)
3. Mapeia cada item **flat** da lista utilizando o idImdb
  
## Executáveis
  
Essa ferramenta possui 3 main-classes
  
1. com.nc.integration.omdb.apps.OmdbIngestor
   - Sincroniza filmes através de uma lista de palavras chave e salva os resultados no banco
     - Ao startar a classe você pode passar a System Property **omdb.integration.titles** que pode ser um arquivo com uma palavra/linha ou uma string que será splittada por *,*
     - Se não passar nada, serão utilizadas para consulta: "Batman", "Matrix", "Beethoven", "God", "Furious", "X-men", "Superman", "Boss", "Mob"
2. com.nc.integration.omdb.apps.OmdbTranslator
   - Transforma os [OmdbRecord](https://github.com/cmuramoto/letscode/blob/master/root/tools/omdb-integration/src/main/java/com/nc/integration/omdb/domain/OmdbRecord.java) salvos pelo processo de ingestão na entidade [Movie](https://github.com/cmuramoto/letscode/blob/master/root/domain/movie-domain-internal/src/main/java/com/nc/domain/internal/Movie.java) que será utilizada pela aplicação.
3. com.nc.integration.omdb.apps.OmdbSqlGenerator
   - Cria arquivos sql para carregamento das tabelas no boot da Aplicação
  
Para executar fora da IDE, basta compilar e executar
  
```bash
java -cp target/omdb-tool.jar com.nc.integration.omdb.apps.(OmdbIngestor|OmdbTranslator|OmdbSqlGenerator)
```
  
Um snapshot pré-sincronizado encontra-se na pasta data deste projeto
   
## Notas
   
A api key *free* limita o número de requisições em aproximadamente 1000. Após o client começará a receber erros:
   
```
{"Response":"False","Error":"Request limit reached!"}
```
