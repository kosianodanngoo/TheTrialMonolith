package io.github.kosianodangoo.trialmonolith.api.mixin;

public interface ITickTracker {
    int the_trial_monolith$getLastTickCount();

    void the_trial_monolith$updateLastTickCount();

    void the_trial_monolith$markUpdating(boolean isUpdating);

    boolean the_trial_monolith$isUpdating();
}
