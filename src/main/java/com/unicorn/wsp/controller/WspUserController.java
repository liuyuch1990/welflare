package com.unicorn.wsp.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.unicorn.wsp.entity.WspOrder;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.entity.WspUserRole;
import com.unicorn.wsp.entity.zznvo.UserPageVo;
import com.unicorn.wsp.entity.zznvo.UserPoi;

import com.unicorn.wsp.mapper.WspUserMapper;
import com.unicorn.wsp.mapper.WspUserRoleMapper;
import com.unicorn.wsp.service.*;
import com.unicorn.wsp.utils.DozerUtil;
import com.unicorn.wsp.utils.EncryptorUtil;
import com.unicorn.wsp.utils.TokenUtils;
import com.unicorn.wsp.utils.ZExcelUtil;
import com.unicorn.wsp.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.unicorn.wsp.common.base.BaseController;
import com.unicorn.wsp.common.result.Result;
import com.unicorn.wsp.entity.vo.WspUserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@Api(tags = {""})
@RequestMapping("/user")
@Slf4j
public class WspUserController
	extends BaseController<WspUserService, WspUserVO> {



    @Autowired
    WspGiftCardService cardService;

    @Autowired
    WspUserRoleMapper wspGiftCardMappingMapper;

    @Autowired
    WspUserService userService;

    @Autowired
    WspUserRoleService userRoleService;

    @Autowired
    WspUserMapper userMapper;

    @Autowired
    WspComService comService;

    @Autowired
    WspGiftCardService giftCardService;

    @Autowired
    WspOrderService orderService;


    /**
     * 迭代：list强制条件查询com
     * */
    @PostMapping("/queryList")
    public ResultVo queryList(@RequestBody UserPageVo user, HttpServletRequest request){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        int roleId = accessToken.getRoleId();
        if(roleId == 2){
            user.setUserCom(accessToken.getComNum());
        }
        PageVo<WspUser> wspUserPageVo = userService.queryList(user);
        return ResultVo.success(wspUserPageVo);
    }


    /**
     * 迭代：导出->强制条件查询com
     * */
    @GetMapping("/exportExcel")
    public /*List<UserPoi>*/ void exportExcel(HttpServletRequest request, HttpServletResponse response){
        AccessToken accessToken = TokenUtils.analyseRequest(request);
        String comNum = accessToken.getComNum();
        LambdaQueryWrapper<WspUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WspUser::getUserCom, comNum);
        List<WspUser> userList = userMapper.selectList(wrapper);

        ArrayList<UserPoi> userPois = new ArrayList<>();
        List<UserPoi> pois = DozerUtil.mapList(userList, UserPoi.class);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = format.format(new Date()) + ".xls";
        try {
            response.setHeader("Content-Disposition", "attachment;filename*= UTF-8''"+ URLEncoder.encode(fileName,"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ZExcelUtil.exportExcel(pois, "用户导出", "全部用户", UserPoi.class, fileName, response);
        //return pois;

    }


    // 管理员登陆
    @PostMapping("/admin/login")
    public ResultVo adminLogin(@RequestBody WspUser loginUser, HttpServletResponse response){

        // 登录判空
        if (StringUtils.isEmpty(loginUser.getUserPhone())) {
            return ResultVo.argsError("手机号为空");
        }else if(StringUtils.isEmpty(loginUser.getUserPwd())){
            return ResultVo.argsError("密码为空");
        }

        // 1 根据手机号查用户
        String userPhone = loginUser.getUserPhone();
        String Pwd = loginUser.getUserPwd();

        List<WspUser> userList = userService.getUser(userPhone);
        if(ObjectUtils.isEmpty(userList) || userList.size() == 0) {
            return ResultVo.failed("手机号或密码错误");
        }


        WspUser user = userList.get(0);

        // 2 如果手机号不为空，就验证密码
        String md5pwd = DigestUtils.md5Hex(Pwd);
        if (!StringUtils.equals(md5pwd, user.getUserPwd())) {
            return ResultVo.failed("手机号或密码错误");
        }



        // 3 验证是不是管理员
        //int count = userRoleService.checkAdmin(user.getUserId());
        int adminLevel = userRoleService.queryAdminLevel(user.getUserId());
        if(adminLevel!=2 && adminLevel!= 3){
            return ResultVo.failed("请检查管理员账号是否正确");
        }

        // 验证管理员公司是否存在
        if(adminLevel==2){
            int i = comService.checkCom(user.getUserCom());
            if(i==0) return ResultVo.failed("该账户绑定公司不存在");
        }

        // 4 验证通过 创建AccessToken
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//暂时不用用户

        AccessToken token = new AccessToken(user.getUserId(),userPhone, userList.get(0).getUserCom(), adminLevel);
        // 3 把AccessToken转成json字符串
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken加密
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);
        //map.put("user",queryUserJson);//暂时不用用户

        /**token返回response*/
        response.setHeader("token", tokenEncryet);
        response.setHeader("flag", ""+adminLevel);

        // 加密AccessToken的json字符串返回前端
        return ResultVo.success(map);
    }



    // 登录新
    @PostMapping("/login")
    public ResultVo login2(@RequestBody LoginVo loginVo, HttpServletResponse response){
        if(ObjectUtil.isEmpty(loginVo)||StringUtils.isEmpty(loginVo.getUserPhone())){
            return ResultVo.failed();
        }
        if(!validateMobilePhone(loginVo.getUserPhone())){
            return ResultVo.failed("手机号不规范");
        }

        log.debug("LoginVO:{}",loginVo);
        // 校验公司编号
        int i = comService.checkCom(loginVo.getUserCom().trim());
        if(i == 0){
            return ResultVo.failed("公司编号不存在");
        }
        // 校验礼品卡
        boolean exist = giftCardService.isExistLogin(loginVo.getGiftCardNum(), loginVo.getUserCom());
        if(!exist){
            return ResultVo.failed("礼品卡不存在");
        }

        /* ***** 允许登录 ******/
        /* ***** 以下为后台处理 ******/
        // 手机号是否注册
        WspUser wspUser = userService.queryUserByPhone(loginVo.getUserPhone());
        if(ObjectUtil.isEmpty(wspUser)){
            /* 新用户 */
            // 存 用户和角色映射关系
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            WspUserRole userRole = new WspUserRole();
            userRole.setUserId(uuid);
            userRole.setRoleId(1);
            int insert = wspGiftCardMappingMapper.insert(userRole);
            if(insert != 1){
                log.info("----新用户绑定角色关系异常，phone：{}",loginVo.getUserPhone());
            }
            WspUser user = new WspUser();
            user.setIsDisable("0");
            user.setUserPhone(loginVo.getUserPhone());
            user.setUserId(uuid);
            user.setUserCom(loginVo.getUserCom());
            log.info("新用户添加 - {}", user);

            // 绑定礼品卡
            int exchange = giftCardService.exchange(loginVo.getGiftCardNum(), uuid, loginVo.getUserPhone());
            if(exchange != 1){
                log.info("----绑定礼品卡异常------");
                log.info("--1---cardNum:{}",loginVo.getGiftCardNum());
                log.info("--2---userid:{}",uuid);
                log.info("--3---phone:{}",loginVo.getUserPhone());
            }
            // 封装token
            Map<String, String> token = createToken(uuid, loginVo.getUserPhone(), loginVo.getUserCom());

            /**token返回response*/
            String tokenEncryet = createTokenEncryet(uuid, loginVo.getUserPhone(), loginVo.getUserCom());
            response.setHeader("token", tokenEncryet);

            return userService.save(user) ? ResultVo.success(new LoginResponseVo(token, user, null)):ResultVo.failed("登录失败");

        }else {

            /* 老用户 */

            String userId = wspUser.getUserId();

            // 组装token
            Map<String, String> token = createToken(userId, loginVo.getUserPhone(), wspUser.getUserCom());

            /**token返回response*/
            String tokenEncryet = createTokenEncryet(userId, loginVo.getUserPhone(), wspUser.getUserCom());
            response.setHeader("token", tokenEncryet);

            // 查出订单list
            List<WspOrder> wspOrders = orderService.queryOrderByUserId(userId);
            // 存在未发货的订单.提前结束方法
            if(ObjectUtil.isNotEmpty(wspOrders)){
                return ResultVo.success(new LoginResponseVo(token, wspUser,null));
            }

            // 用户存在未使用礼品卡，提前结束方法
            Integer s = cardService.onlyOneCard(userId);
            if(s > 0){
                return ResultVo.success(new LoginResponseVo(token, wspUser,null));
            }

            // 绑定礼品卡
            int exchange = giftCardService.exchange(loginVo.getGiftCardNum(), userId, loginVo.getUserPhone());
            if(exchange != 1){
                log.info("----绑定礼品卡异常------");
                log.info("--1---cardNum:{}",loginVo.getGiftCardNum());
                log.info("--2---userid:{}",userId);
                log.info("--3---phone:{}",loginVo.getUserPhone());
            }
            return ResultVo.success(new LoginResponseVo(token, wspUser,null));
        }



    }

    // 封装token
    Map<String, String> createToken(String userId, String userPhone){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//暂时不用用户

        AccessToken token = new AccessToken(userId, userPhone);
        // 3 把AccessToken转成json字符串
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken加密
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);

        return map;
    }

    // 封装token
    Map<String, String> createToken(String userId, String userPhone, String ComNum){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//暂时不用用户

        AccessToken token = new AccessToken(userId, userPhone, ComNum);
        // 3 把AccessToken转成json字符串
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken加密
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);

        return map;
    }

    // 封装token String
    String createTokenEncryet(String userId, String userPhone, String ComNum){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//暂时不用用户

        AccessToken token = new AccessToken(userId, userPhone, ComNum);
        // 3 把AccessToken转成json字符串
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken加密
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);

        return tokenEncryet;
    }



//    @GetMapping("/test/id/{id}")
//    public WspUser test(@PathVariable String id){
//        WspUser userById = userService.getUserById(id);
//        return userById;
//    }



    // 用户查看个人信息
    @GetMapping("/queryById")
    public ResultVo queryById(HttpServletRequest request){
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        List<WspUser> user = userService.getUser(accessToken.getPhone());
        if(ObjectUtil.isEmpty(user)){
            return ResultVo.failed("查无此人");
        }
        return ResultVo.success(user.get(0));
    }



    // 编辑
    @PostMapping("/update")
    public ResultVo update(@RequestBody  WspUser user, HttpServletRequest request){
        // user id
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        // 根据角色判断能编辑哪些属性
        int i = userRoleService.checkAdmin(accessToken.getUserId());
        boolean b = false;
        WspUser wspUser = new WspUser();

        if(i>0){
            wspUser.setUserId(user.getUserId());
            /*管理员*/
            log.info("管理员 - {}",user.getUserId());
            if(ObjectUtil.isNull(user.getUserPwd())&&ObjectUtil.isNull(user.getIsDisable())){
                // 都为空
                return ResultVo.argsError("修改信息不能为空!");
            }
            if(ObjectUtil.isNotNull(user.getUserPwd())){
                wspUser.setUserPwd(DigestUtils.md5Hex(user.getUserPwd()));
            }
            if(ObjectUtil.isNotNull(user.getIsDisable())){
                wspUser.setIsDisable(user.getIsDisable());
            }
            b = service.updateById(wspUser);
            return b?ResultVo.success("修改成功"):ResultVo.failed("修改失败");

        }else{
            wspUser.setUserId(accessToken.getUserId());
            /*普通用户*/
            log.info("普通用户 - {}", user.getUserId());
            // 只能改工号 姓名
            if(ObjectUtil.isNull(user.getUserNo())&&ObjectUtil.isNull(user.getUserName())){
                // 名字 工号都为空
                return ResultVo.argsError("修改信息不能为空");
            }
            if(ObjectUtil.isNotNull(user.getUserNo())){
                wspUser.setUserNo(user.getUserNo());
            }
            if(ObjectUtil.isNotNull(user.getUserName())){
                wspUser.setUserName(user.getUserName());
            }
            b = service.updateById(wspUser);
        }

        return b ? ResultVo.success() : ResultVo.failed();
    }
    // 用户删除




    // 手机号校验
    boolean validateMobilePhone(String in) {
        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
        return pattern.matcher(in).matches();
    }

















































////////////////////////////////////////////////////////////////////////////////////////////



    // 注册(旧)
    @Transactional
    @PostMapping("/register")
    public ResultVo register(@RequestBody WspUser user) {
        if (StringUtils.isEmpty(user.getUserPhone())) {
            return ResultVo.argsError("手机号为空");
        }else if(StringUtils.isEmpty(user.getUserPwd())){
            return ResultVo.argsError("密码为空");
        }else if(StringUtils.isEmpty(user.getUserNo())){
            return ResultVo.argsError("工号为空");
        }else if(StringUtils.isEmpty(user.getUserName())){
            return ResultVo.argsError("姓名为空");
        }else if(!validateMobilePhone(user.getUserPhone())){
            return ResultVo.argsError("手机号不规范");
        }/*else if(user.getUserPwd().length()>16||user.getUserPwd().length()<6){
            return ResultVo.argsError("密码应为6-16位");
        }*/

        // 公司编号校验
        int integer = comService.checkCom(user.getUserCom().trim());
        if(integer == 0){
            return ResultVo.argsError("公司编号不存在");
        }

        // 唯一校验
        List<WspUser> userList = userService.getUser(user.getUserPhone());
        if(ObjectUtils.isNotEmpty(userList) || userList.size() != 0) {
            return ResultVo.failed("手机号已存在");
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        user.setUserId(uuid);

        // 存 用户和角色映射关系
        WspUserRole userRole = new WspUserRole();
        userRole.setUserId(uuid);
        userRole.setRoleId(1);
        // userRoleService.save(userRole);
        int insert = wspGiftCardMappingMapper.insert(userRole);
        if(insert != 1){
            return ResultVo.failed("注册模块异常");
        }

        // 加密密码
        String md5pwd = DigestUtils.md5Hex(user.getUserPwd());
        user.setUserPwd(md5pwd);

        log.info("user - {}", user);

        return userService.save(user) ? ResultVo.success("注册成功"):ResultVo.failed("注册失败");
    }


/*
    // 登录
    @PostMapping("/login-old")
    public ResultVo login(@RequestBody WspUser loginUser, HttpServletResponse response){

        // 登录判空
        if (StringUtils.isEmpty(loginUser.getUserPhone())) {
            return ResultVo.argsError("手机号为空");
        }else if(StringUtils.isEmpty(loginUser.getUserPwd())){
            return ResultVo.argsError("密码为空");
        }

        // 1 根据手机号查用户
        String userPhone = loginUser.getUserPhone();
        String Pwd = loginUser.getUserPwd();

        List<WspUser> userList = userService.getUser(userPhone);
        if(ObjectUtils.isEmpty(userList) || userList.size() == 0) {
            return ResultVo.failed("手机号或密码错误!");
        }

        // 2 如果手机号不为空，就验证密码
        WspUser user = userList.get(0);
        String md5pwd = DigestUtils.md5Hex(Pwd);

        if (!StringUtils.equals(md5pwd, user.getUserPwd())) {
            log.info("pwd解密 - {}",md5pwd);
            log.info("user pwd - {}", user.getUserPwd());
            return ResultVo.failed("手机号或密码错误");
        }

        // 3 验证通过 创建AccessToken
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//暂时不用用户

        AccessToken token = new AccessToken(user.getUserId(),userPhone);
        // 3 把AccessToken转成json字符串
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken加密
        String tokenEncryet = EncryptorUtil.encryet(jsonToken);
        map.put("token",tokenEncryet);
        //map.put("user",queryUserJson);//暂时不用用户


        HashMap<String, String> giftCardVos = cardService.queryCardByUserId(user.getUserId());
        List<WspUser> users = userService.getUser(userPhone);

        LoginResponseVo loginResponseVo = new LoginResponseVo(map, users.get(0), giftCardVos);

        //System.out.println("-----------------------" + loginVO);

        // token返回response
        response.setHeader("token", tokenEncryet);

        // 加密AccessToken的json字符串,用户信息，余额返回前端
        return ResultVo.success(loginResponseVo);
    }

 */











    @PostMapping("/add")
    @ApiOperation(value = "添加", notes = "添加")
    @Override
    protected Result add(@RequestBody @Validated WspUserVO dto) {
        service.save(dto);
        return new Result().success("创建/修改成功！");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除", notes = "删除")
    @Override
    protected Result delete(@PathVariable Long id) {
        service.removeById(id);
        return new Result().success("删除成功！");
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "获取单个", notes = "获取单个")
    @Override
    protected Result get(@PathVariable Long id) {
        return new Result().success(service.getInfoById(id));
    }

    @PutMapping("/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @Override
    protected Result edit(@RequestBody @Validated WspUserVO dto) {
        service.updateById(dto);
        return new Result().success("修改成功！");
    }

    @ApiOperation(value = "查询分页", notes = "查询分页")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspUserVO dto) {
        return new Result().success(service.page(dto));
    }


}

