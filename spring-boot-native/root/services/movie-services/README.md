# services

Camada de serviços que serão consumidos pela api.

## Considerações

Resumidamente, o rastro de dependências da aplicação final e os outros módulos é:

[GameController](https://github.com/cmuramoto/letscode/blob/ee83c575d954e729d4f9db6eb532a36495f0de40/root/app/src/main/java/com/nc/app/api/v1/GameController.java) -> [MovieGameService](https://github.com/cmuramoto/letscode/blob/ee83c575d954e729d4f9db6eb532a36495f0de40/root/domain/movie-domain-frontend/src/main/java/com/nc/domain/frontend/services/v1/MovieGameService.java) -> [esta camada] -> [Repositórios](https://github.com/cmuramoto/letscode/tree/master/root/data/movie-repositories)

Como a aplicação é um monolito, mantivemos a implementação de MovieService neste projeto, porém a interface está definida em domain-frontend, o que reduz o acoplamento da API com a implementação do serviço.

## Coverage

Embora a API tenha testes de cobertura, consideramos o exercício da mesma na API menos importante que neste módulo, pois o a cobertura da api não é capaz de estressar alguns cenários
devido a validações feitas em sua própria camada, impedindo que alguns dados cheguem em estado inválido na camada de serviço.

Além de validar a máquina de estado, os testes deste módulo estressam a estratégia de geração de pares de filmes distintos.

![image](https://user-images.githubusercontent.com/7014591/154997468-929ca06c-514d-4998-baff-663d736eb683.png)

O package **com.nc.services.frontend** está com 0 cobertura, pois a mesma é exercitada pela própria api

![image](https://user-images.githubusercontent.com/7014591/154998276-d6d47494-907b-458a-9804-ba17a8bc67bd.png)

O relatório completo pode ser acessado em [assets/service-coverage](https://github.com/cmuramoto/letscode/tree/master/root/assets/service-coverage)
