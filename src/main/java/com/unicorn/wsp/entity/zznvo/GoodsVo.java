package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.vo.WspGoodsVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends WspGoodsVO {

    String sellOut;

    /**
     * 迭代修改：类型列表
     * */
    List<String> typeList;

}
