package com.example.gmall.service;

import com.example.gmall.bean.UmsMember;
import com.example.gmall.bean.UmsMemberReceiveAddress;
import java.util.List;

public interface UserService {
    List<UmsMember> getAllUsers();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemberId(String memberId);

    UmsMember login(UmsMember umsMember);

    void addUserToken(String token, String memberId);
}
