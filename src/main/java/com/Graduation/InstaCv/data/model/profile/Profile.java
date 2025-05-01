package com.Graduation.InstaCv.data.model.profile;

import com.Graduation.InstaCv.data.model.github.GithubProfile;
import com.Graduation.InstaCv.data.model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "profiles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    @ToString.Exclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private User user;

    @Embedded
    private PersonalDetails personalDetails;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonBackReference
    private GithubProfile githubProfile;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educationList;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Experience> experienceList;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSkill> userSkills;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;

//    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Job> addedJobs;
}
