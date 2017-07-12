package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

final class SequenceFlow<T> extends AbstractFlow<T> {

  static <T> Flow<T> create(Flow<?> first, Flow<T> last) {
    Preconditions.checkNotNull("first", first);
    Preconditions.checkNotNull("last", last);

    List<Flow<?>> sequencedFlows = new ArrayList<>();
    first.appendTo(sequencedFlows);
    last.appendTo(sequencedFlows);

    return create(sequencedFlows);
  }

  private static <T> Flow<T> create(List<Flow<?>> sequencedFlows) {
    Iterator<Flow<?>> iterator = sequencedFlows.iterator();
    Flow<?> first = iterator.next();

    Set<Key<?>> requiredKeys = new HashSet<>();
    Set<Key<?>> providedKeys = new HashSet<>();

    requiredKeys.addAll(first.getRequiredKeys());
    providedKeys.add(first.getProvidedKey());

    Flow<?> cursor = first;
    while (iterator.hasNext()) {
      cursor = iterator.next();
      Set<Key<?>> requiredKeysForStage = cursor.getRequiredKeys();
      requiredKeysForStage.removeAll(providedKeys);
      requiredKeys.addAll(requiredKeysForStage);
      providedKeys.add(cursor.getProvidedKey());
    }

    Flow<T> last = (Flow<T>) cursor;
    return new SequenceFlow<>(UUID.randomUUID(), requiredKeys, last.getProvidedKey(), sequencedFlows);
  }

  private final List<Flow<?>> sequencedFlows;

  private SequenceFlow(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<Flow<?>> sequencedFlows) {
    super(stepId, requiredKeys, providedKey);
    this.sequencedFlows = sequencedFlows;
  }

  @Override
  public <V> V visit(FlowVisitor<V> visitor) {
    List<V> items = new ArrayList<>();
    for (Flow<?> flow : sequencedFlows) {
      items.add(flow.visit(visitor));
    }
    return visitor.visitSequence(getStepId(), getRequiredKeys(), getProvidedKey(), items);
  }

  @Override
  public <N> Flow<N> then(Flow<N> next) {
    List<Flow<?>> newSequencedFlows = new ArrayList<>(sequencedFlows);
    return create(next.appendTo(newSequencedFlows));
  }

  @Override
  public List<Flow<?>> appendTo(List<Flow<?>> previousFlows) {
    previousFlows.addAll(sequencedFlows);
    return previousFlows;
  }

}
