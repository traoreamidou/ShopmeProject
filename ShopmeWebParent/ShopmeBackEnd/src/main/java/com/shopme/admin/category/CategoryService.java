package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;
import com.shopme.common.exception.CategoryNotFoundException;

@Service
@Transactional
public class CategoryService {

	public static final int ROOT_CATEGORIES_PER_PAGE = 4;
	
	@Autowired
	CategoryRepository repo;
	
	public List<Category> listCategories() {
		return (List<Category>) repo.findAll(Sort.by("id").ascending());
	}
	
	
	public List<Category> listByPage(CategoryPageInfo pageInfo, int pageNum, String sortDir, String keyword) {
		Sort sort = Sort.by("name");
		if(sortDir.equals("asc")) { 
			sort = sort.ascending();
		} else if(sortDir.equals("desc")) {
			sort = sort.descending();
		} 
		Pageable pageable = PageRequest.of(pageNum - 1, ROOT_CATEGORIES_PER_PAGE, sort);
		Page<Category> pageCategories = null;
		if(keyword != null && !keyword.isEmpty()) {
			pageCategories =  repo.search(keyword, pageable);	
		} else {
			pageCategories =  repo.findRootCategory(pageable);	
		}
		List<Category> rootCategories = pageCategories.getContent();
		pageInfo.setTotalElements(pageCategories.getTotalElements());
		pageInfo.setTotalPages(pageCategories.getTotalPages());
		if(keyword != null && !keyword.isEmpty()) {
			List<Category> searchResult = pageCategories.getContent();
			for (Category category : searchResult) {
				category.setHasChildren(category.getChildren().size() > 0);
			}
			return searchResult;
		} else {
			return listHierarchicalCategories(rootCategories, sortDir);
		}
		
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategory, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		rootCategory.forEach(cat -> {
			hierarchicalCategories.add(Category.copyFull(cat));
			Set<Category> children = sortSubCategories(cat.getChildren(), sortDir);
			children.forEach(child -> {
				String name = "--" + child.getName();
				hierarchicalCategories.add(Category.copyFull(child, name));
				listSubHierarchicalCategories(hierarchicalCategories, child, 1, sortDir);
			});
		});		
		return hierarchicalCategories;
	}

	public void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		children.forEach(subCategory -> {
			String name = "";
			for(int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			listSubHierarchicalCategories(hierarchicalCategories, subCategory, newSubLevel, sortDir);
		});
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesUsedInForm = new ArrayList<>();
		Iterable<Category> categoresInDB = repo.findRootCategory(Sort.by("name").ascending());
		categoresInDB.forEach(
				category -> {
					if(category.getParent() == null) {
						categoriesUsedInForm.add(Category.copyIdAndName(category));
						Set<Category> children = sortSubCategories(category.getChildren());
						children.forEach(
								subCategory -> {
									String name = "--" + subCategory.getName();
									categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
									listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, 1);
								}
						);
					}
				}
		);
		return categoriesUsedInForm;
 	}
	
	public void listSubCategoriesUsedInForm(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());
		children.forEach(
				subCategory -> {
					String name = "";
					for(int i = 0; i < newSubLevel; i++) {
						name += "--";
					}
					name += subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listSubCategoriesUsedInForm(categoriesUsedInForm, subCategory, newSubLevel);
				}
		);
	}
	
	public Category serviceSaveCategory(Category category) {
		Category parent = category.getParent();
		if(parent != null) {
			String allParentIds = (parent.getAllParentIDs() == null)? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		return repo.save(category);
	}
	
	/*
	 * This method return a specific Category based on its Id
	 * And It will throw CategoryNotFoundException if there is no
	 * any category that matches the provided Id
	 */
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CategoryNotFoundException("Could not find any category with ID " + id);
		}
	}
	
	/**
	 * This method check the uniqueness of a category in category list
	 * @return OK if the category is unique the database
	 */
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);		
		Category categoryByName = repo.findByName(name);
		if(isCreatingNew) {
			if(categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if(categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if(categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			Category categoryByAlias = repo.findByAlias(alias);
			if(categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children) {
		return sortSubCategories(children, "asc");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category cat1, Category cat2) {
				if(sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}			
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}
	
	public void updateCategoryEnableStatus(Integer id, boolean enabled) {
		repo.updateUnabledStatus(id, enabled);
	}
	
	/**
	 * This method allows to delete a category in the list
	 * @param id the id number of the category to be deleted
	 * @throws CategoryNotFoundException if the category ID doesn't exist in the database
	 */
	public void delete(Integer id) throws CategoryNotFoundException{
		Long countById = repo.countById(id);
		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID: " + id);
		}	
		repo.deleteById(id);
	}
	
}
