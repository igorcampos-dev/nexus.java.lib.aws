package com.nexus.aws.cloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Esta classe representa as propriedades de configuração para acesso ao serviço S3 da AWS.
 * Para usar essas propriedades, elas devem ser definidas no arquivo de configuração (application.properties ou application.yml)
 * sob o prefixo 'spring.aws.credentials'.
 *<p>
 * Exemplo de configuração no arquivo application.properties:
 * <pre>{@code
 * spring.aws.credentials.serviceEndpoint=URL_DO_SERVIÇO
 * spring.aws.credentials.accessKey=SUA_CHAVE_DE_ACESSO
 * spring.aws.credentials.secretKey=SUA_CHAVE_SECRETA
 * spring.aws.credentials.region=REGIÃO_DO_BUCKET
 * spring.aws.credentials.bucketName=NOME_DO_BUCKET
 * }</pre>
 *
 * Ao definir as credenciais desta forma, elas serão automaticamente injetadas nesta classe
 * e disponíveis para uso em outras partes do aplicativo.
 */

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.aws.credentials")
public class S3PropertiesImpl implements S3Properties {
    private String serviceEndpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
}
