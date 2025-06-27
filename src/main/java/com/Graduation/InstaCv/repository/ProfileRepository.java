package com.Graduation.InstaCv.repository;

import com.Graduation.InstaCv.data.model.profile.Profile;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);
    @Query("SELECT p.id FROM Profile p WHERE p.user.id = :userId")
    Optional<Long> findProfileIdByUserId(@Param("userId") Long userId);
    // exists by github_profile_username
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Profile p WHERE p.githubProfile.username = :username")
    boolean existsByGithubProfileUsername(@Param("username") String username);
}
