package com.gammago.webssh.websocket;


import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gammago.webssh.domain.dto.SshLoginParams;
import com.gammago.webssh.service.SshService;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.connection.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * SSH控制台
 *
 * @author GammaGo
 * @date 2022/6/8
 */
@Slf4j
@Component
public class SshWebSocketHandler implements WebSocketHandler {

    @Autowired
    private SshService sshService;

    @Value("${ssh.connect.privateKey}")
    private String privateKey;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        Map<String, String> paramMap = HttpUtil.decodeParamMap(Objects.requireNonNull(session.getUri()).getQuery(), Charset.defaultCharset());
        log.debug("用户连接[{}]开始接入,参数:[{}]", session.getId(), paramMap);

        SshLoginParams loginParams = Optional.of(paramMap.get("connectParams"))
                .map(encryptedStr -> SecureUtil.rsa(privateKey, null).decryptStr(encryptedStr, KeyType.PrivateKey))
                .map(paramJson -> JSONUtil.toBean(paramJson, SshLoginParams.class))
                .orElseThrow(() -> new IllegalArgumentException("Connection login Info Error"));

        try {
            sshService.initConnection(session, loginParams);
            log.debug("用户链接[{}]接入成功", session.getId());
        } catch (IOException e) {
            log.error("用户[{}]链接失败", session.getId(), e);
            session.close();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        log.debug("接收到用户[{}]信息:[{}]", session.getId(), message.getPayload());
        try {
            sshService.sendToSsh(session, message.getPayload().toString());
        } catch (ConnectionException connectionException) {
            session.close();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("用户连接[{}]发生异常{}", session.getId(), exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sshService.closeConnection(session);
        log.debug("用户连接[{}]退出", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
