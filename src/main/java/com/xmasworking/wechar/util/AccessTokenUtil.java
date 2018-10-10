package com.xmasworking.wechar.util;

import com.alibaba.fastjson.JSONObject;
import com.xmasworking.wechar.model.AccessTokenModel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author XmasPiano
 * @date 2018/10/10 - 上午8:45
 * Created by IntelliJ IDEA.
 */
@Component
public final class AccessTokenUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenUtil.class);

    @Value("${wechar.appID}")
    private String appId;
    @Value("${wechar.appsecret}")
    private String secret;
    @Value("${wechar.token.path}")
    private String tokenPath;
    @Value("${wechar.tokenURL}")
    private String tokenURL;

    public String gerAccessToken(){
        AccessToken accessToken = isRealAccessToken();

        //未获取到accessToken对象时请求wechar获取Token
        if(accessToken == null){
            accessToken = getSuccessAccessToken();
        }
        return accessToken.getAccessToken();
    }

    private AccessToken isRealAccessToken(){
        LOGGER.info("读取Token缓存文件");
        String accessTokenStr = readFileAccessToken();
        if(!"".equals(accessTokenStr)) {
            try {
                AccessToken accessToken = JSONObject.parseObject(readFileAccessToken(), AccessToken.class);
                //accessToken不为空并且Token未超时
                if (accessToken.getAccessToken() != null && accessToken.getExpiresTime() > System.currentTimeMillis()) {
                    return accessToken;
                }
            }catch (Exception e){
                LOGGER.info("读取Token缓存文件失败："+e.getMessage());
            }
        }
        return null;
    }

    private AccessToken getSuccessAccessToken(){
        LOGGER.info("读取WeChar远程Token");
        AccessTokenModel accessTokenModel = httpGetAccessToken();
        if(accessTokenModel.getAccess_token() != null){
            try {
                AccessToken accessToken = new AccessToken();
                accessToken.setAccessToken(accessTokenModel.getAccess_token());
                //设置超时时间
                accessToken.setExpiresTime(System.currentTimeMillis() + accessTokenModel.getExpires_in() * 1000L);
                //写入缓存文件
                LOGGER.info("写入Token缓存文件");
                writeFileAccessToken(JSONObject.toJSONString(accessToken));
                return accessToken;
            }catch (Exception e){
                LOGGER.error("读取WeChar远程Token异常："+e.getMessage(), e);
                return null;
            }
        }else{
            throw new RuntimeException(accessTokenModel.getErrmsg());
        }
    }

    private boolean writeFileAccessToken(String t) {
        FileSystemResource resource = new FileSystemResource(tokenPath);
        try {
            FileWriter fileWriter = (new FileWriter(resource.getFile()));
            fileWriter.write(t);
            fileWriter.close();
        }catch (FileNotFoundException fileNotFoundException){
            LOGGER.info(fileNotFoundException.getMessage());
        }catch (IOException e) {
            //todo loginfo
            LOGGER.error(e.getMessage(),e);
            return false;
        }
        return true;
    }

    private String readFileAccessToken(){
        String rstr = "";
        try {
            FileSystemResource resource = new FileSystemResource(tokenPath);
            BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
            String str = null;
            while ((str = br.readLine()) != null) {
                rstr += str;
            }
            br.close();
        }catch (FileNotFoundException fileNotFoundException){
            LOGGER.info(fileNotFoundException.getMessage());
        } catch (IOException e) {
            //todo loginfo
            LOGGER.error(e.getMessage(),e);
        }
        return rstr;
    }

    private AccessTokenModel httpGetAccessToken(){
        RestTemplate restTemplate=new RestTemplate();
        if("".equals(appId) || "".equals(secret)){
            throw new RuntimeException("WeChar参数加载错误，AppId或Secret为空...");
        }

        /* 注意：必须 http、https……开头，不然报错，浏览器地址栏不加 http 之类不出错是因为浏览器自动帮你补全了 */
        String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx8047ed4e28fc1ae3&secret=9720b9ba7c30d0db6ce9598fad7f0b72";//tokenURL;
        /* 这个对象有add()方法，可往请求头存入信息 */
        HttpHeaders headers = new HttpHeaders();
        /* 解决中文乱码的关键 , 还有更深层次的问题 关系到 StringHttpMessageConverter，先占位，以后补全*/
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        /* body是Http消息体例如json串 */
        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        /*上面这句返回的是往 url发送 post请求 请求携带信息为entity时返回的结果信息
        String.class 是可以修改的，其实本质上就是在指定反序列化对象类型，这取决于你要怎么解析请求返回的参数*/
        ResponseEntity<AccessTokenModel> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, AccessTokenModel.class);

        return responseEntity.getBody();
    }

    @Data
    class AccessToken{
        private long expiresTime;
        private String AccessToken;
    }

}
