package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String showUsers(Model model) {
        model.addAttribute("user", userService.getAllUsers());
        return "/admin";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("role", roleService.getAllRoles());
        return "/admins/add";
    }

    @PostMapping()
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                          @RequestParam("role") ArrayList<Integer> role) {
        if (bindingResult.hasErrors()) {
            return "/admins/add";
        }
        user.setRoles(roleService.getRolesById(role));
        userService.addUser(user);
        return "redirect:/admin";
    }


    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "/admins/edit";
    }

    @PostMapping("/{id}")
    public String updateUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                         @RequestParam("role") ArrayList<Integer> role) {
        if (bindingResult.hasErrors()) {
            return "/admins/edit";
        }
        user.setRoles(roleService.getRolesById(role));
        userService.updateUser(user);

        return "redirect:/admin";
    }
}
