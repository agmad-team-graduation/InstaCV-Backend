package com.Graduation.InstaCv.gateways.linkedin;

import com.Graduation.InstaCv.data.dto.request.LinkedinAccessTokenRequest;
import com.Graduation.InstaCv.data.dto.response.LinkedinAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "linkedInAuthClient", url = "https://www.linkedin.com")
public interface LinkedinAuthClient {
    @PostMapping(value = "/oauth/v2/accessToken", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    LinkedinAccessTokenResponse getAccessToken(@SpringQueryMap LinkedinAccessTokenRequest request);
}
