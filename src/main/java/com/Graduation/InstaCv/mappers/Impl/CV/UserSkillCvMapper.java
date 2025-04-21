package com.Graduation.InstaCv.mappers.Impl.CV;

import com.Graduation.InstaCv.data.model.CV.EducationCv;
import com.Graduation.InstaCv.data.model.CV.skills.UserSkillCv;
import com.Graduation.InstaCv.data.model.profile.Education;
import com.Graduation.InstaCv.data.model.profile.UserSkill;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserSkillCvMapper implements Mapper<UserSkillCv, UserSkill> {
    @Override
    public UserSkill mapTo(UserSkillCv userSkillCv) {
        return UserSkill.builder()
                .skill(userSkillCv.getSkill())
                .level(userSkillCv.getLevel())
                .build();
    }

    @Override
    public UserSkillCv mapFrom(UserSkill userSkill) {
        return UserSkillCv.builder()
                .skill(userSkill.getSkill())
                .level(userSkill.getLevel())
                .build();
    }
}
