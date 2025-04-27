package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.profile.Profile;

public interface IProfileService {
    Profile getProfileByUserId(Long userId);
    Profile createProfile(Long userId, Profile profile);

}
