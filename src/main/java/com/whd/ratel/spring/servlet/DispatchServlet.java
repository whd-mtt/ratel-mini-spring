package com.whd.ratel.spring.servlet;

import com.whd.ratel.demo.mvc.action.DemoController;
import com.whd.ratel.spring.annotation.Autowired;
import com.whd.ratel.spring.annotation.Controller;
import com.whd.ratel.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/7 22:55
 * @apiNote Describe the function of this class in one sentence
 **/

public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    /***
     * 初始化容器，初始大小为 16
     */
    private Map<String, Object> beanMap = new ConcurrentHashMap<>(16);

    /***
     * 初始化装载beanNames的容器
     */
    private List<String> classNames = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----------调用doPost方法------------");

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //开始容器初始化的进程
        /**
         * 定位：实际上就是把配置文件读取进来
         */
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        /***
         * 加载
         */
        doScanner(contextConfig.getProperty("scanPackage"));

        /***
         * 注册
         */
        doRegister();

        /**
         * 自动依赖注入
         * 在spring中是通过调用getBean()方法触法依赖注入的
         */
        doAutoWired();


//        DemoController controller = (DemoController) beanMap.get("demoController");
//        controller.query(null, null, "whd");

        /**
         * 将@RequestMapping中配置的url和一个方法method关联上
         * 以便于从浏览器获取用户输入的url后，可以找到就听执行的方法，然后通过反射去调用
         *
         * 如果是spring-mvc会多设计一个mapping，HandlerMapping
         */
        initHandlerMapping();

    }

    private void initHandlerMapping() {

    }

    /***
     * 依赖注入
     */
    private void doAutoWired() {

        if (beanMap.isEmpty()){return;}
        beanMap.forEach( (key, value) -> {
            Field[] declaredFields = value.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isAnnotationPresent(Autowired.class)){continue;}
                Autowired autowired = field.getAnnotation(Autowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(value, beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /***
     * 注册实例到ioc容器
     */
    private void doRegister() {
        if (classNames.isEmpty()){return;}
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                /***
                 * 在spring中使用的是多个子方法(策略模式)来处理的
                 */
                if (clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    /**
                     * 在Spring中这个阶段是不会直接put instance, 而是put BeanDefinition
                     */
                    beanMap.put(beanName, clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service annotation = clazz.getAnnotation(Service.class);
                    //默认使用类名首字母小写，如果自己定义了beanName,那么优先使用自己定义的beanName
                    //如果使用了接口，使用接口的类型实现自动注入
                    //在spring中同样会分别调用不同的方法，autowiredByName autowiredByType
                    String beanName = annotation.value();
                    if ("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());
                    }

                    Object instance = clazz.newInstance();
                    beanMap.put(beanName, instance);

                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> interfa : interfaces) {
                        beanMap.put(interfa.getName(), instance);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /***
     * 扫描指定包下面的文件
     * @param packageName the specific package name
     */
    private void doScanner(String packageName) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("/" + packageName.replaceAll("\\.", "/"));
        String urlFile = url.getFile();
        if (urlFile != null){
            File classDir  = new File(urlFile);
            for (File file : classDir.listFiles()) {
                if(file.isDirectory()){
                    doScanner(packageName + "." + file.getName());
                }else {
                    classNames.add(packageName + "." + file.getName().replace(".class", ""));
                }
            }
        }
    }


    /***
     * 读取配置文件
     * @param location the config location
     */
    private void doLoadConfig(String location) {

        //在spring中是通过Reader查找和定位的
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(location.replace("classpath:", ""));
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(is)) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /***
     * 将字符串的首字母转化成小写
     * @param str original string
     * @return
     */
    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
