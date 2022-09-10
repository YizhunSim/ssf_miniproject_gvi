package vttp2022.ssf.ssf_miniproject.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
  @Autowired
  private UserRepository userRepository;

  // @GetMapping
  // public Iterable<User> all(){
  //   return userRepository.findAll();
  // }

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
