package com.springmvc.context;

import com.springmvc.annotation.AutoWired;
import com.springmvc.annotation.Controller;
import com.springmvc.annotation.Service;
import com.springmvc.xml.XmlParse;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebApplicationContext {

    // 配置文件路径
    private String contextConfigLocation;

    // 定义集合 用于存放Bean的权限 包名.类名
    List<String> classNameList = new ArrayList<>();

    // 定义Map集合用于扮演IOC容器
    Map<String, Object> iocMap = new ConcurrentHashMap<>();

    public String getContextConfigLocation() {
        return contextConfigLocation;
    }

    public void setContextConfigLocation(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    public List<String> getClassNameList() {
        return classNameList;
    }

    public void setClassNameList(List<String> classNameList) {
        this.classNameList = classNameList;
    }

    public Map<String, Object> getIocMap() {
        return iocMap;
    }

    public void setIocMap(Map<String, Object> iocMap) {
        this.iocMap = iocMap;
    }

    public WebApplicationContext() {
    }

    public WebApplicationContext(String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * 初始化Spring容器
     */
    public void onRefresh() {
        // 1.进行解析spring配置文件
        String bases = XmlParse.getBasePackages(contextConfigLocation.split(":")[1]);
        String[] packages = bases.split(",");

        // 2.进行包扫描
        for (String pack : packages) {
            executeScanPackage(pack);
        }
        // 3.实例化Bean
        executeInstance();
        // 4.自动注入
        executeAutoWired();
    }

    /**
     * 进行自动注入操作
     */

    void executeAutoWired() {
        // 从ioc容器中遍历注入，查看是否有AutoWired注解
        iocMap.forEach((k, v) -> {
            try {
                Class<?> clazz = v.getClass();
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (declaredField.isAnnotationPresent(AutoWired.class)) {
                        // 获取BeanName
                        String beanName = declaredField.getDeclaredAnnotation(AutoWired.class).value();
                        // 从IOC容器中获取对象
                        Object o = iocMap.get(beanName);
                        // 取消检查机制
                        declaredField.setAccessible(true);
                        declaredField.set(v, o);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * 实例化容器中的bean
     */
    public void executeInstance() {
        try {
            for (String className : classNameList) {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // 控制层bean
                    String beanName = clazz.getSimpleName().substring(0, 1).toLowerCase(Locale.ROOT)
                            + clazz.getSimpleName().substring(1);
                    iocMap.put(beanName, clazz.newInstance());
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    // 服务层bean
                    Service serviceAn = clazz.getDeclaredAnnotation(Service.class);
                    String beanName = serviceAn.value();
                    iocMap.put(beanName, clazz.newInstance());
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描包
     */
    void executeScanPackage(String pack) {
        URL url = this.getClass().getClassLoader()
                .getResource("/" + pack.replaceAll("\\.", "/"));
        this.getClass().getClassLoader().getResource("/" + pack.replaceAll("\\.", "/"));
        String path = url.getFile();
        File dir = new File(path);
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                executeScanPackage(pack + "." + f.getName());
            } else {
                String className = pack + "." + f.getName().replaceAll(".class", "");
                classNameList.add(className);
            }
        }
    }
}
