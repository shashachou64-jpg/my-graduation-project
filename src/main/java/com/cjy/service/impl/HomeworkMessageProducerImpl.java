package com.cjy.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cjy.common.RabbitMQConstants;
import com.cjy.domain.CourseWithStudents;
import com.cjy.domain.Homework;
import com.cjy.domain.StudentWithGroup;
import com.cjy.dto.HomeworkMessageDTO;
import com.cjy.mapper.CourseWithStudentsMapper;
import com.cjy.mapper.StudentWithGroupMapper;
import com.cjy.service.IHomeworkMessageProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HomeworkMessageProducerImpl implements IHomeworkMessageProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StudentWithGroupMapper studentWithGroupMapper;

    @Autowired
    private CourseWithStudentsMapper courseWithStudentsMapper;

    /**
     * 发送教师发布作业消息
     */
    @Override
    public void sendPublishMessage(Homework homework) {
        /**
         * 根据小组id查询学生列表
         */
        List<String> studentNumbers = getStudentNumbersByGroupId(homework.getGroupId(),homework.getCourseId());
        /**
         * 构建作业消息DTO
         */
        HomeworkMessageDTO dto = HomeworkMessageDTO.builder()
                .homeworkId(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .courseId(homework.getCourseId())
                .teacherId(homework.getTeacherId())
                .groupId(homework.getGroupId())
                .startTime(homework.getStartTime())
                .deadline(homework.getDeadline())
                .createTime(homework.getCreateTime())
                .studentNumbers(studentNumbers)
                .build();

        /**
         * 发送作业消息（带 CorrelationData 用于确认回调）
         */
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.HOMEWORK_EXCHANGE,
                RabbitMQConstants.HOMEWORK_PUBLISH_ROUTING_KEY,
                dto,
                correlationData);

        log.info("【MessageProducer】发送作业消息，作业ID：{}，标题：{}，消息ID：{}", 
                homework.getId(), homework.getTitle(), correlationData.getId());
    }

    /**
     * 批量发送学生通知
     */
    @Override
    public void batchSendStudentNotification(Homework homework) {
        /**
         * 构建作业消息DTO
         */
        List<String> studentNumbers = getStudentNumbersByGroupId(homework.getGroupId(),homework.getCourseId());
        HomeworkMessageDTO dto = HomeworkMessageDTO.builder()
                .homeworkId(homework.getId())
                .title(homework.getTitle())
                .description(homework.getDescription())
                .courseId(homework.getCourseId())
                .teacherId(homework.getTeacherId())
                .groupId(homework.getGroupId())
                .startTime(homework.getStartTime())
                .deadline(homework.getDeadline())
                .createTime(homework.getCreateTime())
                .studentNumbers(studentNumbers)
                .build();

        /**
         * 发送作业消息（带 CorrelationData 用于确认回调）
         */
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
            RabbitMQConstants.HOMEWORK_EXCHANGE,
            RabbitMQConstants.HOMEWORK_RECEIVE_ROUTING_KEY,
            dto,
            correlationData);

        log.info("【MessageProducer】批量发送学生通知，作业ID：{}，学生数量：{}，消息ID：{}", 
                homework.getId(), studentNumbers.size(), correlationData.getId());

    }


    private List<String> getStudentNumbersByGroupId(Long groupId, Long courseId) {
        if (groupId == null) {
            //返回课程所有学生
            return courseWithStudentsMapper.selectList(
                new LambdaQueryWrapper<CourseWithStudents>()
                .eq(CourseWithStudents::getCourseId, courseId))
                .stream()
                .map(CourseWithStudents::getStudentNumber)
                .toList();
        }
        return studentWithGroupMapper.selectList(
            new LambdaQueryWrapper<StudentWithGroup>()
            .eq(StudentWithGroup::getGroupId, groupId))
            .stream()
            .map(StudentWithGroup::getStudentNumber)
            .toList();
       
    }
}

