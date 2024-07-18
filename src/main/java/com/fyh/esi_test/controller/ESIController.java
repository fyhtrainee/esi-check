package com.fyh.esi_test.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyh.esi_test.config.ESIConfig;
import com.fyh.esi_test.http.vo.TokenResponseVo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * @author fyh
 */
@RestController
public class ESIController {
    @Resource
    private ESIConfig esiConfig;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {

        System.out.println(code + state);

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

        HttpEntity<MultiValueMap<String, String>> stringHttpEntity = new HttpEntity<>(requestBody, headers);


        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        String result = restTemplate.postForObject("https://login.eveonline.com/v2/oauth/token", stringHttpEntity, String.class);

        TokenResponseVo tokenResponseVo = JSONObject.parseObject(result, TokenResponseVo.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(esiConfig.getRasPublicKey());

        // 获取公钥信息
        JsonNode keysNode = rootNode.get("keys");
        if (keysNode.isArray() && !keysNode.isEmpty()) {
            JsonNode keyNode = keysNode.get(0); // 获取rs256
            String n = keyNode.get("n").asText();
            String e = keyNode.get("e").asText();

            // Base64 解码 n 和 e
            byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
            byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

            // 转换为 BigInteger
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);

            // 创建 RSA 公钥
            RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);


            Jws<Claims> claimsJws = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(tokenResponseVo.getAccess_token());
            System.out.println(claimsJws.getPayload());
        }


        return code + " : " + state;
    }

    @GetMapping("/esi/oauth2")
    public void esiData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String url = "https://login.eveonline.com/v2/oauth/authorize";

        String uriString = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("client_id", esiConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "http://localhost:8080/callback")
                .queryParam("scope", esiConfig.getScope())
                .queryParam("state", "fyhyuheng")
                .toUriString();

        System.out.println(uriString);
        response.sendRedirect(uriString);
    }
}

