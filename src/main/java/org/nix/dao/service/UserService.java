package org.nix.dao.service;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.nix.dao.base.SupperBaseDAOImp;
import org.nix.domain.entity.OvertimeRecord;
import org.nix.domain.entity.PersonalMonthOvertime;
import org.nix.domain.entity.User;
import org.nix.exception.AccountNumberException;
import org.nix.utils.SystemUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Create by zhangpe0312@qq.com on 2018/3/10.
 * <p>
 * 用户类的信息对数据库操作
 */
@Service
@Transactional
public class UserService extends SupperBaseDAOImp<User> {
    //日志记录
    private static Logger logger = Logger.getLogger(UserService.class);

    @Override
    public <T> List<T> findByCriteria(T object, Integer startRow, Integer pageSize) {
        return null;
    }

    @Override
    public <T> Long findByCriteriaCount(T object) {
        return null;
    }

    /**
     * 用户登陆查询
     * 如果账号密码不匹配将抛出账号异常
     *
     * @param serialNumber 警号
     * @param password     账户密码
     * @return 账号密码是否匹配成功
     */
    public User login(String serialNumber, String password) {

        //表示警号列的列名
        String columnSiren = "serialNumber";

        if (SystemUtil.parameterNull(serialNumber, password)) {
            throw new NullPointerException();
        }

        User user = findByProperty(columnSiren, serialNumber);

        if (user == null || !user.getPassword().equals(password)) {
            throw new AccountNumberException();
        }
        return user;
    }

    /**
     * 通过警号查询用户
     * @param serialNumber
     * @return
     */
    public User findUserBySerialNumber(String serialNumber){

        String column = "serialNumber";

        return findByProperty(column,serialNumber);
    }

    /**
     * 通过警号模糊查询用户
     * @param select
     * @return
     */
    public List<User> findBlurryUserBySerialNumber(String select){

        if (select == null){
            select = "";
        }

        String sql = "SELECT * FROM `user` WHERE serialNumber LIKE '%select%'";

        sql = sql.replaceAll("select",select);

        return getListBySQL(sql);
    }



    /**
     * 用户注册
     *
     * @param user 用户对象
     */
    public Object registered(User user) {
        if (SystemUtil.parameterNull(user)) {
            throw new NullPointerException();
        }
        return save(user);
    }

    /**
     * 获得加班时间总时长
     *
     * @param user 需要获取的用户
     * @return 时长
     */
    public double overtimeAllTime(User user) {
        String sql = "SELECT sum(overtimeLength) FROM overtimerecord WHERE `user` = id";
        sql = sql.replaceAll("id", String.valueOf(user.getId()));
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);

        if (SystemUtil.parameterNull(query.uniqueResult())) {
            return 0;
        }

        return (double) query.uniqueResult();
    }

    /**
     * 查询员工加班获取的总工资
     *
     * @param user
     * @return
     */
    public double overtimeAllMoney(User user) {
        String sql = "SELECT sum(overtimeMoney) FROM overtimerecord WHERE `user` = id";
        sql = sql.replaceAll("id", String.valueOf(user.getId()));
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        if (SystemUtil.parameterNull(query.uniqueResult())) {
            return 0;
        }
        return (double) query.uniqueResult();
    }

    /**
     * 获得用户的基础工资  以小时计算
     *
     * @param user 需要查找的人
     * @return 以小时计算的工资
     */
    public double findeUserBasicWageHoures(User user) {
        String sql = "SELECT `user`.basicWage FROM `user` where `user`.id = " + user.getId();

        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);

        double money = (double) query.uniqueResult();

        money = money / 30 / 24 * 1.0;

        return money;
    }

    /**
     * 按id排序查询普通用户信息
     *
     * @param limit       每页行数
     * @param currentPage 当前页
     * @param desc        是否逆序
     * @return 用户列表
     */
    public List<User> userList(int limit, int currentPage, boolean desc) {

        int start = (currentPage - 1) * limit;

        String isDesc = desc ? "DESC" : "";

        String sql = "SELECT * FROM `user` " +
                "WHERE role = (SELECT role.id FROM role WHERE role.`name` = '普通用户') " +
                " ORDER BY id DESC" +
                "limit start , amount ";

        sql = sql.replaceAll("start", String.valueOf(start))
                .replaceAll("amount", String.valueOf(limit))
                .replaceAll("DESC", isDesc);

        return getListBySQL(sql);
    }

    public long userListCount(){

        String sql = " SELECT count(*) FROM `user` " +
                    "WHERE role = (SELECT role.id FROM role WHERE role.`name` = '普通用户')";

        return findBySqlCount(sql);
    }


    /**
     * 删除用户的加班记录
     *
     * @param user
     */
    public int deleteUserOvertime(User user) {

        String sql = "DELETE FROM overtimerecord WHERE overtimerecord.`user` = ?";
        return batchUpdateOrDelete(sql, user.getId());
    }

    /**
     * 用户删除，首先应该删除他绑定外键的表记录
     *
     * @param user 需要删除的用户
     */
    public void deleteUser(User user) {

        logger.info("注销用户"+user.getName()+"开始");

        int temp;

        temp = deleteUserOvertime(user);

        logger.info("删除用户加班记录"+temp+"条");

        temp = deleteUserMonthOvertime(user);

        logger.info("删除用户月统计记录"+temp+"条");

        delete(user);

        logger.info("删除用户基础信息");

        logger.info("注销用户"+user.getName()+"结束");

    }

    public int deleteUserMonthOvertime(User user) {
        String sql = "DELETE FROM personalmonthovertime WHERE personalmonthovertime.`user` = ?";
        return batchUpdateOrDelete(sql, user.getId());
    }




}
