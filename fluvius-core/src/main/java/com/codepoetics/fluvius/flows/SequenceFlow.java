package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

class SequenceFlow<T> extends AbstractFlow<T> {

  static <T> Flow<T> create(final Flow<?> first, final Flow<T> last) {
    Preconditions.checkNotNull("first", first);
    Preconditions.checkNotNull("last", last);

      return create(first, Collections.<Flow<?>>emptyList(), last);
  }

  private static <T> Flow<T> create(final Flow<?> first, final List<Flow<?>> middle, final Flow<T> last) {
    List<Flow<?>> flatLast = last.getAllFlows();
    if (flatLast.size() > 1) {
      return create(first, middle, flatLast);
    }

    final Set<Key<?>> requiredKeys = first.getRequiredKeys();
    final Set<Key<?>> providedKeys = new HashSet<>();
    providedKeys.add(first.getProvidedKey());

    for (final Flow<?> flow : middle) {
      final Set<Key<?>> requiredKeysForStage = flow.getRequiredKeys();
      requiredKeysForStage.removeAll(providedKeys);
      requiredKeys.addAll(requiredKeysForStage);
      providedKeys.add(flow.getProvidedKey());
    }

    final Set<Key<?>> requiredKeysForLast = last.getRequiredKeys();
    requiredKeysForLast.removeAll(providedKeys);
    requiredKeys.addAll(requiredKeysForLast);

    return new SequenceFlow<>(requiredKeys, first, middle, last);
  }

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

  private SequenceFlow(final Set<Key<?>> requiredKeys, final Flow<?> first, final List<Flow<?>> middle, final Flow<T> last) {
    super(requiredKeys, last.getProvidedKey());
    this.first = first;
    this.middle = middle;
    this.last = last;
  }

  private List<Flow<?>> allFlows() {
    final List<Flow<?>> flows = new ArrayList<>(middle.size() + 2);
    flows.add(first);
    flows.addAll(middle);
    flows.add(last);
    return flows;
  }

  @Override
  public <V> V visit(final FlowVisitor<V> visitor) {
    final List<V> items = new ArrayList<>();
    for (final Flow<?> flow : allFlows()) {
      items.add(flow.visit(visitor));
    }
    return visitor.visitSequence(getRequiredKeys(), getProvidedKey(), items);
  }

  @Override
  public <N> Flow<N> then(final Flow<N> next) {
    final List<Flow<?>> newMiddle = new ArrayList<>(middle);
    newMiddle.add(last);
    return create(first, newMiddle, next);
  }

  @Override
  public List<Flow<?>> getAllFlows() {
    return allFlows();
  }

}
