package vttp2022.ssf.ssf_miniproject.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.repositories.UserRepository;
import vttp2022.ssf.ssf_miniproject.services.UserService;

@RestController
@RequestMapping
public class UserRestController {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @PostMapping("/users/check_email")
  public String checkDuplicateEmail(@Param("id") String id, @Param("email") String email){
      return userService.isEmailUnique(id, email) ? "OK" : "Duplicated";

  }

  @GetMapping
  public Iterable<User> all(@RequestParam(defaultValue = "") String email){
    if (email.isEmpty()){
      return userRepository.findAll();
    } else {
      Optional<User> user = Optional.ofNullable(userRepository.findFirstByEmail(email));
      return user.isPresent() ? List.of(user.get()) : Collections.emptyList();
    }
  }

}
