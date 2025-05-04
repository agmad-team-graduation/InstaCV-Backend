package com.Graduation.InstaCv.mappers.Impl.CV;

import com.Graduation.InstaCv.data.model.cv.items.EducationCv;
import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class EducationCvMapper implements Mapper<EducationCv, Education> {
    @Override
    public Education mapTo(EducationCv educationCv) {
        return Education.builder()
                .degree(educationCv.getDegree())
                .school(educationCv.getSchool())
                .city(educationCv.getCity())
                .country(educationCv.getCountry())
                .startDate(educationCv.getStartDate())
                .endDate(educationCv.getEndDate())
                .isPresent(educationCv.isPresent())
                .description(educationCv.getDescription())
                .build();
    }

    @Override
    public EducationCv mapFrom(Education education) {
        return EducationCv.builder()
                .degree(education.getDegree())
                .school(education.getSchool())
                .city(education.getCity())
                .country(education.getCountry())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .isPresent(education.isPresent())
                .description(education.getDescription())
                .build();
    }
}
