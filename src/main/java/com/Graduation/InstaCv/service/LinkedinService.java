package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.data.dto.request.LinkedinAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.AuthLink;
import com.Graduation.InstaCv.data.dto.response.LinkedinAccessTokenResponse;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinProfile;
import com.Graduation.InstaCv.gateways.linkedin.LinkedinAuthClient;
import com.Graduation.InstaCv.service.Interfaces.ILinkedinService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LinkedinService implements ILinkedinService {

    @Value("${linkedin.client.id}")
    private String clientId;

    @Value("${linkedin.client.secret}")
    private String clientSecret;

    @Value("${linkedin.callback.url}")
    private String callbackUrl;

    private final LinkedinAuthClient authClient;

    public LinkedinService(LinkedinAuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public AuthLink getAuthorizationUrl() {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.linkedin.com/oauth/v2/authorization")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", callbackUrl)
                .queryParam("scope", "r_liteprofile r_emailaddress r_basicprofile")
                .encode()           // percent-encode reserved characters (spaces -> %20)
                .toUriString();
        return AuthLink.builder().authLink(url).build();
    }

    public LinkedinAccessTokenResponse getAccessToken(String code) {
        LinkedinAccessTokenRequest req = LinkedinAccessTokenRequest.builder()
                .grantType("authorization_code")
                .code(code)
                .redirectUri(callbackUrl)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
        return authClient.getAccessToken(req);
    }

    @Override
    public LinkedinProfile getUserProfile(String accessToken, boolean forceFetch) {
        return null; // TODO: Implement later, now I want to make sure access token is working
    }
}
