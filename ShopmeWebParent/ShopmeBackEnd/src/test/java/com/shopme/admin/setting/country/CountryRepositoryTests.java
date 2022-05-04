package com.shopme.admin.setting.country;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {
	
	@Autowired
	private CountryRepository repo;
	
	@Test
	public void testCreateCountry() {
		List<Country> countries = List.of(
				new Country("Mali", "ML"), 
				new Country("Benin", "BN"),
				new Country("Togo", "TG"),
				new Country("Nigeria", "NG"),
				new Country("South Africa", "SA"),
				new Country("Republic of India", "RI"),
				new Country("United States of America", "USA")
		);
		Iterable<Country> savedCountry = repo.saveAll(countries);
		assertThat(savedCountry).isNotNull();
		//assertThat(savedCountry.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListCountries() {
		List<Country> listCountries = repo.findAllByOrderByNameAsc();
		listCountries.forEach(System.out::println);
		assertThat(listCountries.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateCategory() {
		Integer id = 5;
		String name = "France";
		String code = "FR";
		Country country = repo.findById(id).get();
		country.setName(name);
		country.setCode(code);
		Country updatedCountry = repo.save(country);
		assertThat(updatedCountry.getName()).isEqualTo(name);		
	}
	
	@Test
	public void testGetCountry() {
		Integer id = 3;
		Country country = repo.findById(id).get();
		assertThat(country).isNotNull();
	}
	
	@Test
	public void testDeleteCountry() {
		Integer id = 5;
		repo.deleteById(id);
		Optional<Country> findById = repo.findById(id);
		assertThat(findById.isEmpty());
	}
}
