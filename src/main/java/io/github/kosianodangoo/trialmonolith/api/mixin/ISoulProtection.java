package io.github.kosianodangoo.trialmonolith.api.mixin;

public interface ISoulProtection {
    boolean the_trial_monolith$isSoulProtected();

    void the_trial_monolith$setSoulProtected(boolean soulProtected);

    boolean the_trial_monolith$shouldBypass();

    void the_trial_monolith$setShouldBypass(boolean shouldBypass);
}
