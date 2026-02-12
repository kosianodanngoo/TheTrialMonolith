package io.github.kosianodangoo.trialmonolith.client.entity;

import io.github.kosianodangoo.trialmonolith.common.entity.TrialMonolithEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class TrialMonolithModel extends HierarchicalModel<TrialMonolithEntity> {
    private final ModelPart root;

    public TrialMonolithModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart root() {
        return root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition root = meshdefinition.getRoot();

        float size = 1f;

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -28.5F * (size + 1), -4.0F, 8.0F, 28.0F, 8.0F, new CubeDeformation(8.0F * size, 28.0F * size, 8.0F * size)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(TrialMonolithEntity trialMonolithEntity, float v, float v1, float v2, float v3, float v4) {

    }
}
