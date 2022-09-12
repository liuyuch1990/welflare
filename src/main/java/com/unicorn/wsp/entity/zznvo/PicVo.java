package com.unicorn.wsp.entity.zznvo;

import com.unicorn.wsp.entity.WspGoodsPic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PicVo extends com.unicorn.wsp.entity.WspGoods {
    List<WspGoodsPic> pics;
    String coverPath;

    // 新加的商品类型种类中文 只能放在这了
    String typeName;


}
