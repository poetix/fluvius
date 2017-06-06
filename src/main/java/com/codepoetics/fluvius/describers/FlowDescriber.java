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
  public <T> FlowDescription visitSingle(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final Operation<T> operation) {
    return new SingleFlowDescription(operation.getName(), toKeyNames(requiredKeys), providedKey.getName());
  }

  @Override
  public <T> FlowDescription visitSequence(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final List<FlowDescription> flows) {
    return new SequenceFlowDescription(toKeyNames(requiredKeys), providedKey.getName(), flows);
  }

  @Override
  public <T> FlowDescription visitBranch(final Set<Key<?>> requiredKeys, final Key<T> providedKey, final FlowDescription defaultBranch, final Map<String, Conditional<FlowDescription>> conditionalBranches) {
    Map<String, FlowDescription> branchDescriptions = new LinkedHashMap<>();
    for (Map.Entry<String, Conditional<FlowDescription>> entry : conditionalBranches.entrySet()) {
      branchDescriptions.put(entry.getKey(), entry.getValue().getValue());
    }
    return new BranchFlowDescription(toKeyNames(requiredKeys), providedKey.getName(), defaultBranch, branchDescriptions);
  }

  @Override
  public Condition visitCondition(final Condition condition) {
    return condition;
  }

  private List<String> toKeyNames(final Collection<Key<?>> keys) {
    List<String> keyNames = new ArrayList<>();
    for (Key<?> key : keys) {
      keyNames.add(key.getName());
    }
    return keyNames;
  }

}
