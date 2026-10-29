package com.pisanadam.ekmek.registry;

import com.pisanadam.ekmek.EkmekModu;
import com.pisanadam.ekmek.compat.Compat;
import com.pisanadam.ekmek.item.BreadItem;
import com.pisanadam.ekmek.item.TooltipBlockItem;
import com.pisanadam.ekmek.item.TooltipItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

public final class ModItems {
	/** Kreatif sekmede gösterim sırası. */
	public static final List<Item> ORDERED = new ArrayList<>();
	/** 17 ekmeğin tamamı: hamurdan fırına giden yol. */
	public static final List<BreadLine> BREADS = new ArrayList<>();
	private static final java.util.Set<String> BREAD_NAMES = new java.util.HashSet<>();

	// ---------------------------------------------------------------- aletler
	public static final Item HAND_MILL = item("hand_mill", props -> new TooltipItem(props, tip("hand_mill")),
		new Item.Properties().stacksTo(1));
	public static final Item SIEVE = item("sieve", props -> new TooltipItem(props, tip("sieve")),
		new Item.Properties().stacksTo(1));

	// --------------------------------------------------------------- bloklar
	public static final Item STONE_OVEN = blockItem("stone_oven", ModBlocks.STONE_OVEN, false);
	public static final Item PROOFING_TROUGH = blockItem("proofing_trough", ModBlocks.PROOFING_TROUGH, false);

	// ----------------------------------------------------------- tarım ürünleri
	public static final Item RYE_SEEDS = blockItem("rye_seeds", ModBlocks.RYE_CROP, true);
	public static final Item RYE = simple("rye", false);
	public static final Item CORN_SEEDS = blockItem("corn_seeds", ModBlocks.CORN_CROP, true);
	public static final Item CORN = food("corn", 2, 0.3F, false);
	public static final Item SESAME_SEEDS = blockItem("sesame_seeds", ModBlocks.SESAME_CROP, true);
	public static final Item OLIVES = food("olives", 1, 0.4F, true);

	// ------------------------------------------------------------------- unlar
	public static final Item WHOLE_WHEAT_FLOUR = simple("whole_wheat_flour", true);
	public static final Item WHITE_FLOUR = simple("white_flour", true);
	public static final Item RYE_FLOUR = simple("rye_flour", true);
	public static final Item CORN_FLOUR = simple("corn_flour", true);

	// --------------------------------------------------------------- malzemeler
	public static final Item SALT = simple("salt", true);
	public static final Item YEAST = simple("yeast", true);
	public static final Item BAKING_SODA = simple("baking_soda", true);
	public static final Item BUTTER = simple("butter", true);
	public static final Item YOGURT = item("yogurt", props -> new TooltipItem(props, tip("yogurt")),
		new Item.Properties().stacksTo(16)
			.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5F).build())
			.usingConvertsTo(Items.BOWL)
			.craftRemainder(Items.BOWL));
	public static final Item OLIVE_OIL = bottle("olive_oil");
	public static final Item BERRY_MUST = bottle("berry_must");
	public static final Item PEKMEZ = bottle("pekmez");

	// ----------------------------------------------------------- ekşi maya
	public static final Item SOURDOUGH_STARTER_HUNGRY = item("sourdough_starter_hungry",
		props -> new TooltipItem(props, tip("sourdough_starter_hungry")), new Item.Properties().stacksTo(16));
	public static final Item SOURDOUGH_STARTER_FED = item("sourdough_starter_fed",
		props -> new TooltipItem(props, tip("sourdough_starter_fed")), new Item.Properties().stacksTo(16));
	public static final Item SOURDOUGH_STARTER_ACTIVE = item("sourdough_starter_active",
		props -> new TooltipItem(props, tip("sourdough_starter_active")),
		new Item.Properties().stacksTo(16).craftRemainder(SOURDOUGH_STARTER_HUNGRY));

	// ------------------------------------------------------------------ ekmekler
	// nutrition / doygunluk, mayalama süresi (tick), pişirme süresi (tick)
	public static final BreadLine SOURDOUGH = bread("sourdough_bread", "sourdough_dough", 7, 0.85F, 3600, 400);
	public static final BreadLine WHOLE_WHEAT_SOURDOUGH = bread("whole_wheat_sourdough_bread", "whole_wheat_sourdough_dough", 8, 0.9F, 3600, 420);
	public static final BreadLine WHOLE_WHEAT = bread("whole_wheat_bread", "whole_wheat_dough", 7, 0.8F, 1600, 400);
	public static final BreadLine VILLAGE_LOAF = bread("village_loaf", "village_loaf_dough", 6, 0.7F, 1200, 360);
	public static final BreadLine BAGUETTE = bread("baguette", "baguette_dough", 6, 0.65F, 1600, 300);
	public static final BreadLine CIABATTA = bread("ciabatta", "ciabatta_dough", 6, 0.7F, 2000, 320);
	public static final BreadLine FOCACCIA = bread("focaccia", "focaccia_dough", 7, 0.75F, 1600, 300);
	public static final BreadLine RYE_BREAD = bread("rye_bread", "rye_dough", 7, 0.95F, 3600, 480);
	public static final BreadLine BRIOCHE = bread("brioche", "brioche_dough", 7, 0.8F, 2000, 300);
	public static final BreadLine CHALLAH = bread("challah", "challah_dough", 7, 0.8F, 1600, 320);
	public static final BreadLine PITA = bread("pita", "pita_dough", 4, 0.6F, 1000, 140);
	public static final BreadLine NAAN = bread("naan", "naan_dough", 5, 0.6F, 1000, 140);
	public static final BreadLine LAVASH = flatBread("lavash", "lavash_dough", 3, 0.5F, 100);
	public static final BreadLine SIMIT = shapedBread("simit", "simit_dough", "raw_simit", 6, 0.7F, 1200, 260);
	public static final BreadLine PIDE = bread("pide", "pide_dough", 6, 0.7F, 1200, 240);
	public static final BreadLine CORNBREAD = flatBread("cornbread", "cornbread_dough", 6, 0.6F, 300);
	public static final BreadLine BAGEL = shapedBread("bagel", "bagel_dough", "boiled_bagel", 6, 0.7F, 1200, 260);

	private ModItems() {
	}

	/**
	 * Bir ekmeğin üretim zinciri.
	 *
	 * @param dough     yoğrulmuş hamur (tezgahta yapılır)
	 * @param proofed   mayalanmış hamur, mayasız ekmeklerde null
	 * @param ovenInput taş fırına konan son hal
	 */
	public record BreadLine(Item bread, Item dough, @Nullable Item proofed, Item ovenInput, int proofTicks, int bakeTicks) {
	}

	private static BreadLine bread(String name, String doughName, int nutrition, float saturation, int proofTicks, int bakeTicks) {
		Item dough = simple(doughName, true);
		Item proofed = simple("proofed_" + doughName, true);
		Item bread = breadItem(name, nutrition, saturation);
		BreadLine line = new BreadLine(bread, dough, proofed, proofed, proofTicks, bakeTicks);
		BREADS.add(line);
		return line;
	}

	/** Mayalanmayan ekmekler (lavaş, mısır ekmeği): hamur doğrudan fırına girer. */
	private static BreadLine flatBread(String name, String doughName, int nutrition, float saturation, int bakeTicks) {
		Item dough = simple(doughName, true);
		Item bread = breadItem(name, nutrition, saturation);
		BreadLine line = new BreadLine(bread, dough, null, dough, 0, bakeTicks);
		BREADS.add(line);
		return line;
	}

	/** Mayalandıktan sonra bir ara işlemden geçen ekmekler (simit: pekmez+susam, bagel: haşlama). */
	private static BreadLine shapedBread(String name, String doughName, String shapedName, int nutrition, float saturation, int proofTicks, int bakeTicks) {
		Item dough = simple(doughName, true);
		Item proofed = simple("proofed_" + doughName, true);
		Item shaped = simple(shapedName, true);
		Item bread = breadItem(name, nutrition, saturation);
		BreadLine line = new BreadLine(bread, dough, proofed, shaped, proofTicks, bakeTicks);
		BREADS.add(line);
		return line;
	}

	private static String tip(String name) {
		return "tooltip." + EkmekModu.MOD_ID + "." + name;
	}

	private static Item simple(String name, boolean tooltip) {
		return item(name, props -> new TooltipItem(props, tooltip ? tip(name) : null), new Item.Properties());
	}

	private static Item food(String name, int nutrition, float saturation, boolean tooltip) {
		return item(name, props -> new TooltipItem(props, tooltip ? tip(name) : null),
			new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build()));
	}

	private static Item breadItem(String name, int nutrition, float saturation) {
		BREAD_NAMES.add(name);
		return item(name, props -> new BreadItem(props, tip(name)),
			new Item.Properties().food(new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build()));
	}

	private static Item bottle(String name) {
		return item(name, props -> new TooltipItem(props, tip(name)),
			new Item.Properties().stacksTo(16).craftRemainder(Items.GLASS_BOTTLE));
	}

	private static Item blockItem(String name, Block block, boolean useItemName) {
		Item.Properties props = new Item.Properties();
		if (useItemName) {
			props.useItemDescriptionPrefix();
		} else {
			props.useBlockDescriptionPrefix();
		}
		return item(name, p -> new TooltipBlockItem(block, p, tip(name)), props);
	}

	/** Kompostlanabilir eşyalar ve kompost şansı. */
	private static float compostChance(String name) {
		return switch (name) {
			case "rye_seeds", "corn_seeds", "sesame_seeds" -> 0.3F;
			case "olives" -> 0.5F;
			case "rye", "corn" -> 0.65F;
			default -> BREAD_NAMES.contains(name) ? 0.85F : 0.0F;
		};
	}

	private static Item item(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, EkmekModu.id(name));
		float compost = compostChance(name);
		if (compost > 0.0F) {
			properties = Compat.compostable(properties, compost);
		}
		Item item = Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
		if (compost > 0.0F) {
			Compat.registerCompostable(item, compost);
		}
		ORDERED.add(item);
		return item;
	}

	public static void init() {
	}
}
