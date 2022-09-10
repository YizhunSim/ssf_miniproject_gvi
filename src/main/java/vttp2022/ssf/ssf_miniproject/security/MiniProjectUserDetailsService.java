package vttp2022.ssf.ssf_miniproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.repositories.UserRepository;

public class MiniProjectUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepo;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findFirstByEmail(email);
    if (user != null){
      return new MiniProjectUserDetails(user);
    }
    throw new UsernameNotFoundException("Could not find user with email: " + email);
  }

}
