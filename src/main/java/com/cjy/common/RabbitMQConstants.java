package com.cjy.common;

/**
 * RabbitMQ消息队列常量
 */
public class RabbitMQConstants {

    /**
     * 作业交换器名称
     */
    public static final String HOMEWORK_EXCHANGE = "homework.exchange";

    /**
     * 教师发布作业队列
     */
    public static final String HOMEWORK_PUBLISH_QUEUE = "homework.publish.queue";

    /**
     * 学生接收作业队列
     */
    public static final String HOMEWORK_RECEIVE_QUEUE = "homework.receive.queue";

    /**
     * 作业发布路由键
     */
    public static final String HOMEWORK_PUBLISH_ROUTING_KEY = "homework.publish";

    /**
     * 作业接收路由键
     */
    public static final String HOMEWORK_RECEIVE_ROUTING_KEY = "homework.receive";

    /**
     * 死信交换器
     */
    public static final String DLX_EXCHANGE = "dlx.exchange";

    /**
     * 死信队列
     */
    public static final String DLX_QUEUE = "dlx.queue";

    /**
     * 死信路由键
     */
    public static final String DLX_ROUTING_KEY = "dlx";
}
