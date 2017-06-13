package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.flows.Flows;
import com.codepoetics.fluvius.scratchpad.Keys;
import org.junit.Test;

import java.util.*;

public class MutationCheckingTest {

  public static final class MutableThing {
    private String foo;
    private List<String[]> bar;
    private final String[] baz = new String[] { "p", "q" };

    public String getFoo() {
      return foo;
    }

    public void setFoo(final String foo) {
      this.foo = foo;
    }

    public List<String[]> getBar() {
      return bar;
    }

    public void setBar(final List<String[]> bar) {
      this.bar = bar;
    }

    public String[] getBaz() {
      return baz;
    }
  }

  private static final Key<Map<String, MutableThing[]>> mutableThings = Keys.named("mutableThings");
  private static final Key<String> output = Keys.named("output");

  @Test(expected = IllegalStateException.class)
  public void youCannotHideFromTheMutationChecker() {
    Flow<String> evilFlow = Flows.obtaining(output).from(mutableThings).using("evil operation", new F1<Map<String, MutableThing[]>, String>() {
      @Override
      public String apply(final Map<String, MutableThing[]> input) {
        input.get("xyzzy")[0].getBar().get(1)[1] = "changed value";
        return "bwahahaha";
      }
    });

    Map<String, MutableThing[]> myMutableThings = createMutableThings();

    Flows.compile(evilFlow, Visitors.mutationChecking(Visitors.getDefault())).run(UUID.randomUUID(), mutableThings.of(myMutableThings));
  }

  @Test(expected = IllegalStateException.class)
  public void youStillCannotHideFromTheMutationChecker() {
    Flow<String> evilFlow = Flows.obtaining(output).from(mutableThings).using("evil operation", new F1<Map<String, MutableThing[]>, String>() {
      @Override
      public String apply(final Map<String, MutableThing[]> input) {
        input.get("xyzzy")[0].getBaz()[1] = "changed value";
        return "bwahahaha";
      }
    });

    Map<String, MutableThing[]> myMutableThings = createMutableThings();

    Flows.compile(evilFlow, Visitors.mutationChecking(Visitors.getDefault())).run(UUID.randomUUID(), mutableThings.of(myMutableThings));
  }

  private Map<String, MutableThing[]> createMutableThings() {
    MutableThing myMutableThing = new MutableThing();
    myMutableThing.setBar(Arrays.asList(new String[] {"a", "b" }, new String[] {"c", "d"}));
    Map<String, MutableThing[]> myMutableThings = new HashMap<>();
    myMutableThings.put("xyzzy", new MutableThing[] { myMutableThing});
    return myMutableThings;
  }
}
