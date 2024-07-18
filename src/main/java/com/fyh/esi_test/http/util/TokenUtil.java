package com.fyh.esi_test.http.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyh.esi_test.config.ESIConfig;
import com.fyh.esi_test.http.vo.Oauth2TokenBo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @author fyh
 */
@Component
public class TokenUtil {

    @Resource
    HttpUtil httpUtil;

    @Resource
    ESIConfig esiConfig;


    public String esiAccessToken(String code) throws NoSuchAlgorithmException, InvalidKeySpecException, JsonProcessingException {
        JsonNode rootNode = new ObjectMapper().readTree(esiConfig.getRasPublicKey());

        Oauth2TokenBo oauth2TokenBo = httpUtil.esiHttp(code);

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

            Jws<Claims> claimsJws = Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(oauth2TokenBo.getAccess_token());
            return claimsJws.getPayload().toString();
        }
        return null;
    }

}
