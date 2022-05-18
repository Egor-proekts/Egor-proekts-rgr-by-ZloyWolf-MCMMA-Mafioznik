package org.prod.tgk.controllers;

import org.prod.tgk.entitys.User;
import org.prod.tgk.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/reg")
    public String reg(Model model) {
        return "reg";
    }

    @GetMapping("/activation")
    public String activation(@RequestParam("code") String code, Model model) {
        if (authService.activate(code)) {
            model.addAttribute("correctCheck", true);
        } else {
            model.addAttribute("correctCheck", false);
        }

        return "activation";
    }

    @PostMapping("/reg")
    public String regAuth(@RequestParam("email") String email,
                          @RequestParam("name") String name,
                          @RequestParam("surname") String surname,
                          @RequestParam("patronymic") String patronymic,
                          @RequestParam("birthday")
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday,
                          @RequestParam("password") String password,
                          @RequestParam("repeatPassword") String repeatPassword,
                          Model model) {
        List<User> allRegistredUsers = authService.getAllUsers();
        boolean errorCheck = false;

        if (allRegistredUsers.stream().anyMatch(e -> e.getEmail().equals(email))) {
            model.addAttribute("error1", "Этот email используется в другом аккаунте");
            errorCheck = true;
        }

        Date nowDate = new Date();
        if (birthday.after(nowDate)) {
            model.addAttribute("error2", "Недоступный день рождения");
            errorCheck = true;
        }

        if (!password.equals(repeatPassword)) {
            model.addAttribute("error3", "Пароли не совпадают");
            errorCheck = true;
        }

        if (errorCheck) {
            return "reg";
        } else {
            authService.saveUser(email, name, surname, patronymic, birthday, password);
            model.addAttribute("welcomeActivate", "Почти готово, для завершения пройдите по ссылке из письма, отправленного на указанный Вами адрес.");
            return "lk";
        }
    }
}
