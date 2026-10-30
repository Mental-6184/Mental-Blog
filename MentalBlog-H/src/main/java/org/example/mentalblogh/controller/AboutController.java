package org.example.mentalblogh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.result.Result;
import org.example.mentalblogh.entity.Milestone;
import org.example.mentalblogh.entity.Project;
import org.example.mentalblogh.entity.Skill;
import org.example.mentalblogh.mapper.MilestoneMapper;
import org.example.mentalblogh.mapper.ProjectMapper;
import org.example.mentalblogh.mapper.SkillMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/about")
public class AboutController {

    private final SkillMapper skillMapper;
    private final ProjectMapper projectMapper;
    private final MilestoneMapper milestoneMapper;

    public AboutController(SkillMapper skillMapper, ProjectMapper projectMapper,
                            MilestoneMapper milestoneMapper) {
        this.skillMapper = skillMapper;
        this.projectMapper = projectMapper;
        this.milestoneMapper = milestoneMapper;
    }

    @GetMapping
    public Result<Map<String, Object>> getAbout() {
        Map<String, Object> data = new HashMap<>();
        data.put("skills", skillMapper.selectList(new LambdaQueryWrapper<Skill>().orderByAsc(Skill::getSort)));
        data.put("projects", projectMapper.selectList(new LambdaQueryWrapper<Project>().orderByAsc(Project::getSort)));
        data.put("milestones", milestoneMapper.selectList(new LambdaQueryWrapper<Milestone>().orderByAsc(Milestone::getSort)));
        return Result.success(data);
    }
}
