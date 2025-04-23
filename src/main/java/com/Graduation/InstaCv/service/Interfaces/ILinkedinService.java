package com.Graduation.InstaCv.service.Interfaces;


import com.Graduation.InstaCv.data.dto.response.AuthLink;
import com.Graduation.InstaCv.data.dto.response.LinkedinAccessTokenResponse;
import com.Graduation.InstaCv.data.model.linkedin.LinkedinProfile;

public interface ILinkedinService {
    public AuthLink getAuthorizationUrl();
    public LinkedinAccessTokenResponse getAccessToken(String code);
    public LinkedinProfile getUserProfile(String accessToken, boolean forceFetch);
}
