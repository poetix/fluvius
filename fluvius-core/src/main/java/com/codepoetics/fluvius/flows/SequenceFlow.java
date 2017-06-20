package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

final class SequenceFlow<T> extends AbstractFlow<T> {

  static <T> Flow<T> create(Flow<?> first, Flow<T> last) {
    Preconditions.checkNotNull("first", first);
    Preconditions.checkNotNull("last", last);

    return create(first, Collections.<Flow<?>>emptyList(), last);
  }

  private static <T> Flow<T> create(Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
    List<Flow<?>> flatLast = last.getAllFlows();
    if (flatLast.size() > 1) {
      return create(first, middle, flatLast);
    }

    Set<Key<?>> requiredKeys = first.getRequiredKeys();
    Set<Key<?>> providedKeys = new HashSet<>();
    providedKeys.add(first.getProvidedKey());

    for (Flow<?> flow : middle) {
      Set<Key<?>> requiredKeysForStage = flow.getRequiredKeys();
      requiredKeysForStage.removeAll(providedKeys);
      requiredKeys.addAll(requiredKeysForStage);
      providedKeys.add(flow.getProvidedKey());
    }

    Set<Key<?>> requiredKeysForLast = last.getRequiredKeys();
    requiredKeysForLast.removeAll(providedKeys);
    requiredKeys.addAll(requiredKeysForLast);

    return new SequenceFlow<>(UUID.randomUUID(), requiredKeys, first, middle, last);
  }

  @SuppressWarnings("unchecked")
  private static <T> Flow<T> create(Flow<?> first, List<Flow<?>> middle, List<Flow<?>> flatLast) {
    List<Flow<?>> newMiddle = new ArrayList<>(middle);
    newMiddle.addAll(flatLast);
    Flow<T> newLast = (Flow<T>) newMiddle.get(newMiddle.size() - 1);
    newMiddle.remove(newMiddle.size() - 1);
    return create(first, newMiddle, newLast);
  }

  private final Flow<?> first;
  private final List<Flow<?>> middle;
  private final Flow<T> last;

  private SequenceFlow(UUID stepId, Set<Key<?>> requiredKeys, Flow<?> first, List<Flow<?>> middle, Flow<T> last) {
    super(stepId, requiredKeys, last.getProvidedKey());
    this.first = first;
    this.middle = middle;
    this.last = last;
  }

  private List<Flow<?>> allFlows() {
    List<Flow<?>> flows = new ArrayList<>(middle.size() + 2);
    flows.add(first);
    flows.addAll(middle);
    flows.add(last);
    return flows;
  }

  @Override
  public <V> V visit(FlowVisitor<V> visitor) {
    List<V> items = new ArrayList<>();
    for (Flow<?> flow : allFlows()) {
      items.add(flow.visit(visitor));
    }
    return visitor.visitSequence(getStepId(), getRequiredKeys(), getProvidedKey(), items);
  }

  @Override
  public <N> Flow<N> then(Flow<N> next) {
    List<Flow<?>> newMiddle = new ArrayList<>(middle);
    newMiddle.add(last);
    return create(first, newMiddle, next);
  }

  @Override
  public List<Flow<?>> getAllFlows() {
    return allFlows();
  }

}
