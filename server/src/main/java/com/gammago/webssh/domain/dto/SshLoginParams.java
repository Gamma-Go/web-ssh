package com.gammago.webssh.domain.dto;

import lombok.Data;

/**
 * SSH登陆参数
 *
 * @author GammaGo
 * @date 2022/6/10
 */
@Data
public class SshLoginParams {

    /**
     * 地址
     */
    private String host;

    /**
     * 端口
     */
    private Short port;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password;
}
