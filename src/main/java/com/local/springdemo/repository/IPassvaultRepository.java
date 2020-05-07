package com.local.springdemo.repository;

import org.springframework.data.repository.CrudRepository;

import com.local.springdemo.model.Passvault;

public interface IPassvaultRepository extends CrudRepository<Passvault, String>{

}
