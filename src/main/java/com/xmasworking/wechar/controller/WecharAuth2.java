package com.xmasworking.wechar.controller;

import com.xmasworking.wechar.model.WeCharAuthModel;
import com.xmasworking.wechar.util.AccessTokenUtil;
import com.xmasworking.wechar.util.CheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 *
 * @author XmasPiano
 * @date 2018/10/7 - 下午2:43
 * Created by IntelliJ IDEA.
 */
@RestController
@RequestMapping("/auth")
public class WecharAuth2{
    @Autowired
    AccessTokenUtil accessTokenUtil;

    @RequestMapping
    public String getKey(WeCharAuthModel weCharModel){

        String signature=weCharModel.getSignature();
        String timestamp=weCharModel.getTimestamp();
        String nonce=weCharModel.getNonce();

        if("".equals(signature) || "".equals(timestamp) || "".equals(nonce)){
            return weCharModel.toString();
        }

        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            return weCharModel.getEchostr();
        }

        return "error";
    }

    @RequestMapping("/access_token")
    public String getAccessToken(){
        accessTokenUtil.gerAccessToken();
        return "success";
    }

}
