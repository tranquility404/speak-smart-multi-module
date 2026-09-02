package com.tranquility.auth.service;

import com.tranquility.auth.JWTUtil;
import com.tranquility.auth.config.OAuthProperties;
import com.tranquility.auth.dto.LoginRequest;
import com.tranquility.auth.dto.RegisterRequest;
import com.tranquility.common.auth.UserAuthPort;
import com.tranquility.common.auth.AuthException;
import com.tranquility.common.user.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserAuthPort port;
    private final JWTUtil jwt;
    private final OAuthProperties authProperties;

    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String registerWithGoogle(String code) {
        String idToken = getToken(code,
                authProperties.google().clientId(),
                authProperties.google().clientSecret(),
                authProperties.google().redirectUri(),
                "https://oauth2.googleapis.com/token",
                "id_token");

        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            log.error("Google Auth Failed! status: {} body: {}",
                    userInfoResponse.getStatusCode(), userInfoResponse.getBody()
            );
            throw new AuthException("Google Auth Failed!");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");

        port.createUserForExternalAuth(
                name,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                picture
        );
        return jwt.generateToken(email);
    }

    public String registerWithGithub(String code) {
        String accessToken = getToken(code,
                authProperties.github().clientId(),
                authProperties.github().clientSecret(),
                authProperties.github().redirectUri(),
                "https://github.com/login/oauth/access_token",
                "access_token");

        String userInfoUrl = "https://api.github.com/user";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userRequest, Map.class);

        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            log.error("Github Auth Failed! status: {} body: {}",
                    userInfoResponse.getStatusCode(), userInfoResponse.getBody()
            );
            throw new AuthException("Github Auth Failed!");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String email = (String) userInfo.get("email");             // Can be null if private
        // If email is null, try another endpoint
        if (email == null) {
            String emailsUrl = "https://api.github.com/user/emails";
            HttpEntity<Void> emailRequest = new HttpEntity<>(userRequest.getHeaders());
            ResponseEntity<List> emailsResponse = restTemplate.exchange(
                    emailsUrl,
                    HttpMethod.GET,
                    emailRequest,
                    List.class
            );

            if (emailsResponse.getStatusCode() == HttpStatus.OK && !emailsResponse.getBody().isEmpty()) {
                Map<String, Object> primaryEmail = (Map<String, Object>) emailsResponse.getBody().get(0);
                email = (String) primaryEmail.get("email");
            }
        }

        if (email == null) throw new AuthException("Email not available from Github!");

        String name = (String) userInfo.get("name");               // Full name (can be null)
        String avatarUrl = (String) userInfo.get("avatar_url");    // Profile picture

        port.createUserForExternalAuth(
                name,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                avatarUrl
        );
        return jwt.generateToken(email);
    }

    public String registerWithLinkedin(String code) {
        String accessToken = getToken(code,
                authProperties.linkedin().clientId(),
                authProperties.linkedin().clientSecret(),
                authProperties.linkedin().redirectUri(),
                "https://www.linkedin.com/oauth/v2/accessToken",
                "access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("X-Restli-Protocol-Version", "2.0.0"); // required for v2 API
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String userInfoUrl = "https://api.linkedin.com/v2/userinfo";
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, Map.class);
        if (userInfoResponse.getStatusCode() != HttpStatus.OK) {
            log.error("LinkedIn Auth Failed! status: {} body: {}",
                    userInfoResponse.getStatusCode(), userInfoResponse.getBody()
            );
            throw new AuthException("LinkedIn Auth Failed!");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();
        String firstName = (String) userInfo.get("given_name"); // OIDC standard
        String lastName = (String) userInfo.get("family_name");
        String fullName = firstName + " " + lastName;
        String email = (String) userInfo.get("email"); // OIDC standard
        String profilePicUrl = (String) userInfo.get("picture"); // OIDC standard

        if (email == null) throw new AuthException("Email not available from LinkedIn!");

        port.createUserForExternalAuth(
                fullName,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()),
                profilePicUrl
        );
        return jwt.generateToken(email);
    }

    public String registerWithEmail(RegisterRequest request) {
        port.createUserForEmailAuth(request.name(), request.email(), passwordEncoder.encode(request.password()));
        return jwt.generateToken(request.email());
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserPrincipal user = port.findUserByEmail(request.email());
        return jwt.generateToken(user.getEmail());
    }

//    OAuth 2.0 token endpoint JSON expect nahi karta, woh application/x-www-form-urlencoded form data expect karta hai.
//    Isliye hum LinkedMultiValueMap use karte hain.
//    Spring isme jo key-value pairs add karte hain, unhe automatically form data (key=value&key2=value2) mein
//    convert kar deta hai. Agar JSON bhejna hota, to hum generally HashMap ya koi POJO use karte
//    aur Spring usse JSON bana deta.

//    HashMap/POJO + application/json → JSON banega.
//    LinkedMultiValueMap + application/x-www-form-urlencoded → Form data banega.
    private String getToken(String code, String clientId, String clientSecret, String redirectUri, String tokenEndpoint, String tokenName) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
        return (String) tokenResponse.getBody().get(tokenName);
    }
}
