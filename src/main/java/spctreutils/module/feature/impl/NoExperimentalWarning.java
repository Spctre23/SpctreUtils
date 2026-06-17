package spctreutils.module.feature.impl;

import spctreutils.module.feature.Feature;

import java.util.List;

public class NoExperimentalWarning extends Feature
{
    public NoExperimentalWarning()
    {
        super("No Experimental Warning", "Removes the 'experimental settings' warning when loading worlds with experimental properties.", List.of());
    }
}
