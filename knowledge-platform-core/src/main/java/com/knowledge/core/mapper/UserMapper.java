package com.knowledge.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.core.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 11695
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    /**
     * 根据用户名查询用户
     */
    UserEntity selectByUsername(@Param("username") String username);


    /**
     * 根据邮箱查询用户
     */
    UserEntity selectByEmail(@Param("email") String email);
}
