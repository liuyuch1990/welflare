package com.unicorn.wsp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public interface TespMapper {

 void queryGiftCardTypeList();
}
