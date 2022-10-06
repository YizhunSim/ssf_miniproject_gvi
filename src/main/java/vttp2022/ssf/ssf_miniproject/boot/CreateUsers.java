package vttp2022.ssf.ssf_miniproject.boot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import vttp2022.ssf.ssf_miniproject.FileUploadUtil;
import vttp2022.ssf.ssf_miniproject.models.Role;
import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.repositories.RoleRepository;
import vttp2022.ssf.ssf_miniproject.repositories.UserRepository;

@Component
@Order(2)
@Slf4j
public class CreateUsers implements CommandLineRunner{
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) throws Exception {
    System.out.println(">>> Hello from the CreateUsers CommandLineRunner...");
    if (userRepository.count() == 0){
      Role admin = roleRepository.findFirstByName("admin");
      Role customer = roleRepository.findFirstByName("customer");

      try{
        // create a Jackson object mapper
        ObjectMapper mapper = new ObjectMapper();
        // create a type definition to convert the array of JSON into a List of Users
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
        };
        // make the JSON data available as an input stream
        InputStream inputStream = getClass().getResourceAsStream("/data/users/MOCK_DATA_USERS.json");
        // convert the JSON to objects
        List<User> users = mapper.readValue(inputStream, typeReference);

        users.stream().forEach((user) -> {
          user.setPassword(passwordEncoder.encode(user.getPassword()));
          user.addRole(customer);
          userRepository.save(user);
        });
        log.info(">>>> " + users.size() + " Users Saved!");
      } catch (IOException e) {
        log.info(">>>> Unable to import users: " + e.getMessage());
      }


      User adminUser = new User();
      adminUser.setFirstName("Adminus");
      adminUser.setLastName("Admistradore");
      adminUser.setEmail("admin@example.com");
      adminUser.setPassword(passwordEncoder.encode("Reindeer Flotilla"));//
      adminUser.addRole(admin);
      adminUser.setPhotos("default-user.png");
      adminUser.setEnabled(true);

      userRepository.save(adminUser);

      flushCleanUserPhotosFolders("user-photos");
      createUserPhotosFolders((List<User>) userRepository.findAll());
      log.info(">>>> Loaded User Data and Created users...");
    }
  }

  private void flushCleanUserPhotosFolders(String dir){
    System.out.println("flushCleanUserPhotosFolders: " + dir);
    FileUploadUtil.cleanDir(dir);
  }

  private void createUserPhotosFolders(List<User> users){
    for (User u : users){
      String userId = u.getId();
      System.out.println("User Id: " + userId);
      String uploadDir = "user-photos/" + userId;

      Path path = Paths.get(uploadDir);
      try {
        Files.createDirectory(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.out.println("Directory: " + uploadDir + " created Successfully!");
      // FileUploadUtil.copyFileToDestination(System.getProperty("user.dir")+"/src/main/resources/static/images/default-user.png", path);
    }
  }
}

