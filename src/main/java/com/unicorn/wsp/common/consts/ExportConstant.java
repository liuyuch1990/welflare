package com.unicorn.wsp.common.consts;

/**
 * 导出信息
 */
public interface ExportConstant {

    String[] STUDENT_EXPORT_HEADER =new String[]{"学员姓名","学员编号","性别","学员单位名称","当前职务","身份证号码","生日","手机号码","其他联系方式","创建人","创建日期","职务级别"} ;

    String STUDENT_EXPORT_TITLE="学员";

    String EXPORT_SUCCESS="导出成功!";

    String[] PROJECT_INFO_HEADER = new String[]{"油库代码","油库名称","油罐编号","容量(m3)","用途","建筑形式","结构形式","现储油料","开工年度","竣工年度","几何尺寸(m)(直径/壁高(长度)/顶高)(长宽高)","罐前阀门","油罐呼吸阀","阻火器","呼吸管道","泡沫发生器","罐壁人孔(个)","罐顶通风孔(个)","覆土罐测量口是否引出罐室外","金属柔性短管长度(mm)","排污方式","内浮盘型号","油罐技术状况","油罐在线检测情况","是否严重变形","是否空闲","最近一次大修","最近一次防腐年度"};

    String[] PROJECT_INFO_HEADER1 = new String[]{"金属","进油管线第一道阀门","进油管线第一道阀门","进油管线第二道阀门","出油管线第一道阀门","出油管线第二道阀门"};

    String[] PROJECT_INFO_HEADER2 = new String[]{"公称容量","实际容量","安全容量","立式","卧式","离壁","贴壁","连拱壁","内浮顶","砼插顶","桁架","品种","数量(t)","形式","编号","公称口径(mm)","使用年度","形式","编号","公称口径(mm)","使用年度","形式","编号","公称口径(mm)","使用年度","形式","编号","公称口径(mm)","使用年度","型号","公称口径(mm)","型号","公称口径(mm)","公称口径","长度(m)","是否专用","型号","数量","鉴定等级","鉴定年度","鉴定单位","油罐未清洗时间(年)","检测年度","检测结论","年度","技术方法","外壁","内壁","罐底"};


    String PROJECT_INFO_TITLE ="项目信息";

    String PROJECT_INFO_NAME ="project_info.xlsx";
}
