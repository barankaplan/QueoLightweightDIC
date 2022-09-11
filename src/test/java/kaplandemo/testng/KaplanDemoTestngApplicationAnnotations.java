package kaplandemo.testng;


import com.queo.KaplansDependencyInjector;
import com.queo.annotations.KaplansBean;
import com.queo.models.ModelForServiceDetails;
import com.queo.pretest.*;
import com.queo.services.LightweightDIC;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;


public class KaplanDemoTestngApplicationAnnotations {


    private LightweightDIC lightweightDIC;
    private Collection<ModelForServiceDetails> modelForServiceDetails;
    private ModelForServiceDetails modelForServiceDetailsDemoA;
    private ModelForServiceDetails modelForServiceDetailsDemoB;
    private ModelForServiceDetails modelForServiceDetailsDemoC;
    private ModelForServiceDetails modelForServiceDetailsDemo;
    private ModelForServiceDetails modelForServiceDetailsDemoRun;

    @BeforeTest
    public void init() throws InterruptedException {
        lightweightDIC = KaplansDependencyInjector.run(DemoRun.class);
        modelForServiceDetails = lightweightDIC.getServicesByAnnotation(KaplansBean.class);
        modelForServiceDetails = lightweightDIC.getServicesByAnnotation(KaplansBean.class);
        modelForServiceDetailsDemoA = lightweightDIC.getServiceDetails(DemoA.class);
        modelForServiceDetailsDemoB = lightweightDIC.getServiceDetails(DemoB.class);
        modelForServiceDetailsDemoC = lightweightDIC.getServiceDetails(DemoC.class);
        modelForServiceDetailsDemo = lightweightDIC.getServiceDetails(Demo.class);
        modelForServiceDetailsDemoRun = lightweightDIC.getServiceDetails(DemoRun.class);

    }
    @Test(groups = {"inject"})
    public void checkDemoAHasFields() throws InterruptedException {
        Collection<String> stringCollectionOfFields= new HashSet<>();

        stringCollectionOfFields.add("private com.queo.pretest.DemoB com.queo.pretest.DemoA.demoB");
        stringCollectionOfFields.add("private com.queo.pretest.DemoC com.queo.pretest.DemoA.demoC");

        Field[] autowireAnnotatedFields = modelForServiceDetailsDemoA.getAutowireAnnotatedFields();
        Function<? super Field, ?> kla= new Function<Field, Object>() {
            @Override
            public Object apply(Field field) {
                return field.toString();
            }
        };

        boolean containsAll= Arrays.stream(autowireAnnotatedFields).toList().stream()
                .map(kla).toList().containsAll(stringCollectionOfFields);

        Assert.assertTrue(containsAll);

    }

    @Test(groups = {"bean"})
    public void checkDemoAHasBean() throws InterruptedException {

        var v1=modelForServiceDetailsDemoA.getBeans();
        var v2=modelForServiceDetailsDemoA.getAnnotation();
        var v3=modelForServiceDetailsDemoA.getServiceType();
        var v4=modelForServiceDetailsDemoA.getScopeType();


    }


    @Test(groups = {"name"})
    public void checkDemoAHasName() throws InterruptedException {
       String instanceName=modelForServiceDetailsDemoA.getInstanceName();
       String demoAName="this is a class named A";
       Assert.assertEquals(instanceName,demoAName);
    }


    @Test(groups = {"annotated"})
    public void test() throws InterruptedException {
        Collection<ModelForServiceDetails> mkolle = new HashSet<>();
        mkolle.add(modelForServiceDetailsDemoA);
        mkolle.add(modelForServiceDetailsDemoB);
        mkolle.add(modelForServiceDetailsDemoC);
        mkolle.add(modelForServiceDetailsDemo);
        mkolle.add(modelForServiceDetailsDemoRun);


        lightweightDIC.getServicesByAnnotation(KaplansBean.class).forEach(System.out::println);

        var bool=lightweightDIC.getServicesByAnnotation(KaplansBean.class).containsAll(mkolle);
        System.out.println(bool);

        System.out.println("------");
        System.out.println(modelForServiceDetailsDemoA.getInstanceName());
        Field[] autowireAnnotatedFields = modelForServiceDetailsDemoA.getAutowireAnnotatedFields();

        System.out.println("------");






        System.out.println("------");


        Function<? super Field, ?> kaplans= new Function<Field, Object>() {
            @Override
            public Object apply(Field field) {
                return Arrays.toString(field.getDeclaredAnnotations());
            }
        };
        Arrays.stream(autowireAnnotatedFields).toList().stream().map(kaplans).
                forEach(System.out::println);
    }




}