package com.unicorn.wsp.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unicorn.wsp.entity.WspCardQuota;
import com.unicorn.wsp.vo.CardTypeQuota;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* <p>
*  spark
* </p>
*
* @author spark
* @since 1.0
*/
public interface WspCardQuotaMapper extends BaseMapper<WspCardQuota>{

    @Select("select t.id, t.com_num, q.goods_type, q.type_quota, d.dict_name, t.card_type_name\n" +
            "from wsp_card_type t \n" +
            "join wsp_card_quota q on t.id = q.card_type_id \n" +
            "join simple_dict d on d.dict_code = q.goods_type\n" +
            "where t.com_num =#{com} and t.id = #{card_id} and d.dict_type = 'goodsType'")
    List<CardTypeQuota> queryCardTypeQuotaList(@Param("com") String com, @Param("card_id") String cardId);

}

