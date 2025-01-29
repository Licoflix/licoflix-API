package com.licoflix.core.service.rest;

import com.licoflix.core.domain.dto.UserDetailsResponse;
import com.licoflix.util.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RestService {

    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String baseUrl;

    public UserDetailsResponse getUserDetails(String token, String timezone) {
        String url = baseUrl + "/auth/user/get";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Timezone", timezone);
            headers.set("Authorization", token);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<DataResponse<UserDetailsResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Error in get UserDetails info.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in get UserDetails info.", e);
        }
    }
}
