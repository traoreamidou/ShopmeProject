package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;


@Service
@Transactional
public class CustomerService {
	
	public static final int CUSTOMERS_PER_PAGE = 4;
	
	@Autowired
	private CustomerRepository customerRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
	}
	
	public List<Customer> listAll() {
		return (List<Customer>) customerRepo.findAll();
	}
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public void updateCustomerEnableStatus(Integer id, boolean enabled) {
		customerRepo.updateCustomerUnabledStatus(id, enabled);
	}
	
	public Customer getCustomer(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException e) {
			throw new CustomerNotFoundException("Could not find any customer with ID " + id);
		}
	}
	
	public void save(Customer customerInForm) {
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		
		if (!customerInForm.getPassword().isEmpty()) {
			String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(encodedPassword);			
		} else {
			customerInForm.setPassword(customerInDB.getPassword());
		}		
		
		customerInForm.setEnabled(customerInDB.isEnabled());
		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());
		
		customerRepo.save(customerInForm);	
	}
	
	public void delete(Integer id) throws CustomerNotFoundException{
		Long countById = customerRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any customer with ID: " + id);
		}	
		customerRepo.deleteById(id);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepo.findByEmail(email);

		if (existCustomer != null && existCustomer.getId() != id) {
			// found another customer having the same email
			return false;
		}
		
		return true;
	}
}
