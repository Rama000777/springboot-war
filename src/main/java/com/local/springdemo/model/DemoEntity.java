package com.local.springdemo.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document
@NoArgsConstructor
public class DemoEntity {

	@Id
	private String uniqueId;
	private String firstName;
	private String lastName;
	private Date dob;
}
