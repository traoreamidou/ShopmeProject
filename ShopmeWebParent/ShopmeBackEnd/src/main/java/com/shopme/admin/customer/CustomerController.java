package com.shopme.admin.customer;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;


@Controller
public class CustomerController {

	private String defaultRedirectURL = "redirect:/customers/page/1?sortField=firstName&sortDir=asc";
	
	@Autowired
	private CustomerService service;
	
	@GetMapping("/customers")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(@PagingAndSortingParam(listName = "listCustomers", moduleURL = "/customers") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		service.listByPage(pageNum, helper);
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id, 
			@PathVariable(name = "status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCustomerEnableStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		redirectAttributes.addFlashAttribute("message", "The customer ID: " + id + " has been " + status);
		return defaultRedirectURL;
	}
	
	@GetMapping("/customers/details/{id}")
	public String viewCustomerDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Customer customer = service.getCustomer(id);
			model.addAttribute("customer", customer);
			return "customers/customer_detail_modal";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			List<Country> listCountries = service.listAllCountries();			
			Customer customer = service.getCustomer(id);
			model.addAttribute("customer", customer); 
			model.addAttribute("listCountries", listCountries);
			model.addAttribute("pageTitle", "Edit customer (ID: "+ id +" )");
			return "customers/customer_form";
		} catch (CustomerNotFoundException e) {
			redirectAttributes.addFlashAttribute("exMessage", e.getMessage());
			return defaultRedirectURL;
		}		
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
		service.save(customer);
		redirectAttributes.addFlashAttribute("message", "The customer has been added successfully");
		return defaultRedirectURL;
	}
	
	@GetMapping("/customers/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully");
		} catch(CustomerNotFoundException ex) {
			redirectAttributes.addFlashAttribute("exMessage", ex.getMessage());
		} 
		return defaultRedirectURL;
	}
}
