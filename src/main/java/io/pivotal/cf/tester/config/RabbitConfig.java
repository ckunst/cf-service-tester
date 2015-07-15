package io.pivotal.cf.tester.config;

import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Configuration
public class RabbitConfig {

	@Value("${rabbit.queueName:testQueue}")
	private String rabbitQueueName;

	@Autowired
	public MessageListener testMessageHandler;

	@Autowired
	public ErrorHandler testErrorHandler;
	
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setIgnoreDeclarationExceptions(true);
		return rabbitAdmin;
	}
	
	@Bean
	public Queue testQueue() {
	    return new Queue(rabbitQueueName);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setRoutingKey(rabbitQueueName);
		return rabbitTemplate;
	}
	
	
	@Bean
	public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory) {
	    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	    container.setConnectionFactory(connectionFactory);
	    container.setQueues(testQueue());
	    container.setMessageListener(new MessageListenerAdapter(testMessageHandler));
	    container.setErrorHandler(testErrorHandler);
	    return container;
	}

}