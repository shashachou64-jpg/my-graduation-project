package com.cjy.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.common.RedisConstants;
import com.cjy.common.Result;
import com.cjy.domain.College;
import com.cjy.domain.Personal;
import com.cjy.domain.Position;
import com.cjy.domain.Teacher;
import com.cjy.domain.User;
import com.cjy.domain.UserWithIdentity;
import com.cjy.dto.BatchTeacherDTO;
import com.cjy.dto.EditTeacherDTO;
import com.cjy.dto.TeacherDTO;
import com.cjy.utils.CacheClient;
import com.cjy.utils.TrimConverter;
import com.cjy.vo.TeacherInfoDTO;
import com.cjy.vo.TeacherTotalVO;
import com.cjy.vo.TeacherVO;

import com.alibaba.fastjson2.JSON;

import com.cjy.mapper.CollegeMapper;
import com.cjy.mapper.PersonalMapper;
import com.cjy.mapper.PositionMapper;
import com.cjy.mapper.TeacherMapper;
import com.cjy.mapper.UserMapper;
import com.cjy.mapper.UserWithIdentityMapper;
import com.cjy.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private CollegeMapper collegeMapper;

    @Autowired
    private PositionMapper positionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PersonalMapper personalMapper;

    @Autowired
    private UserWithIdentityMapper userWithIdentityMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheClient cacheClient;

    @Override
    public Result addTeacher(TeacherDTO teacherDTO) {
        /**
         * 学院名称是否存在于数据库中
         * 如果有，则获取学院ID
         * 如果没有返回错误
         */
        College college = collegeMapper
                .selectOne(new LambdaQueryWrapper<College>()
                        .eq(College::getName, teacherDTO.getCollegeName()));
        if (college == null) {
            return Result.error("该学院不存在");
        }
        Long collegeId = college.getId();

        /**
         * 职位名称是否存在于数据库中
         * 如果有，则获取职位ID
         * 如果没有返回错误
         */
        Position position = positionMapper.selectOne(
                new LambdaQueryWrapper<Position>()
                        .eq(Position::getName, teacherDTO.getPosition()));

        if (position == null) {
            return Result.error("该职位不存在");
        }
        Long positionId = position.getId();

        /**
         * 添加老师
         * 添加教师表
         * 添加用户表
         * 添加个人信息表
         * 添加用户身份表
         */
        Teacher teacher = new Teacher();
        teacher.setName(teacherDTO.getName());
        teacher.setCollegeId(collegeId);
        teacher.setPositionId(positionId);
        teacher.setGender(teacherDTO.getGender());
        int insertTeacher = teacherMapper.insert(teacher);
        if (insertTeacher == 0) {
            return Result.error("添加教师失败");
        }

        /**
         * 添加用户表
         * 用户名：学校代码：13469+学院代码：2位+教师编码：4位
         */
        // 学院编码：学院id，如果1位的话自动补0
        String collegeCode = String.format("%02d", collegeId);
        // 教师编码：教师id，如果1位的话自动补0
        String teacherCode = String.format("%04d", teacher.getId());
        User user = new User();
        user.setUsername("13469" + collegeCode + teacherCode);
        user.setPassword("123456");
        int insertUser = userMapper.insert(user);
        if (insertUser == 0) {
            return Result.error("添加用户失败");
        }

        /**
         * 添加个人信息表
         */
        Personal personal = new Personal();
        personal.setUserId(user.getId());
        personal.setName(teacherDTO.getName());
        personal.setSex(teacherDTO.getGender());
        int insertPersonal = personalMapper.insert(personal);
        if (insertPersonal == 0) {
            return Result.error("添加个人信息失败");
        }

        /**
         * 添加用户身份表
         */
        UserWithIdentity userWithIdentity = new UserWithIdentity();
        userWithIdentity.setUserId(user.getId());
        userWithIdentity.setIdentityId(2L);
        int insertUserWithIdentity = userWithIdentityMapper.insert(userWithIdentity);
        if (insertUserWithIdentity == 0) {
            return Result.error("添加用户身份失败");
        }
        return Result.success("添加教师成功");

    }

    @Override
    public List<TeacherVO> getAllTeacherInfo() {
        // 1. 查询所有教师
        List<Teacher> teacherList = teacherMapper.selectList(null);

        // 2. 查询所有学院
        Map<Long, String> collegeMap = collegeMapper.selectList(null).stream()
                .collect(Collectors.toMap(College::getId, College::getName));

        // 3. 查询所有职位、
        Map<Long, String> positionMap = positionMapper.selectList(null).stream()
                .collect(Collectors.toMap(Position::getId, Position::getName));

        return teacherList.stream()
                .map(teacher -> {
                    TeacherVO vo = new TeacherVO();
                    vo.setUsername(Long
                            .parseLong(
                                    "13469" +
                                            String.format("%02d", teacher.getCollegeId()) +
                                            String.format("%04d", teacher.getId()))); // 工号
                    vo.setName(teacher.getName()); // 姓名
                    vo.setGender(teacher.getGender()); // 性别
                    vo.setCollegeName(collegeMap.get(teacher.getCollegeId())); // 学院名称
                    vo.setPositionName(positionMap.get(teacher.getPositionId())); // 职位名称
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Result deleteTeacher(String username) {
        // 获得教师id
        String teacherId = username.substring(8);
        // 如果前面是0则去除0
        String result = teacherId.replaceAll("^0+", "");
        Long teacherIdLong = Long.parseLong(result);
        // 如果不在数据库中则返回失败
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getId, teacherIdLong));
        if (teacher == null) {
            return Result.error("该教师不存在，无法删除");
        }
        // 删除教师表
        this.remove(
                new LambdaQueryWrapper<Teacher>()
                        .eq(Teacher::getId, teacherIdLong));
        // 删除用户表
        Long userId = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username))
                .getId();

        userMapper.delete(
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, userId));

        // 删除个人信息表
        personalMapper.delete(
                new LambdaQueryWrapper<Personal>()
                        .eq(Personal::getUserId, userId));
        // 删除用户身份表
        userWithIdentityMapper.delete(
                new LambdaQueryWrapper<UserWithIdentity>()
                        .eq(UserWithIdentity::getUserId, userId));
        return Result.success("删除成功");

    }

    @Override
    public Result editTeacherInfo(EditTeacherDTO editTeacherDTO) {
        try {
            /**
             * 逻辑判断
             * 修改教师表
             * 修改用户表
             * 修改个人信息表
             * 修改用户身份表
             */
            // 获得教师id
            String teacherId = editTeacherDTO.getUsername().substring(8);
            // 如果前面是0则去除0
            String result = teacherId.replaceAll("^0+", "");
            Long teacherIdLong = Long.parseLong(result);
            // 如果不在数据库中则返回失败
            Teacher teacher = teacherMapper
                    .selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getId, teacherIdLong));
            if (teacher == null) {
                return Result.error("该教师不存在，无法修改");
            }
            // 修改教师表
            teacher.setName(editTeacherDTO.getName());
            teacher.setCollegeId(editTeacherDTO.getCollegeId());
            teacher.setPositionId(editTeacherDTO.getPositionId());
            teacher.setGender(editTeacherDTO.getGender());
            int updateTeacher = teacherMapper.update(teacher,
                    new LambdaQueryWrapper<Teacher>().eq(Teacher::getId, teacherIdLong));
            if (updateTeacher == 0) {
                return Result.error("修改教师失败");
            }
            return Result.success("修改成功");
        } catch (Exception e) {
            return Result.error("修改失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result batchAddTeaInfo(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        System.out.println("文件名：" + fileName + "，大小：" + fileSize + "字节，类型：" + contentType);

        try {
            // 先把文件读到字节数组，这样可以使用多次输入流
            byte[] fileBytes = file.getBytes();
            ByteArrayInputStream bis = new java.io.ByteArrayInputStream(fileBytes);

            // 直接读取数据，EasyExcel 会根据 @ExcelProperty 注解自动匹配表头
            List<BatchTeacherDTO> batchTeaList = EasyExcel.read(bis)
                    .head(BatchTeacherDTO.class)
                    .sheet(0)
                    .headRowNumber(0) // 表头在第0行（第一行），数据从第1行开始
                    .doReadSync();

            /**
             * 检查名字和性别是否为空
             * 注意：排除表头行（name为"姓名"的行）
             */
            StringBuilder errorMessage = new StringBuilder();
            for (int i = 0; i < batchTeaList.size(); i++) {
                BatchTeacherDTO dto = batchTeaList.get(i);
                
                // 跳过表头行（当name为"姓名"时认为是表头行）
                if ("姓名".equals(dto.getName())) {
                    continue;
                }
                
                if (dto.getName() == null || dto.getName().trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：名字为空\n");
                }
                if (dto.getGender() == null || dto.getGender().trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：性别为空\n");
                }
                if (dto.getCollegeName() == null || dto.getCollegeName().trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：学院为空\n");
                }
                if (dto.getPositionName() == null || dto.getPositionName().trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：职位为空\n");
                }
            }

            /**
             * 添加数据
             */
            // 从导入数据中提取所有职位名称并去重
            List<String> positionNameList = batchTeaList.stream()
                    .map(BatchTeacherDTO::getPositionName)
                    .collect(Collectors.toList());

            // 如果有空值则返回在第几行数据有错（遍历原始数据）
            for (int i = 0; i < batchTeaList.size(); i++) {
                String positionName = batchTeaList.get(i).getPositionName();
                // 跳过表头行
                if ("职位".equals(positionName)) {
                    continue;
                }
                if (positionName == null || positionName.trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：职位为空\n");
                }
            }

            // 得到职位名称和职位id的映射
            Map<String, Long> positionToIdMap = positionMapper.selectList(
                    new LambdaQueryWrapper<Position>()
                            .in(Position::getName, positionNameList.stream().distinct().collect(Collectors.toList()))
                            .select(Position::getName, Position::getId))
                    .stream()
                    .collect(Collectors.toMap(Position::getName, Position::getId));

            // 从导入数据中提取所有学院名称
            List<String> collegeNameList = batchTeaList.stream()
                    .map(BatchTeacherDTO::getCollegeName)
                    .collect(Collectors.toList());
            // 如果有空值则返回在第几行数据有错（遍历原始数据）

            for (int i = 0; i < batchTeaList.size(); i++) {
                String collegeName = batchTeaList.get(i).getCollegeName();
                // 跳过表头行
                if ("学院".equals(collegeName)) {
                    continue;
                }
                if (collegeName == null || collegeName.trim().isEmpty()) {
                    errorMessage.append("第").append(i + 1).append("行数据有错：学院为空\n");
                }
            }

            // 得到学院名称和学院id的映射
            Map<String, Long> collegeToIdMap = collegeMapper.selectList(
                    new LambdaQueryWrapper<College>()
                            .in(College::getName, collegeNameList.stream().distinct().collect(Collectors.toList()))
                            .select(College::getName, College::getId))
                    .stream()
                    .collect(Collectors.toMap(College::getName, College::getId));

            if (!errorMessage.toString().isEmpty()) {
                throw new RuntimeException(errorMessage.toString());
            }


            /**
             * 添加到表
             * 教师表
             * 用户表
             * 用户信息表
             * 用户身份表
             */
            for (int i = 0; i < batchTeaList.size(); i++) {
                BatchTeacherDTO dto = batchTeaList.get(i);
                
                // 跳过表头行
                if ("姓名".equals(dto.getName())) {
                    continue;
                }
                
                // 校验学院是否存在
                Long collegeId = collegeToIdMap.get(dto.getCollegeName());
                if (collegeId == null) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：学院'" + dto.getCollegeName() + "'不存在于系统中");
                }
                
                // 校验职位是否存在
                Long positionId = positionToIdMap.get(dto.getPositionName());
                if (positionId == null) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：职位'" + dto.getPositionName() + "'不存在于系统中");
                }
                
                // 教师表
                Teacher teacher = new Teacher();
                teacher.setName(dto.getName());
                teacher.setCollegeId(collegeId);
                teacher.setPositionId(positionId);
                teacher.setGender(dto.getGender());
                int insertTeacher = teacherMapper.insert(teacher);
                if (insertTeacher == 0) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：添加教师失败");
                }

                // 用户表
                User user = new User();
                user.setUsername("13469" + String.format("%02d", teacher.getCollegeId()) + String.format("%04d", teacher.getId()));
                user.setPassword("123456");
                int insertUser = userMapper.insert(user);
                if (insertUser == 0) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：添加用户失败");
                }

                // 用户信息表
                Personal personal = new Personal();
                personal.setUserId(user.getId());
                personal.setName(dto.getName());
                personal.setSex(dto.getGender());
                int insertPersonal = personalMapper.insert(personal);
                if (insertPersonal == 0) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：添加用户信息失败");
                }

                // 用户身份表
                UserWithIdentity userWithIdentity = new UserWithIdentity();
                userWithIdentity.setUserId(user.getId());
                userWithIdentity.setIdentityId(2L);
                int insertUserWithIdentity = userWithIdentityMapper.insert(userWithIdentity);
                if (insertUserWithIdentity == 0) {
                    throw new RuntimeException("第" + (i + 1) + "行数据有错：添加用户身份失败");
                }
            }
            return Result.success("导入成功");
        } catch (Exception e) {
            System.out.println("导入异常详情: ");
            e.printStackTrace();
            // 抛出 RuntimeException 以便事务能够回滚
            throw new RuntimeException("导入失败: " + e.getMessage(), e);
        }
    }

    @Override
    public TeacherTotalVO getTeacherTotalInfo() {
        // 1. 查询所有教师人数
        Long totalTeacher = teacherMapper.selectCount(null);
        System.out.println("总教师人数：" + totalTeacher);
        //查询男女教师人数
        Long manTeacher=teacherMapper.selectCount(
            new LambdaQueryWrapper<Teacher>().eq(Teacher::getGender, "男")
        );
        Long womanTeacher=teacherMapper.selectCount(
            new LambdaQueryWrapper<Teacher>().eq(Teacher::getGender, "女")
        );

        /**
         * 查询所有学院名称
         * 和学院对应名称的对应的教师人数
         */
        List<Map<String,Object>> collegeTeacherList = teacherMapper.getCollegeTeacherList();

        //转换格式
        List<String> collegeNames=collegeTeacherList.stream()
        .map(map->(String)map.get("collegeName"))
        .collect(Collectors.toList());

        List<Long> collegeTeacherCounts=collegeTeacherList.stream()
        .map(map->(Long)map.get("teacherCount"))
        .collect(Collectors.toList());

        //转换为VO
        List<TeacherTotalVO.CollegeTeacherVO> collegeTeacherVOList=new ArrayList<>();
        for (int i = 0; i < collegeNames.size(); i++) {
            TeacherTotalVO.CollegeTeacherVO collegeTeacherVO=new TeacherTotalVO.CollegeTeacherVO();
            collegeTeacherVO.setCollegeName(collegeNames.get(i));
            collegeTeacherVO.setCollegeTeacher(collegeTeacherCounts.get(i));
            collegeTeacherVOList.add(collegeTeacherVO);
        }


        return new TeacherTotalVO(totalTeacher,manTeacher,womanTeacher,collegeTeacherVOList);
    }

    /**
     * 获取教师个人信息
     */
    @Override
    public Result getTeacherInfo(Long id) {
        /**
         * 根据id查询教师信息
         */
        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>().eq(Teacher::getId, id));
        if (teacher == null) {
            return Result.error("该教师不存在");
        }
        //查询到教师信息，封装成vo
        TeacherInfoDTO teacherInfoDTO = teacherToVO(teacher);

        //存入缓存
        cacheClient.setWithLogicExpireAndRandom(RedisConstants.TEACHER_INFO + id, teacherInfoDTO, RedisConstants.TEACHER_INFO_TTL, TimeUnit.MINUTES);

        return Result.success(teacherInfoDTO);
    }

    private TeacherInfoDTO teacherToVO(Teacher teacher) {
        TeacherInfoDTO teacherInfoDTO = new TeacherInfoDTO();
        teacherInfoDTO.setId(teacher.getId());
        teacherInfoDTO.setName(teacher.getName());
        teacherInfoDTO.setCollegeName(collegeMapper.selectOne(new LambdaQueryWrapper<College>().eq(College::getId, teacher.getCollegeId())).getName());
        teacherInfoDTO.setPositionName(positionMapper.selectOne(new LambdaQueryWrapper<Position>().eq(Position::getId, teacher.getPositionId())).getName());
        teacherInfoDTO.setGender(teacher.getGender());
        return teacherInfoDTO;
    }   
}