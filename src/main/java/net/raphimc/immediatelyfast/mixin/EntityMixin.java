import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @ModifyExpressionValue(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;isControlledByPlayer()Z"
        )
    )
    private boolean fixFallDistance(boolean original) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            return false;
        }
        return original;
    }
}