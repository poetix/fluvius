package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.compilation.FlowCompiler;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.compilation.Compilers;
import com.codepoetics.fluvius.flows.Flows;
import org.junit.Test;

import java.util.*;

public class MutationCheckingTest {

  private static final FlowCompiler compiler = Compilers.builder().mutationChecking().build();

  public static final class MutableThing {
    private String foo;
    private List<String[]> bar;
    private final String[] baz = new String[] { "p", "q" };

    public String getFoo() {
      return foo;
    }

    public void setFoo(String foo) {
      this.foo = foo;
    }

    public List<String[]> getBar() {
      return bar;
    }

    public void setBar(List<String[]> bar) {
      this.bar = bar;
    }

    public String[] getBaz() {
      return baz;
    }
  }

  private static final Key<Map<String, MutableThing[]>> mutableThings = Key.named("mutableThings");
  private static final Key<String> output = Key.named("output");

  @Test(expected = IllegalStateException.class)
  public void youCannotHideFromTheMutationChecker() throws Exception {
    Flow<String> evilFlow = Flows.obtaining(output).from(mutableThings).using("evil operation", new F1<Map<String, MutableThing[]>, String>() {
      @Override
      public String apply(Map<String, MutableThing[]> input) {
        input.get("xyzzy")[0].getBar().get(1)[1] = "changed value";
        return "bwahahaha";
      }
    });

    Map<String, MutableThing[]> myMutableThings = createMutableThings();

    compiler.compile(evilFlow)
        .run(UUID.randomUUID(), mutableThings.of(myMutableThings));
  }

  @Test(expected = IllegalStateException.class)
  public void youStillCannotHideFromTheMutationChecker() throws Exception {
    Flow<String> evilFlow = Flows.obtaining(output).from(mutableThings).using("evil operation", new F1<Map<String, MutableThing[]>, String>() {
      @Override
      public String apply(Map<String, MutableThing[]> input) {
        input.get("xyzzy")[0].getBaz()[1] = "changed value";
        return "bwahahaha";
      }
    });

    Map<String, MutableThing[]> myMutableThings = createMutableThings();

    compiler.compile(evilFlow)
        .run(UUID.randomUUID(), mutableThings.of(myMutableThings));
  }

  private Map<String, MutableThing[]> createMutableThings() {
    MutableThing myMutableThing = new MutableThing();
    myMutableThing.setBar(Arrays.asList(new String[] {"a", "b" }, new String[] {"c", "d"}));
    Map<String, MutableThing[]> myMutableThings = new HashMap<>();
    myMutableThings.put("xyzzy", new MutableThing[] { myMutableThing});
    return myMutableThings;
  }
}
