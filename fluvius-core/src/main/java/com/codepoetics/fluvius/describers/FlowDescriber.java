package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.*;
import com.codepoetics.fluvius.api.description.FlowDescription;
import com.codepoetics.fluvius.api.scratchpad.Key;

import java.util.*;

/**
 * A FlowVisitor that traverses a Flow to create a FlowDescription.
 */
public final class FlowDescriber implements FlowVisitor<FlowDescription> {

  /**
   * Traverse the supplied Flow to construct a FlowDescription.
   *
   * @param flow The Flow to traverse.
   * @return The constructed FlowDescription.
   */
  public static FlowDescription describe(Flow<?> flow) {
    return flow.visit(new FlowDescriber());
  }

  private FlowDescriber() {
  }

  @Override
  public <T> FlowDescription visitSingle(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, Operation<T> operation) {
    return new SingleFlowDescription(stepId, operation.getName(), toKeyNames(requiredKeys), providedKey.getName());
  }

  @Override
  public <T> FlowDescription visitSequence(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, List<FlowDescription> flows) {
    return new SequenceFlowDescription(stepId, toKeyNames(requiredKeys), providedKey.getName(), flows);
  }

  @Override
  public <T> FlowDescription visitBranch(UUID stepId, Set<Key<?>> requiredKeys, Key<T> providedKey, FlowDescription defaultBranch, List<Conditional<FlowDescription>> conditionalBranches) {
    Map<String, FlowDescription> branchDescriptions = new LinkedHashMap<>();
    for (Conditional<FlowDescription> conditional : conditionalBranches) {
      branchDescriptions.put(conditional.getCondition().getDescription(), conditional.getValue());
    }
    return new BranchFlowDescription(stepId, toKeyNames(requiredKeys), providedKey.getName(), defaultBranch, branchDescriptions);
  }

  @Override
  public Condition visitCondition(Condition condition) {
    return condition;
  }

  private List<String> toKeyNames(Collection<Key<?>> keys) {
    List<String> keyNames = new ArrayList<>();
    for (Key<?> key : keys) {
      keyNames.add(key.getName());
    }
    return keyNames;
  }

}
