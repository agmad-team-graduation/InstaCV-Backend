package com.Graduation.InstaCv.service;

import com.Graduation.InstaCv.model.Job;
import com.Graduation.InstaCv.repository.JobRepository;
import com.Graduation.InstaCv.service.Interfaces.IJobService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JobService implements IJobService {
    private final JobRepository jobRepository;

    @Override
    public Job addJob(Job job) {
        return jobRepository.save(job);
    }
}
