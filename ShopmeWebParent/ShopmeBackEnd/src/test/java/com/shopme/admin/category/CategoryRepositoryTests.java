package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;

@DataJpaTest(showSql =false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

	@Autowired
	CategoryRepository repo;
	
	@Test
	public void testCreateRootCategory() {
		Category category = new Category("Electronics");
		Category savedCategory = repo.save(category);
		assertThat(savedCategory.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent = new Category(5);
		Category phone = new Category("Gaming Laptops", parent);
		Category savedCategory = repo.save(phone);
		assertThat(savedCategory.getId()).isGreaterThan(0);		
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(1).get();
		System.out.println(category.getName());
		Set<Category> children = category.getChildren();
		children.forEach(subCategory -> System.out.println(subCategory.getName()));
		assertThat(children.size()).isGreaterThan(0);		
	}
	
	@Test
	public void  testPrintHierarchicalCategories() {
		Iterable<Category> categories = repo.findAll();
		categories.forEach(
				category -> {
					if(category.getParent() == null) {
						System.out.println(category.getName());
						Set<Category> children = category.getChildren();
						children.forEach(
								subCategory -> {
									System.out.println("--" + subCategory.getName());
									printChildren(subCategory, 1);
								}
						);
					}
				}
		);
	}
	
	@Test
	public void printChildren(Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = parent.getChildren();
		children.forEach(
				subCategory -> {
					for(int i = 0; i < newSubLevel; i++) {
						System.out.print("--");
					}
					System.out.println(subCategory.getName());
					printChildren(subCategory, newSubLevel);
				}
		);
	}
	
	@Test
	public void testListRootCategories() {
		List<Category> rootCategories = repo.findRootCategory(Sort.by("name").ascending());
		rootCategories.forEach(cat -> System.out.println(cat.getName()));
	}
	
	@Test
	public void testFindByName() {
		String name = "Computers";
		Category category = repo.findByName(name);
		assertThat(category).isNotNull();
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByAlias() {
		String name = "Electronics";
		Category category = repo.findByAlias(name);
		assertThat(category).isNotNull();
		assertThat(category.getAlias()).isEqualTo(name);
	}
}
