package com.cjy.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.cjy.common.RabbitMQConstants;
import com.cjy.dto.HomeworkMessageDTO;
import com.cjy.service.IHomeworkMessageConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HomeworkConsumerImpl implements IHomeworkMessageConsumer {
    @RabbitListener(queues = RabbitMQConstants.HOMEWORK_PUBLISH_QUEUE)
    @Override
    public void handlePublishMessage(HomeworkMessageDTO dto) {
        log.info("【MessageConsumer】接收作业发布消息，作业ID：{}，标题：{}",
                dto.getHomeworkId(), dto.getTitle());

        try {
            if (dto.getStudentNumbers() != null && !dto.getStudentNumbers().isEmpty()) {
                for (String studentNumber : dto.getStudentNumbers()) {
                    sendNotificationToStudent(studentNumber, dto);
                }
            } else {
                log.warn("【MessageConsumer】学生列表为空，作业ID：{}", dto.getHomeworkId());
            }
        } catch (Exception e) {
            log.error("【MessageConsumer】处理作业消息失败，作业ID：{}，错误：{}",
                    dto.getHomeworkId(), e.getMessage(), e);
        }

    }

    public void sendNotificationToStudent(String studentNumber, HomeworkMessageDTO dto) {
        // 构建通知给学生的消息
        String message = String.format(
                "您有一份新作业待完成：【%s】截止时间：%s",
                dto.getTitle(),
                dto.getDeadline() != null ? dto.getDeadline().toString() : "待定");

        log.info("【通知】学生{}，消息：{}", studentNumber, message);
    }

}
