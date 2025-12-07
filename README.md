# dev-web

## Como executar o projeto

Siga estes passos para subir e executar a aplicação localmente (usando o `Makefile` que contém os comandos Docker/Maven):

1. Executar o comando do Makefile para subir os containers:

```bash
make compose-up
```

2. Entrar no container (ou executar comandos dentro dele):

```bash
make compose-exec
```

3. Instalar dependências e construir o projeto Maven:

```bash
make mvn-install
```

4. Iniciar a aplicação com Jetty:

```bash
mvn jetty:run
```

Observações:

- Se você estiver usando Windows e tiver problemas com o `make`, copie os comandos equivalentes do `Makefile` e execute-os diretamente no terminal do Docker ou no PowerShell.

## Documentação do projeto

- Planilha: https://docs.google.com/spreadsheets/d/1IC2FNd3fSgpcPLx3wZaPcB-BciEdY3G4dwswS-5rmug/edit?gid=482448427#gid=482448427
- Plano de Teste: https://docs.google.com/document/d/135tsFK0pquIvSGZFxvGexExLG1BcolCLroOFifX4JlM/edit?tab=t.0#heading=h.7dcpsg9hh7ah
- Apresentação 1: https://docs.google.com/presentation/d/1MzGGpjdAGGiqAMmD4SF3jS_z9BJLkfIxSmVUCCw2q34/edit?usp=sharing
- Apresentação 2: https://www.canva.com/design/DAG6MboqR_c/FKqvnsudnBcQurDMS7nTyw/view?utm_content=DAG6MboqR_c&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hbd32ef2f72#3
- ISO 25010: https://docs.google.com/document/d/1i06svy2MNHs0WXXpls2p8WpXPQZWZG8W2BiH6d5HWTA/edit?usp=sharing
# Como chegar em cada tipo de testes
### Testes de integração no postman
dev-web/collections-postman 
### Testes de sistema no selenium 
dev-web/webApplication/src/test/java/com/mycompany/webapplication/selenium
## TestLink

- Projeto: **bdj: Banco Digital Java**
