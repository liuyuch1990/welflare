package com.unicorn.wsp.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.unicorn.wsp.entity.WspUser;
import com.unicorn.wsp.service.WspUserService;
import com.unicorn.wsp.utils.EncryptorUtil;
import com.unicorn.wsp.vo.AccessToken;
import com.unicorn.wsp.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    WspUserService userService;

    private static final String OPTIONS_METHOD = "OPTIONS";
    private static final String TOKEN = "token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.放行options预检请求
        String method = request.getMethod();
        if(StringUtils.equals(OPTIONS_METHOD, method.toUpperCase())) {
            log.info("options请求");
            return true;
        }

        // 临时
        /*if(1 == 1)
        return true;*/

        // 2.验证请求头中是否有token
        String tokenHeader = request.getHeader(TOKEN);
        if(StringUtils.isEmpty(tokenHeader)) {
            // 2.1 没有就直接返回前端
            this.noAuth(response, "没有认证数据");
            return false;
        }


        // 临时  如果token等于unicorn413,直接放行
        /*if(StringUtils.equals(tokenHeader, "unicorn413")){
            return true;
        }*/

        // 3.解密token(解码之后是一串json,将json转成AccessToken)，验证有效期
        try {
            String jsonToken = EncryptorUtil.decrypt(tokenHeader);
            AccessToken accessToken = JSONObject.parseObject(jsonToken, AccessToken.class);

            // 3.1 如果过期就直接返回前端
            if(new Date().getTime() - accessToken.getExpire() >= 0) {
                log.warn("token过期了");
                this.noAuth(response,"token过期");
                return false;
            }

            // 3.2 验证手机号
            // 3.2.1 根据用户名查询用户
            List<WspUser> userList = userService.getUser(accessToken.getPhone());

            // 如果根据手机号没查到，直接返回前端
            if(ObjectUtils.isEmpty(userList) || userList.size() == 0) {
                log.warn("用户不存在");
                this.noAuth(response, "认证失败");
                return false;
            }

            WspUser wspUser = userList.get(0);

            // 如果用户名不为空，重新设置token的有效时间，加密后设置到响应头中
            accessToken = new AccessToken(accessToken.getUserId(), accessToken.getPhone(), wspUser.getUserCom(), accessToken.getRoleId());
            response.setHeader(TOKEN, EncryptorUtil.encrypt(JSONObject.toJSONString(accessToken)));
            log.info("权限校验结束");
            return true;

        } catch(Exception e){
            log.error(e.getMessage(), e);
            this.noAuth(response, "服务器异常");
            return false;
        }
    }

    // 没有认证
    private void noAuth(HttpServletResponse response, String message) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.print(JSONObject.toJSONString(ResultVo.noAuth(message)));
        out.flush();
        out.close();

    }

}
