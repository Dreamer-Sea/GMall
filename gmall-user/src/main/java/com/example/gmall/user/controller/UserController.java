package com.example.gmall.user.controller;

import com.example.gmall.user.bean.UmsMember;
import com.example.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/getAllUsers")
    @ResponseBody
    public List<UmsMember> getAllUsers(){
        List<UmsMember> umsMembers = userService.getAllUsers();
        return umsMembers;
    }

    @RequestMapping("/index")
    @ResponseBody
    public String index(){
        return "Hello World";
    }
}
