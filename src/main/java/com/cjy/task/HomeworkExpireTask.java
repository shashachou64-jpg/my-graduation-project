package com.cjy.task;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cjy.common.RedisConstants;
import com.cjy.domain.Homework;
import com.cjy.mapper.HomeworkMapper;
import com.cjy.utils.CacheClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Slf4j
@Component
public class HomeworkExpireTask {

    @Autowired
    private HomeworkMapper homeworkMapper;
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private CacheClient cacheClient;

    // 状态常量（根据你的数据库）
    private static final long STATUS_ONGOING = 0L;   // 进行中
    private static final long STATUS_EXPIRED = 2L;  // 已过期

    /**
     * 每5分钟检查一次过期作业
     * 只更新真正过期的记录
     */
    @Scheduled(fixedRate = 300000)
    public void updateExpiredHomework() {
        log.info("开始检查过期作业...");
        
        // 精准更新：只更新截止时间已过 且 状态为进行中 的记录
        LambdaUpdateWrapper<Homework> wrapper = new LambdaUpdateWrapper<>();
        wrapper.lt(Homework::getDeadline, new Date())      // deadline < now
               .eq(Homework::getStatus, STATUS_ONGOING)   // 状态=进行中
               .set(Homework::getStatus, STATUS_EXPIRED) // 设置为已过期
               .set(Homework::getUpdateTime, new Date());
        
        int affectedRows = homeworkMapper.update(null, wrapper);
        
        if (affectedRows > 0) {
            log.info("更新了 {} 条过期作业", affectedRows);
            // 清理缓存
            clearHomeworkCache();
        }
    }

    /**
     * 清理作业相关缓存
     */
    private void clearHomeworkCache() {
        try {
            // 清理所有作业列表缓存
            Set<String> listKeys = stringRedisTemplate.keys(RedisConstants.HOMEWORK_LIST + "*");
            if (listKeys != null && !listKeys.isEmpty()) {
                stringRedisTemplate.delete(listKeys);
            }
            
            // 清理所有作业详情缓存  
            Set<String> infoKeys = stringRedisTemplate.keys(RedisConstants.HOMEWORK_INFO + "*");
            if (infoKeys != null && !infoKeys.isEmpty()) {
                stringRedisTemplate.delete(infoKeys);
            }
            
            // 如果有教师作业集合缓存也要清理
            Set<String> teacherKeys = stringRedisTemplate.keys(RedisConstants.TEACHER_HOMEWORK_SET + "*");
            if (teacherKeys != null && !teacherKeys.isEmpty()) {
                stringRedisTemplate.delete(teacherKeys);
            }
            
            Set<String> teacherListKeys = stringRedisTemplate.keys(RedisConstants.TEACHER_HOMEWORK_LIST + "*");
            if (teacherListKeys != null && !teacherListKeys.isEmpty()) {
                stringRedisTemplate.delete(teacherListKeys);
            }
            
            log.info("清理作业相关缓存完成");
        } catch (Exception e) {
            log.warn("清理缓存失败", e);
        }
    }
}