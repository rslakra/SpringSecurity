package com.rslaka.springsecurity.oauth2.repository;

import java.util.List;
import java.util.Optional;

import com.rslaka.springsecurity.oauth2.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByUserNameAndEnabled(String userName, short enabled);

    List<UserInfo> findAllByEnabled(short enabled);

    Optional<UserInfo> findById(Integer id);
}
