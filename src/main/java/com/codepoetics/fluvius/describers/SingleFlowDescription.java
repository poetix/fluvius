package com.codepoetics.fluvius.describers;

import com.codepoetics.fluvius.api.description.DescriptionWriter;
import com.codepoetics.fluvius.api.description.FlowDescription;

import java.util.List;

final class SingleFlowDescription implements FlowDescription {

    private final String name;
    private final List<String> requiredKeyNames;
    private final String providedKeyName;

    SingleFlowDescription(String name, List<String> requiredKeyNames, String providedKeyName) {
        this.name = name;
        this.requiredKeyNames = requiredKeyNames;
        this.providedKeyName = providedKeyName;
    }

    @Override
    public void writeTo(DescriptionWriter descriptionWriter) {
        descriptionWriter.writeSingleFlow(name, requiredKeyNames, providedKeyName);
    }
}
