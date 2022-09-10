package vttp2022.ssf.ssf_miniproject.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import vttp2022.ssf.ssf_miniproject.models.Role;
import vttp2022.ssf.ssf_miniproject.repositories.RoleRepository;

@Component
@Order(1)
@Slf4j
public class CreateRoles implements CommandLineRunner {
  @Autowired
  private RoleRepository roleRepository;

 @Override
 public void run(String... args) throws Exception {
   System.out.println(">>> Hello from the CreateRoles CommandLineRunner...");

    if (roleRepository.count() == 0) {
      System.out.println(">>> Role repository count == 0");
      Role adminRole = Role.builder().name("admin").build();
      Role customerRole = Role.builder().name("customer").build();
      roleRepository.save(adminRole);
      roleRepository.save(customerRole);
      log.info(">>>> Created admin and customer roles...");
    }
 }
}
