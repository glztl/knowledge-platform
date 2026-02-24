package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.common.utils.JwtUtil;
import com.knowledge.core.common.utils.PasswordEncoderUtil;
import com.knowledge.core.dto.UserLoginDTO;
import com.knowledge.core.dto.UserRegisterDTO;
import com.knowledge.core.entity.UserEntity;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.mapper.UserMapper;
import com.knowledge.core.service.UserService;
import com.knowledge.core.vo.LoginVO;
import com.knowledge.core.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    @Resource
    private PasswordEncoderUtil passwordEncoderUtil;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(UserRegisterDTO dto) {
        // 1. 检查用户名是否已存在
        if (baseMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, dto.getUsername())) > 0) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (baseMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getEmail, dto.getEmail())) > 0) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "邮箱已存在");
        }

        // 3. 创建用户
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoderUtil.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        // 正常状态
        user.setStatus(1);

        // 4. 保存到数据库
        baseMapper.insert(user);

        // 5. 转换为VO返回
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }


    @Override
    public LoginVO login(UserLoginDTO dto) {
        // 1. 查询用户（支持用户名或邮箱登录）
        UserEntity user = baseMapper.selectByUsername(dto.getAccount());
        if (user == null) {
            user = baseMapper.selectByEmail(dto.getAccount());
        }

        // 2. 用户不存在
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 3. 密码验证
        if (!passwordEncoderUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "密码错误");
        }

        // 4. 用户状态检查
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "用户已被禁用");
        }

        // 5. 生成 JWT Token
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String token = jwtUtil.generateToken(userPrincipal);

        // 6. 构建响应
        LoginVO vo = new LoginVO();
        UserVO userVO = new UserVO();
        org.springframework.beans.BeanUtils.copyProperties(user, userVO);
        vo.setUser(userVO);
        vo.setToken(token);
        // 24小时
        vo.setExpire(jwtUtil.getExpiration());
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = baseMapper.selectByUsername(username);
        if (user == null) {
            user = baseMapper.selectByEmail(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "用户已被禁用");
        }

        return UserPrincipal.create(user);
    }
}
