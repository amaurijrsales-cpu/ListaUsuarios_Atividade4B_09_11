
package com.lunaltas.ListaUsuarios.controllers;

import com.lunaltas.ListaUsuarios.dao.UserDAO;
import com.lunaltas.ListaUsuarios.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/index")
    public String index(ModelMap model) {
        List<User> listUser = userDAO.findAll();

        model.addAttribute("users", listUser);
        model.addAttribute("size", listUser.size());

        return "users/index";
    }

    @GetMapping("/new")
    public String userNew(ModelMap model) {
        model.addAttribute("user", new User());

        return "users/new";
    }

    @PostMapping("/create")
    public String userCreate(@ModelAttribute User user) {
        userDAO.save(user);

        return "redirect:/users/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, ModelMap model) {
        User user = userDAO.findById(id);

        if (user == null) {
            return "redirect:/users/index";
        }

        model.addAttribute("user", user);

        return "users/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute User user) {
        userDAO.update(user);

        return "redirect:/users/index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userDAO.delete(id);

        return "redirect:/users/index";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable Long id, ModelMap model) {
        User user = userDAO.findById(id);

        if (user == null) {
            return "redirect:/users/index";
        }

        model.addAttribute("user", user);

        return "users/show";
    }
}
