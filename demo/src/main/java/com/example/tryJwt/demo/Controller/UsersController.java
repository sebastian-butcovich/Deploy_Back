package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Servicies.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
public class UsersController {
    @Autowired
    private UsersService userService;
    @GetMapping
    public List<Users> listarUsuarios()
    {
        return userService.listarUsuarios();
    }
}