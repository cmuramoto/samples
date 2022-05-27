# app

Aplicação que expõe os endpoints utilizando um container embedded.

A aplicação é completamente stateless (do ponto de vista de sessões http) e a segurança é controlada mediante um token JWT.

Todos caminhos são protegidos por padrão, exceto:

1. **/v3/api-docs** - Provê o [schema da api](https://github.com/cmuramoto/letscode/blob/master/root/assets/api-schema.json) no padrão openapi 3.0 em formato json
2. **/swagger-ui** - Interface para visualização dos schemas
3. **/authenticate** - Endpoint de autenticação

## Testando a API - Básico

Para testar a api você pode importar a [coleção de requests](https://github.com/cmuramoto/letscode/blob/master/root/assets/insomnia.json) no [Insomnia](https://insomnia.rest/download).

O fluxo de uso da API é:

```
POST <http|https>://<address>/v1/authenticate

{
	"username": "mary",
	"password": "changeme"
}
```
Ou, no Insomnia

![image](https://user-images.githubusercontent.com/7014591/154858248-10b51092-371a-4440-8e16-a1f0b5552f9b.png)



Se os dados conferirem, a resposta conterá um jwt no formato:

```
{
	"token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJ5IiwiZXhwIjoxNjQ1Mzk5NDQ1LCJpYXQiOjE2NDUzODE0NDV9.ITxYHNbksmOR5AEtDQcMgGVbJuuwHo76jueJF67l1eCJVMxuoGYd6HWX4EO24yDMerrI01XBapnZlyq6QAzZPw"
}
```

De posse de um token, os endpoints do caminho base **/game** poderão ser acessados. O token tem validade de 1h e deve ser setado no environment do Insomnia:


![image](https://user-images.githubusercontent.com/7014591/154857950-e20647f6-08f1-439f-9492-6e083fc6fc27.png)

A partir daí, você pode utilizar os métodos dos endpoints protegidos. Todos esses endpoints requererem um header **Authorization** com valor no formato:

```
Bearer <token>
```

Para efeito de testes de autenticação disponibilizamos um endpoint **/v1/game/me**, que recupera informações de um usuário através do JWT:

```
{
	"name": "mary",
	"ranking": {
		"user": "mary",
		"played": 1,
		"hits": 3,
		"score": 0.6
	}
}
```

## Testando a API - Fluxo

O fluxo de jogo é validado como um teste de integração em ApiIntegrationTests.


## Usuários

A aplicação boota com 2 usuários. Para cadastrar adicionais basta alterar o script src/main/resources/data.sql

As senhas devem ser codificadas com BCryptPasswordEncoder

## Executando

Ao compilar o projeto será gerado um jar shaded em target/app.jar

Para publicar a api basta executar:

```bash
java -cp target/app.jar com.nc.app.boot.Main
```

## Docker

Não estamos utilizando o plugin do springboot para geração de imagens, pois, embora mais simples o resultado é inferior em relação a uma imagem criada de forma customizada.

Caso queira utilizar o plugin do springboot para gerar a imagem basta adicionar no pom.xml:

```xml
<plugin>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-maven-plugin</artifactId>
  <configuration>
    <image>
      <builder>paketobuildpacks/builder:tiny</builder>
      <env>
        <BP_NATIVE_IMAGE>false</BP_NATIVE_IMAGE>
      </env>
    </image>
  </configuration>
</plugin>
```

E executar:

```
mvn spring-boot:build-image
```

A imagem produzida utilizando este plugin fica com **238MB**.

Para construir nossa imagem, utilizamos a receita (Dockerfile) encontrada na pasta **.devops**.

Essa receita é superior em termos de footprint em relação ao plugin do spring-boot, pois a imagem do é baseada em um jdk-17 compilado para linux alpine e contém apenas 1 gc (Parallel), o que reduz o tamanho dos binários. Opcionalmente outras imagens com gcs diferentes podem ser baixadas no [dockerhub](https://hub.docker.com/repository/docker/cmuramoto/alpine-jdk).

Na receita, também utilizamos jlink para reduzir o tamanho da imagem final, empacotando apenas os módulos necessários do jdk.

Esses módulos podem ser obtidos utilizando o seguinte comando:

```bash
jdeps --ignore-missing-deps --list-deps -cp ./ app.jar
```

A imagem gerada ao executar ./build.docker.sh fica com apenas **103MB**, ou seja, mais de *50%* menor em relação à imagem produzida pelo spring-boot-maven-plugin.

Com a imagem construída, basta rodar

```
docker container run -p 127.0.0.1:<porta_local_livre>:8080/tcp cmuramoto/letscode:app-1.0.0
```

E.g.,

```
docker container run -p 127.0.0.1:8080:8080/tcp cmuramoto/letscode:app-1.0.0
```
