package com.whd.ratel.spring.framework.context.support;

import com.whd.ratel.spring.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author whd.java@gmail.com
 * @date 2018/12/10 22:32
 * @apiNote 用来定位配置文件，对配置文件进行查找，读取，解析
 **/
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    /***
     * 在配置文件中用来获取自动扫描的包名的key
     */
    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String... locations){
        //在spring中是通过Reader查找和定位的
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream is = classLoader.getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (Objects.nonNull(is)) {is.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }


    /***
     * 递归扫描指定包下面的class文件，并且保存到内存中
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
                    registryBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
                }
            }
        }
    }

    public List<String> loadBeanDefinitions(){
        return this.registryBeanClasses;
    }


    /***
     * 每注册一个className，就返回一个BeanDefinition自己包装，只是为了对配置信息进行一个包装
     * @param className
     * @return
     */
    public BeanDefinition registerBean(String className){
        if (this.registryBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }


    public Properties getConfig(){
        return this.config;
    }

    /***
     * 将字符串的首字母转化成小写
     * @param str original string
     * @return str
     */
    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
