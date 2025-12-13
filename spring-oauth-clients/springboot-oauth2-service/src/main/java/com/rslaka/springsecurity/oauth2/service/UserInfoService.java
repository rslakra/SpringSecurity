package com.rslaka.springsecurity.oauth2.service;

import java.util.List;
import java.util.Optional;

import com.rslaka.springsecurity.oauth2.model.UserInfo;
import com.rslaka.springsecurity.oauth2.repository.UserInfoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInfoService(UserInfoRepository userInfoRepository, PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserInfo getUserInfoByUserName(String userName) {
        short enabled = 1;
        return userInfoRepository.findByUserNameAndEnabled(userName, enabled);
    }

    public List<UserInfo> getAllActiveUserInfo() {
        return userInfoRepository.findAllByEnabled((short) 1);
    }

    public UserInfo getUserInfoById(Integer id) {
        return userInfoRepository.findById(id).orElse(null);
    }

    public UserInfo addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        return userInfoRepository.save(userInfo);
    }

    public UserInfo updateUser(Integer id, UserInfo userRecord) {
        Optional<UserInfo> optional = userInfoRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
		
        UserInfo userInfo = optional.get();
        userInfo.setUserName(userRecord.getUserName());
        userInfo.setPassword(userRecord.getPassword());
        userInfo.setRole(userRecord.getRole());
        userInfo.setEnabled(userRecord.getEnabled());
        return userInfoRepository.save(userInfo);
    }

    public void deleteUser(Integer id) {
        userInfoRepository.deleteById(id);
    }

    public UserInfo updatePassword(Integer id, UserInfo userRecord) {
        Optional<UserInfo> optional = userInfoRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        UserInfo userInfo = optional.get();
        userInfo.setPassword(passwordEncoder.encode(userRecord.getPassword()));
        return userInfoRepository.save(userInfo);
    }

    public UserInfo updateRole(Integer id, UserInfo userRecord) {
        Optional<UserInfo> optional = userInfoRepository.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        UserInfo userInfo = optional.get();
        userInfo.setRole(userRecord.getRole());
        return userInfoRepository.save(userInfo);
    }
}
