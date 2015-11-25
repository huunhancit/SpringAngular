package com.tma.spm.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

	@RequestMapping(value = "/demo", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public String index() {
		return "dsadas";
	}
}
