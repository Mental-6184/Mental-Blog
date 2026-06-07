package org.example.mentalblogh.controller.admin;

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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/about")
public class AdminAboutController {

    private final SkillMapper skillMapper;
    private final ProjectMapper projectMapper;
    private final MilestoneMapper milestoneMapper;

    public AdminAboutController(SkillMapper skillMapper, ProjectMapper projectMapper,
                                 MilestoneMapper milestoneMapper) {
        this.skillMapper = skillMapper;
        this.projectMapper = projectMapper;
        this.milestoneMapper = milestoneMapper;
    }

    // ===== 获取全部数据 =====
    @GetMapping
    public Result<Map<String, Object>> getAll() {
        Map<String, Object> data = new HashMap<>();
        data.put("skills", skillMapper.selectList(new LambdaQueryWrapper<Skill>().orderByAsc(Skill::getSort)));
        data.put("projects", projectMapper.selectList(new LambdaQueryWrapper<Project>().orderByAsc(Project::getSort)));
        data.put("milestones", milestoneMapper.selectList(new LambdaQueryWrapper<Milestone>().orderByAsc(Milestone::getSort)));
        return Result.success(data);
    }

    // ===== 技能栈 CRUD =====
    @GetMapping("/skills")
    public Result<List<Skill>> getSkills() {
        return Result.success(skillMapper.selectList(new LambdaQueryWrapper<Skill>().orderByAsc(Skill::getSort)));
    }

    @PostMapping("/skills")
    public Result<Void> createSkill(@RequestBody Skill skill) {
        skillMapper.insert(skill);
        return Result.success("创建成功", null);
    }

    @PutMapping("/skills/{id}")
    public Result<Void> updateSkill(@PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        skillMapper.updateById(skill);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/skills/{id}")
    public Result<Void> deleteSkill(@PathVariable Long id) {
        skillMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    // ===== 项目经历 CRUD =====
    @GetMapping("/projects")
    public Result<List<Project>> getProjects() {
        return Result.success(projectMapper.selectList(new LambdaQueryWrapper<Project>().orderByAsc(Project::getSort)));
    }

    @PostMapping("/projects")
    public Result<Void> createProject(@RequestBody Project project) {
        projectMapper.insert(project);
        return Result.success("创建成功", null);
    }

    @PutMapping("/projects/{id}")
    public Result<Void> updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        projectMapper.updateById(project);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/projects/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        projectMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    // ===== 成长里程碑 CRUD =====
    @GetMapping("/milestones")
    public Result<List<Milestone>> getMilestones() {
        return Result.success(milestoneMapper.selectList(new LambdaQueryWrapper<Milestone>().orderByAsc(Milestone::getSort)));
    }

    @PostMapping("/milestones")
    public Result<Void> createMilestone(@RequestBody Milestone milestone) {
        milestoneMapper.insert(milestone);
        return Result.success("创建成功", null);
    }

    @PutMapping("/milestones/{id}")
    public Result<Void> updateMilestone(@PathVariable Long id, @RequestBody Milestone milestone) {
        milestone.setId(id);
        milestoneMapper.updateById(milestone);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/milestones/{id}")
    public Result<Void> deleteMilestone(@PathVariable Long id) {
        milestoneMapper.deleteById(id);
        return Result.success("删除成功", null);
    }
}
