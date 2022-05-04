package com.shopme.admin.user.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {

	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	
	@Autowired
	UserService service;
	
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		service.listByPage(pageNum, helper);		
		
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create new user");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if(!multipartFile.isEmpty()) {
			String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(filename);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, filename, multipartFile);
		} else {
			if(user.getPhotos().isEmpty()) 
				user.setPhotos(null);
			service.save(user);
		}		
		redirectAttributes.addFlashAttribute("message", "The user has been added successfully");
		return getRedirectURLtoAffectedUser(user);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		String firstPartEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles(); 
			model.addAttribute("user", user); 
			model.addAttribute("pageTitle", "Edit user (ID: "+ id +" )");
			model.addAttribute("listRoles", listRoles);
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("exMessage", e.getMessage());
			return defaultRedirectURL;
		}		
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
		} catch(UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("exMessage", ex.getMessage());
		} 
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id, 
			@PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateUserEnableStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The user ID: " + id + " has been " + status);
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listUsers();
		UserCsvExporter exporter = new UserCsvExporter(); 
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listUsers();
		UserExcelExporter exporter = new UserExcelExporter(); 
		exporter.export(listUsers, response);
	}
	
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listUsers();
		UserPdfExporter exporter = new UserPdfExporter(); 
		exporter.export(listUsers, response);
	}
}
