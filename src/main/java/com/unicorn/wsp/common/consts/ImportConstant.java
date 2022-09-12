package com.unicorn.wsp.common.consts;

import java.util.HashMap;
import java.util.Map;

public class ImportConstant {

    public static Map<String,Integer> traineeMap=new HashMap<>();

    public static Map<String,Integer> trainingCategoryMap=new HashMap<>();

    public static Map<String,Integer> trainingClassTypeMap=new HashMap<>();

    public static Map<Integer,String> excelTemplatePathMap =new HashMap<>();

    public static Map<Integer,String> excelProjectInfoMap =new HashMap<>();

    public static Map<Integer,String> customerTypeMap=new HashMap<>();

    public static Map<Integer,String> customerReletionMap=new HashMap<>();

    public static Map<Integer,String> companyTypeMap=new HashMap<>();

    public static Map<String,Integer> courseTypeMap=new HashMap<>();

    public static Map<String,Integer> courseClassificationMap=new HashMap<>();

    public static Map<String,Integer> genderMap=new HashMap<>();

    public static Map<String,Integer> teacherTypeMap=new HashMap<>();

    public static Map<Integer,String> siteTypeMap=new HashMap<>();

    public static Map<String,Integer> positionLevelMap=new HashMap<>();

    public static Map<String,Integer> projectItemName =new HashMap<>();

    static{
        traineeMap.put("局级领导",1);
        traineeMap.put("处级领导",2);
        traineeMap.put("科级领导",3);
        traineeMap.put("科级以下公务员",4);
        traineeMap.put("国企领导",5);
        traineeMap.put("国企员工",6);
        traineeMap.put("局级和处级",7);
        traineeMap.put("处级和科级",8);
        trainingCategoryMap.put("党建与党性教育",1);
        trainingCategoryMap.put("职务技能（权力机关）",2);
        trainingCategoryMap.put("业务技能",3);
        trainingCategoryMap.put("综合能力培养",4);
        trainingCategoryMap.put("创新创业",5);
        trainingCategoryMap.put("国际形势和发展规划",6);
        trainingCategoryMap.put("行政服务与社会治理",7);
        trainingCategoryMap.put("各产业发展",8);
        trainingCategoryMap.put("企业经营管理",9);
        trainingCategoryMap.put("人才工作",10);
        trainingCategoryMap.put("精准扶贫",11);
        trainingCategoryMap.put("其他",12);

        trainingClassTypeMap.put("外出",1);
        trainingClassTypeMap.put("来深",2);
        trainingClassTypeMap.put("国际",3);
        trainingClassTypeMap.put("其他",4);

        excelTemplatePathMap.put(1,"/template/客户信息导入模板.xlsx");
        excelTemplatePathMap.put(2,"/template/学员信息导入模板.xlsx");
        excelTemplatePathMap.put(3,"/template/教师信息导入模板.xlsx");
        excelTemplatePathMap.put(4,"/template/课程信息导入模板.xlsx");
        excelTemplatePathMap.put(5,"/template/教学点信息导入模板.xlsx");
        excelTemplatePathMap.put(6,"/template/培训班列表导入模板.xlsx");
        excelTemplatePathMap.put(7,"/template/培训班导入课表信息模板.xlsx");
        excelTemplatePathMap.put(8,"/template/培训班导入学员模板.xlsx");
        excelTemplatePathMap.put(9,"/template/酒店信息导入模板.xlsx");
        excelTemplatePathMap.put(10,"/template/餐厅信息导入模板.xlsx");
        excelTemplatePathMap.put(11,"/template/供应商信息导入模板.xlsx");
        excelTemplatePathMap.put(12,"/template/项目信息导入模板.xlsx");

        excelProjectInfoMap.put(0,"项目立项");
        excelProjectInfoMap.put(1,"项目执行");
        excelProjectInfoMap.put(2,"项目决算");
        excelProjectInfoMap.put(3,"项目完成");

        customerTypeMap.put(1,"甲方");
        customerTypeMap.put(2,"乙方");
        customerTypeMap.put(3,"甲方乙方");

        customerReletionMap.put(1,"正式");
        customerReletionMap.put(2,"潜在");

        companyTypeMap.put(1,"机关事业单位");
        companyTypeMap.put(2,"国企");
        companyTypeMap.put(3,"私企");

        courseTypeMap.put("现场教学",2);
        courseTypeMap.put("专题讲座教学",3);
        courseTypeMap.put("研讨",4);
        courseTypeMap.put("行动学习",5);
        courseTypeMap.put("远程教学",6);

        courseClassificationMap.put("党建与党性教育",1);
        courseClassificationMap.put("职务技能（权力机关）",2);
        courseClassificationMap.put("业务技能",3);
        courseClassificationMap.put("综合能力培养",4);
        courseClassificationMap.put("创新创业",5);
        courseClassificationMap.put("国际形势和发展规划",6);
        courseClassificationMap.put("行政服务与社会治理",7);
        courseClassificationMap.put("各产业发展",8);
        courseClassificationMap.put("企业经营管理",9);
        courseClassificationMap.put("人才工作",10);
        courseClassificationMap.put("精准扶贫",11);
        courseClassificationMap.put("其他",12);

        genderMap.put("男",1);
        genderMap.put("女",2);

        teacherTypeMap.put("专职",1);
        teacherTypeMap.put("兼职",2);
        teacherTypeMap.put("特聘",3);

        siteTypeMap.put(1,"自有教学点");
        siteTypeMap.put(2,"合作教学点");

        positionLevelMap.put("正部",1);
        positionLevelMap.put("副部",2);
        positionLevelMap.put("正厅（局）",3);
        positionLevelMap.put("副厅（局）",4);
        positionLevelMap.put("正处",5);
        positionLevelMap.put("副处",6);
        positionLevelMap.put("正科",7);
        positionLevelMap.put("副科",8);
        positionLevelMap.put("办事员",9);

        projectItemName.put("课酬",1);
        projectItemName.put("交通",2);
        projectItemName.put("餐饮",3);
        projectItemName.put("个税",4);
        projectItemName.put("资料费",5);
        projectItemName.put("培训费",6);
        projectItemName.put("考官费",7);
        projectItemName.put("短信费",8);
        projectItemName.put("师资费",9);
        projectItemName.put("笔试命题费",10);
        projectItemName.put("面试命题费",11);
        projectItemName.put("阅卷费",12);
        projectItemName.put("摄影摄像",13);
        projectItemName.put("代付体检",14);
        projectItemName.put("考务人员工作餐",15);
        projectItemName.put("场地费",16);
        projectItemName.put("其他",17);
        projectItemName.put("住宿费",18);
        projectItemName.put("保险费",19);
        projectItemName.put("教室租金",20);
        projectItemName.put("数据采集费",21);
        projectItemName.put("差旅费",22);
        projectItemName.put("会议费",23);
        projectItemName.put("国际合作与交流费",24);
        projectItemName.put("专家咨询费",25);
        projectItemName.put("劳务费",26);
        projectItemName.put("印刷出版费",27);
        projectItemName.put("税费",28);

    }
}
