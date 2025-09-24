import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyExpressionValue;

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