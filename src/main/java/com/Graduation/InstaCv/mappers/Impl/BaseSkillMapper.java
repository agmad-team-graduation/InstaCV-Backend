package com.Graduation.InstaCv.mappers.Impl;

import com.Graduation.InstaCv.data.dto.BaseSkillDto;
import com.Graduation.InstaCv.data.model.BaseSkill;
import com.Graduation.InstaCv.mappers.Mapper;
import org.springframework.stereotype.Component;

@Component
public class BaseSkillMapper implements Mapper<BaseSkill, BaseSkillDto> {
    @Override
    public BaseSkillDto mapTo(BaseSkill baseSkill) {
        return BaseSkillDto.builder()
                .id(baseSkill.getId())
                .skill(baseSkill.getSkill())
                .build();
    }

    @Override
    public BaseSkill mapFrom(BaseSkillDto baseSkillDto) {
        return BaseSkill.builder()
                .id(baseSkillDto.getId())
                .skill(baseSkillDto.getSkill())
                .build();
    }
}
