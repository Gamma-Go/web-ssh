package com.gammago.webssh.manager;

import com.gammago.webssh.domain.dto.WebSocketSessionInfo;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSH链接管理Manager
 *
 * @author GammaGo
 * @date 2022/6/10
 */
public class SshConnectManager {

    /**
     * 链接存储
     * key：链接id
     * value：SSH链接信息
     */
    private final static Map<String, WebSocketSessionInfo> CONNECTION_MAP = new ConcurrentHashMap<>();

    /**
     * 获取链接信息
     *
     * @param connectId 链接id
     * @return 链接信息
     */
    public static WebSocketSessionInfo getConnection(String connectId) {
        return CONNECTION_MAP.get(connectId);
    }

    /**
     * 保存链接信息
     *
     * @param connection 链接信息
     */
    public static void saveConnection(WebSocketSessionInfo connection) {
        if (Objects.isNull(connection)) {
            throw new IllegalArgumentException("Connection can not be empty");
        }
        if (Objects.isNull(connection.getSocketSession())) {
            throw new IllegalArgumentException("Connection session is null");
        }
        CONNECTION_MAP.put(connection.getSocketSession().getId(), connection);
    }

    /**
     * 移除链接
     *
     * @param connectId 链接ID
     */
    public static void removeConnection(String connectId) {
        WebSocketSessionInfo info = CONNECTION_MAP.get(connectId);
        if (Objects.nonNull(info)) {
            if (Objects.nonNull(info.getExpect())) {
                try {
                    info.getExpect().close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(info.getSshClient())) {
                try {
                    info.getSshClient().close();
                } catch (IOException ignored) {
                }
            }
            if (Objects.nonNull(info.getSocketSession())) {
                try {
                    info.getSocketSession().close();
                } catch (IOException ignored) {
                }
            }
            CONNECTION_MAP.remove(connectId);
        }
    }

    /**
     * 关闭并移除所有链接
     */
    public static void removeAll() {
        CONNECTION_MAP.keySet().forEach(SshConnectManager::removeConnection);
    }
}
