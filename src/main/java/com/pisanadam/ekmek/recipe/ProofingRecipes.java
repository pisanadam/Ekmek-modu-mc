package com.pisanadam.ekmek.recipe;

import com.pisanadam.ekmek.registry.ModItems;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/** Mayalama teknesi tarifleri: hamur → mayalanmış hamur, beslenmiş ekşi maya → aktif ekşi maya. */
public final class ProofingRecipes {
	public record Proofing(Item result, int ticks) {
	}

	/** Beslenen ekşi mayanın kabarıp aktifleşmesi (gerçekte 4-12 saat). */
	public static final int STARTER_TICKS = 6000;

	private static final Map<Item, Proofing> RECIPES = new IdentityHashMap<>();

	private ProofingRecipes() {
	}

	public static @Nullable Proofing get(ItemStack stack) {
		return stack.isEmpty() ? null : RECIPES.get(stack.getItem());
	}

	public static boolean isResult(ItemStack stack) {
		for (Proofing p : RECIPES.values()) {
			if (stack.is(p.result())) {
				return true;
			}
		}
		return false;
	}

	public static void init() {
		for (ModItems.BreadLine line : ModItems.BREADS) {
			if (line.proofed() != null) {
				RECIPES.put(line.dough(), new Proofing(line.proofed(), line.proofTicks()));
			}
		}
		RECIPES.put(ModItems.SOURDOUGH_STARTER_FED, new Proofing(ModItems.SOURDOUGH_STARTER_ACTIVE, STARTER_TICKS));
	}
}
