package com.shopme.admin.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategory(Sort sort);
	
	public Long countById(Integer id);
	
	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public Page<Category> findRootCategory(Pageable pageable);
	

	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	@Query("UPDATE Category c set c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateUnabledStatus(Integer id, boolean enabled);
	
}
