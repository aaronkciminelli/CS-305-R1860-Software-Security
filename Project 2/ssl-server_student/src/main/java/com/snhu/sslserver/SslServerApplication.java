package com.snhu.sslserver;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.apache.catalina.connector.Connector;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class SslServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(SslServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SslServerApplication.class, args);
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

}

@RestController
class SslServerController {

    private static final Logger logger = LoggerFactory.getLogger(SslServerController.class);

    @RequestMapping("/hash")
    public String myHash() {
        try {
            String data = "Hello World Check Sum!";
            String name = processName("Aaron Ciminelli");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] sha256 = md.digest(name.getBytes(StandardCharsets.UTF_8));
            return "data: " + data + "</br></br>" + "Name: " + name + "</br></br>" + "Name of Cipher Used: CheckSum Value: " + bytesToHex(sha256);
        } catch (Exception e) {
            logger.error("Error processing hash", e);
            return "An error occurred. Please try again later.";
        }
    }

    private String processName(String name) {
        String[] parts = name.split("\\s+");
        return parts[0] + " " + parts[parts.length - 1];
    }

    public String bytesToHex(byte[] bytes) {
        BigInteger hex = new BigInteger(1, bytes);
        StringBuilder checksum = new StringBuilder(hex.toString(16));

        while (checksum.length() < 32) {
            checksum.insert(0, '0');
        }
        return checksum.toString();
    }
}
