package vazkii.zetaimplforge.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import vazkii.zeta.client.event.ZPreTextureStitch;

public record ForgeZPreTextureStitch(TextureStitchEvent.Pre e) implements ZPreTextureStitch {
	@Override
	public TextureAtlas getAtlas() {
		return e.getAtlas();
	}

	@Override
	public boolean addSprite(ResourceLocation sprite) {
		return e.addSprite(sprite);
	}
}