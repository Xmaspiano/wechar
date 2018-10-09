package com.xmasworking.wechar.controller;

import com.xmasworking.wechar.model.WeCharModel;
import com.xmasworking.wechar.util.CheckUtil;
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

    @RequestMapping
    public String getKey(WeCharModel weCharModel){

        String signature=weCharModel.getSignature();
        String timestamp=weCharModel.getTimestamp();
        String nonce=weCharModel.getNonce();

        if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
            return weCharModel.getEchostr();
        }

        return "error";
    }

}
