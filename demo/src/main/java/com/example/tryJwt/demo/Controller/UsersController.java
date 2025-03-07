package com.example.tryJwt.demo.Controller;

import com.example.tryJwt.demo.FileRequest.RegisterRequest;
import com.example.tryJwt.demo.FileRequest.UpdateUsers;
import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Servicies.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = "*")
public class UsersController {
    @Autowired
    private UsersService userService;
    @GetMapping
    public List<Users> listarUsuarios()
    {
        return userService.listarUsuarios();
    }
    @CrossOrigin(origins = "*")
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UpdateUsers updateUsers, @RequestParam Map<String,String> param)
    {
       return userService.updateUser(updateUsers,param);
    }
    @GetMapping("/whoami")
    public RegisterRequest quienSoy(@RequestParam String token)
    {
        return userService.quienSoy(token);
    }
}