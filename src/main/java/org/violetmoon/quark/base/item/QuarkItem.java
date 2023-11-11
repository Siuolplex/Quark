package org.violetmoon.quark.base.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.client.handler.RequiredModTooltipHandler;
import org.violetmoon.zeta.item.IZetaItem;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.function.BooleanSupplier;

public class QuarkItem extends ZetaItem implements IZetaItem {

	private final ZetaModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkItem(String regname, ZetaModule module, Properties properties) {
		super(properties);

		Quark.ZETA.registry.registerItem(this, regname);
		this.module = module;

		if(module != null && module.category.isAddon())
			RequiredModTooltipHandler.map(this, module.category.requiredMod);
	}

	@Override
	public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == CreativeModeTab.TAB_SEARCH)
			super.fillItemCategory(group, items);
	}

	@Override
	public QuarkItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public ZetaModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}