package com.queo.pretest;


import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedClass;
import com.queo.annotations.KaplansNamedField;


@KaplansBean
@KaplansNamedClass("this is a class named A")
public class DemoA implements Demo {

    @KaplansInject
    @KaplansNamedField("this is a class named B")
    private DemoB demoB;

    @KaplansInject
    private DemoC demoC;

    public DemoA() {
        System.out.println("a is created only one time,even if it is used in different classes !");
        System.out.println(hashCode());
    }
}
