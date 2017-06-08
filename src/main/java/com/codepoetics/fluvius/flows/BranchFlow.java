package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.exceptions.IllegalBranchOutputKeyException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    public <V> Conditional<V> toConditional(final FlowVisitor<V> visitor) {
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
  }

  static <T> Flow<T> create(final Flow<T> defaultFlow, final Condition condition, final Flow<T> ifTrue) {
    Preconditions.checkNotNull("defaultFlow", defaultFlow);
    Preconditions.checkNotNull("condition", condition);
    Preconditions.checkNotNull("ifTrue", ifTrue);

    Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>();
    branches.put(condition.getDescription(), new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }

  private static <T> Flow<T> create(final Flow<T> defaultFlow, final Map<String, ConditionalFlow<T>> branches) {
    Key<T> defaultOutputKey = defaultFlow.getProvidedKey();
    Set<Key<?>> requiredKeys = defaultFlow.getRequiredKeys();

    for (ConditionalFlow<T> conditionalFlow : branches.values()) {
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
  private final Map<String, ConditionalFlow<T>> branches;

  private BranchFlow(final Set<Key<?>> inputKeys, final Key<T> outputKey, final Flow<T> defaultFlow, final Map<String, ConditionalFlow<T>> branches) {
    super(inputKeys, outputKey);
    this.defaultFlow = defaultFlow;
    this.branches = branches;
  }

  @Override
  public <V> V visit(final FlowVisitor<V> visitor) {
    Map<String, Conditional<V>> branchActions = new LinkedHashMap<>();
    for (final ConditionalFlow<T> conditionalFlow : branches.values()) {
      branchActions.put(
          conditionalFlow.getCondition().getDescription(),
          conditionalFlow.toConditional(visitor));
    }
    return visitor.visitBranch(getRequiredKeys(), getProvidedKey(), defaultFlow.visit(visitor), branchActions);
  }

  @Override
  public Flow<T> orIf(final Condition condition, final Flow<T> ifTrue) {
    Map<String, ConditionalFlow<T>> branches = new LinkedHashMap<>(this.branches);
    branches.put(condition.getDescription(), new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }


}
