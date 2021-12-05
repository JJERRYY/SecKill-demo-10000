package com.xq.tmall.core;

import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

@ExtRocketMQTemplateConfiguration(nameServer = "${rocketmq.extNameServer:rocketmq.name-server}")
public class ExtRocketMQTemplate extends RocketMQTemplate {
}
