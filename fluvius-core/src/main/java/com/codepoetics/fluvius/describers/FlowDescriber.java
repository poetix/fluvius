package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.description.FlowDescription;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.*;

/**
 * A FlowVisitor that traverses a Flow to create a FlowDescription.
 */
public class FlowDescriber implements FlowVisitor<FlowDescription> {

  /**
   * Traverse the supplied Flow to construct a FlowDescription.
   *
   * @param flow The Flow to traverse.
   * @return The constructed FlowDescription.
   */
  public static FlowDescription describe(final Flow<?> flow) {
    return flow.visit(new FlowDescriber());
  }

  private FlowDescriber() {
  }

  @Override
  public <T> FlowDescription visitSingle(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return new SingleFlowDescription(stepId, operation.getName(), toKeyNames(requiredKeys), providedKey.getName());
  }

  @Override
  public <T> FlowDescription visitSequence(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<FlowDescription> flows) {
    return new SequenceFlowDescription(stepId, toKeyNames(requiredKeys), providedKey.getName(), flows);
  }

  @Override
  public <T> FlowDescription visitBranch(final UUID stepId, final Set<Key<?>> requiredKeys, final Key<T> providedKey, final FlowDescription defaultBranch, final List<Conditional<FlowDescription>> conditionalBranches) {
    final Map<String, FlowDescription> branchDescriptions = new LinkedHashMap<>();
    for (final Conditional<FlowDescription> conditional : conditionalBranches) {
      branchDescriptions.put(conditional.getCondition().getDescription(), conditional.getValue());
    }
    return new BranchFlowDescription(stepId, toKeyNames(requiredKeys), providedKey.getName(), defaultBranch, branchDescriptions);
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return condition;
  }

  private List<String> toKeyNames(final Collection<Key<?>> keys) {
    final List<String> keyNames = new ArrayList<>();
    for (final Key<?> key : keys) {
      keyNames.add(key.getName());
    }
    return keyNames;
  }

}
