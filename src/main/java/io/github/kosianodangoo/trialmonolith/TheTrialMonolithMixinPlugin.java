package io.github.kosianodangoo.trialmonolith;

import io.github.kosianodangoo.trialmonolith.transformer.GenericTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TheTrialMonolithMixinPlugin implements IMixinConfigPlugin {
    static {
        new GenericTransformer();
    }

    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {
        GenericTransformer.transform(GenericTransformer.Phase.PostMixin, classNode);
    }
}
