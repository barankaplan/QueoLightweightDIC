package kaplandemo.testng;


import com.queo.KaplansDependencyInjector;
import com.queo.annotations.KaplansBean;
import com.queo.enums.ScopeType;
import com.queo.models.ModelForDependencies;
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
import java.util.function.Predicate;


public class KaplansDemoTestngApplication {


    private LightweightDIC lightweightDIC;
    private Collection<ModelForServiceDetails> modelForServiceDetails;
    private ModelForServiceDetails modelForServiceDetailsDemoA;
    private ModelForServiceDetails modelForServiceDetailsDemoB;
    private ModelForServiceDetails modelForServiceDetailsDemoC;
    private ModelForServiceDetails modelForServiceDetailsDemoD;
    private ModelForServiceDetails modelForServiceDetailsDemoRun;

    @BeforeTest
    public void init() {
        lightweightDIC = KaplansDependencyInjector.run(DemoRun.class);
        modelForServiceDetails = lightweightDIC.getServicesByAnnotation(KaplansBean.class);
        modelForServiceDetailsDemoA = lightweightDIC.getServiceDetails(DemoA.class);
        modelForServiceDetailsDemoB = lightweightDIC.getServiceDetails(DemoB.class);
        modelForServiceDetailsDemoC = lightweightDIC.getServiceDetails(DemoC.class);
        modelForServiceDetailsDemoD = lightweightDIC.getServiceDetails(DemoNotAnnotated.class);
        modelForServiceDetailsDemoRun = lightweightDIC.getServiceDetails(DemoRun.class);

    }

    @Test(groups = {"Inject"})
    public void checkIfDemoAHasFields() {
        Collection<String> stringCollectionOfFields = new HashSet<>();

        stringCollectionOfFields.add("private com.queo.pretest.DemoB com.queo.pretest.DemoA.demoB");
        stringCollectionOfFields.add("private com.queo.pretest.DemoC com.queo.pretest.DemoA.demoC");

        Field[] autowireAnnotatedFields = modelForServiceDetailsDemoA.getAutowireAnnotatedFields();
        Function<? super Field, ?> kla = new Function<Field, Object>() {
            @Override
            public Object apply(Field field) {
                return field.toString();
            }
        };

        boolean containsAll = Arrays.stream(autowireAnnotatedFields).toList().stream()
                .map(kla).toList().containsAll(stringCollectionOfFields);

        Assert.assertTrue(containsAll);

    }

    @Test(groups = {"Bean"})
    public void checkIfDemoAIsSingleton() {
        var scopeType = modelForServiceDetailsDemoA.getScopeType();
        Assert.assertEquals(scopeType, ScopeType.SINGLETON);
    }

    @Test(groups = {"Bean"})
    public void checkIFDemoAHasName() throws InterruptedException {
        String instanceName = modelForServiceDetailsDemoA.getInstanceName();
        String demoAName = "this is a class named A";
        Assert.assertEquals(instanceName, demoAName);
    }


    @Test(groups = {"bean"})
    public void checkIfDemoAMatchesInDemoRunAsNamed() throws InterruptedException {
        var autowireAnnotatedFields = modelForServiceDetailsDemoRun.getResolvedFields();
        Function<? super ModelForDependencies, ?> getKaplansNamedField = new Function<ModelForDependencies, Object>() {
            @Override
            public Object apply(ModelForDependencies modelForDependencies) {
                return modelForDependencies.getInstanceName();
            }
        };
        String demoAName = "this is a class named A";

        String demoANameNotMatched = "this iss aa class named A";

        var isMatched = autowireAnnotatedFields.stream().map(getKaplansNamedField).toList().contains(demoAName);
        Assert.assertTrue(isMatched);
        var isNotMatched = autowireAnnotatedFields.stream().map(getKaplansNamedField).toList().contains(demoANameNotMatched);
        Assert.assertFalse(isNotMatched);
    }


    @Test(groups = {"annotated"})
    public void checkIfAllAnnotatedClassesFetched() throws InterruptedException {
        Collection<ModelForServiceDetails> listOfAnnotatedClass = new HashSet<>();
        listOfAnnotatedClass.add(modelForServiceDetailsDemoA);
        listOfAnnotatedClass.add(modelForServiceDetailsDemoB);
        listOfAnnotatedClass.add(modelForServiceDetailsDemoC);
//      listOfAnnotatedClass.add(modelForServiceDetailsDemoD);// it is not annotated!
        listOfAnnotatedClass.add(modelForServiceDetailsDemoRun);

        var containsAll = modelForServiceDetails.containsAll(listOfAnnotatedClass);
        Assert.assertTrue(containsAll);
    }

    @Test(groups = {"annotated"})
    public void checkIfAllAnnotatedClassesFetchedUsingNotAnnotatedClass() throws InterruptedException {
        Collection<ModelForServiceDetails> listOfAnnotatedClass = new HashSet<>();
        listOfAnnotatedClass.add(modelForServiceDetailsDemoA);
        listOfAnnotatedClass.add(modelForServiceDetailsDemoB);
        listOfAnnotatedClass.add(modelForServiceDetailsDemoC);
        listOfAnnotatedClass.add(modelForServiceDetailsDemoD);// it is not annotated!
        listOfAnnotatedClass.add(modelForServiceDetailsDemoRun);

        var containsAll = modelForServiceDetails.containsAll(listOfAnnotatedClass);
        Assert.assertFalse(containsAll);
    }


    @Test(groups = {"annotated"})
    public void checkIfFieldsIndDemoAareAnnotatedByKaplansInject() throws InterruptedException {

        Field[] autowireAnnotatedFields = modelForServiceDetailsDemoA.getAutowireAnnotatedFields();


        Function<? super Field, ?> kaplans = new Function<Field, Object>() {
            @Override
            public String apply(Field field) {
                return Arrays.toString(field.getDeclaredAnnotations());
            }
        };


        Predicate<Object> predicate= new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                return o.toString().contains("KaplansInject");

            }
        };
        var fieldsHaveKaplansInjectAnnotation=Arrays.stream(autowireAnnotatedFields).toList().stream().map(kaplans).anyMatch(predicate);
        Assert.assertTrue(fieldsHaveKaplansInjectAnnotation);

    }

}