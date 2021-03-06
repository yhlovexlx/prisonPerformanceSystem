package org.nix.domain.entity.dto.user;

import org.apache.log4j.Logger;
import org.nix.dao.service.UserService;
import org.nix.domain.entity.User;
import org.nix.domain.entity.dto.ResultDto;
import org.nix.utils.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by zhangpe0312@qq.com on 2018/3/15.
 */
@Service
public class UserListDTO implements ResultDto{
    //日志记录
    private static Logger logger = Logger.getLogger(UserListDTO.class);

    @Autowired
    private UserService userService;

    private List<User> users = new ArrayList<>();

    //总数
    private int total = 0;

    //行数
    private int limit = 10;

    //当前页
    private int currentPage = 1;

    //是否逆序
    private boolean desc = false;

    @Override
    public ResultDto resultDto(Object... objects) {

        users = userService.userList(limit,currentPage,desc);

        if (SystemUtil.parameterNull(users)){
            return this;
        }

        setTotal();

        //过滤属性
        setParmaterNull();

        return this;
    }


    public UserListDTO setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public UserListDTO setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public UserListDTO setDesc(boolean desc) {
        this.desc = desc;
        return this;
    }

    public void setTotal() {
        this.total = (int) userService.userListCount();
    }

    public List<User> getUsers() {
        return users;
    }

    public int getTotal() {
        return total;
    }


    public void setParmaterNull(){
        for (int i = 0; i <users.size() ; i++) {
            users.get(i).setRole(null);
            users.get(i).setOvertimeRecords(null);
            users.get(i).setPassword(null);
        }
    }

}
