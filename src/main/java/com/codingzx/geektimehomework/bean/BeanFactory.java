package com.codingzx.geektimehomework.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * @author codingzx
 * @description
 * @date 2021/8/15 15:09
 */
@Component
public class BeanFactory {

    @Autowired
    static
    ApplicationContext  applicationContext;

    //  注释掉防止测试方法报错
    //
//    @Bean("people1")
//    @Scope("singleton")
//    public People people() {
//        People people = new People();
//        people.setId(11);
//        people.setAge(22);
//        people.setName("jack");
//        return people;
//    }

    @Data
    @ToString
    @Component
    public static class People implements Serializable, ApplicationContextAware, BeanNameAware {
        private int id;
        private int age;
        private String name;

        private static ApplicationContext applicationContext;


        public People() {
        }

        public People(int id, int age, String name) {
            this.id = id;
            this.age = age;
            this.name = name;
        }

        public People create() {
            this.applicationContext.getBeanDefinitionNames();
            return new People(666, 12, "张大壮");
        }

       // 每次容器加载时候的运行 ，且类必须有@Component注解  实现ApplicationContextAware方法会注入上下文
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            System.out.println("applicationContext正在初始化,application:"+applicationContext);
            this.applicationContext = applicationContext;
        }

        @Override
        public void setBeanName(String s) {
            this.name = s;
        }

        public static ApplicationContext getContext(){
            return applicationContext;
        }

        public static <T> T getBean(Class<T> clazz){
            if(applicationContext==null){
                System.out.println("applicationContext是空的");
            }else{
                System.out.println("applicationContext不是空的");
            }
            return applicationContext.getBean(clazz);
        }
    }


    public static void main(String[] args) {
//        People people=People.getBean(People.class);
//        applicationContext.getBeanDefinitionNames();
////        People.getBean(People.class);
//        People.getContext();
//       people.create();
    }

}
