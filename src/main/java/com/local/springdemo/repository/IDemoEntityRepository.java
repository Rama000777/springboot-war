package com.local.springdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.local.springdemo.model.DemoEntity;

public interface IDemoEntityRepository extends CrudRepository<DemoEntity, String>{

}
