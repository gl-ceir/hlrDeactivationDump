package com.gl.eirs.hlrdeactivation;

import com.gl.eirs.hlrdeactivation.service.impl.FileService;
import com.gl.eirs.hlrdeactivation.service.impl.MainService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableEncryptableProperties
public class HlrDeactivationApplication implements CommandLineRunner {

	@Autowired
	FileService fileService;

	@Autowired
	MainService mainService;
	public static void main(String[] args) {
		SpringApplication.run(HlrDeactivationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mainService.hlrDeactivationProcess();
	}
}
