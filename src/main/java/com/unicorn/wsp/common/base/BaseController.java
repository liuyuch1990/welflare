package com.unicorn.wsp.common.base;

import com.unicorn.wsp.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * curd controller 接口少不需要维护 可以不继承
 * @param <S>
 * @param <D>
 */
public abstract class BaseController<S,D>{

    @Autowired
    protected S service;

    protected abstract Result add(@RequestBody D dto);

    protected abstract Result delete(@PathVariable Long id);

    protected abstract Result get(@PathVariable Long id);

    protected abstract Result edit(@RequestBody D dto);

    protected abstract Result page(@RequestBody D dto);
}
