package io.github.kosianodangoo.trialmonolith.transformer;

import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import java.util.EnumSet;

public class TheTrialMonolithPlugin implements ILaunchPluginService {

    @Override
    public String name() {
        return "the_trial_monolith_plugin";
    }

    @Override
    public int processClassWithFlags(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (classNode.name.startsWith("io/github/kosianodangoo/trialmonolith/transformer"))
            return ComputeFlags.NO_REWRITE;
        if (!reason.equals(ITransformerActivity.CLASSLOADING_REASON))
            return ComputeFlags.NO_REWRITE;
        return GenericTransformer.transform(
                phase == Phase.AFTER ? GenericTransformer.Phase.ILaunchPluginService
                                     : GenericTransformer.Phase.ILaunchPluginServiceBefore,
                classNode);
    }

    @Override
    public EnumSet<Phase> handlesClass(Type type, boolean b) {
        if (type.getClassName().startsWith("io.github.kosianodangoo.trialmonolith.transformer"))
            return EnumSet.noneOf(Phase.class);
        return EnumSet.of(Phase.AFTER, Phase.BEFORE);
    }

}
