package com.shopme.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;


@Controller
public class ProductController {
	
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
	
	@Autowired
	private ProductService prodService;
	
	@Autowired
	private BrandService branService;
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model,
			Integer categoryId) {
		prodService.listByPage(pageNum, helper, categoryId);
		
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("listCategories", listCategories);
		
		return "products/products";	
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		List<Brand> listBrands = branService.listAll();
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");	
		model.addAttribute("numberOfExistingExtraImage", 0);
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes redirectAttributes, 
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart, 
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) throws IOException {
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				prodService.saveProductPrice(product);
				redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");			
				return defaultRedirectURL;
			}
		}
		
		ProductSavedHeader.setMainImageName(mainImageMultipart, product);
		ProductSavedHeader.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSavedHeader.setNewExtraImageNames(extraImageMultiparts, product);
		ProductSavedHeader.setProductDetails(detailIDs, detailNames, detailValues, product);
		Product savedProduct = prodService.save(product);
		ProductSavedHeader.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		ProductSavedHeader.deleteExtraImageWhichIsRemovedOnForm(product);
		redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		prodService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			prodService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model, 
							RedirectAttributes redirectAttributes, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Product product = prodService.get(id);
			List<Brand> listBrands = branService.listAll();
			Integer numberOfExistingExtraImage = product.getImages().size();
			boolean isReadOnlyForSalesperson = false;			
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + " )");
			model.addAttribute("numberOfExistingExtraImage", numberOfExistingExtraImage);
		
			return "products/product_form";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/products/details/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Product product = prodService.get(id);
			model.addAttribute("product", product);
			return "products/product_detail_modal";
		} catch (ProductNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
}
