package com.cjy.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cjy.common.RedisConstants;
import com.cjy.common.Result;
import com.cjy.domain.Homework;
import com.cjy.dto.PublishHomeworkDTO;
import com.cjy.mapper.HomeworkMapper;
import com.cjy.service.IHomeworkMessageProducer;
import com.cjy.service.IHomeworkService;
import com.cjy.utils.CacheClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements IHomeworkService {
    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private IHomeworkMessageProducer homeworkMessageProducer;


    @Override
    public Result publishHomework(PublishHomeworkDTO dto) {
        log.info("===========================================================");
        log.info("发布作业，标题：{}", dto.getCourseId());
        log.info("======================================================");


        if(dto.getStartTime() != null && dto.getStartTime().after(dto.getDeadline())){
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
         * 缓存作业信息
         */
        log.info("====================开始缓存作业信息====================");
        try {
            cacheClient.setWithLogicExpireAndRandom(RedisConstants.HOMEWORK_INFO + homework.getId(), homework, RedisConstants.HOMEWORK_INFO_TTL, TimeUnit.MINUTES);
            log.info("====================缓存作业信息成功====================");
        } catch (Exception e) {
            // TODO: handle exception
            log.error("====================缓存作业信息失败====================", e);
        }
        
        /**
         * 更新作业列表到缓存
         */
        log.info("====================开始更新作业列表到缓存====================");
        try {
            cacheClient.setWithLogicExpireAndRandom(RedisConstants.HOMEWORK_LIST, homework, RedisConstants.HOMEWORK_LIST_TTL, TimeUnit.MINUTES);
            log.info("====================更新作业列表到缓存成功====================");
        } catch (Exception e) {
            // TODO: handle exception
            log.error("====================更新作业列表到缓存失败====================", e);
        }

        try {
            homeworkMessageProducer.sendPublishMessage(homework);
            log.info("====================发送作业发布消息到队列成功====================");
        } catch (Exception e) {
            log.error("====================发送作业发布消息到队列失败====================", e);
        }
        return Result.success("发布作业成功");
    }
    
}
