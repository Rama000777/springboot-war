package com.local.springdemo.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KakfaService {
	@Autowired
	private KafkaTemplate<Object, Object> template;
}
