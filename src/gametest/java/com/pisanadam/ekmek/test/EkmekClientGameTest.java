package com.pisanadam.ekmek.test;

import com.pisanadam.ekmek.block.entity.ProofingTroughBlockEntity;
import com.pisanadam.ekmek.block.entity.StoneOvenBlockEntity;
import com.pisanadam.ekmek.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Uçtan uca istemci testi: dünyayı kurar, fırın/tekne mantığını doğrular ve ekran görüntüleri alır.
 */
public class EkmekClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext ctx) {
		try (TestSingleplayerContext sp = ctx.worldBuilder().create()) {
			waitForChunksRender(sp);
			TestServerContext server = sp.getServer();
			server.runCommand("time set noon");
			server.runCommand("weather clear");

			BlockPos base = server.computeOnServer(s -> s.getPlayerList().getPlayers().get(0).blockPosition());
			int x = base.getX();
			int y = base.getY();
			int z = base.getZ();

			// --- sahne ---
			server.runCommand(cmd("setblock %d %d %d ekmekmodu:stone_oven[facing=north,lit=true]", x - 2, y, z + 3));
			server.runCommand(cmd("setblock %d %d %d ekmekmodu:stone_oven[facing=north]", x - 1, y, z + 3));
			server.runCommand(cmd("setblock %d %d %d ekmekmodu:proofing_trough[stage=1]", x + 1, y, z + 3));
			server.runCommand(cmd("setblock %d %d %d ekmekmodu:proofing_trough[stage=2]", x + 2, y, z + 3));
			server.runCommand(cmd("setblock %d %d %d minecraft:campfire", x + 3, y - 1, z + 3));
			server.runCommand(cmd("setblock %d %d %d minecraft:water_cauldron[level=3]", x + 3, y, z + 3));
			String[] crops = {"rye_crop", "corn_crop", "sesame_crop"};
			int[] ages = {1, 3, 5, 7, 7};
			for (int r = 0; r < crops.length; r++) {
				for (int i = 0; i < ages.length; i++) {
					server.runCommand(cmd("setblock %d %d %d minecraft:farmland[moisture=7]", x - 2 + i, y - 1, z + 5 + r));
					server.runCommand(cmd("setblock %d %d %d ekmekmodu:%s[age=%d]", x - 2 + i, y, z + 5 + r, crops[r], ages[i]));
				}
			}
			server.runCommand(cmd("tp @p %d.5 %d %d.5 0 30", x, y + 1, z));

			// --- fırın ve tekne mantığı ---
			BlockPos ovenPos = new BlockPos(x - 1, y, z + 3);
			BlockPos troughPos = new BlockPos(x + 1, y, z + 3);
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				StoneOvenBlockEntity oven = (StoneOvenBlockEntity) level.getBlockEntity(ovenPos);
				oven.setItem(StoneOvenBlockEntity.SLOT_INPUT, new ItemStack(ModItems.SOURDOUGH.proofed(), 2));
				oven.setItem(StoneOvenBlockEntity.SLOT_FUEL, new ItemStack(Items.COAL));
				ProofingTroughBlockEntity trough = (ProofingTroughBlockEntity) level.getBlockEntity(troughPos);
				trough.setItem(0, new ItemStack(ModItems.VILLAGE_LOAF.dough(), 3));
			});

			waitForChunksRender(sp);
			ctx.waitTicks(40);
			ctx.takeScreenshot("ekmek_sahne");

			ctx.waitTicks(420);
			server.runOnServer(s -> {
				StoneOvenBlockEntity oven = (StoneOvenBlockEntity) s.overworld().getBlockEntity(ovenPos);
				ItemStack result = oven.getItem(StoneOvenBlockEntity.SLOT_RESULT);
				if (!result.is(ModItems.SOURDOUGH.bread())) {
					throw new AssertionError("Taş fırın ekşi mayalı ekmek pişirmedi: " + result);
				}
			});
			// sıcak ortamda (yanan fırın 2 blok yakında) 1200 tick'lik mayalanma ~600 tick sürer
			ctx.waitTicks(220);
			server.runOnServer(s -> {
				ProofingTroughBlockEntity trough = (ProofingTroughBlockEntity) s.overworld().getBlockEntity(troughPos);
				ItemStack content = trough.getItem(0);
				if (!content.is(ModItems.VILLAGE_LOAF.proofed()) || content.getCount() != 3) {
					throw new AssertionError("Hamur mayalanmadı: " + content);
				}
			});

			// tekne görünümü (stage=2 olmalı) yakından
			BlockPos troughPos2 = troughPos;
			int clientStage = ctx.computeOnClient(mc -> mc.level.getBlockState(troughPos2).getValue(com.pisanadam.ekmek.block.ProofingTroughBlock.STAGE));
			if (clientStage != 2) {
				throw new AssertionError("İstemcide tekne evresi 2 değil: " + clientStage);
			}
			server.runCommand(cmd("tp @p %d.5 %d %d.5 0 50", x + 1, y + 1, z + 1));
			waitForChunksRender(sp);
			ctx.waitTicks(10);
			ctx.takeScreenshot("ekmek_tekne_yakin");

			// --- easter egg: ekmek nimettir (yerden ekmek al) ---
			server.runOnServer(s -> {
				ServerPlayer player = s.getPlayerList().getPlayers().get(0);
				net.minecraft.world.entity.item.ItemEntity bread = new net.minecraft.world.entity.item.ItemEntity(
					player.level(), player.getX(), player.getY(), player.getZ(), new ItemStack(ModItems.SIMIT.bread()));
				bread.setDeltaMovement(0, 0, 0);
				player.level().addFreshEntity(bread);
			});
			ctx.waitTicks(60);
			server.runOnServer(s -> {
				ServerPlayer player = s.getPlayerList().getPlayers().get(0);
				if (!player.hasEffect(net.minecraft.world.effect.MobEffects.LUCK)) {
					throw new AssertionError("Ekmek nimettir easter egg'i tetiklenmedi; simit envanterde mi: "
						+ player.getInventory().contains(new ItemStack(ModItems.SIMIT.bread())) + " konum " + player.position());
				}
			});
			// --- easter egg: ekmek çarpar (lava atılan ekmek) ---
			server.runCommand(cmd("setblock %d %d %d minecraft:lava", x + 4, y, z + 6));
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level, x + 4.5, y + 1.5, z + 6.5, new ItemStack(ModItems.PIDE.bread())));
			});
			ctx.waitTicks(40);

			// --- loot tablosu: olgun çavdar kırılınca çavdar düşmeli ---
			BlockPos ryePos = new BlockPos(x + 1, y, z + 5);
			server.runOnServer(s -> {
				ServerLevel level = s.overworld();
				level.destroyBlock(ryePos, true);
				boolean dropped = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(ryePos).inflate(2))
					.stream().anyMatch(e -> e.getItem().is(ModItems.RYE));
				if (!dropped) {
					throw new AssertionError("Olgun çavdar ekini çavdar düşürmedi (loot tablosu yüklenmedi mi?)");
				}
			});

			// --- easter egg: sohbet ---
			ctx.runOnClient(mc -> mc.player.connection.sendChat("Ekmek nimettir!"));
			ctx.waitTicks(10);
			ctx.runOnClient(mc -> mc.player.connection.sendChat("ekmek köper"));
			ctx.waitTicks(10);

			// --- fırın arayüzü ---
			server.runOnServer(s -> {
				ServerPlayer player = s.getPlayerList().getPlayers().get(0);
				player.openMenu((MenuProvider) s.overworld().getBlockEntity(ovenPos));
			});
			ctx.waitTicks(10);
			ctx.takeScreenshot("ekmek_firin_arayuzu");
			ctx.setScreen(() -> null);

			// --- envanter görüntüleri ---
			server.runCommand("gamemode survival @a");
			List<Item> all = new ArrayList<>(ModItems.ORDERED);
			for (int page = 0; page * 36 < all.size(); page++) {
				List<Item> slice = all.subList(page * 36, Math.min(all.size(), (page + 1) * 36));
				server.runOnServer(s -> {
					ServerPlayer player = s.getPlayerList().getPlayers().get(0);
					player.getInventory().clearContent();
					for (int i = 0; i < slice.size(); i++) {
						player.getInventory().setItem(i, new ItemStack(slice.get(i)));
					}
				});
				ctx.waitTicks(5);
				ctx.setScreen(() -> new InventoryScreen(Minecraft.getInstance().player));
				ctx.waitTicks(5);
				ctx.takeScreenshot("ekmek_envanter_" + page);
				ctx.setScreen(() -> null);
			}
		}
	}

	/** 26.1'de getClientLevel(), 26.2+'da getConnection() üzerinden; testi tüm sürümlerde ortak tutmak için yansıma. */
	private static void waitForChunksRender(TestSingleplayerContext sp) {
		for (String name : new String[] {"getConnection", "getClientLevel"}) {
			try {
				Object target = sp.getClass().getMethod(name).invoke(sp);
				target.getClass().getMethod("waitForChunksRender").invoke(target);
				return;
			} catch (NoSuchMethodException ignored) {
				// diğer sürümün API'sini dene
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException(e);
			}
		}
		throw new IllegalStateException("waitForChunksRender bulunamadı");
	}

	private static String cmd(String format, Object... args) {
		return String.format(java.util.Locale.ROOT, format, args);
	}
}
