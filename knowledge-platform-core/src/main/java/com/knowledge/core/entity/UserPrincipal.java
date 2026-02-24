package com.knowledge.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 用户详情
 * @author: nuts_tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String nickname;
    private String avatar;
    private Integer status;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 默认授予 USER 角色
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 状态为1表示未锁定
        return status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 状态为1表示启用
        return status == 1;
    }

    /**
     * 将 UserEntity 转换为 UserPrincipal
     */
    public static UserPrincipal create(UserEntity user) {
        UserPrincipal principal = new UserPrincipal();
        principal.setId(user.getId());
        principal.setUsername(user.getUsername());
        principal.setEmail(user.getEmail());
        principal.setPassword(user.getPassword());
        principal.setNickname(user.getNickname());
        principal.setAvatar(user.getAvatar());
        principal.setStatus(user.getStatus());
        return principal;
    }
}
