/**
 * XiangBI File: src/main/java/com/panther/smartBI/config/RabbitMQConfig.java
 * Responsibility: Project configuration module.
 */
package com.panther.smartBI.config;

import com.panther.smartBI.constant.BiMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 基础组件配置，应用启动时自动声明交换机、队列和绑定关系。
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 创建图表生成交换机。
     */
    @Bean
    public DirectExchange biExchange() {
        return new DirectExchange(BiMQConstant.BI_EXCHANGE_NAME, true, false);
    }

    /**
     * 创建图表生成队列。
     */
    @Bean
    public Queue biQueue() {
        return new Queue(BiMQConstant.BI_QUEUE_NAME, true, false, false);
    }

    /**
     * 绑定交换机、队列和 routingKey。
     */
    @Bean
    public Binding biBinding(Queue biQueue, DirectExchange biExchange) {
        return BindingBuilder.bind(biQueue).to(biExchange).with(BiMQConstant.BI_ROUTING_KEY);
    }
}
