package com.codepoetics.fluvius.visitors;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;
import com.codepoetics.fluvius.mutation.MutableState;

import java.util.*;

final class MutationCheckingVisitor<V> implements FlowVisitor<V> {

  private final FlowVisitor<V> innerVisitor;

  MutationCheckingVisitor(final FlowVisitor<V> innerVisitor) {
    this.innerVisitor = innerVisitor;
  }

  @Override
  public <T> V visitSingle(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return innerVisitor.visitSingle(stepId, requiredKeys, providedKey, new MutationCheckingOperation<>(operation));
  }

  @Override
  public <T> V visitSequence(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<V> items) {
    return innerVisitor.visitSequence(stepId, requiredKeys, providedKey, items);
  }

  @Override
  public <T> V visitBranch(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final V defaultBranch,
                           final List<Conditional<V>> conditionalBranches) {
    return innerVisitor.visitBranch(stepId, requiredKeys, providedKey, defaultBranch, conditionalBranches);
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return innerVisitor.visitCondition(new MutationCheckingCondition(condition));
  }

  private static final class MutationCheckingOperation<T> implements Operation<T> {

    private final Operation<T> innerOperation;

    private MutationCheckingOperation(final Operation<T> innerOperation) {
      this.innerOperation = innerOperation;
    }

    @Override
    public String getName() {
      return innerOperation.getName();
    }

    @Override
    public T run(final Scratchpad scratchpad) throws Exception {
      final Map<Key<?>, Object> before = getMutableState(scratchpad);

      T result = innerOperation.run(scratchpad);

      final Map<Key<?>, Object> after = getMutableState(scratchpad);
      testForMutation(before, after);

      return result;
    }

  }

  private static final class MutationCheckingCondition implements Condition {

    private final Condition innerCondition;

    private MutationCheckingCondition(final Condition innerCondition) {
      this.innerCondition = innerCondition;
    }

    @Override
    public String getDescription() {
      return innerCondition.getDescription();
    }

    @Override
    public boolean test(final UUID flowId, final Scratchpad scratchpad) {
      final Map<Key<?>, Object> before = getMutableState(scratchpad);
      final boolean result = innerCondition.test(flowId, scratchpad);
      final Map<Key<?>, Object> after = getMutableState(scratchpad);
      testForMutation(before, after);
      return result;
    }
  }

  private static Map<Key<?>, Object> getMutableState(final Scratchpad scratchpad) {
    final Map<Key<?>, Object> before = scratchpad.toMap();
    final Map<Key<?>, Object> beforeState = new HashMap<>(before.size());
    for (final Map.Entry<Key<?>, Object> entry : before.entrySet()) {
      beforeState.put(entry.getKey(), MutableState.of(entry.getValue()));
    }
    return beforeState;
  }

  private static void testForMutation(final Map<Key<?>, Object> before, final Map<Key<?>, Object> after) {
    for (final Key<?> key : before.keySet()) {
      final Object beforeValue = before.get(key);
      final Object afterValue = after.get(key);
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
