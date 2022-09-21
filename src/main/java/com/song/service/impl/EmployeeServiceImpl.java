package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.mapper.EmployeeMapper;
import com.song.pojo.Employee;
import com.song.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService { }
