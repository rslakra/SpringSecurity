package com.rslaka.springsecurity.oauth2.contoller;

import java.util.List;

import com.rslaka.springsecurity.oauth2.model.UserInfo;
import com.rslaka.springsecurity.oauth2.service.UserInfoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserInfoService userInfoService;

    public UserController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllUser(@RequestHeader HttpHeaders requestHeader) {
        List<UserInfo> userInfos = userInfoService.getAllActiveUserInfo();
        if (userInfos == null || userInfos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(userInfos);
    }

    @PostMapping("/user")
    public ResponseEntity<UserInfo> addUser(@RequestBody UserInfo userRecord) {
        UserInfo savedUser = userInfoService.addUser(userRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserInfo> updateUser(@RequestBody UserInfo userRecord, @PathVariable Integer id) {
        UserInfo updatedUser = userInfoService.updateUser(id, userRecord);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/changePassword/{id}")
    public ResponseEntity<UserInfo> updateUserPassword(@RequestBody UserInfo userRecord, @PathVariable Integer id) {
        UserInfo updatedUser = userInfoService.updatePassword(id, userRecord);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/changeRole/{id}")
    public ResponseEntity<UserInfo> updateUserRole(@RequestBody UserInfo userRecord, @PathVariable Integer id) {
        UserInfo updatedUser = userInfoService.updateRole(id, userRecord);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userInfoService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable Integer id) {
        UserInfo userInfo = userInfoService.getUserInfoById(id);
        if (userInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userInfo);
    }
}
