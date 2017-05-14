package it.skarafaz.download.configuration;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
public class EmbeddedServletContainerConfiguration {
    private static final String AJP_PROTOCOL = "AJP/1.3";
    private Integer ajpPort;

    public void setAjpPort(Integer ajpPort) {
        this.ajpPort = ajpPort;
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(getAjpConnector());

        return tomcat;
    }

    private Connector getAjpConnector() {
        Connector connector = new Connector(AJP_PROTOCOL);
        connector.setPort(ajpPort);

        return connector;
    }
}
