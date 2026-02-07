package com.cjy.service;

import java.util.List;

import com.cjy.domain.Homework;
import com.cjy.dto.HomeworkMessageDTO;

public interface IHomeworkMessageProducer {
    /**
     * 发送教师发布作业消息
     * 
     * @param homework      作业对象
     * @param studentNumbers 学生学号列表
     */
    void sendPublishMessage(Homework homework, List<String> studentNumbers);

    /**
     * 批量发送学生通知
     * 
     * @param dto 作业消息DTO
     */
    void batchSendStudentNotification(HomeworkMessageDTO dto);
}
