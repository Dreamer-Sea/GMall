package com.example.gmall.user.mapper;

import com.example.gmall.user.bean.UmsMember;

import java.util.List;

public interface UserMapper {
    List<UmsMember> selectAllUsers();
}
