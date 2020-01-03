package com.example.gmall.user.service;

import com.example.gmall.user.bean.UmsMember;
import com.example.gmall.user.bean.UmsMemberReceiveAddress;
import java.util.List;

public interface UserService {
    List<UmsMember> getAllUsers();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);
}
