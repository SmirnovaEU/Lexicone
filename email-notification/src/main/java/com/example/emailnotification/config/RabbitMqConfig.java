package com.example.emailnotification.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    //using now
    @Bean
    public Queue repeatNotificationQueue() {
        return new Queue("repeat-notifications-queue");
    }

    //not using now
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("main-exchange");
    }

    @Bean
    public Binding notificationBinding(){
        return BindingBuilder.bind(repeatNotificationQueue())
          .to(topicExchange())
          .with("notification.repeat");

    }
}
