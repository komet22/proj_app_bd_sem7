package app.model.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import app.model.Customer;

@Transactional
@Repository
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

}