package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.Condition;
import com.codepoetics.fluvius.api.Flow;
import com.codepoetics.fluvius.api.functional.Predicate;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.conditions.Conditions;

import java.util.*;

abstract class AbstractFlow<T> implements Flow<T> {

  private final UUID stepId;
  private final Set<Key<?>> requiredKeys;
  private final Key<T> providedKey;

  AbstractFlow(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey) {
    this.stepId = stepId;
    this.requiredKeys = requiredKeys;
    this.providedKey = providedKey;
  }

  @Override
  public UUID getStepId() {
    return stepId;
  }

  @Override
  public final Set<Key<?>> getRequiredKeys() {
    return new HashSet<>(requiredKeys);
  }

  @Override
  public final Key<T> getProvidedKey() {
    return providedKey;
  }

  @Override
  public <N> Flow<N> then(Flow<N> next) {
    return SequenceFlow.create(this, next);
  }

  @Override
  public Flow<T> orIf(Condition condition, Flow<T> ifTrue) {
    return BranchFlow.create(this, condition, ifTrue);
  }

  @Override
  public BranchOnResultFirstCondition<T> branchOnResult() {

    return new BranchOnResultFirstCondition<T>() {
      @Override
      public <V> BranchOnResultSubsequentConditions<T, V> onFailure(Flow<V> failureFlow) {
        return new ResultBranchBuilder<>(
            AbstractFlow.this,
            BranchBuilder.startingWith(
                Conditions.keyRecordsFailure(getProvidedKey()),
                failureFlow));
      }

      @Override
      public <V> BranchOnResultSubsequentConditions<T, V> onCondition(String description, Predicate<T> predicate, Flow<V> flow) {
        return new ResultBranchBuilder<>(
            AbstractFlow.this,
            BranchBuilder.startingWith(
                Conditions.keyMatches(getProvidedKey(), description, predicate),
                flow));
      }
    };
  }

  private static final class ResultBranchBuilder<T, V> implements BranchOnResultSubsequentConditions<T, V> {

    private final Flow<T> firstFlow;
    private BranchBuilder<V> branchBuilder;

    private ResultBranchBuilder(Flow<T> firstFlow, BranchBuilder<V> branchBuilder) {
      this.firstFlow = firstFlow;
      this.branchBuilder = branchBuilder;
    }

    @Override
    public BranchOnResultSubsequentConditions<T, V> onCondition(String description, Predicate<T> predicate, Flow<V> flow) {
      return new ResultBranchBuilder<>(
          firstFlow,
          branchBuilder.orIf(Conditions.keyMatches(firstFlow.getProvidedKey(), description, predicate), flow));
    }

    @Override
    public Flow<V> otherwise(Flow<V> defaultFlow) {
      return firstFlow.then(branchBuilder.otherwise(defaultFlow));
    }
  }

  @Override
  public List<Flow<?>> appendTo(List<Flow<?>> previousFlows) {
    previousFlows.add(this);
    return previousFlows;
  }

}
