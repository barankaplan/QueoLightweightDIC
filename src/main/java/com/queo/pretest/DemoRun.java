package com.queo.pretest;


import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedField;

@KaplansBean
public class DemoRun {

    @KaplansInject
    @KaplansNamedField("this is a class named A")
    private Demo demo;
}
