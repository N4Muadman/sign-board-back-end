package com.techbytedev.signboardmanager.controller;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth/canva")
public class CanvaAuthController {

    private static final Logger logger = LoggerFactory.getLogger(CanvaAuthController.class);

    // Truyền trực tiếp giá trị
    private final String clientId = "OC-AZZ6L9_JWeQ6";
    private final String clientSecret = "<NEW_CLIENT_SECRET>"; // Thay bằng Client Secret mới sau khi tạo
    private final String redirectUri = "http://127.0.0.1:3000/callback";

    @PostMapping
    public ResponseEntity<?> exchangeCodeForToken(@RequestBody CanvaAuthRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", request.getCode());

        logger.info("Request body for Canva token exchange: {}", body.toString());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        String tokenUrl = "https://api.canva.com/v1/oauth/token";

        try {
            logger.info("Attempting to exchange Canva code for token. Redirect URI used: {}", redirectUri);
            ResponseEntity<CanvaAuthResponse> response = restTemplate.postForEntity(
                tokenUrl,
                entity,
                CanvaAuthResponse.class
            );
            logger.info("Successfully exchanged code for token: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            logger.error("Error exchanging code for token: Status Code: {}, Response: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode())
                .body(new ErrorResponse("Failed to exchange code for token: " + e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Generic error exchanging code for token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to exchange code for token due to an internal error: " + e.getMessage()));
        }
    }
}

class CanvaAuthRequest {
    private String code;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}

class CanvaAuthResponse {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String refresh_token;
    public String getAccess_token() { return access_token; }
    public void setAccess_token(String access_token) { this.access_token = access_token; }
    public String getToken_type() { return token_type; }
    public void setToken_type(String token_type) { this.token_type = token_type; }
    public int getExpires_in() { return expires_in; }
    public void setExpires_in(int expires_in) { this.expires_in = expires_in; }
    public String getRefresh_token() { return refresh_token; }
    public void setRefresh_token(String refresh_token) { this.refresh_token = refresh_token; }
}

class ErrorResponse {
    private String error;
    public ErrorResponse(String error) { this.error = error; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}