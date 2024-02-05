package org.example.springcloudapigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestController {
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @RequestMapping("/getToken")
    public String hello() {
        return jwtTokenProvider.generateToken("msisdn", "code");
    }
}
