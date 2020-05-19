package config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @description:
 * @author: zhangtb
 */
@Component
@Getter
@PropertySource(value = "classpath:serverconfig.properties")
public class ServerConfig {
    @Value("${server.ip}")
    private String ip;

    @Value("${server.ip}")
    private int port;

    @Value("${server.id}")
    private int id;

    @Value("${server.name}")
    private String name;
}
