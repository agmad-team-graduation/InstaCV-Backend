package com.Graduation.InstaCv.data.model.job;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "remote_job_data")
public class RemoteJobData {
    @Id
    private Long id;

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    @Column(name = "apply_url", length = 2000)
    private String applyUrl;

    private String date;


}
