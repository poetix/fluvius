package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.exceptions.IllegalBranchOutputKeyException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

class BranchFlow<T> extends AbstractFlow<T> {

  private static final class ConditionalFlow<T> {
    private final Condition condition;
    private final Flow<T> ifTrue;

    private ConditionalFlow(final Condition condition, final Flow<T> ifTrue) {
      this.condition = condition;
      this.ifTrue = ifTrue;
    }

    public Condition getCondition() {
      return condition;
    }

    public Flow<T> getIfTrue() {
      return ifTrue;
    }

    <V> Conditional<V> toConditional(final FlowVisitor<V> visitor) {
      return new RealConditional<>(visitor.visitCondition(condition), ifTrue.visit(visitor));
    }
  }

  private static final class RealConditional<V> implements Conditional<V> {
    private final Condition condition;
    private final V value;

    private RealConditional(final Condition condition, final V value) {
      this.condition = condition;
      this.value = value;
    }

    @Override
    public Condition getCondition() {
      return condition;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    public <V2> Conditional<V2> map(F1<? super V, ? extends V2> mapper) {
      return new RealConditional<>(condition, mapper.apply(value));
    }
  }

  static <T> Flow<T> create(final Flow<T> defaultFlow, final Condition condition, final Flow<T> ifTrue) {
    Preconditions.checkNotNull("defaultFlow", defaultFlow);
    Preconditions.checkNotNull("condition", condition);
    Preconditions.checkNotNull("ifTrue", ifTrue);

    List<ConditionalFlow<T>> branches = new ArrayList<>(1);
    branches.add(new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }

  private static <T> Flow<T> create(final Flow<T> defaultFlow, final List<ConditionalFlow<T>> branches) {
    Key<T> defaultOutputKey = defaultFlow.getProvidedKey();
    Set<Key<?>> requiredKeys = defaultFlow.getRequiredKeys();

    for (ConditionalFlow<T> conditionalFlow : branches) {
      Key<T> conditionalOutputKey = conditionalFlow.getIfTrue().getProvidedKey();
      if (!conditionalOutputKey.equals(defaultOutputKey)) {
        throw IllegalBranchOutputKeyException.create(
            defaultOutputKey,
            conditionalFlow.getCondition().getDescription(),
            conditionalOutputKey);
      }
      requiredKeys.addAll(conditionalFlow.getIfTrue().getRequiredKeys());
    }

    return new BranchFlow<>(requiredKeys, defaultOutputKey, defaultFlow, branches);
  }

  private final Flow<T> defaultFlow;
  private final List<ConditionalFlow<T>> branches;

  private BranchFlow(final Set<Key<?>> inputKeys, final Key<T> outputKey, final Flow<T> defaultFlow, final List<ConditionalFlow<T>> branches) {
    super(inputKeys, outputKey);
    this.defaultFlow = defaultFlow;
    this.branches = branches;
  }

  @Override
  public <V> V visit(final FlowVisitor<V> visitor) {
    List<Conditional<V>> branchActions = new ArrayList<>(branches.size());
    for (final ConditionalFlow<T> conditionalFlow : branches) {
      branchActions.add(
          conditionalFlow.toConditional(visitor));
    }
    return visitor.visitBranch(getRequiredKeys(), getProvidedKey(), defaultFlow.visit(visitor), branchActions);
  }

  @Override
  public Flow<T> orIf(final Condition condition, final Flow<T> ifTrue) {
    List<ConditionalFlow<T>> branches = new ArrayList<>(this.branches);
    branches.add(new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }


}
