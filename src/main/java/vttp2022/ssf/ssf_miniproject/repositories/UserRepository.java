package vttp2022.ssf.ssf_miniproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import vttp2022.ssf.ssf_miniproject.models.User;

public interface UserRepository extends PagingAndSortingRepository<User, String>{
  User findFirstByEmail(String email);
  
}
