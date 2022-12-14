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
     * ?????????list??????????????????com
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
     * ???????????????->??????????????????com
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
        ZExcelUtil.exportExcel(pois, "????????????", "????????????", UserPoi.class, fileName, response);
        //return pois;

    }


    // ???????????????
    @PostMapping("/admin/login")
    public ResultVo adminLogin(@RequestBody WspUser loginUser, HttpServletResponse response){

        // ????????????
        if (StringUtils.isEmpty(loginUser.getUserPhone())) {
            return ResultVo.argsError("???????????????");
        }else if(StringUtils.isEmpty(loginUser.getUserPwd())){
            return ResultVo.argsError("????????????");
        }

        // 1 ????????????????????????
        String userPhone = loginUser.getUserPhone();
        String Pwd = loginUser.getUserPwd();

        List<WspUser> userList = userService.getUser(userPhone);
        if(ObjectUtils.isEmpty(userList) || userList.size() == 0) {
            return ResultVo.failed("????????????????????????");
        }


        WspUser user = userList.get(0);

        // 2 ??????????????????????????????????????????
        String md5pwd = DigestUtils.md5Hex(Pwd);
        if (!StringUtils.equals(md5pwd, user.getUserPwd())) {
            return ResultVo.failed("????????????????????????");
        }



        // 3 ????????????????????????
        //int count = userRoleService.checkAdmin(user.getUserId());
        int adminLevel = userRoleService.queryAdminLevel(user.getUserId());
        if(adminLevel!=2 && adminLevel!= 3){
            return ResultVo.failed("????????????????????????????????????");
        }

        // ?????????????????????????????????
        if(adminLevel==2){
            int i = comService.checkCom(user.getUserCom());
            if(i==0) return ResultVo.failed("??????????????????????????????");
        }

        // 4 ???????????? ??????AccessToken
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//??????????????????

        AccessToken token = new AccessToken(user.getUserId(),userPhone, userList.get(0).getUserCom(), adminLevel);
        // 3 ???AccessToken??????json?????????
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken??????
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);
        //map.put("user",queryUserJson);//??????????????????

        /**token??????response*/
        response.setHeader("token", tokenEncryet);
        response.setHeader("flag", ""+adminLevel);

        // ??????AccessToken???json?????????????????????
        return ResultVo.success(map);
    }



    // ?????????
    @PostMapping("/login")
    public ResultVo login2(@RequestBody LoginVo loginVo, HttpServletResponse response){
        if(ObjectUtil.isEmpty(loginVo)||StringUtils.isEmpty(loginVo.getUserPhone())){
            return ResultVo.failed();
        }
        if(!validateMobilePhone(loginVo.getUserPhone())){
            return ResultVo.failed("??????????????????");
        }

        log.debug("LoginVO:{}",loginVo);
        // ??????????????????
        int i = comService.checkCom(loginVo.getUserCom().trim());
        if(i == 0){
            return ResultVo.failed("?????????????????????");
        }
        // ???????????????
        boolean exist = giftCardService.isExistLogin(loginVo.getGiftCardNum(), loginVo.getUserCom());
        if(!exist){
            return ResultVo.failed("??????????????????");
        }

        /* ***** ???????????? ******/
        /* ***** ????????????????????? ******/
        // ?????????????????????
        WspUser wspUser = userService.queryUserByPhone(loginVo.getUserPhone());
        if(ObjectUtil.isEmpty(wspUser)){
            /* ????????? */
            // ??? ???????????????????????????
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            WspUserRole userRole = new WspUserRole();
            userRole.setUserId(uuid);
            userRole.setRoleId(1);
            int insert = wspGiftCardMappingMapper.insert(userRole);
            if(insert != 1){
                log.info("----????????????????????????????????????phone???{}",loginVo.getUserPhone());
            }
            WspUser user = new WspUser();
            user.setIsDisable("0");
            user.setUserPhone(loginVo.getUserPhone());
            user.setUserId(uuid);
            user.setUserDept(loginVo.getUserDept());
            user.setUserCom(loginVo.getUserCom());
            log.info("??????????????? - {}", user);

            // ???????????????
            int exchange = giftCardService.exchange(loginVo.getGiftCardNum(), uuid, loginVo.getUserPhone());
            if(exchange != 1){
                log.info("----?????????????????????------");
                log.info("--1---cardNum:{}",loginVo.getGiftCardNum());
                log.info("--2---userid:{}",uuid);
                log.info("--3---phone:{}",loginVo.getUserPhone());
            }
            // ??????token
            Map<String, String> token = createToken(uuid, loginVo.getUserPhone(), loginVo.getUserCom());

            /**token??????response*/
            String tokenEncryet = createTokenEncryet(uuid, loginVo.getUserPhone(), loginVo.getUserCom());
            response.setHeader("token", tokenEncryet);

            return userService.save(user) ? ResultVo.success(new LoginResponseVo(token, user, null)):ResultVo.failed("????????????");

        }else {

            /* ????????? */

            String userId = wspUser.getUserId();

            // ??????token
            Map<String, String> token = createToken(userId, loginVo.getUserPhone(), wspUser.getUserCom());

            /**token??????response*/
            String tokenEncryet = createTokenEncryet(userId, loginVo.getUserPhone(), wspUser.getUserCom());
            response.setHeader("token", tokenEncryet);

            // ????????????list
            List<WspOrder> wspOrders = orderService.queryOrderByUserId(userId);
            // ????????????????????????.??????????????????
            if(ObjectUtil.isNotEmpty(wspOrders)){
                return ResultVo.success(new LoginResponseVo(token, wspUser,null));
            }

            // ???????????????????????????????????????????????????
            Integer s = cardService.onlyOneCard(userId);
            if(s > 0){
                return ResultVo.success(new LoginResponseVo(token, wspUser,null));
            }

            // ???????????????
            int exchange = giftCardService.exchange(loginVo.getGiftCardNum(), userId, loginVo.getUserPhone());
            if(exchange != 1){
                log.info("----?????????????????????------");
                log.info("--1---cardNum:{}",loginVo.getGiftCardNum());
                log.info("--2---userid:{}",userId);
                log.info("--3---phone:{}",loginVo.getUserPhone());
            }
            return ResultVo.success(new LoginResponseVo(token, wspUser,null));
        }



    }

    // ??????token
    Map<String, String> createToken(String userId, String userPhone){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//??????????????????

        AccessToken token = new AccessToken(userId, userPhone);
        // 3 ???AccessToken??????json?????????
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken??????
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);

        return map;
    }

    // ??????token
    Map<String, String> createToken(String userId, String userPhone, String ComNum){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//??????????????????

        AccessToken token = new AccessToken(userId, userPhone, ComNum);
        // 3 ???AccessToken??????json?????????
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken??????
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);
        map.put("token",tokenEncryet);

        return map;
    }

    // ??????token String
    String createTokenEncryet(String userId, String userPhone, String ComNum){
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//??????????????????

        AccessToken token = new AccessToken(userId, userPhone, ComNum);
        // 3 ???AccessToken??????json?????????
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken??????
        String tokenEncryet = EncryptorUtil.encrypt(jsonToken);

        return tokenEncryet;
    }



//    @GetMapping("/test/id/{id}")
//    public WspUser test(@PathVariable String id){
//        WspUser userById = userService.getUserById(id);
//        return userById;
//    }



    // ????????????????????????
    @GetMapping("/queryById")
    public ResultVo queryById(HttpServletRequest request){
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        List<WspUser> user = userService.getUser(accessToken.getPhone());
        if(ObjectUtil.isEmpty(user)){
            return ResultVo.failed("????????????");
        }
        return ResultVo.success(user.get(0));
    }



    // ??????
    @PostMapping("/update")
    public ResultVo update(@RequestBody  WspUser user, HttpServletRequest request){
        // user id
        String tokenHeader = request.getHeader("token");
        String jsonToken = EncryptorUtil.decrypt(tokenHeader);
        AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

        // ???????????????????????????????????????
        int i = userRoleService.checkAdmin(accessToken.getUserId());
        boolean b = false;
        WspUser wspUser = new WspUser();

        if(i>0){
            wspUser.setUserId(user.getUserId());
            /*?????????*/
            log.info("????????? - {}",user.getUserId());
            if(ObjectUtil.isNull(user.getUserPwd())&&ObjectUtil.isNull(user.getIsDisable())){
                // ?????????
                return ResultVo.argsError("????????????????????????!");
            }
            if(ObjectUtil.isNotNull(user.getUserPwd())){
                wspUser.setUserPwd(DigestUtils.md5Hex(user.getUserPwd()));
            }
            if(ObjectUtil.isNotNull(user.getIsDisable())){
                wspUser.setIsDisable(user.getIsDisable());
            }
            b = service.updateById(wspUser);
            return b?ResultVo.success("????????????"):ResultVo.failed("????????????");

        }else{
            wspUser.setUserId(accessToken.getUserId());
            /*????????????*/
            log.info("???????????? - {}", user.getUserId());
            // ??????????????? ??????
            if(ObjectUtil.isNull(user.getUserNo())&&ObjectUtil.isNull(user.getUserName())){
                // ?????? ???????????????
                return ResultVo.argsError("????????????????????????");
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
    // ????????????




    // ???????????????
    boolean validateMobilePhone(String in) {
        Pattern pattern = Pattern.compile("^[1]\\d{10}$");
        return pattern.matcher(in).matches();
    }

















































////////////////////////////////////////////////////////////////////////////////////////////



    // ??????(???)
    @Transactional
    @PostMapping("/register")
    public ResultVo register(@RequestBody WspUser user) {
        if (StringUtils.isEmpty(user.getUserPhone())) {
            return ResultVo.argsError("???????????????");
        }else if(StringUtils.isEmpty(user.getUserPwd())){
            return ResultVo.argsError("????????????");
        }else if(StringUtils.isEmpty(user.getUserNo())){
            return ResultVo.argsError("????????????");
        }else if(StringUtils.isEmpty(user.getUserName())){
            return ResultVo.argsError("????????????");
        }else if(!validateMobilePhone(user.getUserPhone())){
            return ResultVo.argsError("??????????????????");
        }/*else if(user.getUserPwd().length()>16||user.getUserPwd().length()<6){
            return ResultVo.argsError("????????????6-16???");
        }*/

        // ??????????????????
        int integer = comService.checkCom(user.getUserCom().trim());
        if(integer == 0){
            return ResultVo.argsError("?????????????????????");
        }

        // ????????????
        List<WspUser> userList = userService.getUser(user.getUserPhone());
        if(ObjectUtils.isNotEmpty(userList) || userList.size() != 0) {
            return ResultVo.failed("??????????????????");
        }

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        user.setUserId(uuid);

        // ??? ???????????????????????????
        WspUserRole userRole = new WspUserRole();
        userRole.setUserId(uuid);
        userRole.setRoleId(1);
        // userRoleService.save(userRole);
        int insert = wspGiftCardMappingMapper.insert(userRole);
        if(insert != 1){
            return ResultVo.failed("??????????????????");
        }

        // ????????????
        String md5pwd = DigestUtils.md5Hex(user.getUserPwd());
        user.setUserPwd(md5pwd);

        log.info("user - {}", user);

        return userService.save(user) ? ResultVo.success("????????????"):ResultVo.failed("????????????");
    }


/*
    // ??????
    @PostMapping("/login-old")
    public ResultVo login(@RequestBody WspUser loginUser, HttpServletResponse response){

        // ????????????
        if (StringUtils.isEmpty(loginUser.getUserPhone())) {
            return ResultVo.argsError("???????????????");
        }else if(StringUtils.isEmpty(loginUser.getUserPwd())){
            return ResultVo.argsError("????????????");
        }

        // 1 ????????????????????????
        String userPhone = loginUser.getUserPhone();
        String Pwd = loginUser.getUserPwd();

        List<WspUser> userList = userService.getUser(userPhone);
        if(ObjectUtils.isEmpty(userList) || userList.size() == 0) {
            return ResultVo.failed("????????????????????????!");
        }

        // 2 ??????????????????????????????????????????
        WspUser user = userList.get(0);
        String md5pwd = DigestUtils.md5Hex(Pwd);

        if (!StringUtils.equals(md5pwd, user.getUserPwd())) {
            log.info("pwd?????? - {}",md5pwd);
            log.info("user pwd - {}", user.getUserPwd());
            return ResultVo.failed("????????????????????????");
        }

        // 3 ???????????? ??????AccessToken
        Map<String,String> map = new HashMap<>();
        //String queryUserJson = JSONObject.toJSONString(user);//??????????????????

        AccessToken token = new AccessToken(user.getUserId(),userPhone);
        // 3 ???AccessToken??????json?????????
        String jsonToken = JSONObject.toJSONString(token);
        // jsontoken??????
        String tokenEncryet = EncryptorUtil.encryet(jsonToken);
        map.put("token",tokenEncryet);
        //map.put("user",queryUserJson);//??????????????????


        HashMap<String, String> giftCardVos = cardService.queryCardByUserId(user.getUserId());
        List<WspUser> users = userService.getUser(userPhone);

        LoginResponseVo loginResponseVo = new LoginResponseVo(map, users.get(0), giftCardVos);

        //System.out.println("-----------------------" + loginVO);

        // token??????response
        response.setHeader("token", tokenEncryet);

        // ??????AccessToken???json?????????,?????????????????????????????????
        return ResultVo.success(loginResponseVo);
    }

 */











    @PostMapping("/add")
    @ApiOperation(value = "??????", notes = "??????")
    @Override
    protected Result add(@RequestBody @Validated WspUserVO dto) {
        service.save(dto);
        return new Result().success("??????/???????????????");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "??????", notes = "??????")
    @Override
    protected Result delete(@PathVariable Long id) {
        service.removeById(id);
        return new Result().success("???????????????");
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "????????????", notes = "????????????")
    @Override
    protected Result get(@PathVariable Long id) {
        return new Result().success(service.getInfoById(id));
    }

    @PutMapping("/edit")
    @ApiOperation(value = "??????", notes = "??????")
    @Override
    protected Result edit(@RequestBody @Validated WspUserVO dto) {
        service.updateById(dto);
        return new Result().success("???????????????");
    }

    @ApiOperation(value = "????????????", notes = "????????????")
    @PostMapping("/page")
    @Override
    protected Result page(@RequestBody @Validated WspUserVO dto) {
        return new Result().success(service.page(dto));
    }


}

