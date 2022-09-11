package com.queo.pretest;


import com.queo.annotations.KaplansBean;
import com.queo.annotations.KaplansInject;
import com.queo.annotations.KaplansNamedField;

@KaplansBean
public class DemoRun {

    @KaplansInject
    @KaplansNamedField("baran")
    private Demo demo;
}
