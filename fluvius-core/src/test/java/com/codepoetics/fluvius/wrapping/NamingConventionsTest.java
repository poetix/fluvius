package com.codepoetics.fluvius.wrapping;

import com.codepoetics.fluvius.api.annotations.KeyName;
import com.codepoetics.fluvius.api.annotations.OperationName;
import com.codepoetics.fluvius.api.annotations.StepMethod;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.KeyProvider;
import com.codepoetics.fluvius.scratchpad.Keys;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class NamingConventionsTest {

  public static final class UsefullyNamedTypeA { }
  public static final class UsefullyNamedTypeB { }
  public static final class UsefullyNamedTypeC { }

  public interface DoSomethingStep {
    @StepMethod
    UsefullyNamedTypeC getOutput(UsefullyNamedTypeA a, UsefullyNamedTypeB b);
  }

  public static final class DoSomethingStepImpl implements DoSomethingStep {
    @Override
    public UsefullyNamedTypeC getOutput(UsefullyNamedTypeA a, UsefullyNamedTypeB b) {
      return null;
    }
  }

  @OperationName("The operation")
  public interface AnnotatedStep {
    @StepMethod("result")
    String getOutput(@KeyName("inputA") UsefullyNamedTypeA a, @KeyName("inputB") UsefullyNamedTypeB b);
  }

  public static final class AnnotatedStepImpl implements AnnotatedStep {
    @Override
    public String getOutput(UsefullyNamedTypeA a, UsefullyNamedTypeB b) {
      return null;
    }
  }

  @Test
  public void testWrapUnannotatedStepClass() {
    KeyProvider keyProvider = Keys.createProvider();
    Method stepMethod = Reflection.getStepMethod(DoSomethingStepImpl.class);

    Key<?>[] requiredKeys = Reflection.getParameterKeys(stepMethod, keyProvider);
    Key<String> providedKey = Reflection.getOutputKey(stepMethod, keyProvider);

    assertEquals("Do something", Reflection.getOperationName(stepMethod));
    assertEquals("usefullyNamedTypeA", requiredKeys[0].getName());
    assertEquals("usefullyNamedTypeB", requiredKeys[1].getName());
    assertEquals("usefullyNamedTypeC", providedKey.getName());
  }

  @Test
  public void testWrapAnnotatedStepClass() {
    KeyProvider keyProvider = Keys.createProvider();
    Method stepMethod = Reflection.getStepMethod(AnnotatedStepImpl.class);

    Key<?>[] requiredKeys = Reflection.getParameterKeys(stepMethod, keyProvider);
    Key<String> providedKey = Reflection.getOutputKey(stepMethod, keyProvider);

    assertEquals("The operation", Reflection.getOperationName(stepMethod));
    assertEquals("inputA", requiredKeys[0].getName());
    assertEquals("inputB", requiredKeys[1].getName());
    assertEquals("result", providedKey.getName());
  }

  @Test
  public void operatorNames() {
    assertEquals("A", Naming.getOperationName("a"));
    assertEquals("A", Naming.getOperationName("A"));
    assertEquals("AB", Naming.getOperationName("AB"));
    assertEquals("A bc", Naming.getOperationName("ABc"));
    assertEquals("A bc d", Naming.getOperationName("ABcD"));
    assertEquals("A bc de", Naming.getOperationName("ABcDe"));
    assertEquals("XML configuration", Naming.getOperationName("XMLConfiguration"));
    assertEquals("New XML configuration", Naming.getOperationName("NewXMLConfiguration"));
  }

}
