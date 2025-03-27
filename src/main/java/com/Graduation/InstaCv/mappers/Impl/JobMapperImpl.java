package com.Graduation.InstaCv.mappers.impl;

import com.Graduation.InstaCv.data.dto.JobDto;
import com.Graduation.InstaCv.data.model.Job;
import com.Graduation.InstaCv.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class JobMapperImpl implements Mapper<Job, JobDto> {
    private ModelMapper modelMapper;

    public JobMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public JobDto mapTo(Job job) {
        return modelMapper.map(job,JobDto.class);
    }

    @Override
    public Job mapFrom(JobDto jobDto) {
        return modelMapper.map(jobDto,Job.class);
    }
}
