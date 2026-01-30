package com.cjy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 应用启动监听器
 * 在应用完全启动后输出启动信息
 */
@Slf4j
@Component
public class ApplicationStartupListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Environment env = event.getApplicationContext().getEnvironment();
            String protocol = "http";
            if (env.getProperty("server.ssl.key-store") != null) {
                protocol = "https";
            }

            String serverPort = env.getProperty("server.port", "8080");
            String contextPath = env.getProperty("server.servlet.context-path", "/");

            String hostAddress = "localhost";
            try {
                hostAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                log.warn("获取主机地址失败: {}", e.getMessage());
            }

            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("📚 学生分组管理系统已启动");
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("🔗 本地访问地址: {}://{}:{}{}", protocol, "localhost", serverPort, contextPath);
            log.info("🔗 外部访问地址: {}://{}:{}{}", protocol, hostAddress, serverPort, contextPath);
            log.info("🔗 接口文档地址: {}://{}:{}{}/swagger-ui.html", protocol, "localhost", serverPort, contextPath);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("🚀 应用已就绪，等待您的使用！");

        } catch (Exception e) {
            log.error("应用启动监听器执行失败: {}", e.getMessage(), e);
        }
    }
}
