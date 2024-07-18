package com.fyh.esi_test.http.vo;

import lombok.Data;

/**
 * @author fyh
 */
@Data
public class Oauth2TokenBo {
    private String access_token;
    private String expires_in;
    private String token_type;
    private String refresh_token;
}
