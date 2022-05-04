package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 1);
		Category category = entityManager.find(Category.class, 10);
		Product product = new Product();
		product.setName("Acer Aspire Laptop");
		product.setAlias("Acer_Aspire_Laptop");
		product.setShortDescription("Short description for Acer Aspire");
		product.setFullDescription("Full description for Acer Aspire");
		product.setBrand(brand);
		product.setCategory(category);
		product.setPrice(678);
		product.setCost(600);
		product.setEnabled(true);
		product.setInStock(true);
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());		
		Product savedProduct = repo.save(product);
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void listAllProduct() {
		Iterable<Product> iterableProducts = repo.findAll();
		iterableProducts.forEach(System.out::println);
	}
	
	@Test
	public void testGetProductById() {
		Integer id = 1;
		Product obj = repo.findById(id).get();
		System.out.println(obj);
		assertThat(obj).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product obj = repo.findById(id).get();
		obj.setPrice(499);
		repo.save(obj);
		Product updatedProduct = entityManager.find(Product.class, id);
		assertThat(updatedProduct.getPrice()).isEqualTo(499);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		Optional<Product> obj = repo.findById(id);
		assertThat(!obj.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		product.setMainImage("main_image.jpg");
		product.addExtraImage("extra_image_1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra_image_3.png");
		Product savedProduct = repo.save(product);
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void saveProductWithDetails() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		product.addDetail("Device Memory", "128 GB");
		product.addDetail("CPU Model", "MediaTek");
		product.addDetail("OS", "Android 10");
		Product savedProduct = repo.save(product);
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
}
