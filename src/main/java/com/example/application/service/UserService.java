package com.example.application.service;

import com.example.application.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    @Value("${user.service.url}")
    private String baseUrl;

    public UserService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public UserAccount getUserDetails(String userId) {
        return restTemplate.getForObject(baseUrl + "/api/user/" + userId, UserAccount.class);
    }

    public void updateUserDetails(String userId, UserAccount user) {
        restTemplate.put(baseUrl + "/api/user/" + userId, user);
    }


}
