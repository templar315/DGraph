package ua.edu.donntu.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Data
public class ApplicationProperties {

    public final Dropbox dropbox = new Dropbox();

    @Data
    public static class Dropbox {
        private String identifier;
        private String token;
    }
}
