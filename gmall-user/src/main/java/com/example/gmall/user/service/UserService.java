package com.example.gmall.user.service;

import com.example.gmall.user.bean.UmsMember;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    List<UmsMember> getAllUsers();
}
