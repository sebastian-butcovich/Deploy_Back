package com.example.tryJwt.demo.Servicies;

import com.example.tryJwt.demo.Modelo.Users;
import com.example.tryJwt.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {
    @Autowired
    private UserRepository userRepository;
    public List<Users> listarUsuarios ()
    {
        return userRepository.findAll();
    }
}
