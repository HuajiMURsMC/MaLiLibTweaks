package me.jvav.masapinyin.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InventoryOverlay.class)
public class MixinInventoryOverlay {

    @Inject(method = "getInventoryType(Lnet/minecraft/inventory/Inventory;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(Inventory inv, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        if (cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC && inv instanceof AbstractFurnaceBlockEntity) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }

    @Inject(method = "getInventoryType(Lnet/minecraft/item/ItemStack;)Lfi/dy/masa/malilib/render/InventoryOverlay$InventoryRenderType;",
            at = @At(value = "RETURN"), cancellable = true)
    private static void checkAbstractFurnaceBlockEntity(ItemStack stack, CallbackInfoReturnable<InventoryOverlay.InventoryRenderType> cir) {
        Item item = stack.getItem();
        if (cir.getReturnValue() == InventoryOverlay.InventoryRenderType.GENERIC &&
                item instanceof BlockItem &&
                ((BlockItem) item).getBlock() instanceof AbstractFurnaceBlock) {
            cir.setReturnValue(InventoryOverlay.InventoryRenderType.FURNACE);
        }
    }

    @Inject(method = "renderEquipmentOverlayBackground", at = @At(value = "HEAD"))
    private static void preRenderEquipmentOverlayBackground(int i, int xOff, LivingEntity yOff, MatrixStack texture, CallbackInfo ci) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.applyModelViewMatrix();
    }
}
