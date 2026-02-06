package com.cjy.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.Channel;
import com.cjy.common.RabbitMQConstants;
import com.cjy.dto.HomeworkMessageDTO;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HomeworkMessageConsumerImpl {

    
}
