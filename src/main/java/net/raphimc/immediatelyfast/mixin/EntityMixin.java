import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

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