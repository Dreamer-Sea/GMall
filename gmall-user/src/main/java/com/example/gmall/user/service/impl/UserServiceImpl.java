package com.example.gmall.user.service.impl;

import com.example.gmall.user.bean.UmsMember;
import com.example.gmall.user.mapper.UserMapper;
import com.example.gmall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public List<UmsMember> getAllUsers() {
        List<UmsMember> umsMemberList = userMapper.selectAllUsers();
        return umsMemberList;
    }
}
