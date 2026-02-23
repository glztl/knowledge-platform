package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.common.utils.PasswordEncoderUtil;
import com.knowledge.core.dto.UserLoginDTO;
import com.knowledge.core.dto.UserRegisterDTO;
import com.knowledge.core.entity.UserEntity;
import com.knowledge.core.mapper.UserMapper;
import com.knowledge.core.service.UserService;
import com.knowledge.core.vo.LoginVO;
import com.knowledge.core.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private PasswordEncoderUtil passwordEncoderUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register (UserRegisterDTO userRegisterDTO) {
        // 检查用户名是否存在
        if (baseMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, userRegisterDTO.getUsername())) > 0) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "用户名已存在");
        }

        // 检查邮箱是否已经存在
        if (baseMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getEmail, userRegisterDTO.getEmail())) > 0) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "邮箱已存在");
        }

        // 创建用户
        UserEntity user = new UserEntity();
        user.setUsername(userRegisterDTO.getUsername());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoderUtil.encode(userRegisterDTO.getPassword()));
        user.setNickname(userRegisterDTO.getNickname() != null ? userRegisterDTO.getNickname() : userRegisterDTO.getUsername());
        user.setStatus(1);

        // 保存到数据库
        baseMapper.insert(user);

        // 返回
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }


    @Override
    public LoginVO login (UserLoginDTO userLoginDTO) {
        // 查询用户 (用户名、邮箱登陆)
        UserEntity user = baseMapper.selectByUsername(userLoginDTO.getAccount());
        if (user == null) {
            user = baseMapper.selectByEmail(userLoginDTO.getAccount());
        }

        // 用户不存在
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 密码验证
        if (!passwordEncoderUtil.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "密码错误");
        }

        // 用户状态检查
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "用户已被禁用");
        }

        // 生成Token
        String token = "mock_token_" + user.getId();

        LoginVO vo = new LoginVO();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        vo.setUser(userVO);
        vo.setToken(token);
        vo.setExpire(86400000L); // 24小时
        return vo;
    }

    @Override
    public UserVO getById(Long userId) {
        UserEntity user = baseMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    public UserEntity getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public UserEntity getByEmail(String email) {
        return baseMapper.selectByEmail(email);
    }
}
