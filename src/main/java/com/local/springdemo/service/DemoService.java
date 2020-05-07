package com.local.springdemo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.local.springdemo.exception.DemoEntityException;
import com.local.springdemo.model.DemoEntity;
import com.local.springdemo.repository.IDemoEntityRepository;

@Service
public class DemoService {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	IDemoEntityRepository demoEntityRepository;

	public DemoEntity addDemoEntity(DemoEntity demoEntity) {
		return demoEntityRepository.save(demoEntity);
	}

	public List<DemoEntity> getDemoEntitys() {
		return toList(demoEntityRepository.findAll());
	}

	public DemoEntity updateDemoEntity(DemoEntity demoEntity) throws DemoEntityException {
		if (StringUtils.isEmpty(demoEntity.getUniqueId())) {
			throw new DemoEntityException("DemoEntity ID is required");
		}
		if (getDemoEntity(demoEntity.getUniqueId()) != null) {
			return demoEntityRepository.save(demoEntity);
		}
		return demoEntity;
	}

	public void deleteDemoEntity(String demoEntityId) throws DemoEntityException {
		if (StringUtils.isEmpty(demoEntityId)) {
			throw new DemoEntityException("DemoEntity ID is required");
		}
		if (getDemoEntity(demoEntityId) != null) {
			demoEntityRepository.deleteById(demoEntityId);
		}
	}

	public DemoEntity getDemoEntity(String demoEntityId) throws DemoEntityException {
		if (StringUtils.isEmpty(demoEntityId)) {
			throw new DemoEntityException("DemoEntity ID is required");
		}
		Optional<DemoEntity> demoEntityOptional = demoEntityRepository.findById(demoEntityId);
		if (!demoEntityOptional.isPresent()) {
			throw new DemoEntityException("DemoEntity Not Found");
		}
		return demoEntityOptional.get();
	}

	private <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
	}
}
