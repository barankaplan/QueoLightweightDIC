package com.queo.pretest;

import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedField;

@KaplansBean
public class DemoC implements Demo {

    @KaplansInject
    @KaplansNamedField("demoB in C")
    private DemoB demoB;



    public DemoC() {
        System.out.println("c is created only one time,even if it is used in different classes !");
        System.out.println(hashCode());
    }
}
