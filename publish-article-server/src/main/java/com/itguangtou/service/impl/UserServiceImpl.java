package com.itguangtou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itguangtou.enums.ResultCodeEnum;
import com.itguangtou.enums.UserStatusEnum;
import com.itguangtou.exception.BusinessException;
import com.itguangtou.mapper.AdminUserMapper;
import com.itguangtou.pojo.dto.LoginDTO;
import com.itguangtou.pojo.entity.AdminUser;
import com.itguangtou.service.UserService;
import com.itguangtou.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public String login(LoginDTO loginDTO) {
        AdminUser user = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, loginDTO.getUsername()));
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }
        if (!Integer.valueOf(UserStatusEnum.NORMAL.getCode()).equals(user.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        return jwtUtils.generateToken(user.getUsername());
    }
}
