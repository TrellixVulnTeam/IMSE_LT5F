package com.example.demo.security;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.stereotype.*;

/*
https://stackoverflow.com/questions/46124028/spring-boot-security-rest-basic-authentication-from-database //TODO citate
 */
@Component
public class AuthProvider implements AuthenticationProvider {

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {

    UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken("", "", new ArrayList<>());

    String username = authentication.getName();
    String password = authentication.getCredentials().toString();

    User user = getAuthenticatedUser(username);

    if (user != null) {
      String userRole = user.getRole().toString();

      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(userRole));

      if(password.equals(user.getPassword())){
        result = new UsernamePasswordAuthenticationToken(username, password, authorities);
      }
    }

    return result;
  }

  private User getAuthenticatedUser(String username) {
    User result = null;

    Admin admin = adminRepository.findAdminByUsername(username);
    Customer customer = customerRepository.findCustomerByUsername(username);

    if(admin != null){
      result = admin;
    }
    else if(customer != null){
      result = customer;
    }

    return result;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
