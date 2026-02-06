package com.cjy.service;

import java.util.List;

import com.cjy.domain.Homework;

public interface IHomeworkMessageProducer {
    /**
     * 教师发布作业
     * 
     * @param homework 保存后的作业对象（包含ID）
     */
    void sendPublishMessage(Homework homework);

    /**
     * 批量发送学生通知
     * 
     * @param homework 作业对象
     * @param studentNumbers 需要通知的学生学号列表
     */
    void batchSendStudentNotification(Homework homework);
}
