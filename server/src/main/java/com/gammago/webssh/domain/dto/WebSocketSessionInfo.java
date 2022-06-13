package com.gammago.webssh.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.sf.expectit.Expect;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocket链接信息
 *
 * @author GammaGo
 * @date 2022/6/8
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketSessionInfo {

    /**
     * WebSocket Session信息
     */
    private WebSocketSession socketSession;

    /**
     * SSH链接客户端
     */
    private SSHClient sshClient;

    /**
     * Expect
     */
    private Expect expect;
}
