package kaplandemo.testng;


import com.queo.KaplansDependencyInjector;
import com.queo.annotations.KaplansBean;
import com.queo.models.ModelForServiceDetails;
import com.queo.pretest.*;
import com.queo.services.LightweightDIC;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collection;


public class KaplanDemoTestngApplicationAnnotations {


    private LightweightDIC lightweightDIC;
    private Collection<ModelForServiceDetails> modelForServiceDetails;
    private ModelForServiceDetails modelForServiceDetailsDemoA;
    private ModelForServiceDetails modelForServiceDetailsDemoB;
    private ModelForServiceDetails modelForServiceDetailsDemoC;
    private ModelForServiceDetails modelForServiceDetailsDemo;
    private ModelForServiceDetails modelForServiceDetailsDemoRun;

    @BeforeTest
    public void setup() throws InterruptedException {
         lightweightDIC= KaplansDependencyInjector.run(DemoRun.class);
         modelForServiceDetails =lightweightDIC.getServicesByAnnotation(KaplansBean.class);
         modelForServiceDetailsDemoA =lightweightDIC.getServiceDetails(DemoA.class);
         modelForServiceDetailsDemoB =lightweightDIC.getServiceDetails(DemoB.class);
         modelForServiceDetailsDemoC =lightweightDIC.getServiceDetails(DemoC.class);
         modelForServiceDetailsDemo =lightweightDIC.getServiceDetails(Demo.class);
         modelForServiceDetailsDemoRun =lightweightDIC.getServiceDetails(DemoRun.class);

    }

    @Test(groups = {"annotated"})
    public void test() throws InterruptedException {
       lightweightDIC.getAllServices().forEach(System.out::println);
    }


}