package com.codingzx.geektimehomework;

import com.codingzx.geektimehomework.bean.BeanFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class GeektimeHomeworkApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void helloTest() {
        // 通过类型获取  同一个类只能由一个Bean
        BeanFactory.People people = BeanFactory.People.getBean(BeanFactory.People.class);
        System.out.println("初始化获取的people：    " + people);

        // 通过名称获取
//        ApplicationContext applicationContext = BeanFactory.People.getContext();
//        BeanFactory.People people1 = (BeanFactory.People) applicationContext.getBean("people1");
//        System.out.println("Bean中获取的people：    " + people1);

        BeanFactory.People.getContext().getBeanDefinitionNames();
//        People.getBean(People.class);
        BeanFactory.People people2 = people.create();
        System.out.println("自己主动构建的people:     " + people2);
    }

}
