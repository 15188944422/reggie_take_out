package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.mapper.UserMapper;
import com.song.pojo.User;
import com.song.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
