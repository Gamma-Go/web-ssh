package com.gammago.webssh.service;

import com.gammago.webssh.domain.dto.SshLoginParams;
import com.gammago.webssh.domain.dto.WebSocketSessionInfo;
import com.gammago.webssh.manager.SshConnectManager;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * SSH链接服务
 *
 * @author GammaGo
 * @date 2022/5/29
 */
@Service
@Slf4j
public class SshService {

    /**
     * 初始化链接
     *
     * @param session     WebSocket Session
     * @param loginParams 登陆信息
     */
    public void initConnection(WebSocketSession session, SshLoginParams loginParams) throws IOException {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        sshClient.loadKnownHosts();
        sshClient.connect(loginParams.getHost(), loginParams.getPort());
        sshClient.authPassword(loginParams.getUser(), loginParams.getPassword());
        Session sshSession = sshClient.startSession();
        sshSession.allocateDefaultPTY();
        Session.Shell shell = sshSession.startShell();
        Expect expect = new ExpectBuilder()
                .withOutput(shell.getOutputStream())
                .withInputs(shell.getInputStream(), shell.getErrorStream())
                .withEchoInput(new OutputAppend(session))
                .build();
        SshConnectManager.saveConnection(WebSocketSessionInfo.builder()
                .socketSession(session)
                .sshClient(sshClient)
                .expect(expect)
                .build());
    }

    /**
     * 发送命令到SSH
     *
     * @param session WebSocket Session
     * @param cmdStr  命令参数
     * @throws IOException 异常信息
     */
    public void sendToSsh(WebSocketSession session, String cmdStr) throws IOException {
        WebSocketSessionInfo connection = SshConnectManager.getConnection(session.getId());
        connection.getExpect().send(cmdStr);
    }

    /**
     * 关闭SSH链接
     *
     * @param session Socket session
     */
    public void closeConnection(WebSocketSession session) {
        SshConnectManager.removeConnection(session.getId());
    }


    static class OutputAppend implements Appendable {

        private final WebSocketSession session;

        public OutputAppend(WebSocketSession session) {
            this.session = session;
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException {
            session.sendMessage(new TextMessage(csq));
            return this;
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException {
            CharSequence cs = (csq == null ? "null" : csq);
            session.sendMessage(new TextMessage(cs.subSequence(start, end).toString()));
            return this;
        }

        @Override
        public Appendable append(char c) throws IOException {
            session.sendMessage(new TextMessage(String.valueOf(c)));
            return this;
        }
    }
}
