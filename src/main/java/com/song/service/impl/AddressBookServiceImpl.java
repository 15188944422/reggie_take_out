package com.song.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.mapper.AddressBookMapper;
import com.song.pojo.AddressBook;
import com.song.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
