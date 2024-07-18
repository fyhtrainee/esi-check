package com.fyh.esi_test.http.util;

import com.alibaba.fastjson2.JSONObject;
import com.fyh.esi_test.config.ESIConfig;
import com.fyh.esi_test.http.vo.Oauth2TokenBo;
import jakarta.annotation.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;

/**
 * @author fyh
 */
@Component
public class HttpUtil {
    @Resource
    ESIConfig esiConfig;

    public Oauth2TokenBo esiHttp(String code) {

        String encode64 = Base64.getEncoder().encodeToString((esiConfig.getClientId() + ":" + esiConfig.getSecretKey()).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        headers.set("Authorization", "Basic " + encode64);
        headers.setHost(InetSocketAddress.createUnresolved("login.eveonline.com", 80));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", code);


        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890));
        clientHttpRequestFactory.setProxy(proxy);


        String result = new RestTemplate(clientHttpRequestFactory)
                .postForObject(esiConfig.getOauth2Http(), new HttpEntity<>(requestBody, headers), String.class);

        return JSONObject.parseObject(result, Oauth2TokenBo.class);
    }
}
