package vttp2022.ssf.ssf_miniproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  @Bean
  public UserDetailsService userDetailsService(){
    return new MiniProjectUserDetailsService();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }


  public DaoAuthenticationProvider authenticationProvider(){
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.authorizeRequests()
              .antMatchers("/login").permitAll()
              .antMatchers("/users/**", "/settings/**").hasAuthority("admin")
              .anyRequest().authenticated()
              .and().formLogin()
              .loginPage("/login")
                  .usernameParameter("email")
                  .permitAll()
              .and()
              .rememberMe().key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
              .and()
              .authenticationProvider(authenticationProvider())
              .logout().permitAll();

      http.headers().frameOptions().sameOrigin();
      return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
      return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
  }
}
