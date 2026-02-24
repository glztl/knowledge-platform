package com.knowledge.core.service;

import com.knowledge.core.dto.UserLoginDTO;
import com.knowledge.core.dto.UserRegisterDTO;
import com.knowledge.core.entity.UserEntity;
import com.knowledge.core.vo.LoginVO;
import com.knowledge.core.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务
 * @author: nuts_tian
 */
public interface UserService extends UserDetailsService {

    UserVO register(UserRegisterDTO userRegisterDTO);


    LoginVO login(UserLoginDTO userLoginDTO);


    UserVO getById(Long userId);


    UserEntity getByUsername(String username);


    UserEntity getByEmail(String email);
}
