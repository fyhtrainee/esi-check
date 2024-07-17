package com.fyh.esi_test.controller;

import com.fyh.esi_test.config.ESIConfig;
import jakarta.annotation.Resource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author fyh
 */
@RestController
public class ESIController {
    @Resource
    private ESIConfig esiConfig;

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code,
                           @RequestParam("state") String state) {

        return code + " : " + state;
    }

    @GetMapping("/esi/oauth2")
    public String esiData() {

        String url = "https://login.eveonline.com/v2/oauth/authorize";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 7890)));

        String uriString = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("client_id", esiConfig.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", "http://localhost:8080/callback")
                .queryParam("scope", esiConfig.getScope())
                .queryParam("state", "fyhyuheng")
                .toUriString();
        System.out.println(uriString);
        new RestTemplate(factory).getForObject(uriString, String.class);


        return "success";
    }
}

