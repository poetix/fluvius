package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.mutation.MutableState;

import java.util.*;

final class MutationCheckingVisitor<V> implements FlowVisitor<V> {

  private final FlowVisitor<V> innerVisitor;

  MutationCheckingVisitor(FlowVisitor<V> innerVisitor) {
    this.innerVisitor = innerVisitor;
  }

  @Override
  public <T> V visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return innerVisitor.visitSingle(stepId, requiredKeys, providedKey, new MutationCheckingOperation<>(operation));
  }

  @Override
  public <T> V visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<V> items) {
    return innerVisitor.visitSequence(stepId, requiredKeys, providedKey, items);
  }

  @Override
  public <T> V visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, V defaultBranch,
                           List<Conditional<V>> conditionalBranches) {
    return innerVisitor.visitBranch(stepId, requiredKeys, providedKey, defaultBranch, conditionalBranches);
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return innerVisitor.visitCondition(new MutationCheckingCondition(condition));
  }

  private static final class MutationCheckingOperation<T> implements Operation<T> {

    private final Operation<T> innerOperation;

    private MutationCheckingOperation(Operation<T> innerOperation) {
      this.innerOperation = innerOperation;
    }

    @Override
    public String getName() {
      return innerOperation.getName();
    }

    @Override
    public T run(Scratchpad scratchpad) throws Exception {
      Map<Key<?>, Object> before = getMutableState(scratchpad);

      T result = innerOperation.run(scratchpad);

      Map<Key<?>, Object> after = getMutableState(scratchpad);
      testForMutation(before, after);

      return result;
    }

  }

  private static final class MutationCheckingCondition implements Condition {

    private final Condition innerCondition;

    private MutationCheckingCondition(Condition innerCondition) {
      this.innerCondition = innerCondition;
    }

    @Override
    public String getDescription() {
      return innerCondition.getDescription();
    }

    @Override
    public boolean test(UUID flowId, Scratchpad scratchpad) {
      Map<Key<?>, Object> before = getMutableState(scratchpad);
      boolean result = innerCondition.test(flowId, scratchpad);
      Map<Key<?>, Object> after = getMutableState(scratchpad);
      testForMutation(before, after);
      return result;
    }
  }

  private static Map<Key<?>, Object> getMutableState(Scratchpad scratchpad) {
    Map<Key<?>, Object> before = scratchpad.toMap();
    Map<Key<?>, Object> beforeState = new HashMap<>(before.size());
    for (Map.Entry<Key<?>, Object> entry : before.entrySet()) {
      beforeState.put(entry.getKey(), MutableState.of(entry.getValue()));
    }
    return beforeState;
  }

  private static void testForMutation(Map<Key<?>, Object> before, Map<Key<?>, Object> after) {
    for (Key<?> key : before.keySet()) {
      Object beforeValue = before.get(key);
      Object afterValue = after.get(key);
      if (!Objects.equals(beforeValue, afterValue)) {
        throw new IllegalStateException(
            String.format("Operation mutated value %s in scratchpad from %s to %s",
                key.getName(),
                beforeValue,
                afterValue));
      }
    }
  }
}
