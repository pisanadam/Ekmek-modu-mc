package com.pisanadam.ekmek.recipe;

import com.pisanadam.ekmek.registry.ModItems;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

/**
 * Taş fırın tarifleri. Ekmekler yalnızca taş fırında pişer;
 * normal fırın/tütsüleyicide pişirilemezler.
 */
public final class BakingRecipes {
	public record Baking(Item result, int count, int ticks) {
		public ItemStack resultStack() {
			return new ItemStack(this.result, this.count);
		}
	}

	private static final Map<Item, Baking> RECIPES = new IdentityHashMap<>();

	private BakingRecipes() {
	}

	private static void add(Item input, Item result, int count, int ticks) {
		RECIPES.put(input, new Baking(result, count, ticks));
	}

	public static @Nullable Baking get(ItemStack stack) {
		return stack.isEmpty() ? null : RECIPES.get(stack.getItem());
	}

	public static boolean canBake(ItemStack stack) {
		return get(stack) != null;
	}

	public static Map<Item, Baking> all() {
		return RECIPES;
	}

	public static void init() {
		for (ModItems.BreadLine line : ModItems.BREADS) {
			add(line.ovenInput(), line.bread(), 1, line.bakeTicks());
		}
		// Deniz/kaynak suyunu buharlaştırarak tuz elde etme (kova geri verilir)
		add(Items.WATER_BUCKET, ModItems.SALT, 3, 600);
		// Meyve şırasını kaynatıp koyulaştırarak pekmez yapma
		add(ModItems.BERRY_MUST, ModItems.PEKMEZ, 1, 400);
	}
}
