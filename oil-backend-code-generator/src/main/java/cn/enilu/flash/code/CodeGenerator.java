package cn.enilu.flash.code;


import java.util.List;

/**
 * 手动生成代码<br>
 * 虽然本系统提供了代码生成插件方便根据java实体生成相关业务代码，但是部分idea版本存在兼容性问题（且也没有提供eclipse的插件），因此可以使用本工具类来手动生成代码
 *
 * @Author enilu
 * @Date 2021/5/17 11:28
 * @Version 1.0
 */
public class CodeGenerator {
    public static void main(String[] args) throws Exception {
        //要生成的代码表名称
        String tableName = "wsp_role";
        generator(tableName);
   }

    private static void generator(String talbeName) throws Exception {

        String basepath = System.getProperty("user.dir");
        String[] arr = talbeName.split("\\.");
//        String entityName = arr[arr.length-1];
        String userPath ="bs";
//        String basePackage = talbeName.split(".bean.entity.")[0];
        List<String> param = Lists.newArrayList();
        param.add("-basePath");
        param.add(basepath);
        param.add("-i");
        param.add(talbeName);
        param.add("-u");
        param.add("/"+userPath);
        param.add("-p");
        param.add("com.unicorn.wsp");
        param.add("-v");
        param.add("all");
        param.add("-mod");
        param.add("entity");
        param.add("-ctr");
        param.add("controller");
        param.add("-sev");
        param.add("service");
        param.add("-repo");
        param.add("mapper");
        param.add("controller");
        param.add("service");
        param.add("view");
        param.add("mapper");
        String[] args = param.toArray(new String[param.size()]);
        Generator.generator(args);


    }
}
