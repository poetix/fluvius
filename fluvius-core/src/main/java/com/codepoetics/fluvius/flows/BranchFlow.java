package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Conditional;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.FlowVisitor;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.exceptions.IllegalBranchOutputKeyException;
import com.codepoetics.fluvius.preconditions.Preconditions;

import java.util.*;

final class BranchFlow<T> extends AbstractFlow<T> {

  private static final class ConditionalFlow<T> {
    private final Condition condition;
    private final Flow<T> ifTrue;

    private ConditionalFlow(Condition condition, Flow<T> ifTrue) {
      this.condition = condition;
      this.ifTrue = ifTrue;
    }

    public Condition getCondition() {
      return condition;
    }

    public Flow<T> getIfTrue() {
      return ifTrue;
    }

    <V> Conditional<V> toConditional(FlowVisitor<V> visitor) {
      return Conditional.of(visitor.visitCondition(condition), ifTrue.visit(visitor));
    }
  }

  static <T> Flow<T> create(Flow<T> defaultFlow, Condition condition, Flow<T> ifTrue) {
    Preconditions.checkNotNull("defaultFlow", defaultFlow);
    Preconditions.checkNotNull("condition", condition);
    Preconditions.checkNotNull("ifTrue", ifTrue);

    List<ConditionalFlow<T>> branches = new ArrayList<>(1);
    branches.add(new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }

  private static <T> Flow<T> create(Flow<T> defaultFlow, List<ConditionalFlow<T>> branches) {
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

    return new BranchFlow<>(UUID.randomUUID(), requiredKeys, defaultOutputKey, defaultFlow, branches);
  }

  private final Flow<T> defaultFlow;
  private final List<ConditionalFlow<T>> branches;

  private BranchFlow(UUID stepId, Set<Key<?>> inputKeys, Key<T> outputKey, Flow<T> defaultFlow, List<ConditionalFlow<T>> branches) {
    super(stepId, inputKeys, outputKey);
    this.defaultFlow = defaultFlow;
    this.branches = branches;
  }

  @Override
  public <V> V visit(FlowVisitor<V> visitor) {
    List<Conditional<V>> branchActions = new ArrayList<>(branches.size());
    for (ConditionalFlow<T> conditionalFlow : branches) {
      branchActions.add(
          conditionalFlow.toConditional(visitor));
    }
    return visitor.visitBranch(getStepId(), getRequiredKeys(), getProvidedKey(), defaultFlow.visit(visitor), branchActions);
  }

  @Override
  public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
    List<ConditionalFlow<T>> branches = new ArrayList<>(this.branches);
    branches.add(new ConditionalFlow<>(condition, ifTrue));
    return create(defaultFlow, branches);
  }


}
