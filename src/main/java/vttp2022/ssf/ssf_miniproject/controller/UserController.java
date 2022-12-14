package vttp2022.ssf.ssf_miniproject.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vttp2022.ssf.ssf_miniproject.FileUploadUtil;
import vttp2022.ssf.ssf_miniproject.export.UserCSVExporter;
import vttp2022.ssf.ssf_miniproject.export.UserExcelExporter;
import vttp2022.ssf.ssf_miniproject.export.UserPDFExporter;
import vttp2022.ssf.ssf_miniproject.models.Role;
import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.services.UserNotFoundException;
import vttp2022.ssf.ssf_miniproject.services.UserService;

@Controller
public class UserController {

  @Autowired
  private UserService userService;

  @GetMapping("/users")
    public String listAll(Model model){
         return listByPage(1, model, "firstName", "asc", null);

    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField, @Param("sortOrder") String sortOrder, @Param("keyword") String keyword){
        System.out.println("Sort Field: " + sortField);
        System.out.println("Sort Order: " + sortOrder);

        Page<User> page = userService.listByPage(pageNum, sortField, sortOrder, keyword);
        List<User> listUsers = page.getContent();

        System.out.println("PageNum = " + pageNum);
        System.out.println("Total elements = " + page.getTotalElements());
        System.out.println("Total pages = " + page.getTotalPages());

        long startCount = (pageNum - 1) * userService.USER_PER_PAGE + 1;
        long endCount = startCount + userService.USER_PER_PAGE - 1;
        if (endCount > page.getTotalElements()){
            endCount = page.getTotalElements();
        }

        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);

        return "users/users";

    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRoles = userService.listRoles();

        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
       System.out.println(multipartFile.getOriginalFilename());
        if (user.isEnabled()){
            user.setEnabled(true);
        }else{
            user.setEnabled(false);
        }
        if (!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);

            User savedUser = userService.save(user);

            String uploadDir = "user-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else{
            if (user.getPhotos().isEmpty()){
                user.setPhotos(null);
            }
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return getRedirectURLtoAffectedUser(user);
    }

    private String getRedirectURLtoAffectedUser(User user){
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortOrder=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") String id, RedirectAttributes redirectAttributes, Model model){
       try{
        User user = userService.get(id);
        List<Role> listRoles = userService.listRoles();
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "EditUser (ID: " + id + ")");
        model.addAttribute("listRoles", listRoles);
        return "users/user_form";
       }catch(UserNotFoundException ex){
           redirectAttributes.addFlashAttribute("message", ex.getMessage());
           return "redirect:/users";
       }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") String id, RedirectAttributes redirectAttributes){
        try{

            userService.delete(id);
            String uploadDir = "user-photos/" + userService.get(id).getId();
            System.out.println("deleteUser Clean up directory: " + uploadDir);
            FileUploadUtil.cleanDir(uploadDir);

            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        }catch(UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") String id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        userService.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The User ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserCSVExporter exporter = new UserCSVExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserPDFExporter exporter = new UserPDFExporter();
        exporter.export(listUsers, response);
    }
  
}
