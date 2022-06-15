package com.gammago.webssh;

import com.gammago.webssh.manager.SshConnectManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author gammago
 */
@SpringBootApplication
public class WebSshApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSshApplication.class, args);
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(SshConnectManager::removeAll));
    }
}
