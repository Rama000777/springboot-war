package com.local.springdemo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
public class Passvault {

	private String url;
	private String username;
	private String password;
	private int responseCode;
	private String status;
	
	@Id
	private String id;

}