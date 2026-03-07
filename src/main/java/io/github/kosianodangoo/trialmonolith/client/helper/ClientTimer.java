package io.github.kosianodangoo.trialmonolith.client.helper;


import net.minecraft.client.Timer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientTimer {
    public static Timer timer = new Timer(40f, 0);
}
