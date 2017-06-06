package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
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

    return new SequenceFlow<>(requiredKeys, first, middle, last);
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
    List<Flow<?>> flows = new ArrayList<>(middle.size() + 2);
    flows.add(first);
    flows.addAll(middle);
    flows.add(last);
    return flows;
  }

  @Override
  public <V> V visit(final FlowVisitor<V> visitor) {
    List<V> items = new ArrayList<>();
    for (Flow<?> flow : allFlows()) {
      items.add(flow.visit(visitor));
    }
    return visitor.visitSequence(getRequiredKeys(), getProvidedKey(), items);
  }

  @Override
  public <N> Flow<N> then(final Flow<N> next) {
    List<Flow<?>> newMiddle = new ArrayList<>(middle);
    newMiddle.add(last);
    return create(first, newMiddle, next);
  }
}
