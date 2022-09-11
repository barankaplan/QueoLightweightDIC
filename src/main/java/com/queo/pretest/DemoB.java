package com.queo.pretest;//package com.queo.pretest;


import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansNamedClass;

@KaplansBean
@KaplansNamedClass("this is a class named B")
public class DemoB {

    public DemoB() {
        System.out.println("b is created only one time,even if it is used in different classes !");
        System.out.println(hashCode());
    }
}
