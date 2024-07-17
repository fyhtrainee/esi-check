package com.fyh.esi_test.http.vo;

import lombok.Data;

@Data
public class TokenResponseVo {
    private String access_token;
    private String expires_in;
    private String token_type;
    private String refresh_token;
}
