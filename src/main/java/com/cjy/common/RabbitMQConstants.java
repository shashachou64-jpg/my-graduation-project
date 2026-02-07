package com.cjy.common;

/**
 * RabbitMQ消息队列常量
 */
public class RabbitMQConstants {

    private RabbitMQConstants() {}

    // ========== Exchange ==========
    public static final String HOMEWORK_EXCHANGE = "homework.exchange";
    public static final String DLX_EXCHANGE = "dlx.exchange"; // 死信交换器

    // ========== Queue ==========
    public static final String HOMEWORK_PUBLISH_QUEUE = "homework.publish.queue";
    public static final String HOMEWORK_RECEIVE_QUEUE = "homework.receive.queue";
    public static final String DLX_QUEUE = "dlx.queue"; // 死信队列

    // ========== RoutingKey ==========
    public static final String HOMEWORK_PUBLISH_ROUTING_KEY = "homework.publish";
    public static final String HOMEWORK_RECEIVE_ROUTING_KEY = "homework.receive";
    public static final String DLX_ROUTING_KEY = "dlx.routing"; // 死信路由键
}
