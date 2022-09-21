package com.song.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
