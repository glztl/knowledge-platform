package com.knowledge.core.service;

import com.knowledge.core.dto.UserLoginDTO;
import com.knowledge.core.dto.UserRegisterDTO;
import com.knowledge.core.entity.UserEntity;
import com.knowledge.core.vo.LoginVO;
import com.knowledge.core.vo.UserVO;

/**
 * 用户服务
 */
public interface UserService {

    UserVO register(UserRegisterDTO userRegisterDTO);


    LoginVO login(UserLoginDTO userLoginDTO);


    UserVO getById(Long userId);


    UserEntity getByUsername(String username);


    UserEntity getByEmail(String email);
}
