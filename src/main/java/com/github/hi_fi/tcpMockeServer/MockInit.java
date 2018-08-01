package com.github.hi_fi.tcpMockeServer;

import com.github.hi_fi.tcpMockeServer.data.FieldsToClear;
import com.github.hi_fi.tcpMockeServer.data.RegexpFilters;
import com.github.hi_fi.tcpMockeServer.model.Mock;
import com.github.hi_fi.tcpMockeServer.model.Mock.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.http.management.IntegrationGraphController;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.mapping.OutboundMessageMapper;
import org.springframework.messaging.MessageHandler;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix="mock")
public class MockInit {
	
	private Map<String, String> mockServices = new HashMap<String, String>();
	private Map<String, String> backendServices = new HashMap<String, String>();

	private List<Mock> services;
	
	public void addMock(Mock mock) {
	    this.services.add(mock);
	}
	public void setServices(List<Mock> services) {
		this.services = services;
	}
	
	public List<Mock> getServices() {
		return this.services;
	}

	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private DefaultListableBeanFactory beanFactory;
	
	@Autowired
	private FieldsToClear ftc;

	@Autowired
	private RegexpFilters rf;
	
	@PostConstruct
	public void init() {
		AutowireCapableBeanFactory bf = this.applicationContext.getAutowireCapableBeanFactory();
		System.out.println("Checking properties");
		for (Mock mock : services) {
		    BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TcpMockServerSerializer.class);
            builder.addConstructorArgValue(100000);
            builder.addConstructorArgValue(mock.getMessageStarter());
            beanFactory.registerBeanDefinition(mock.getName() + "Serializer", builder.getBeanDefinition());
			this.startMock(mock);
			this.startMockBackendConnection(mock);
		}
		// iterate over properties and register new beans
	}
	
	public void startMock(Mock mock) {
		if(!mockServices.containsKey(mock.getName())){
            BeanDefinitionBuilder builder;
                //ConnectionFactory for mock's incoming port
                builder = BeanDefinitionBuilder.rootBeanDefinition(TcpNioServerConnectionFactory.class);
                builder.addConstructorArgValue(mock.getMockPort());
                builder.addPropertyReference("serializer", mock.getName() + "Serializer");
                builder.addPropertyReference("deserializer", mock.getName() + "Serializer");
                beanFactory.registerBeanDefinition(mock.getName() + "MockConnFactory", builder.getBeanDefinition());
                //Gateway for mock's incoming requests
                builder = BeanDefinitionBuilder.rootBeanDefinition(TcpInboundGateway.class);
                builder.addPropertyReference("connectionFactory", mock.getName() + "MockConnFactory");
                builder.addPropertyReference("requestChannel", mock.getName() + "MockIncomingChannel");
                beanFactory.registerBeanDefinition(mock.getName() + "IncomingGateway", builder.getBeanDefinition());
                mockServices.put(mock.getName(), mock.getName() + "IncomingGateway");
			//Enricher that adds mocks information to message headers. This created from channel automatically.
			IntegrationFlow flow = IntegrationFlows.from(mock.getName() + "MockIncomingChannel")
							.enrichHeaders(h -> h.header("mockName", mock.getName()).header("mockBeanName", mock.getMockBeanName()))
							.channel("ServiceChannel")
							.get();
			beanFactory.registerSingleton(mock.getName() + "IncomingFlow", flow);
            beanFactory.initializeBean(flow, mock.getName() + "IncomingFlow");
            ftc.setFields(mock.getName(), mock.getFieldsToClear());
			rf.setRegexpFilters(mock.getName(), mock.getRegexpFilters());
		}
		((TcpInboundGateway) beanFactory.getBean(mockServices.get(mock.getName()))).start();
	}
	
	public void startMockBackendConnection(Mock mock) {
		if (!backendServices.containsKey(mock.getName())) {
			//ConnectionFactory for mock's connection to real endpoint
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(TcpNioClientConnectionFactory.class);
			builder.addConstructorArgValue(mock.getTargetHost());
			builder.addConstructorArgValue(mock.getTargetPort());
            builder.addPropertyReference("serializer", mock.getName() + "Serializer");
            builder.addPropertyReference("deserializer", mock.getName() + "Serializer");
			builder.addPropertyReference("mapper", "mapper");
			beanFactory.registerBeanDefinition(mock.getName() + "TargetConnFactory", builder.getBeanDefinition());
			
			if (mock.getEndpointType().equals(Type.TCP)) {
				//Gateway for mock's connection to real endpoint
				builder = BeanDefinitionBuilder.rootBeanDefinition(TcpOutboundGateway.class);
				builder.addPropertyReference("connectionFactory", mock.getName() + "TargetConnFactory");
				beanFactory.registerBeanDefinition(mock.getName() + "OutgoingGateway", builder.getBeanDefinition());
				backendServices.put(mock.getName(), mock.getName() + "OutgoingGateway");
			} else if (mock.getEndpointType().equals(Type.HTTP)) {
				builder = BeanDefinitionBuilder.rootBeanDefinition(TcpSendingMessageHandler.class);
				builder.addPropertyReference("connectionFactory", mock.getName() + "TargetConnFactory");
				beanFactory.registerBeanDefinition(mock.getName() + "SendingMessageHandler", builder.getBeanDefinition());
				backendServices.put(mock.getName(), mock.getName() + "SendingMessageHandler");
				builder = BeanDefinitionBuilder.rootBeanDefinition(TcpReceivingChannelAdapter.class);
				builder.addPropertyReference("connectionFactory", mock.getName() + "TargetConnFactory");
				builder.addPropertyReference("outputChannel", "fromCustomReceiver");
				beanFactory.registerBeanDefinition(mock.getName()+"ClientReceivingChannel", builder.getBeanDefinition());
			}
			
			//Channel to take the requests to incoming GW.
			builder = BeanDefinitionBuilder.rootBeanDefinition(DirectChannel.class);
			beanFactory.registerBeanDefinition(mock.getName() + "TargetOutgoingChannel", builder.getBeanDefinition());	
			((DirectChannel) beanFactory.getBean(mock.getName() + "TargetOutgoingChannel")).subscribe((MessageHandler) (beanFactory.getBean(backendServices.get(mock.getName()))));
			
		}
		if (mock.getEndpointType().equals(Type.TCP)) {
			((TcpOutboundGateway) beanFactory.getBean(backendServices.get(mock.getName()))).start();
		} else {
			((TcpSendingMessageHandler) beanFactory.getBean(backendServices.get(mock.getName()))).start();

		}
	}
}
