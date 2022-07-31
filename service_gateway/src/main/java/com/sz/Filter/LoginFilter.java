package com.sz.Filter;

import com.alibaba.fastjson.JSONObject;
import com.sz.commonutils.Helper.JWTHelper;
import com.sz.commonutils.Result.Result;
import com.sz.commonutils.Result.ResultCode;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LoginFilter implements GlobalFilter, Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if(antPathMatcher.match("/**/inner/**", path)){
            ServerHttpResponse response = exchange.getResponse();
            return out(response, ResultCode.FAIL);
        }

        Long userId = this.getUserId(request);

        if(antPathMatcher.match("/api/**/auth/**", path)){
            if(userId == null){
                ServerHttpResponse response = exchange.getResponse();
                return out(response, ResultCode.FAIL);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> out(ServerHttpResponse response, ResultCode resultCode) {
        Result result = Result.build(null, resultCode);
        byte[] bytes = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(dataBuffer));
    }

    private Long getUserId(ServerHttpRequest request) {
        String token = "";
        List<String> tokens = request.getHeaders().get("token");

        if(tokens != null && tokens.size() > 0){
            token = tokens.get(0);
        }

        if(token != null){
            return JWTHelper.getUserId(token);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
