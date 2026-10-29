package com.pisanadam.ekmek.mixin;

import com.pisanadam.ekmek.event.EasterEggs;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** "Ekmek nimettir": yerdeki ekmeği alan oyuncuyu yakalar. */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
	@Unique
	private boolean ekmekmodu$wasBread;

	// Envantere eklenince yerdeki yığın boşalır; bu yüzden ekmek olup olmadığına önceden bakılır.
	@Inject(method = "playerTouch", at = @At("HEAD"))
	private void ekmekmodu$checkBread(Player player, CallbackInfo ci) {
		this.ekmekmodu$wasBread = EasterEggs.isBread(((ItemEntity) (Object) this).getItem());
	}

	@Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
	private void ekmekmodu$onPickup(Player player, CallbackInfo ci) {
		if (this.ekmekmodu$wasBread) {
			EasterEggs.onBreadPickedUp(player, (ItemEntity) (Object) this);
		}
	}
}
