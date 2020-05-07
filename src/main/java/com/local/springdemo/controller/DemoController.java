package com.local.springdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.local.springdemo.exception.DemoEntityException;
import com.local.springdemo.model.DemoEntity;
import com.local.springdemo.service.DemoService;

@RestController
@RequestMapping("/demo")
public class DemoController {

	@Autowired
	DemoService demoService;

	@PostMapping(path = "/add")
	public ResponseEntity<DemoEntity> addDemoEntity(@RequestBody DemoEntity demoEntity) {
		return new ResponseEntity<>(demoService.addDemoEntity(demoEntity), HttpStatus.CREATED);
	}

	@PutMapping(path = "/update")
	public ResponseEntity<Object> updateDemoEntity(@RequestBody DemoEntity demoEntity) {
		try {
			return new ResponseEntity<>(demoService.updateDemoEntity(demoEntity), HttpStatus.OK);
		} catch (DemoEntityException exception) {
			return new ResponseEntity<>("Error Occured" + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(path = "/delete/{demoEntityId}")
	public ResponseEntity<String> deleteDemoEntity(@PathVariable(value = "demoEntityId") String demoEntityId) {
		try {
			demoService.deleteDemoEntity(demoEntityId);
			return new ResponseEntity<>("DemoEntity Record Deleted Successfully", HttpStatus.OK);
		} catch (DemoEntityException exception) {
			return new ResponseEntity<>("DemoEntity Record Not Found", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(path = "/{demoEntityId}")
	public ResponseEntity<DemoEntity> getDemoEntity(@PathVariable(value = "demoEntityId") String demoEntityId)
			throws DemoEntityException {
		return new ResponseEntity<>(demoService.getDemoEntity(demoEntityId), HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<DemoEntity>> getDemoEntitys() {
		return new ResponseEntity<>(demoService.getDemoEntitys(), HttpStatus.OK);
	}
}