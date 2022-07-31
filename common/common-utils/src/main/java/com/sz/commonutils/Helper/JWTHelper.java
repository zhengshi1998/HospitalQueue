package com.sz.commonutils.Helper;

import io.jsonwebtoken.*;

import java.util.Date;

public class JWTHelper {
    private static final long tokenExpiration = 24 * 60 * 60 * 1000;
    private static final String tokenSignKey = "123456";

    public static String createToken(Long userId, String userName){
        return Jwts.builder()
                .setSubject("HOSPITAL-QUEUE-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
    }

    public static Long getUserId(String token){
        if(token == null) return null;
        else {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey)
                                                 .parseClaimsJws(token);
            return claimsJws.getBody().get("userId", Long.class);
        }
    }

    public static String getUsername(String token){
        if(token == null) return null;
        else {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey)
                    .parseClaimsJws(token);
            return claimsJws.getBody().get("userName", String.class);
        }
    }

    public static void main(String[] args) {
        String people2sz = createToken(123165L, "people2sz");
        System.out.println(people2sz);
        System.out.println(getUserId(people2sz));
        System.out.println(getUsername(people2sz));
    }
}
