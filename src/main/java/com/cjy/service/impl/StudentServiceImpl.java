package com.cjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.mapper.CollegeMapper;
import com.cjy.mapper.MajorMapper;
import com.cjy.mapper.PersonalMapper;
import com.cjy.mapper.StudentMapper;
import com.cjy.mapper.UserMapper;
import com.cjy.domain.Class;
import com.cjy.domain.College;
import com.cjy.domain.Major;
import com.cjy.domain.Personal;
import com.cjy.domain.Result;
import com.cjy.domain.Student;
import com.cjy.domain.User;
import com.cjy.domain.UserWithIdentity;
import com.cjy.domain.dto.BatchStudentDTO;
import com.cjy.domain.dto.StudentDTO;
import com.cjy.domain.vo.StudentVO;
import com.cjy.mapper.ClassMapper;
import com.cjy.mapper.UserWithIdentityMapper;
import com.cjy.service.IPersonalService;
import com.cjy.service.IStudentService;
import com.cjy.service.IUserWithIdentityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStudentService {
        @Autowired
        private StudentMapper studentMapper;

        @Autowired
        private CollegeMapper collegeMapper;

        @Autowired
        private MajorMapper majorMapper;

        @Autowired
        private ClassMapper classMapper;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private PersonalMapper personalMapper;

        @Autowired
        private IPersonalService iPersonalService;

        @Autowired
        private UserWithIdentityMapper userWithIdentityMapper;

        @Autowired
        private IUserWithIdentityService iUserWithIdentityService;

        // =====================================================
        // 公用方法
        // =====================================================
        /**
         * 将Student对象转换为StudentVO对象
         * 
         * @param student
         * @return
         */
        private List<StudentVO> convertToVOList(List<Student> studentList) {
                // 空列表检查
                if (studentList.isEmpty()) {
                        return List.of();
                }

                // 收集所有id
                List<Long> collegeIdList = studentList.stream().map(Student::getCollegeId).distinct().toList();
                List<Long> classIdList = studentList.stream().map(Student::getClassId).distinct().toList();
                List<Long> majorIdList = studentList.stream().map(Student::getMajorId).distinct().toList();

                // 批量查询所有学院（ID列表为空时跳过查询）
                List<College> collegeList = collegeIdList.isEmpty() ? List.of()
                                : collegeMapper.selectList(
                                                new LambdaQueryWrapper<College>().in(College::getId, collegeIdList));

                // 批量查询所有班级
                List<Class> classList = classIdList.isEmpty() ? List.of()
                                : classMapper.selectList(new LambdaQueryWrapper<Class>().in(Class::getId, classIdList));

                // 批量查询所有专业
                List<Major> majorList = majorIdList.isEmpty() ? List.of()
                                : majorMapper.selectList(new LambdaQueryWrapper<Major>().in(Major::getId, majorIdList));

                // 构建映射
                Map<Long, String> collegeIdToNameMap = collegeList.stream()
                                .collect(Collectors.toMap(College::getId, College::getName));
                Map<Long, String> classIdToNameMap = classList.stream()
                                .collect(Collectors.toMap(Class::getId, Class::getName));
                Map<Long, String> majorIdToNameMap = majorList.stream()
                                .collect(Collectors.toMap(Major::getId, Major::getName));

                // 构建学生VO列表
                return studentList.stream().map(s -> {
                        StudentVO studentVO = new StudentVO();

                        studentVO.setNumber(s.getNumber());
                        studentVO.setName(s.getName());
                        studentVO.setYear(s.getYear().toString());
                        studentVO.setGender(s.getGender());
                        studentVO.setCollegeName(collegeIdToNameMap.get(s.getCollegeId()));
                        studentVO.setMajorName(majorIdToNameMap.get(s.getMajorId()));
                        studentVO.setClassName(classIdToNameMap.get(s.getClassId()));
                        return studentVO;
                }).toList();
        }

        @Override
        public Result addStuInfo(StudentDTO studentDTO) {
                /**
                 * 1.判断学号是否重复
                 * 2.判断学院ID是否存在
                 * 3.判断专业ID是否存在
                 * 4.添加学生信息
                 * 5.添加用户表 初始化密码123456
                 * 6.添加个人信息表
                 */
                // 1.判断学号是否重复
                if (studentMapper
                                .selectCount(new LambdaQueryWrapper<Student>().eq(Student::getNumber,
                                                studentDTO.getNumber())) > 0) {
                        return Result.error("学号已存在");
                }
                // 2.判断学院ID是否存在
                Long count = collegeMapper
                                .selectCount(new LambdaQueryWrapper<College>().eq(College::getId,
                                                studentDTO.getCollegeId()));

                if (count == 0) {
                        return Result.error("学院不存在");
                }
                // 3.判断专业ID是否存在
                Long countMajor = majorMapper
                                .selectCount(new LambdaQueryWrapper<Major>().eq(Major::getId, studentDTO.getMajorId()));
                if (countMajor == 0) {
                        return Result.error("专业不存在");
                }

                // 4.判断班级ID是否存在
                Long countClass = classMapper
                                .selectCount(new LambdaQueryWrapper<Class>().eq(Class::getId, studentDTO.getClassId()));
                if (countClass == 0) {
                        return Result.error("班级不存在");
                }

                // 4.添加学生表
                Student student = new Student();
                student.setNumber(studentDTO.getNumber());
                student.setName(studentDTO.getName());
                student.setCollegeId(studentDTO.getCollegeId());
                student.setMajorId(studentDTO.getMajorId());
                student.setGender(studentDTO.getGender());
                student.setYear(studentDTO.getYear());
                student.setClassId(studentDTO.getClassId());
                studentMapper.insert(student);

                // 5.添加用户表 初始化密码123456
                User user = new User();
                user.setUsername(studentDTO.getNumber());
                user.setPassword("123456");
                userMapper.insert(user);

                // 6.添加个人信息表
                Personal personal = new Personal();
                personal.setUserId(user.getId());
                personal.setName(studentDTO.getName());
                personal.setSex(studentDTO.getGender());
                personalMapper.insert(personal);

                // 添加用户身份表
                UserWithIdentity userWithIdentity = new UserWithIdentity();
                userWithIdentity.setUserId(user.getId());
                userWithIdentity.setIdentityId(3L);
                userWithIdentityMapper.insert(userWithIdentity);
                return Result.success("添加成功");
        }

        @Override
        public List<StudentVO> getStudentVOList() {
                List<StudentVO> studentVOList = studentMapper.selectStudentVOList();
                return studentVOList;
        }

        @Override
        public Result addBatchStuInfo(List<BatchStudentDTO> batchStudentDTOList) {
                if (batchStudentDTOList.isEmpty() || batchStudentDTOList == null) {
                        return Result.error("你导入的是空数据");
                }
                /**
                 * 判断学号是否重复
                 */
                long distinctCount = batchStudentDTOList.stream()
                                .map(BatchStudentDTO::getNumber).map(String::trim)
                                .distinct()
                                .count();
                if (distinctCount != batchStudentDTOList.size()) {
                        return Result.error("有学号重复，请检查后重新导入");
                }

                /**
                 * 判断学号是否与数据库重复
                 */
                List<String> numberList = batchStudentDTOList.stream()
                                .map(BatchStudentDTO::getNumber)
                                .map(String::trim)
                                .toList();

                // 批量查询
                List<String> existingNumberList = studentMapper.selectList(
                                new LambdaQueryWrapper<Student>()
                                                .in(Student::getNumber, numberList))
                                .stream()
                                .map(Student::getNumber)
                                .toList();

                if (!existingNumberList.isEmpty()) {
                        return Result.error("以下学号已存在，请检查后重新导入：" + String.join(", ", existingNumberList));
                }

                /**
                 * 判断学院是否存在
                 */
                List<String> collegeNameList = batchStudentDTOList.stream()
                                .map(BatchStudentDTO::getCollegeName)
                                .map(String::trim)
                                .distinct()
                                .toList();

                // 批量查询
                List<String> existingCollegeNameList = collegeMapper.selectList(
                                new LambdaQueryWrapper<College>()
                                                .in(College::getName, collegeNameList))
                                .stream()
                                .map(College::getName).map(String::trim)
                                .toList();

                // 检查哪些学院不存在
                List<String> notExistColleges = collegeNameList.stream()
                                .filter(name -> !existingCollegeNameList.contains(name))
                                .toList();
                if (!notExistColleges.isEmpty()) {
                        return Result.error("以下学院不存在，请检查后重新导入：" + String.join(", ", notExistColleges));
                }

                /**
                 * 根据学院判断专业是否存在，以及专业-学院是否匹配
                 * 判断专业与学院的对应关系是否正确
                 */

                // 获取所有专业名称
                List<String> majorNameList = batchStudentDTOList.stream()
                                .map(BatchStudentDTO::getMajorName)
                                .distinct()
                                .toList();

                // 得到数据中所有专业列表
                List<Major> existingMajorList = majorMapper.selectList(
                                new LambdaQueryWrapper<Major>().in(Major::getName, majorNameList));
                System.out.println("========== 调试信息 ==========");
                System.out.println("数据库中的专业列表:");
                existingMajorList.forEach(
                                m -> System.out.println("  专业: " + m.getName() + ", collegeId: " + m.getCollegeId()));

                // 得到数据中所有学院映射
                Map<String, Long> collegeNameToId = collegeMapper.selectList(
                                new LambdaQueryWrapper<College>().in(College::getName, collegeNameList)).stream()
                                .collect(Collectors.toMap(College::getName, College::getId));
                System.out.println("数据库中的学院映射:");
                collegeNameToId.forEach((name, id) -> System.out.println("  学院: " + name + ", id: " + id));
                System.out.println("==============================");

                // 遍历DTO列表，检查专业与学院的对应关系
                for (BatchStudentDTO dto : batchStudentDTOList) {
                        String collegeName = dto.getCollegeName();
                        String majorName = dto.getMajorName();
                        // 查询专业所对应的学院名称是否与数据中一致
                        Long collegeId = collegeNameToId.get(collegeName);
                        List<Major> majorList = existingMajorList.stream()
                                        .filter(m -> m.getName().equals(majorName))
                                        .toList();
                        System.out.println("========== 专业-学院匹配调试 ==========");
                        System.out.println("学生数据: 学院=[" + collegeName + "], 专业=[" + majorName + "]");
                        System.out.println("学院ID: " + collegeId);
                        System.out.println("匹配的专业数量: " + majorList.size());
                        if (!majorList.isEmpty()) {
                                System.out.println("专业列表:");
                                majorList.forEach(m -> System.out
                                                .println("  专业: " + m.getName() + ", collegeId: " + m.getCollegeId()));
                        }
                        System.out.println("==========================================");

                        if (majorList.isEmpty()) {
                                return Result.error("专业 [" + majorName + "] 不存在");
                        }
                        if (majorList.get(0).getCollegeId().longValue() != collegeId.longValue()) {
                                System.out.println("!!! 匹配失败 !!!");
                                System.out.println("  专业的collegeId: " + majorList.get(0).getCollegeId());
                                System.out.println("  学院的id: " + collegeId);
                                return Result.error("专业 [" + majorName + "] 不属于学院 [" + collegeName + "]");
                        }
                }

                // 5. 判断班级是否存在且属于对应专业
                List<String> classNameList = batchStudentDTOList.stream()
                                .map(BatchStudentDTO::getClassName)
                                .map(String::trim)
                                .distinct()
                                .toList();
                List<Class> existingClasses = classMapper.selectList(
                                new LambdaQueryWrapper<Class>()
                                                .in(Class::getName, classNameList));
                Map<String, Long> majorNameToId = existingMajorList.stream()
                                .collect(Collectors.toMap(Major::getName, Major::getId));
                for (BatchStudentDTO dto : batchStudentDTOList) {
                        String majorName = dto.getMajorName();
                        String className = dto.getClassName();
                        Long majorId = majorNameToId.get(majorName);
                        List<Class> classes = existingClasses.stream()
                                        .filter(c -> c.getName().equals(className))
                                        .toList();
                        if (classes.isEmpty()) {
                                return Result.error("班级 [" + className + "] 不存在");
                        }
                        boolean match = classes.stream()
                                        .anyMatch(c -> c.getMajorId().longValue() == majorId.longValue());
                        if (!match) {
                                return Result.error("班级 [" + className + "] 不属于专业 [" + majorName + "]");
                        }
                }

                /**
                 * 批量添加学生信息
                 * 1.添加学生表
                 * 2.添加用户表 初始化密码123456
                 * 3.添加个人信息表
                 * 4.添加用户身份表
                 */
                // 1.添加学生表
                List<Student> studentList = batchStudentDTOList.stream()
                                .map(dto -> {
                                        Student student = new Student();
                                        student.setNumber(dto.getNumber());
                                        student.setName(dto.getName());
                                        student.setGender(dto.getGender());
                                        student.setYear(dto.getYear().intValue());
                                        /**
                                         * 学院id 专业id 班级id
                                         */
                                        student.setCollegeId(collegeNameToId.get(dto.getCollegeName()));
                                        student.setMajorId(majorNameToId.get(dto.getMajorName()));
                                        // 根据班级名称查id
                                        student.setClassId(classMapper
                                                        .selectOne(new LambdaQueryWrapper<Class>().eq(Class::getName,
                                                                        dto.getClassName()))
                                                        .getId());
                                        return student;
                                }).toList();
                if (studentList.isEmpty() || studentList == null) {
                        return Result.error("学生信息添加失败");
                }
                this.saveBatch(studentList);

                // 2.添加用户表 初始化密码123456
                List<User> userList = batchStudentDTOList.stream()
                                .map(dto -> {
                                        User user = new User();
                                        user.setUsername(dto.getNumber());
                                        user.setPassword("123456");
                                        return user;
                                }).toList();
                userMapper.insertBatch(userList);

                // 3.查询新插入的用户（获取ID）
                List<User> insertedUsers = userMapper.selectList(
                                new LambdaQueryWrapper<User>().in(User::getUsername, numberList));

                // 构建用户学号 -> 用户ID 的映射
                Map<String, Long> numberToUserId = insertedUsers.stream()
                                .collect(Collectors.toMap(User::getUsername, User::getId));

                // 3.添加个人信息表
                List<Personal> personalList = batchStudentDTOList.stream()
                                .map(dto -> {
                                        Personal personal = new Personal();
                                        personal.setName(dto.getName());
                                        personal.setSex(dto.getGender());
                                        personal.setUserId(numberToUserId.get(dto.getNumber()));
                                        return personal;
                                }).toList();
                personalMapper.insertBatch(personalList);

                // 4.添加用户身份表
                List<UserWithIdentity> userWithIdentityList = insertedUsers.stream()
                                .map(u -> {
                                        UserWithIdentity uwi = new UserWithIdentity();
                                        uwi.setUserId(u.getId());
                                        uwi.setIdentityId(3L);
                                        return uwi;
                                }).toList();
                userWithIdentityMapper.insertBatch(userWithIdentityList);

                return Result.success("成功导入 " + batchStudentDTOList.size() + " 名学生");
        }

        @Override
        public List<StudentVO> searchStudent(String searchType, String keyword) {
                /**
                 * 按照姓名搜索
                 */
                if (searchType.equals("name")) {
                        List<Student> studentList = studentMapper.selectList(
                                        new LambdaQueryWrapper<Student>().like(Student::getName, keyword.trim()));

                        return convertToVOList(studentList);
                } else if (searchType.equals("college")) {
                        /**
                         * 按照学院搜索
                         */
                        List<College> collegeList = collegeMapper.selectList(
                                        new LambdaQueryWrapper<College>().like(College::getName, keyword));
                        // 根据学院id查询学生
                        List<Student> studentList = studentMapper.selectList(
                                        new LambdaQueryWrapper<Student>().in(Student::getCollegeId, collegeList
                                                        .stream()
                                                        .map(College::getId)
                                                        .toList()));
                        return convertToVOList(studentList);
                } else {
                        /**
                         * 按照专业搜索
                         */
                        List<Major> majorList = majorMapper.selectList(
                                        new LambdaQueryWrapper<Major>().like(Major::getName, keyword));
                        // 根据专业id查询学生
                        List<Student> studentList = studentMapper.selectList(
                                        new LambdaQueryWrapper<Student>().in(Student::getMajorId,
                                                        majorList.stream().map(Major::getId).toList()));
                        return convertToVOList(studentList);
                }

        }

        @Override
        public Result updateStuInfo(StudentDTO studentDTO) {
                // 1.判断学院ID是否存在
                Long count = collegeMapper
                                .selectCount(new LambdaQueryWrapper<College>().eq(College::getId, studentDTO.getCollegeId()));
                if (count == 0) {
                        return Result.error("学院不存在");
                }
                // 2.判断专业ID是否存在
                Long countMajor = majorMapper
                                .selectCount(new LambdaQueryWrapper<Major>().eq(Major::getId, studentDTO.getMajorId()));
                if (countMajor == 0) {
                        return Result.error("专业不存在");
                }
                // 3.判断班级ID是否存在
                Long countClass = classMapper
                                .selectCount(new LambdaQueryWrapper<Class>().eq(Class::getId, studentDTO.getClassId()));
                if (countClass == 0) {
                        return Result.error("班级不存在");
                }
                // 4.修改学生信息
                Student student = new Student();
                student.setNumber(studentDTO.getNumber());
                student.setName(studentDTO.getName());
                student.setGender(studentDTO.getGender());
                student.setYear(studentDTO.getYear().intValue());
                student.setCollegeId(studentDTO.getCollegeId());
                student.setMajorId(studentDTO.getMajorId());
                student.setClassId(studentDTO.getClassId());
                studentMapper.updateById(student);
                return Result.success("修改成功");
        }

        @Override
        public Result deleteStudent(String number) {
                /**
                 * 删除学生信息
                 * 1.删除学生表
                 * 2.删除用户表
                 * 3.删除个人信息表
                 * 4.删除用户身份表
                 */
        }
}