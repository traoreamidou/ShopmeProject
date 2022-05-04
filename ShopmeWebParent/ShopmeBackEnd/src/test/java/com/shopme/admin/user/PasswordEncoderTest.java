package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testEncodePassord() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "nam2020";
		String encodedPassword = bCryptPasswordEncoder.encode(rawPassword);
		System.out.println(encodedPassword);		
		boolean matches = bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
		assertThat(matches).isTrue();
	}
}
