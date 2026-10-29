package com.pisanadam.ekmek.event;

import com.pisanadam.ekmek.block.entity.ProofingTroughBlockEntity;
import com.pisanadam.ekmek.compat.Compat;
import com.pisanadam.ekmek.registry.ModItems;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Easter egg'ler:
 * <ul>
 *   <li><b>Ekmek nimettir</b> – yere düşmüş ekmeği yerden alınca öpüp alnına koyarsın (Şans etkisi);
 *       sohbete "ekmek nimettir" yazınca da karşılık gelir.</li>
 *   <li><b>Ekmek çarpar</b> – ekmeği ateşe/lava atana zararsız bir şimşek çakar.</li>
 *   <li><b>Ekmek köper</b> – teknede unutulan hamur köpürüp taşar; sohbete "ekmek köper" yazınca
 *       yakındaki teknelerdeki hamurlar coşar.</li>
 * </ul>
 */
public final class EasterEggs {
	private static final Locale TR = Locale.forLanguageTag("tr");
	private static final long PICKUP_COOLDOWN = 20 * 30;
	private static final long CHAT_COOLDOWN = 20 * 60;
	private static final Map<UUID, Long> LAST_PICKUP = new HashMap<>();
	private static final Map<String, Long> LAST_CHAT = new HashMap<>();

	private EasterEggs() {
	}

	public static void init() {
		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> onChat(sender, message.signedContent()));
	}

	public static boolean isBread(ItemStack stack) {
		if (stack.is(Items.BREAD)) {
			return true;
		}
		for (ModItems.BreadLine line : ModItems.BREADS) {
			if (stack.is(line.bread())) {
				return true;
			}
		}
		return false;
	}

	/** Ekmek nimettir: yerdeki ekmek alındı. ItemEntity mixin'inden çağrılır. */
	public static void onBreadPickedUp(Player player, ItemEntity entity) {
		if (!(player instanceof ServerPlayer serverPlayer) || !(player.level() instanceof ServerLevel level)) {
			return;
		}
		long now = level.getGameTime();
		Long last = LAST_PICKUP.get(player.getUUID());
		if (last != null && now - last < PICKUP_COOLDOWN) {
			return;
		}
		LAST_PICKUP.put(player.getUUID(), now);
		serverPlayer.sendSystemMessage(Component.translatable("message.ekmekmodu.easter.nimet_pickup").withStyle(ChatFormatting.GOLD), true);
		serverPlayer.addEffect(new MobEffectInstance(MobEffects.LUCK, 20 * 60));
		level.sendParticles(ParticleTypes.HEART, player.getX(), player.getEyeY() + 0.3, player.getZ(), 3, 0.3, 0.2, 0.3, 0.0);
		level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.4F, 1.6F);
	}

	/** Ekmek çarpar: ekmek ateşte/lavda yok oldu. */
	public static void onBreadDestroyed(ItemEntity entity) {
		if (!(entity.level() instanceof ServerLevel level)) {
			return;
		}
		Compat.spawnVisualLightning(level, entity.getX(), entity.getY(), entity.getZ());
		Component msg = Component.translatable("message.ekmekmodu.easter.carpar").withStyle(ChatFormatting.RED);
		for (ServerPlayer p : level.getPlayers(p -> p.distanceToSqr(entity) < 32 * 32)) {
			p.sendSystemMessage(msg);
		}
	}

	/** Ekmek köper: teknede unutulan hamur taştı. */
	public static void onOverproof(ServerLevel level, BlockPos pos, ItemStack dough) {
		level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 20, 0.35, 0.15, 0.35, 0.02);
		level.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 15, 0.4, 0.1, 0.4, 0.0);
		level.playSound(null, pos, SoundEvents.SLIME_BLOCK_PLACE, SoundSource.BLOCKS, 1.0F, 0.6F);
		Component msg = Component.translatable("message.ekmekmodu.easter.koper", dough.getHoverName()).withStyle(ChatFormatting.YELLOW);
		for (ServerPlayer p : level.getPlayers(p -> p.blockPosition().closerThan(pos, 16))) {
			p.sendSystemMessage(msg);
		}
	}

	private static boolean cooledDown(ServerPlayer player, String phrase, ServerLevel level) {
		String key = player.getUUID() + ":" + phrase;
		long now = level.getGameTime();
		Long last = LAST_CHAT.get(key);
		if (last != null && now - last < CHAT_COOLDOWN) {
			return false;
		}
		LAST_CHAT.put(key, now);
		return true;
	}

	private static void onChat(ServerPlayer player, String raw) {
		String text = raw.toLowerCase(TR).replaceAll("[^\\p{L} ]", "").replaceAll("\\s+", " ").trim();
		boolean nimet = text.contains("ekmek nimettir");
		boolean koper = text.contains("ekmek köper") || text.contains("ekmek koper");
		if (!nimet && !koper) {
			return;
		}
		ServerLevel level = (ServerLevel) player.level();
		// sohbet olayı ağ iş parçacığından gelebilir; dünyaya sunucu iş parçacığında dokun
		level.getServer().execute(() -> {
			if (nimet && cooledDown(player, "nimet", level)) {
				Component reply = Component.translatable("message.ekmekmodu.easter.nimet_chat").withStyle(ChatFormatting.GOLD);
				level.getServer().getPlayerList().broadcastSystemMessage(reply, false);
				if (isBread(player.getMainHandItem()) || isBread(player.getOffhandItem())) {
					player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 5));
					level.sendParticles(ParticleTypes.HEART, player.getX(), player.getEyeY() + 0.3, player.getZ(), 5, 0.4, 0.2, 0.4, 0.0);
				}
			}
			if (koper && cooledDown(player, "koper", level)) {
				int boosted = 0;
				for (BlockPos p : BlockPos.betweenClosed(player.blockPosition().offset(-8, -3, -8), player.blockPosition().offset(8, 3, 8))) {
					if (level.getBlockEntity(p) instanceof ProofingTroughBlockEntity trough && trough.cheer()) {
						level.sendParticles(ParticleTypes.BUBBLE_POP, p.getX() + 0.5, p.getY() + 0.7, p.getZ() + 0.5, 10, 0.3, 0.1, 0.3, 0.0);
						boosted++;
					}
				}
				Component reply = Component.translatable(boosted > 0 ? "message.ekmekmodu.easter.koper_chat_boost" : "message.ekmekmodu.easter.koper_chat", boosted)
					.withStyle(ChatFormatting.YELLOW);
				level.getServer().getPlayerList().broadcastSystemMessage(reply, false);
			}
		});
	}
}
