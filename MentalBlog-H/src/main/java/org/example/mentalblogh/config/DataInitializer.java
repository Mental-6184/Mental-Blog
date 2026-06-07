package org.example.mentalblogh.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mentalblogh.common.constant.UserRole;
import org.example.mentalblogh.entity.*;
import org.example.mentalblogh.mapper.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final SkillMapper skillMapper;
    private final ProjectMapper projectMapper;
    private final MilestoneMapper milestoneMapper;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder,
                           CategoryMapper categoryMapper, TagMapper tagMapper,
                           SkillMapper skillMapper, ProjectMapper projectMapper,
                           MilestoneMapper milestoneMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.skillMapper = skillMapper;
        this.projectMapper = projectMapper;
        this.milestoneMapper = milestoneMapper;
    }

    @Override
    public void run(String... args) {
        // === 1. 管理员账号 ===
        LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
        userQuery.eq(User::getUsername, "admin");
        User admin = userMapper.selectOne(userQuery);
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setNickname("管理员");
            admin.setEmail("admin@mentalblog.com");
            admin.setRole(UserRole.ADMIN);
            admin.setStatus(0);
            admin.setBio("博客管理员");
        }
        admin.setPassword(passwordEncoder.encode("admin123"));
        if (admin.getId() == null) {
            userMapper.insert(admin);
        } else {
            userMapper.updateById(admin);
        }
        System.out.println(">>> 管理员账号已就绪 (admin / admin123)");

        // === 2. 默认分类 ===
        String[][] defaultCategories = {
            {"tech", "技术", "Java、Spring Boot、Vue 等技术相关文章", "1"},
            {"life", "生活", "生活感悟与日常分享", "2"},
            {"project", "项目", "项目实战经验与架构分享", "3"},
            {"reading", "读书笔记", "阅读笔记与书评分享", "4"},
            {"essay", "随笔", "随心所想，随笔记录", "5"},
        };
        for (String[] cat : defaultCategories) {
            LambdaQueryWrapper<Category> cq = new LambdaQueryWrapper<>();
            cq.eq(Category::getSlug, cat[0]);
            if (categoryMapper.selectCount(cq) == 0) {
                Category c = new Category();
                c.setSlug(cat[0]);
                c.setName(cat[1]);
                c.setDescription(cat[2]);
                c.setSort(Integer.parseInt(cat[3]));
                categoryMapper.insert(c);
            }
        }
        System.out.println(">>> 默认分类已就绪 (tech/life/project/reading/essay)");

        // === 3. 默认标签 ===
        String[] defaultTags = {"Java", "Spring Boot", "Vue", "MySQL", "Redis", "Docker", "Linux", "TypeScript", "Python", "前端", "后端", "架构"};
        for (String tagName : defaultTags) {
            LambdaQueryWrapper<Tag> tq = new LambdaQueryWrapper<>();
            tq.eq(Tag::getName, tagName);
            if (tagMapper.selectCount(tq) == 0) {
                Tag t = new Tag();
                t.setName(tagName);
                t.setSlug(tagName.toLowerCase().replaceAll("\\s+", "-"));
                tagMapper.insert(t);
            }
        }
        System.out.println(">>> 默认标签已就绪 (12 个常用标签)");

        // === 4. 关于我 - 默认技能栈 ===
        String[][] defaultSkills = {
            {"Java", "90", "1"}, {"Spring Boot", "85", "2"}, {"Vue 3", "80", "3"},
            {"MySQL", "80", "4"}, {"Redis", "70", "5"}, {"Docker", "65", "6"},
            {"Linux", "70", "7"}, {"TypeScript", "75", "8"}
        };
        for (String[] s : defaultSkills) {
            LambdaQueryWrapper<Skill> sq = new LambdaQueryWrapper<>();
            sq.eq(Skill::getName, s[0]);
            if (skillMapper.selectCount(sq) == 0) {
                Skill sk = new Skill();
                sk.setName(s[0]); sk.setLevel(Integer.parseInt(s[1])); sk.setSort(Integer.parseInt(s[2]));
                skillMapper.insert(sk);
            }
        }
        System.out.println(">>> 默认技能已就绪 (8 项技能)");

        // === 5. 关于我 - 默认项目 ===
        String[][] defaultProjects = {
            {"MentalBlog", "全功能个人博客系统，前后端分离架构", "https://github.com", "1"},
            {"游戏抽奖系统", "基于 Web 的在线抽奖游戏平台", "", "2"},
            {"接单平台", "连接学生与项目的在线协作平台", "", "3"},
            {"车库管理系统", "智能车库车辆出入管理系统", "", "4"}
        };
        for (String[] p : defaultProjects) {
            LambdaQueryWrapper<Project> pq = new LambdaQueryWrapper<>();
            pq.eq(Project::getName, p[0]);
            if (projectMapper.selectCount(pq) == 0) {
                Project pr = new Project();
                pr.setName(p[0]); pr.setDescription(p[1]); pr.setUrl(p[2]); pr.setSort(Integer.parseInt(p[3]));
                projectMapper.insert(pr);
            }
        }
        System.out.println(">>> 默认项目已就绪 (4 个项目)");

        // === 6. 关于我 - 默认成长里程碑 ===
        String[][] defaultMilestones = {
            {"2023", "进入大学", "软件工程专业，开始编程之旅", "1"},
            {"2024", "学习 Java 开发", "掌握 Spring Boot 后端开发技术栈", "2"},
            {"2025", "开始接项目", "参与多个 Web 项目开发，积累实战经验", "3"},
            {"2026", "开发 MentalBlog", "从零搭建完整博客系统，前后端分离", "4"}
        };
        for (String[] m : defaultMilestones) {
            LambdaQueryWrapper<Milestone> mq = new LambdaQueryWrapper<>();
            mq.eq(Milestone::getTitle, m[1]);
            if (milestoneMapper.selectCount(mq) == 0) {
                Milestone ms = new Milestone();
                ms.setYear(m[0]); ms.setTitle(m[1]); ms.setDescription(m[2]); ms.setSort(Integer.parseInt(m[3]));
                milestoneMapper.insert(ms);
            }
        }
        System.out.println(">>> 默认里程碑已就绪 (4 个里程碑)");
    }
}
