package vttp2022.ssf.ssf_miniproject.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import vttp2022.ssf.ssf_miniproject.models.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, String>{
    Role findFirstByName(String role);
}
