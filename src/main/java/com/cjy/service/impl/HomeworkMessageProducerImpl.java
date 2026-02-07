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


    public void sendPublishMessage(Homework homework, List<String> studentNumbers) {
        // 构建作业消息DTO
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

        // ========== 2. 发送到RabbitMQ ==========
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.HOMEWORK_EXCHANGE,
                RabbitMQConstants.HOMEWORK_PUBLISH_ROUTING_KEY,
                dto,
                correlationData);

        log.info("【MessageProducer】发送作业发布消息，作业ID：{}，标题：{}，学生数量：{}，消息ID：{}",
                homework.getId(), homework.getTitle(), studentNumbers.size(), correlationData.getId());
    }

    @Override
    public void batchSendStudentNotification(HomeworkMessageDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchSendStudentNotification'");
    }
    
}

