package cn.softlq.ss2h.service.impl;

import cn.softlq.ss2h.dao.IUserDao;
import cn.softlq.ss2h.po.TUserEntity;
import cn.softlq.ss2h.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements IUserService {
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private IUserDao userDao;

    @Override
    public Map<String, Object> userLogin(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;
        if (userEntity != null
                && userEntity.getUserid() != null && userEntity.getUpwd() != null
                && !"".equals(userEntity.getUserid()) && !"".equals(userEntity.getUpwd())) {
            try {
                TUserEntity result = userDao.userFindOne(userEntity.getUserid());
                if (result != null) {
                    if (result.getUpwd().equals(userEntity.getUpwd())) {
                        //登陆成功
                        map.put("url", "/user/userManage");
                        //跟踪用户信息
                        session.setAttribute("userId", result.getUserid());
                        session.setAttribute("urole", result.getUrole());
                    } else {
                        //密码错误
                        error = 4;
                        map.put("message", "密码错误");
                    }
                } else {
                    //用户名错误
                    error = 3;
                    map.put("message", "用户名错误");
                }
            } catch (Exception e) {
                //服务器异常
                error = 2;
                map.put("message", "服务器异常");
                e.printStackTrace();
            }
        } else {
            //非法登陆
            error = 1;
            map.put("message", "非法登陆");
        }
        map.put("error", error);
        return map;
    }

    @Override
    public Map<String, Object> userLogon(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;
        if (userEntity != null && userEntity.getUserid() != null && !"".equals(userEntity.getUserid())) {
            //清除用户信息
            session.setAttribute("userId", null);
            session.setAttribute("urole", null);
            map.put("message", "成功退出");
        } else {
            //参数异常
            error = 1;
            map.put("message", "参数异常");
        }
        map.put("error", error);
        return map;
    }

    @Override
    public boolean checkLogin() {
        return null != session.getAttribute("userId");
    }


    @Override
    public Map<String, Object> userAdd(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;

        if (userEntity != null
                && userEntity.getUserid() != null && userEntity.getUpwd() != null && userEntity.getUrole() != null &&
                userEntity.getOrgid() != null && userEntity.getUname() != null
                && !"".equals(userEntity.getUserid()) && !"".equals(userEntity.getUpwd()) && !"".equals(userEntity.getUrole()) &&
                !"".equals(userEntity.getOrgid()) && !"".equals(userEntity.getUname())) {
            try {
                TUserEntity result = userDao.userFindOne(userEntity.getUserid());
                if (result == null) {
                    userDao.userAdd(userEntity);
                    map.put("message", "添加成功");
                } else {
                    //该账号已注册
                    error = 3;
                    map.put("message", "该账号已注册");
                }

            } catch (Exception e) {
                //保存失败
                error = 2;
                map.put("message", "保存失败");
                e.printStackTrace();
            }

        } else {
            //非法操作
            error = 1;
            map.put("message", "非法操作");
        }
        map.put("error", error);
        return map;
    }


    @Override
    public Map<String, Object> userDelete(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;
        if (userEntity != null && userEntity.getUserid() != null && !"".equals(userEntity.getUserid())) {
            try {
                userDao.userDelete(userEntity);
                map.put("message", "删除成功");
            } catch (Exception e) {
                //删除失败
                error = 2;
                map.put("message", "删除失败");
                e.printStackTrace();
            }
        } else {
            //非法操作
            error = 1;
            map.put("message", "非法操作");
        }
        map.put("error", error);
        return map;
    }


    @Override
    public Map<String, Object> userUpdate(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;

        if (userEntity != null
                && userEntity.getUserid() != null && userEntity.getUpwd() != null && userEntity.getOrgid() != null && userEntity.getUname() != null
                && !"".equals(userEntity.getUserid()) && !"".equals(userEntity.getUpwd()) && !"".equals(userEntity.getOrgid()) && !"".equals(userEntity.getUname())) {
            try {
                TUserEntity result = userDao.userFindOne(userEntity.getUserid());
                if (result != null) {
                    result.setUname(userEntity.getUname());
                    result.setUpwd(userEntity.getUpwd());
                    if (userEntity.getOrgid() != null || !"".equals(userEntity.getOrgid())) {
                        result.setOrgid(userEntity.getOrgid());
                    }
                    userDao.userUpdate(result);
                    map.put("message", "修改成功");
                } else {
                    //修改失败
                    error = 3;
                    map.put("message", "修改失败");
                }

            } catch (Exception e) {
                //修改失败
                error = 2;
                map.put("message", "修改失败");
                e.printStackTrace();
            }

        } else {
            //非法操作
            error = 1;
            map.put("message", "非法操作");
        }
        map.put("error", error);
        return map;
    }


    @Override
    public Map<String, Object> userFindAll(int currentTotal, int current, String province) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;
        if (currentTotal >= 0 && current >= 0) {
            try {
                if (province == null || "".equals(province)) province = "0";
                List<TUserEntity> users = userDao.userFindAll(currentTotal, current, province);
                for (int i = 0; i < users.size(); i++) {
                    TUserEntity userEntity = users.get(i);
                    if ("1".equals(userEntity.getUrole())) {
                        userEntity.setUrole("管理员");
                    } else {
                        userEntity.setUrole("供应商");
                    }
                    if (null == userEntity.getOrgid()) {
                        userEntity.setOrgid("");
                    } else {
                        //查找省份
                        userEntity.setOrgid(userDao.userProvinceName(userEntity.getOrgid()));
                    }

                }

                map.put("users", users);
                //获取数据的条数
                map.put("total", userDao.userFindAllTotal(province));
                map.put("message", "");
            } catch (Exception e) {
                //获取数据异常
                error = 2;
                map.put("message", "获取数据异常");
                e.printStackTrace();
            }
        } else {
            //请求参数异常
            error = 1;
            map.put("message", "请求参数异常");
        }
        map.put("error", error);
        return map;
    }


    @Override
    public Map<String, Object> userFindOne(TUserEntity userEntity) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int error = 0;

        if (userEntity != null
                && userEntity.getUserid() != null && !"".equals(userEntity.getUserid())) {
            try {
                TUserEntity result = userDao.userFindOne(userEntity.getUserid());
                Map<String, String> m = new HashMap<>();

                m.put("userid", result.getUserid());
                m.put("upwd", result.getUpwd());
                m.put("orgid", userDao.userProvinceName(result.getOrgid()));
                m.put("uname", result.getUname());

                map.put("user", m);
            } catch (Exception e) {
                //获取失败
                error = 2;
                map.put("message", "获取失败");
                e.printStackTrace();
            }

        } else {
            //请求参数异常
            error = 1;
            map.put("message", "请求参数异常");
        }
        map.put("error", error);
        return map;
    }


    @Override
    public void userFunction() throws Exception {
        String userid = (String) session.getAttribute("userId");
        TUserEntity result = userDao.userFindOne(userid);
        if (userid != null && !"".equals(userid)) {
            request.setAttribute("func", userDao.userFunction(result.getUrole()));
        }
    }


    @Override
    public void getVisibleProvince() throws Exception {
        String userid = (String) session.getAttribute("userId");
        TUserEntity result = userDao.userFindOne(userid);
        if (userid != null && !"".equals(userid)) {
            request.setAttribute("orgs", userDao.getVisibleProvince(result.getUrole(), result.getOrgid()));
        }
    }

}
