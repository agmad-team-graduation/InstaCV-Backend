package com.Graduation.InstaCv.repository;


import com.Graduation.InstaCv.data.model.job.RemoteJobData;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.Set;


@Repository

public interface RemoteJobDataRepository extends JpaRepository<RemoteJobData, Long> {
    @Query("SELECT r.remoteId FROM RemoteJobData r")
    Set<String> findAllRemoteIds();
    Optional<RemoteJobData> findByRemoteId(String remoteId);
}