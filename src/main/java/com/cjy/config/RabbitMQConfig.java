package com.cjy.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import com.cjy.common.RabbitMQConstants;

/**
 * RabbitMQ配置类
 * 配置消息队列的交换器、队列和绑定关系
 */
@Slf4j
@Configuration
public class RabbitMQConfig {

    /**
     * 1. 配置死信交换器（Dead Letter Exchange）
     * 用于处理无法被消费的消息
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(RabbitMQConstants.DLX_EXCHANGE, true, false);
    }

    /**
     * 2. 配置死信队列
     * 存放无法被正常处理的消息
     */
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(RabbitMQConstants.DLX_QUEUE).build();
    }

    /**
     * 3. 绑定死信队列到死信交换器
     */
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(RabbitMQConstants.DLX_ROUTING_KEY);
    }

    /**
     * 4. 配置作业主交换器（Topic类型）
     * 支持路由键模式匹配
     */
    @Bean
    public TopicExchange homeworkExchange() {
        return new TopicExchange(RabbitMQConstants.HOMEWORK_EXCHANGE, true, false);
    }

    /**
     * 5. 配置教师发布作业队列
     * 设置死信交换器和死信路由键
     */
    @Bean
    public Queue homeworkPublishQueue() {
        return QueueBuilder.durable(RabbitMQConstants.HOMEWORK_PUBLISH_QUEUE)
                .deadLetterExchange(RabbitMQConstants.DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 6. 配置学生接收作业队列
     * 设置死信交换器和死信路由键
     */
    @Bean
    public Queue homeworkReceiveQueue() {
        return QueueBuilder.durable(RabbitMQConstants.HOMEWORK_RECEIVE_QUEUE)
                .deadLetterExchange(RabbitMQConstants.DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitMQConstants.DLX_ROUTING_KEY)
                .build();
    }

    /**
     * 7. 绑定发布队列到交换器
     */
    @Bean
    public Binding publishBinding() {
        return BindingBuilder.bind(homeworkPublishQueue())
                .to(homeworkExchange())
                .with(RabbitMQConstants.HOMEWORK_PUBLISH_ROUTING_KEY);
    }

    /**
     * 8. 绑定接收队列到交换器
     */
    @Bean
    public Binding receiveBinding() {
        return BindingBuilder.bind(homeworkReceiveQueue())
                .to(homeworkExchange())
                .with(RabbitMQConstants.HOMEWORK_RECEIVE_ROUTING_KEY);
    }

    /**
     * 9. 配置JSON消息转换器
     * 用于序列化和反序列化消息对象
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 10. 配置RabbitTemplate
     * 用于发送消息
     */
    @Bean
    public RabbitTemplate rabbitTemplate(@NonNull ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
    
    // 【关键配置】开启消息确认机制
    rabbitTemplate.setMandatory(true);
    
    // 1. ConfirmCallback - 确认是否到达Exchange
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
        String messageId = correlationData != null ? correlationData.getId() : "未知";
        if (ack) {
            log.info("【消息确认】消息已成功到达Exchange，ID: {}", messageId);
        } else {
            log.error("【消息确认】消息到达Exchange失败，ID: {}, 原因: {}", messageId, cause);
            // 发送告警、重试、记录数据库
        }
    });
    
    // 2. ReturnsCallback - 确认是否路由到Queue
    rabbitTemplate.setReturnsCallback(returned -> {
        log.error("【消息路由失败】消息无法路由到队列，Message: {}, ReplyCode: {}, ReplyText: {}",
            returned.getMessage(), returned.getReplyCode(), returned.getReplyText());
        // 可能原因：队列名称写错了、队列满了、权限不够
    });
    
    return rabbitTemplate;
    }
}
