package com.shopme;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.carouselImageSlider.CarouselImagesServices;
import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.carouselImages.CarouselImages;

@Controller
public class MainController {
	
	@Autowired CategoryService categoryService;
	
	@Autowired CarouselImagesServices carouselImgService;

	@GetMapping("") 
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listParentsCategories();
		//List<CarouselImages> listCarouselImages = carouselImgService.findAll();
		//model.addAttribute("listCarouselImages", listCarouselImages);
		model.addAttribute("listCategories", listCategories);
		return "index";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/"; 
	}	
}
