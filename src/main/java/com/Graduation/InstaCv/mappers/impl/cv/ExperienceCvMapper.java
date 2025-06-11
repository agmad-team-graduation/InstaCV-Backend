package com.Graduation.InstaCv.mappers.impl.cv;

import com.Graduation.InstaCv.data.model.cv.items.ExperienceCv;
import com.Graduation.InstaCv.data.model.profile.Experience;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class ExperienceCvMapper implements Mapper<ExperienceCv, Experience> {

    @Override
    public Experience mapTo(ExperienceCv experienceCv) {
        return Experience.builder()
                .jobTitle(experienceCv.getJobTitle())
                .company(experienceCv.getCompany())
                .city(experienceCv.getCity())
                .country(experienceCv.getCountry())
                .startDate(experienceCv.getStartDate())
                .endDate(experienceCv.getEndDate())
                .isPresent(experienceCv.isPresent())
                .description(experienceCv.getDescription())
                .build();
    }

    @Override
    public ExperienceCv mapFrom(Experience experience) {
        return ExperienceCv.builder()
                .jobTitle(experience.getJobTitle())
                .company(experience.getCompany())
                .city(experience.getCity())
                .country(experience.getCountry())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .isPresent(experience.isPresent())
                .description(experience.getDescription())
                .build();
    }
}