package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {

	@Autowired
	BrandRepository repo;
	
	@Test
	public void createFirstBrand() {
		Category laptops = new Category(6);
		Brand acer = new Brand("Acer");
		acer.getCategories().add(laptops);
		Brand savedBrand = repo.save(acer);
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void createSecondBrand() {
		Category cellPhones = new Category(4);
		Category tablets = new Category(7);
		Brand apple = new Brand("Apple");
		apple.getCategories().add(cellPhones);
		apple.getCategories().add(tablets);
		Brand savedBrand = repo.save(apple);
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void createThirdBrand() {
		Brand samsung = new Brand("Samsung");
		samsung.getCategories().add(new Category(29));
		samsung.getCategories().add(new Category(24));
		Brand savedBrand = repo.save(samsung);
		assertThat(savedBrand).isNotNull();
		assertThat(savedBrand.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testFindAll() {
		Iterable<Brand> brands = repo.findAll();
		brands.forEach(System.out::println);
		assertThat(brands).isNotEmpty();
	}
	
	@Test
	public void testGetById() {
		Brand brand = repo.findById(1).get();
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void testUpadteName() {
		String newName = "Samsung Electronis";
		Brand samsung = repo.findById(3).get();
		samsung.setName(newName);
		Brand savedBrand = repo.save(samsung);
		assertThat(savedBrand.getName()).isEqualTo(newName);		
	}
	
	@Test
	public void testDelete() {
		Integer id = 2;
		repo.deleteById(id);
		Optional<Brand> result = repo.findById(id);
		assertThat(result.isEmpty());
	}
	
	@Test
	public void testUpdateId() {
		Brand brand = repo.findById(3).get();
		brand.setId(2);
		Brand savedBrand = repo.save(brand);
		assertThat(savedBrand.getId()).isEqualTo(2);
	}
}
