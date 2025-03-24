package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.response.ExtractedJobSkillResponse;
import com.Graduation.InstaCv.data.model.JobSkill;
import com.Graduation.InstaCv.mappers.Mapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobSkillMapper implements Mapper<JobSkill, ExtractedJobSkillResponse> {
    private final ModelMapper modelMapper;

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(JobSkill.class, ExtractedJobSkillResponse.class)
                .addMapping(JobSkill::getName, ExtractedJobSkillResponse::setName)
                .addMapping(JobSkill::getModelConfidence, ExtractedJobSkillResponse::setConfidence);

        modelMapper.createTypeMap(ExtractedJobSkillResponse.class, JobSkill.class)
                .addMapping(ExtractedJobSkillResponse::getName, JobSkill::setName)
                .addMapping(ExtractedJobSkillResponse::getConfidence, JobSkill::setModelConfidence);
    }

    @Override
    public ExtractedJobSkillResponse mapTo(JobSkill jobSkill) {
        return modelMapper.map(jobSkill, ExtractedJobSkillResponse.class);
    }

    @Override
    public JobSkill mapFrom(ExtractedJobSkillResponse extractedJobSkillResponse) {
        return modelMapper.map(extractedJobSkillResponse, JobSkill.class);
    }
}
