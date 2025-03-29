package com.Graduation.InstaCv.service.Interfaces;

import com.Graduation.InstaCv.data.model.User;
import com.Graduation.InstaCv.data.model.profile.Profile;

import java.util.UUID;

public interface IProfileService {
    User getProfile(Long userId);
    User fullUpdateProfile(Long userId, Profile profile);
}
