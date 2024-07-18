package com.fyh.esi_test.http.vo;

import lombok.Data;

import java.util.List;

/**
 * @author fyh
 */
@Data
public class AccessTokenBo {
    private List<String> scp;
    private String jti;
    private String kid;
    private String sub;
    private String azp;
    private String tenant;
    private String tier;
    private String region;
    private List<String> aud;
    private String name;
    private String owner;
    private Long exp;
    private Long iat;
    private String iss;
}
