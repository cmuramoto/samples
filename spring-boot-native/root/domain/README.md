# domain

Esse módulo divide as entidades da camada mais interna (*internal*) da aplicação e utilizadas no perímetro (*frontend*).

Essa divisão é feita de forma a manter estáveis os contratos da API, permitindo, se necessário alterações no modelo relacional.

Adicionalmente, os contratos da API são completamente desacoplados de dependências (exceto javax.validation) e podem ser distribuídos de forma separada juntamente 
com um [Client Tipado](https://github.com/cmuramoto/letscode/blob/master/root/app/src/test/java/com/nc/app/test/GameClient.java).
