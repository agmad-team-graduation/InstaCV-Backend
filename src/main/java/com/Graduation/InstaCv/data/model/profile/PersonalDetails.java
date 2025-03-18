package com.Graduation.InstaCv.data.model.profile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalDetails {
    @Column(name = "profile_full_name")
    private String fullName;
    @Column(name="profile_email")
    private String email;
    @Column(name="profile_phone")
    private String phone;
    @Column(name="profile_address")
    private String address;
}
