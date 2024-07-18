package com.fyh.esi_test.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fyh.esi_test.config.ESIConfig;
import com.fyh.esi_test.http.util.TokenUtil;
import com.fyh.esi_test.http.vo.AccessTokenBo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author fyh
 */
@RestController
public class ESIController {
    @Resource
    private ESIConfig esiConfig;
    @Resource
    private TokenUtil tokenUtil;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {


        String accessToken = tokenUtil.esiAccessToken(code);

        AccessTokenBo accessTokenBo = JSONObject.parseObject(accessToken, AccessTokenBo.class);

        return code + " : " + state;
    }

    @GetMapping("/esi/oauth2")
    public void esiData(HttpServletResponse response) throws IOException {

        String url = "https://login.eveonline.com/v2/oauth/authorize";

        String uriString = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("client_id", esiConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "http://localhost:8080/callback")
                .queryParam("scope", esiConfig.getScope())
                .queryParam("state", "fyhyuheng")
                .toUriString();
        response.sendRedirect(uriString);
    }
}

