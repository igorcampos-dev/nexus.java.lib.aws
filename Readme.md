# Projeto Nexus - Biblioteca de Utilidades para o serviço cloud AWS

uma biblioteca de utilitários cloud AWS como parte do projeto Nexus. O objetivo principal desta biblioteca é fornecer funcionalidades para manipulação de arquivos do serviço bucketS3.

## Como Utilizar

Siga os passos abaixo para começar a usar a Biblioteca de Utilidades:

1. Acesse o site do [JitPack](https://jitpack.io/#igorcampos-dev/nexus.java.lib.aws) através deste link.
2. Na seção de **Releases**, clique no botão "Get It" abaixo do campo "Status". Isso indica que a biblioteca está compilando corretamente.
3. No seu projeto, adicione as duas dependências conforme explicado no site do JitPack.

## Sobre o projeto

Este projeto oferece uma classe utilitária que proporciona uma abstração para operações com o Bucket S3 da AWS, especialmente no que diz respeito a arquivos. A classe é denominada `S3`.

### Como utilizar

Para utilizar esta classe, siga os passos abaixo:

#### implementação personalizada:

1. Implemente a interface `S3` na sua classe:

```java
import com.nexus.aws.cloud.S3;

public class SuaClasse implements S3 {

    @Override
    public void createFolder(String folderName){
        // your implementation here
    }
    
}
```

#### implementação default (padrão)

1. Injete a interface, pois ela já possui uma implementação desenvolvida:

```java
import com.nexus.aws.cloud.S3;
import org.springframework.beans.factory.annotation.Autowired;

public class SuaClasse {

    @Autowired
    private final S3 s3;
    
}
```

### Sobre a implementação default (padrão)

A implementação padrão desenvolvida,executa operações como:

1. Criar pasta;
2. Atualizar arquivo;
3. Listar arquivos de uma pasta;
4. deletar arquivo;
5. atualizar arquivo;
6. busca conteúdo de um arquivo

### Configurando as credenciais

Para configurar suas credenciais, será preciso possuir as seguintes credenciais:

1. accessKey;
2. secretKey;
3. region;
4. bucketName;
5. serviceEndpoint; (defina: `https://s3.amazonaws.com`)


com elas em mãos, configure-as no arquivo `application.properties` ou `application.yml`

A. exemplo usando `application.yml`:

```yml
spring:
  aws:
    credentials:
      serviceEndpoint: "URL_DO_SEU_ENDPOINT"
      accessKey: "SUA_ACCESS_KEY"
      secretKey: "SUA_SECRET_KEY"
      region: "REGIÃO_DA_AWS"
      bucketName: "NOME_DO_SEU_BUCKET"
```

B. exemplo usando `application.properties`:

```properties
spring.aws.credentials.serviceEndpoint=URL_DO_SEU_ENDPOINT
spring.aws.credentials.accessKey=SUA_ACCESS_KEY
spring.aws.credentials.secretKey=SUA_SECRET_KEY
spring.aws.credentials.region=REGIÃO_DA_AWS
spring.aws.credentials.bucketName=NOME_DO_SEU_BUCKET
```