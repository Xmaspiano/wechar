package com.xmasworking.wechar.model;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @author XmasPiano
 * @date 2018/10/10 - 上午10:14
 * Created by IntelliJ IDEA.
 */
@Data
public class AccessTokenModel {
    String access_token;
    long expires_in;
    long errcode;
    String errmsg;
}
