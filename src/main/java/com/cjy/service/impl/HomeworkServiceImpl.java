package com.cjy.service.impl;

import java.util.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.cjy.common.RedisConstants;

import com.cjy.common.RedisData;
import com.cjy.common.Result;
import com.cjy.domain.CourseWithStudents;
import com.cjy.domain.Homework;
import com.cjy.domain.HomeworkSubmission;
import com.cjy.domain.Student;
import com.cjy.domain.StudentWithGroup;
import com.cjy.domain.Teacher;
import com.cjy.dto.PublishHomeworkDTO;
import com.cjy.mapper.CourseMapper;
import com.cjy.mapper.CourseWithStudentsMapper;
import com.cjy.mapper.HomeworkMapper;
import com.cjy.mapper.HomeworkSubmissionMapper;
import com.cjy.mapper.StudentMapper;
import com.cjy.mapper.StudentWithGroupMapper;
import com.cjy.mapper.TeacherMapper;
import com.cjy.service.IHomeworkMessageProducer;
import com.cjy.service.IHomeworkSubmissionService;
import com.cjy.service.IHomeworkService;
import com.cjy.utils.CacheClient;
import com.cjy.vo.HomeworkSubmissionVO;
import com.cjy.vo.HomeworkVO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements IHomeworkService {
    private static final Map<Long, List<HomeworkSubmission>> LOCAL_CACHE = new ConcurrentHashMap<>();

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private IHomeworkMessageProducer homeworkMessageProducer;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    @Autowired
    private CourseWithStudentsMapper courseWithStudentsMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentWithGroupMapper studentWithGroupMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // =================================================================
    // 工具方法
    // =================================================================
    /**
     * 计算作业提交缓存过期时间
     * 
     * @param deadline
     * @return
     */
    private long calculateSubmissionCacheTTL(java.util.Date deadline) {
        if (deadline == null) {
            return RedisConstants.HOMEWORK_SUBMISSION_TTL * 60;
        }

        long now = System.currentTimeMillis();
        long deadlineTime = deadline.getTime();
        long diffMillis = deadlineTime - now;

        // 截止时间距离现在的时间差
        long diffSeconds = (deadlineTime - now) / 1000;

        // 增加30分钟缓冲期
        long bufferSeconds = 30 * 60;
        long ttlSeconds = diffSeconds + bufferSeconds;

        // 如果截止时间过了，默认使用ttl
        if (diffSeconds < 0) {
            return RedisConstants.HOMEWORK_SUBMISSION_TTL * 60;
        }

        // 限制最大为7天
        long maxTTL = 7 * 24 * 60 * 60;
        if (ttlSeconds > maxTTL) {
            ttlSeconds = maxTTL;
        }

        return ttlSeconds;
    }

    /**
     * 将作业列表转换为VO列表
     * 
     * @param homeworkList
     * @return
     */
    private List<HomeworkVO> ConvertToVO(List<Homework> homeworkList) {
        List<HomeworkVO> homeworkVOs = new ArrayList<>();
        if (homeworkList == null || homeworkList.isEmpty()) {
            return homeworkVOs;
        }
        for (Homework hw : homeworkList) {
            // 查询课程信息（加空值检查）
            String courseName = "未知课程";
            if (hw.getCourseId() != null) {
                com.cjy.domain.Course course = courseMapper.selectById(hw.getCourseId());
                if (course != null) {
                    courseName = course.getCourseName();
                }
            }

            // 查询教师信息（加空值检查）
            String teacherName = "未知教师";
            if (hw.getTeacherId() != null) {
                com.cjy.domain.Teacher teacher = teacherMapper.selectById(hw.getTeacherId());
                if (teacher != null) {
                    teacherName = teacher.getName();
                }
            }

            // 查询发布学生数量
            Long publishStudentCount = homeworkSubmissionMapper.selectCount(
                    new LambdaQueryWrapper<HomeworkSubmission>()
                            .eq(HomeworkSubmission::getHomeworkId, hw.getId()));
            // 查询所属课程班级人数
            Long courseClassStudentCount = courseWithStudentsMapper.selectCount(
                    new LambdaQueryWrapper<CourseWithStudents>()
                            .eq(CourseWithStudents::getCourseId, hw.getCourseId()));

            // 提交率
            // 查询有多少人的作业状态为1
            // 除以发布学生数量
            Double submitRate = (double) homeworkSubmissionMapper.selectCount(
                    new LambdaQueryWrapper<HomeworkSubmission>()
                            .eq(HomeworkSubmission::getHomeworkId, hw.getId())
                            .eq(HomeworkSubmission::getSubmitStatus, 1))
                    / publishStudentCount;

            HomeworkVO vo = HomeworkVO.builder()
                    .id(hw.getId())
                    .title(hw.getTitle())
                    .courseName(courseName)
                    .teacherName(teacherName)
                    .totalScore(hw.getTotalScore())
                    .status(hw.getStatus())
                    .remark(hw.getRemark())
                    .submitRate(submitRate)
                    .publishStudentCount(publishStudentCount)
                    .courseClassStudentCount(courseClassStudentCount)
                    // 格式化时间字段（yyyy年MM月dd日 HH:mm:ss）
                    .startTimeStr(DateUtil.format(hw.getStartTime(), "yyyy年MM月dd日 HH:mm:ss"))
                    .deadlineStr(DateUtil.format(hw.getDeadline(), "yyyy年MM月dd日 HH:mm:ss"))
                    .createTimeStr(DateUtil.format(hw.getCreateTime(), "yyyy年MM月dd日 HH:mm:ss"))
                    .updateTimeStr(DateUtil.format(hw.getUpdateTime(), "yyyy年MM月dd日 HH:mm:ss"))
                    .build();
            homeworkVOs.add(vo);
        }
        return homeworkVOs;
    }

    /**
     * 解析JSON日期字符串为Date对象
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            // 尝试解析 ISO 8601 日期格式
            return JSON.parseObject(dateStr, Date.class);
        } catch (Exception e) {
            // 如果是时间戳格式
            try {
                return new Date(Long.parseLong(dateStr));
            } catch (NumberFormatException ex) {
                // 如果是 yyyy-MM-dd HH:mm:ss 格式
                try {
                    java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(dateStr,
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
                } catch (Exception e2) {
                    log.warn("无法解析日期: {}", dateStr);
                    return null;
                }
            }
        }
    }

    /**
     * 将作业提交信息转换为VO
     * 
     * @param submissions
     * @return
     */
    private List<HomeworkSubmissionVO> SubmissionToVO(List<HomeworkSubmission> submissions) {
        List<HomeworkSubmissionVO> homeworkSubmissionVOs = new ArrayList<>();
        if (submissions == null || submissions.isEmpty()) {
            return homeworkSubmissionVOs;
        }
        for (HomeworkSubmission submission : submissions) {
            String studentName = studentMapper.selectOne(
                    new LambdaQueryWrapper<Student>()
                            .eq(Student::getNumber, submission.getStudentNumber()))
                    .getName();
            homeworkSubmissionVOs.add(HomeworkSubmissionVO.builder()
                    .homeworkId(submission.getHomeworkId())
                    .studentNumber(submission.getStudentNumber())
                    .studentName(studentName)
                    .submitStatus(submission.getSubmitStatus())
                    .submitTime(DateUtil.format(submission.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"))
                    .score(submission.getScore() == null ? 0 : submission.getScore())
                    .build());
        }
        return homeworkSubmissionVOs;
    }

    // =================================================================
    // 接口服务方法
    // =================================================================
    @Override
    public Result publishHomework(PublishHomeworkDTO dto) {
        log.info("===========================================================");
        log.info("发布作业，标题：{}", dto.getCourseId());
        log.info("======================================================");

        if (dto.getStartTime() != null && dto.getStartTime().after(dto.getDeadline())) {
            return Result.error("开始时间不能大于截止时间");
        }

        Timestamp createTime = Timestamp.valueOf(LocalDateTime.now());

        Homework homework = Homework.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .courseId(dto.getCourseId())
                .teacherId(dto.getTeacherId())
                .groupId(dto.getGroupId())
                .startTime(dto.getStartTime() == null ? new java.util.Date(createTime.getTime()) : dto.getStartTime())
                .deadline(dto.getDeadline())
                .status(1L)
                .totalScore(dto.getTotalScore())
                .createTime(new java.util.Date(createTime.getTime()))
                .updateTime(new java.util.Date(createTime.getTime()))
                .remark(dto.getDescription())
                .build();

        log.info("homework: {}", homework);
        boolean saved = this.save(homework);

        if (!saved) {
            throw new RuntimeException("发布作业失败");
        }
        log.info("【Service】作业保存成功，作业ID：{}，标题：{}", homework.getId(), homework.getTitle());

        /**
         * 得到学生列表
         * 如果没有小组信息——全班同学
         * 有小组信息——小组同学
         * 添加作业提交表
         */
        List<String> studentNumbers = new ArrayList<>();
        if (homework.getGroupId() == null) {
            // 全班同学
            studentNumbers = courseWithStudentsMapper.selectList(
                    new LambdaQueryWrapper<CourseWithStudents>()
                            .eq(CourseWithStudents::getCourseId, homework.getCourseId()))
                    .stream()
                    .map(CourseWithStudents::getStudentNumber)
                    .toList();
        } else {
            studentNumbers = studentWithGroupMapper.selectList(
                    new LambdaQueryWrapper<StudentWithGroup>()
                            .eq(StudentWithGroup::getGroupId, homework.getGroupId()))
                    .stream()
                    .map(StudentWithGroup::getStudentNumber)
                    .toList();
        }

        if (studentNumbers.isEmpty()) {
            log.error("学生列表为空");
            return Result.error("学生列表为空");
        }

        // 数据库插入学生作业提交信息
        List<HomeworkSubmission> submissions = new ArrayList<>();
        for (String stu : studentNumbers) {
            HomeworkSubmission homeworkSubmission = HomeworkSubmission.builder()
                    .homeworkId(homework.getId())
                    .studentNumber(stu)
                    .submitStatus(0L) // 0未提交 1已提交 2已批改
                    .submitContent("") // 提交内容
                    .createTime(new java.util.Date(createTime.getTime()))
                    .updateTime(new java.util.Date(createTime.getTime()))
                    .build();
            // 插入数据库
            int count = homeworkSubmissionMapper.insert(homeworkSubmission);
            if (count != 1) {
                log.error("学生{}提交作业失败", stu);
                return Result.error("学生" + stu + "提交作业失败");
            }
            submissions.add(homeworkSubmission);
        }
        log.info("====================添加作业提交表成功，学生数量：{}====================", studentNumbers.size());

        /**
         * 缓存作业信息
         */
        log.info("====================开始缓存作业信息====================");
        try {
            cacheClient.setWithLogicExpireAndRandom(RedisConstants.HOMEWORK_INFO + homework.getId(), homework,
                    RedisConstants.HOMEWORK_INFO_TTL, TimeUnit.MINUTES);
            log.info("====================缓存作业信息成功====================");

        } catch (Exception e) {
            // TODO: handle exception
            log.error("====================缓存作业信息失败====================", e);
        }

        String teacherHomeworkKey = RedisConstants.TEACHER_HOMEWORK_SET + dto.getTeacherId();
        cacheClient.sAdd(teacherHomeworkKey, homework.getId().toString());
        // 设置Set的过期时间（与单个作业一致）
        cacheClient.expire(teacherHomeworkKey, RedisConstants.HOMEWORK_INFO_TTL, TimeUnit.MINUTES);
        /**
         * 批量缓存学生作业提交信息
         */
        log.info("====================开始缓存作业提交信息====================");
        try {

            for (HomeworkSubmission hm : submissions) {
                String studentName = studentMapper.selectOne(
                        new LambdaQueryWrapper<Student>()
                                .eq(Student::getNumber, hm.getStudentNumber()))
                        .getName();

                String key = RedisConstants.HOMEWORK_SUBMISSION + homework.getId() + ":" + hm.getStudentNumber();
                cacheClient.hSet(key, "submitStatus", hm.getSubmitStatus());
                cacheClient.hSet(key, "submitContent", hm.getSubmitContent() == null ? "" : hm.getSubmitContent());
                cacheClient.hSet(key, "score", hm.getScore() == null ? 0 : hm.getScore());
                cacheClient.hSet(key, "createTime", hm.getCreateTime());
                cacheClient.hSet(key, "updateTime", hm.getUpdateTime());
                cacheClient.hSet(key, "submitTime", hm.getSubmitTime());
                cacheClient.hSet(key, "number", hm.getStudentNumber());
                cacheClient.hSet(key, "homeworkId", hm.getHomeworkId());
                cacheClient.hSet(key, "studentName", studentName);

                cacheClient.hExpire(key, RedisConstants.HOMEWORK_SUBMISSION_TTL, TimeUnit.MINUTES);

            }

            log.info("====================缓存作业提交信息成功====================");

        } catch (Exception e) {
            log.error("====================批量缓存学生作业提交信息失败====================", e);
        }

        /**
         * 缓存学生名单
         */
        log.info("====================开始缓存学生名单====================");
        try {
            String key = RedisConstants.HOMEWORK_SUBMISSION_STUDENTS + homework.getId();
            for (String stuNumber : studentNumbers) {
                cacheClient.sAdd(key, stuNumber);
            }
            cacheClient.expire(key, RedisConstants.HOMEWORK_SUBMISSION_STUDENTS_TTL, TimeUnit.MINUTES);
            log.info("====================缓存学生名单成功====================");
        } catch (Exception e) {
            log.error("====================缓存学生名单失败====================", e);
        }
        try {
            homeworkMessageProducer.sendPublishMessage(homework, studentNumbers);
            log.info("====================发送作业发布消息到队列成功====================");
        } catch (Exception e) {
            log.error("====================发送作业发布消息到队列失败====================", e);
        }
        return Result.success("发布作业成功");
    }

    /**
     * 查询教师发布的作业
     */
    @Override
    public Result listHomeworkByTeacherId(Long teacherId) {
        // 查询教师id是否存在
        Teacher teacher = teacherMapper.selectById(teacherId);
        if (teacher == null) {
            log.error("该教师不存在");
            return Result.error("该教师不存在");
        }
        /**
         * 先从缓存查
         */
        String teacherHomeworkKey = RedisConstants.TEACHER_HOMEWORK_SET + teacherId;
        Set<Long> homeworkIds = cacheClient.sMembersAsLongs(teacherHomeworkKey);

        List<Homework> homeworkList = new ArrayList<>();
        Set<Long> notCachedHomeworkIds = new HashSet<>();

        if (homeworkIds != null && !homeworkIds.isEmpty()) {
            log.info("【查询教师作业】缓存命中作业ID数量：{}", homeworkIds.size());

            for (Long ids : homeworkIds) {
                String key = RedisConstants.HOMEWORK_INFO + ids;
                String json = stringRedisTemplate.opsForValue().get(key);
                if (json != null && !json.isEmpty()) {
                    // 命中缓存 - 需要先解析RedisData结构
                    try {
                        RedisData redisData = JSON.parseObject(json, RedisData.class);
                        if (redisData != null && redisData.getData() != null) {
                            // data字段可能是JSONObject或JSONArray，需要进一步解析为Homework
                            // 避免类加载器问题，直接从JSON字符串解析，不依赖getData()返回的对象类型
                            Object data = redisData.getData();
                            String dataJson = (data instanceof String) ? (String) data : JSON.toJSONString(data);
                            // 直接解析为Homework类型，避免类加载器冲突
                            Homework homework = JSON.parseObject(dataJson, Homework.class);
                            if (homework != null) {
                                homeworkList.add(homework);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("【查询教师作业】缓存解析失败，key={}，error={}", key, e.getMessage());
                        notCachedHomeworkIds.add(ids);
                    }
                } else {
                    // 缓存未命中
                    notCachedHomeworkIds.add(ids);

                }
            }
        }

        if (!notCachedHomeworkIds.isEmpty()) {
            log.info("【查询教师作业】缓存未命中作业ID数量：{}，开始查询数据库", notCachedHomeworkIds.size());

            List<Homework> homeworkListDB = this.list(
                    new LambdaQueryWrapper<Homework>()
                            .eq(Homework::getTeacherId, teacherId)
                            .in(Homework::getId, notCachedHomeworkIds));

            // 回填缓存
            for (Homework homework : homeworkListDB) {
                String homeworkKey = RedisConstants.HOMEWORK_INFO + homework.getId();
                cacheClient.setWithLogicExpireAndRandom(
                        homeworkKey,
                        homework,
                        RedisConstants.HOMEWORK_INFO_TTL,
                        TimeUnit.MINUTES);

                homeworkList.add(homework);
            }

            // 6. 确保缓存中的ID列表完整（添加新作业ID）
            for (Homework homework : homeworkListDB) {
                cacheClient.sAdd(teacherHomeworkKey, homework.getId().toString());
            }
            cacheClient.expire(teacherHomeworkKey, RedisConstants.TEACHER_INFO_TTL, TimeUnit.MINUTES);
        }

        // 缓存完全没有命中
        if (homeworkList.isEmpty() || homeworkList == null) {
            log.info("【查询教师作业】缓存完全没有命中，开始查询数据库");

            List<Homework> homeworkListDB = this.list(
                    new LambdaQueryWrapper<Homework>()
                            .eq(Homework::getTeacherId, teacherId));

            // 批量回填缓存
            for (Homework hw : homeworkListDB) {
                String homeworkKey = RedisConstants.HOMEWORK_INFO + hw.getId();
                cacheClient.setWithLogicExpireAndRandom(
                        homeworkKey,
                        hw,
                        RedisConstants.HOMEWORK_INFO_TTL,
                        TimeUnit.MINUTES);
                homeworkList.add(hw);

                // 回填作业id到Set
                cacheClient.sAdd(teacherHomeworkKey, hw.getId().toString());
            }

            cacheClient.expire(teacherHomeworkKey, RedisConstants.TEACHER_INFO_TTL, TimeUnit.MINUTES);

        }

        log.info("【查询教师作业】查询完成，返回作业数量：{}", homeworkList.size());
        // 封装vo
        List<HomeworkVO> homeworkVOs = ConvertToVO(homeworkList);
        return Result.success(homeworkVOs);
    }

    /**
     * 查询学生所有作业
     */
    @Override
    public Result listHomeworkByStudentId(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listHomeworkByStudentId'");
    }

    /**
     * 教师查看作业详情
     */
    @Override
    public Result detailHomeworkAll(Long homeworkId, Long teacherId) {
        log.info("====================开始查询作业详情====================");
        log.info("====================作业id：{}，教师id：{}====================", homeworkId, teacherId);
        // 1.从缓存里查课程id是否存在该作业
        log.info("====================开始查询缓存====================");
        String key = RedisConstants.TEACHER_HOMEWORK_SET + teacherId;
        boolean exists = cacheClient.sIsMember(key, homeworkId.toString());
        // 不存在 从数据库查
        if (!exists) {
            log.info("====================缓存不存在，查数据库====================");
            Homework homework = this.getOne(
                    new LambdaQueryWrapper<Homework>()
                            .eq(Homework::getId, homeworkId)
                            .eq(Homework::getTeacherId, teacherId));
            // 作业不存在
            if (homework == null) {
                log.error("====================作业不存在====================");
                return Result.error("作业不存在");
            }
            // 回填缓存
            cacheClient.sAdd(key, homeworkId.toString());
            cacheClient.expire(key, RedisConstants.TEACHER_INFO_TTL, TimeUnit.MINUTES);
            log.info("====================回填缓存成功====================");
        }
        log.info("====================缓存存在，从缓存查学生列表====================");
        String stuKey = RedisConstants.HOMEWORK_SUBMISSION_STUDENTS + homeworkId;
        Set<String> stuNumbers = cacheClient.sMembers(stuKey);
        // 判断是否存在
        if (stuNumbers == null || stuNumbers.isEmpty()) {
            log.info("====================学生列表不存在，从数据库查====================");
            stuNumbers = homeworkSubmissionMapper.selectList(
                    new LambdaQueryWrapper<HomeworkSubmission>()
                            .eq(HomeworkSubmission::getHomeworkId, homeworkId))
                    .stream()
                    .map(HomeworkSubmission::getStudentNumber)
                    .collect(Collectors.toSet());
            if (stuNumbers == null || stuNumbers.isEmpty()) {
                log.error("====================学生列表不存在====================");
                return Result.error("学生列表不存在");
            }
            // 回填缓存
            for (String stuNumber : stuNumbers) {
                cacheClient.sAdd(stuKey, stuNumber);
            }
            cacheClient.expire(stuKey, RedisConstants.HOMEWORK_SUBMISSION_STUDENTS_TTL, TimeUnit.MINUTES);
            log.info("====================回填缓存成功====================");
        }

        log.info("====================查询学生列表成功====================");

        // 从缓存中得到学生提交信息表
        List<HomeworkSubmission> submissions = new ArrayList<>();
        for (String sn : stuNumbers) {
            String snKey = RedisConstants.HOMEWORK_SUBMISSION + homeworkId + ":" + sn;
            HomeworkSubmission submission;

            // 尝试从Hash缓存中读取
            Map<String, String> hashData = cacheClient.hGetAll(snKey, String.class);
            if (hashData != null && !hashData.isEmpty()) {
                // 从Hash中构建Submission对象
                submission = HomeworkSubmission.builder()
                        .homeworkId(homeworkId)
                        .studentNumber(sn)
                        .submitStatus(
                                hashData.get("submitStatus") != null ? Long.parseLong(hashData.get("submitStatus"))
                                        : null)
                        .submitContent(hashData.get("submitContent"))
                        .score(hashData.get("score") != null ? Long.parseLong(hashData.get("score")) : null)
                        .createTime(parseDate(hashData.get("createTime")))
                        .updateTime(parseDate(hashData.get("updateTime")))
                        .submitTime(parseDate(hashData.get("submitTime")))
                        .build();
                submissions.add(submission);
            } else {
                // 缓存不存在，从数据库查
                submission = homeworkSubmissionMapper.selectOne(
                        new LambdaQueryWrapper<HomeworkSubmission>()
                                .eq(HomeworkSubmission::getHomeworkId, homeworkId)
                                .eq(HomeworkSubmission::getStudentNumber, sn));

                String studentName = studentMapper.selectOne(
                        new LambdaQueryWrapper<Student>()
                                .eq(Student::getNumber, submission.getStudentNumber()))
                        .getName();

                // 回填缓存 - 使用Hash结构
                cacheClient.hSet(snKey, "submitStatus", submission.getSubmitStatus());
                cacheClient.hSet(snKey, "submitContent",
                        submission.getSubmitContent() == null ? "" : submission.getSubmitContent());
                cacheClient.hSet(snKey, "score", submission.getScore() == null ? 0 : submission.getScore());
                cacheClient.hSet(snKey, "createTime",
                        submission.getCreateTime() == null ? null : submission.getCreateTime().getTime());
                cacheClient.hSet(snKey, "updateTime",
                        submission.getUpdateTime() == null ? null : submission.getUpdateTime().getTime());
                cacheClient.hSet(snKey, "submitTime",
                        submission.getSubmitTime() == null ? null : submission.getSubmitTime().getTime());
                cacheClient.hSet(snKey, "number", submission.getStudentNumber());
                cacheClient.hExpire(snKey, RedisConstants.HOMEWORK_SUBMISSION_TTL, TimeUnit.MINUTES);
                cacheClient.hSet(snKey, "homeworkId", homeworkId);
                cacheClient.hSet(snKey, "studentName", studentName);
                submissions.add(submission);
            }
        }

        log.info("====================查询到{}条学生提交信息，存入本地缓存====================", submissions.size());

        LOCAL_CACHE.put(homeworkId, submissions);

        // 返回前25条数据
        int endIndex = Math.min(25, submissions.size());
        List<HomeworkSubmission> result = submissions.subList(0, endIndex);

        log.info("====================返回{}条数据====================", result.size());

        List<HomeworkSubmissionVO> homeworkSubmissionVOs = SubmissionToVO(result);

        return Result.success(homeworkSubmissionVOs);

    }

    /** 分页查询作业提交情况 */
    @Override
    public Result detailHomeworkPage(Long homeworkId, Long teacherId, Integer pagenum, Integer pagesize) {
        log.info("====================开始分页查询作业提交情况====================");
        log.info("====================作业id：{}，教师id：{}，页码：{}，每页条数：{}====================", homeworkId, teacherId,
                pagenum, pagesize);

        // 从本地缓存获取数据
        List<HomeworkSubmission> submissions = LOCAL_CACHE.get(homeworkId);
        // 缓存未命中，需要先调用普通查询方法加载数据
        if (submissions == null || submissions.isEmpty()) {
            log.info("====================缓存未命中，调用普通查询方法加载数据====================");
            Result firstLoad = detailHomeworkAll(homeworkId, teacherId);

            submissions = LOCAL_CACHE.get(homeworkId);
            if (submissions == null) {
                return Result.error("加载数据失败");
            }
        }

        // 计算分页偏移量
        int startIndex = (pagenum - 1) * pagesize;
        int endIndex = Math.min(startIndex + pagesize, submissions.size());

        // 边界判断
        Map<String, Object> result = new HashMap<>();
        if (startIndex >= submissions.size()) {
            log.info("====================页码超出范围，返回空数据====================");
            result.put("records", new ArrayList<>());
            result.put("total", submissions.size());
            result.put("currentPage", pagenum);
            result.put("pageSize", pagesize);
            return Result.success(result);
        }

        // 6.截取当前页数据
        List<HomeworkSubmission> pageList = submissions.subList(startIndex, endIndex);

        log.info("====================返回第{}页数据，共{}条====================", pagenum, pageList.size());

        result.put("records", pageList);
        result.put("total", submissions.size());
        result.put("currentPage", pagenum);
        result.put("pageSize", pagesize);

        List<HomeworkSubmissionVO> homeworkSubmissionVOs = SubmissionToVO(pageList);

        return Result.success(homeworkSubmissionVOs);

    }
}
