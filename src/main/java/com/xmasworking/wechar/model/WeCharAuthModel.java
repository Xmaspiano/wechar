package com.xmasworking.wechar.model;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @author XmasPiano
 * @date 2018/10/9 - 上午11:23
 * Created by IntelliJ IDEA.
 */
@Data
public class WeCharAuthModel {
    /**
     微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     */
    String signature;
    /**
     时间戳
     */
    String timestamp;
    /**
     随机数
     */
    String nonce;
    /**
     随机字符串
     */
    String echostr;
}
