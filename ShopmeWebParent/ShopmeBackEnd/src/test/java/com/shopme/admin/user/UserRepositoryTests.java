package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUSerWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User user = new User("traoreamidou6729@gmail.com", "Th001994", "Hamidou", "Traore");
		user.addRole(roleAdmin);
		User savedUser = repo.save(user);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUSerWithTwoRoles() {
		User userRavi = new User("ravi@gmail.com", "ravi2022", "Ravi", "Kumar");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		userRavi.addRole(roleEditor);
		userRavi.addRole(roleAssistant);
		User savedUser = repo.save(userRavi);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		repo.findAll().forEach(System.out::println);
	}
	
	@Test
	public void testGetUserById() {
		User user = repo.findById(1).get();
		System.out.println(user);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User user = repo.findById(1).get();
		user.setEnabled(true);
		user.setEmail("amidou6729@gmail.com");
		repo.save(user);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User user = repo.findById(2).get();
		Role roleEditor = new Role(3);
		Role roleSalesperson = new Role(2);
		user.getRoles().remove(roleEditor);
		user.addRole(roleSalesperson);
		repo.save(user);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 1;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		User user = repo.getUserByEmail("ravi@gmail.com");
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testUnableUser() {
		Integer id = 1;
		repo.updateUnabledStatus(id, true);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(user -> System.out.println(user));
		assertThat(listUser.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "yahoo";
		int pageNum = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		List<User> listUser = page.getContent();
		listUser.forEach(System.out::println);
		assertThat(listUser.size()).isGreaterThan(0);		
	}
}
