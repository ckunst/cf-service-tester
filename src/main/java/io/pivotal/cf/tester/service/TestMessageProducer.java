package io.pivotal.cf.tester.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class TestMessageProducer {
	private static Logger log = LoggerFactory.getLogger(TestMessageProducer.class);
		
	@Value("${vcap.application.name:cf-tester}")
	private String applicationName;
	
	@Value("${vcap.application.instance_id:cf-tester}")
	private String instanceId;
	
	@Value("${vcap.application.instance_index:0}")
	private int instanceIndex;
	
	@Value("${rabbit.queueName:testQueue}")
	private String rabbitQueueName;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Scheduled(fixedRateString="${rabbit.publishRate:1000}")
	public void publish() {
		final Date now = new Date();
		String timeString = Util.DTF.print(now.getTime());
		
		String messageBody = new StringBuilder(applicationName)
			.append(" [").append(instanceId).append("]")
			.append(" (").append(instanceIndex).append(")")
			.append(" PUB ")
			.append(timeString)
			.toString();
		
		Message message = MessageBuilder
				.withBody(messageBody.getBytes())
				.setTimestamp(now)
				.build();
				
		rabbitTemplate.send(rabbitQueueName, message);
		
		log.info(messageBody);

	}
	
}
