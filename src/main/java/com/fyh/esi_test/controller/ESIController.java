package com.fyh.esi_test.controller;

import com.fyh.esi_test.config.ESIConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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

        System.out.println(code + state);
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

        request.getRequestDispatcher(uriString).forward(request,response);
    }
}

