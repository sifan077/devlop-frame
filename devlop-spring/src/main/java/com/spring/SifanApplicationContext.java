package com.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sifan应用程序上下文
 *
 * @author 思凡
 * @date 2023/03/09
 */
public class SifanApplicationContext {

    // 配置类
    private Class configClass;

    // 单例池
    private ConcurrentHashMap<String, Object> singletonObjects
            = new ConcurrentHashMap<>();

    // 储存扫描到的组件定义
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap
            = new ConcurrentHashMap<>();


    /**
     * sifan应用程序上下文构造方法
     *
     * @param configClass 配置类
     */
    public SifanApplicationContext(Class configClass) {
        this.configClass = configClass;
        // 扫描配置类
        scan(configClass);
        // 遍历类定义的Map
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            // 如果不是原型Bean对象就创建对象加入单例对象池
            if (!beanDefinition.getScope().equals("prototype")) {
                Object object = createBean(beanDefinition);
                singletonObjects.put(beanName, object);
            }
        });
    }

    /**
     * 创建bean
     *
     * @param beanDefinition bean定义
     * @return {@link Object}
     */
    private Object createBean(BeanDefinition beanDefinition) {
        // 从类定义中获取Class
        Class clazz = beanDefinition.getClazz();
        try {
            // 获取空构造参数，来创建对象返回
            Object instance = clazz.getDeclaredConstructor().newInstance();
            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(AutoWired.class)) {
                    if (field.isAnnotationPresent(Scope.class)
                            && field.getDeclaredAnnotation(Scope.class).value().equals("prototype")) {
                        Object bean = getBean(field.getName());
                        field.setAccessible(true);
                        field.set(instance, bean);
                    } else {
                        Object bean;
                        if (singletonObjects.containsKey(field.getName())) {
                            bean = singletonObjects.get(field.getName());
                        } else {
                            bean = createBean(beanDefinitionMap.get(field.getName()));
                        }
                        field.setAccessible(true);
                        field.set(instance, bean);
                    }
                }
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void scan(Class configClass) {
        // 解析配置类
        // 获取ComponentScan注解的包路径  --> 扫描
        ComponentScan componentAnnotation
                = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentAnnotation.value();
//        System.out.println(path);
        // 开始扫描
        // 加载所有类
        ClassLoader classLoader = SifanApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path.replaceAll("\\.", "/"));
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempFile : files) {
//                System.out.println(tempFile);
                // 获取类名
                if (tempFile.getName().endsWith(".class")) {
                    String className = tempFile.getName().split("\\.")[0];
//                  System.out.println(className);
                    Class<?> clazz = null;
                    try {
                        clazz = classLoader.loadClass(path + "." + className);
                        // 如果存在组件注解
                        if (clazz.isAnnotationPresent(Component.class)) {
                            // 表示是当前类一个bean
                            // 解析当前类是单例bean还是prototype的bean
                            // BeanDefinition 定义Bean
                            Component annotationComponent = clazz.getDeclaredAnnotation(Component.class);
                            // 获取注解内的Bean名
                            String beanName = annotationComponent.value();
                            // 创建类定义对象
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            // 如果扫描到的类含有Scope注解
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                // 获取Scope注解
                                Scope annotationScope = clazz.getDeclaredAnnotation(Scope.class);
                                // 在类定义中添加Scope注解的value值
                                beanDefinition.setScope(annotationScope.value());
                            } else {
                                // 如果不存在就是单例对象
                                beanDefinition.setScope("singleton");
                            }
                            // 将类定义对象存入类定义对象池
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 获取Bean对象
     *
     * @param beanName bean名字
     * @return {@link Object}
     */
    public Object getBean(String beanName) {
        //  如果类定义池存在对应beanName的key
        if (beanDefinitionMap.containsKey(beanName)) {
            // 获取对应的类定义
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            // 如果是单例bean对象，从单例对象池返回
            if (!beanDefinition.getScope().equals("prototype")) {
                return singletonObjects.get(beanName);
            } else {
                // 否则,创建一个prototype对象返回
                return createBean(beanDefinition);
            }
        } else {
            throw new NullPointerException("不存在对应的Bean对象");
        }
    }
}
