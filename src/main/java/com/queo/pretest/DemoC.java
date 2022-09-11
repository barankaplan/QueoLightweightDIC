package com.queo.pretest;

import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedClass;
import com.queo.annotations.KaplansNamedField;

@KaplansBean
@KaplansNamedClass("this is a class named C")
public class DemoC implements Demo {

    @KaplansInject
    @KaplansNamedField("this is a class named B")
    private DemoB demoB;



    public DemoC() {
        System.out.println("c is created only one time,even if it is used in different classes !");
        System.out.println(hashCode());
    }
}
