package vttp2022.ssf.ssf_miniproject.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vttp2022.ssf.ssf_miniproject.models.Role;
import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.repositories.RoleRepository;
import vttp2022.ssf.ssf_miniproject.repositories.UserRepository;

@Service
public class UserService {
  public static final int USER_PER_PAGE = 5;

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private RoleRepository roleRepo;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public User getByEmail(String email){
    return userRepo.findFirstByEmail(email);
  }

  public List<User> listAll(){
    return (List<User>) userRepo.findAll();
  }

   public Page<User> listByPage(int pageNum, String sortField, String sortOrder, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USER_PER_PAGE, sort);
        if (keyword != null){
            // return userRepo.findAll(keyword, pageable);
        }
        return userRepo.findAll(pageable);
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRepo.findAll();
    }

    public User save(User user) {
        boolean isUpdatingExistingUser = user.getId() != null;

        if (isUpdatingExistingUser){
            User existingUser = userRepo.findById(user.getId()).get();

            if (user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            }else{
                encodePassword(user);
            }
        }else{
            encodePassword(user);
        }
        return userRepo.save(user);
    }

    public User updateAccount(User userInForm){
        User userInDB = userRepo.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }
        // if (userInForm.getPhotos() != null){
        //     userInDB.setPhotos(userInForm.getPhotos());
        // }

        // userInDB.setFirstName(userInForm.getFirstName());
        // userInDB.setLastName(userInForm.getLastName());

        return userRepo.save(userInDB);

    }

    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email){
//        System.out.println("UserService: isEmailUnique - id: " + id + " email: " + email);
        User userByEmail = userRepo.findFirstByEmail(email);
//        System.out.println("UserService: isEmailUnique - userByEmail: " + userByEmail);
        if (userByEmail == null){
            return true;
        }
        boolean isCreatingNew = (id == null);

        if (isCreatingNew){
            if (userByEmail != null) {
                return false;
            }
        }else{
            if (Integer.parseInt(userByEmail.getId()) != id){
                return false;
            }
        }
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try{
            return userRepo.findById(id.toString()).get();
        } catch(NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    public String getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
         String currentUserName = authentication.getName();
         return currentUserName;
        }else{
            return null;
        }
    }

    // public void delete (Integer id) throws UserNotFoundException {
    //     Long countById = userRepo.countById(id);

    //     if (countById == null || countById == 0){
    //         throw new UserNotFoundException("Could not find any user with ID " + id);
    //     }

    //     userRepo.deleteById(id);
    // }


}
