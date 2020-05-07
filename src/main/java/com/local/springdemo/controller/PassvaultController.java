package com.local.springdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.local.springdemo.model.Passvault;
import com.local.springdemo.service.PassvaultService;

@RestController
@RequestMapping("/pass")
@CrossOrigin(origins = "http://localhost:4200")
public class PassvaultController {
	@Autowired
	PassvaultService passvaultService;

	@GetMapping(path = "/vaults")
	public ResponseEntity<List<Passvault>> getAllPassvault() {
		return new ResponseEntity<>(passvaultService.getAllPassvault(), HttpStatus.OK);
	}

	@GetMapping(path = "/loaddata")
	public ResponseEntity<String> loadData(@RequestParam String datadirectory) {
		passvaultService.loadData(datadirectory);
		return new ResponseEntity<>("Completed Succcessfully", HttpStatus.OK);
	}
}
