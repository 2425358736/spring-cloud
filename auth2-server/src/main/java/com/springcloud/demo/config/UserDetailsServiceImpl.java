package com.springcloud.demo.config;

import com.springcloud.demo.domain.Permission;
import com.springcloud.demo.domain.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public User loadUserByUsername(String userName) throws UsernameNotFoundException {
        if (StringUtils.equals(userName,"liuzhiqiang")) {
            User user = new User();
            user.setUserName(userName);
            user.setPassword("123456789");
            List<Permission> list = new ArrayList<>();
            list.add(new Permission("permission1"));
            list.add(new Permission("permission2"));
            list.add(new Permission("permission3"));
            user.setList(list);
            // 以上内容在真实环境中数据来自于数据库
            return user;
        } else {
            User user = new User();
            user.setUserName(userName);
            user.setPassword("123456");
            List<Permission> list = new ArrayList<>();
            list.add(new Permission("ceshi1"));
            list.add(new Permission("ceshi2"));
            list.add(new Permission("ceshi3"));
            user.setList(list);
            // 以上内容在真实环境中数据来自于数据库
            return user;
        }
    }
}
