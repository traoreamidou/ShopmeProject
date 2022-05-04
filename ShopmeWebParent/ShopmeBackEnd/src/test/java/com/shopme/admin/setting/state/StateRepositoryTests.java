package com.shopme.admin.setting.state;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class StateRepositoryTests {

	@Autowired
	private StateRepository repo;
	
	@Autowired
	private EntityManager manager;
	
	@Test
	public void testCreateStateInIvoryCoast() {
		Integer id = 2;
		Country country = manager.find(Country.class, id);
		List<State> listStates =  List.of(
			new State("Agneby", country),
			new State("Denguele", country),
			new State("Dix Huit Montagnes", country),
			new State("Haut Sassandra", country),
			new State("Lacs", country),
			new State("Moyen Cavally", country),
			new State("Marahoue", country),
			new State("Moyen Comoe", country),
			new State("N'zi Comoe", country),
			new State("Worodougou", country), 
			new State("Zanzan", country),
			new State("Vallee Du Bandama", country)
		);
		Iterable<State> savedStates = repo.saveAll(listStates);
		assertThat(savedStates).isNotNull();
		savedStates.forEach(state -> assertThat(state.getId()).isGreaterThan(0));
	}
	
	@Test
	public void testListStatesByCategory() {
		Integer countryId = 2;
		Country country = manager.find(Country.class, countryId);
		List<State> listStates = repo.findByCountryOrderByNameAsc(country);
		listStates.forEach(System.out::println);
		assertThat(listStates.size()).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateState() {
		Integer id = 4;
		String stateName = "Savanes";
		State savedState = repo.findById(id).get();
		savedState.setName(stateName);
		State updatedState = repo.save(savedState);
		assertThat(updatedState.getName()).isEqualTo(stateName);
	}
	
	@Test
	public void testGetState() {
		Optional<State> findById = repo.findById(1);
		assertThat(findById.isPresent());
	}
	
	@Test
	public void testDeleteState() {
		Integer stateId = 1;
		repo.deleteById(stateId);
		Optional<State> findById = repo.findById(stateId);
		assertThat(findById.isEmpty());
	}
}
