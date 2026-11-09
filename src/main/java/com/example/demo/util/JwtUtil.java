package com.example.demo.util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${token.secret}")
    private String secret;

    @Value("${token.expireTime}")
    private Integer expireTime;

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    //生成token,存入userId
    public String createToken(Long userId){
        Date now = new Date();
        //过期时间：当前时间+分秒转毫秒
        Date expireDate = new Date(now.getTime() + expireTime * 60 * 1000L);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSignKey(),SignatureAlgorithm.HS256)
                .compact();
    }
    // 从token拿到userId
    public Long getUserId(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    //检验token是否有效
    public boolean verifyToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (Exception e){
            //签名错误、过期、篡改都会进这里
            return false;
        }
    }






}
