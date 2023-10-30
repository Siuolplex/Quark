package vazkii.zetaimplforge.client.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.event.ScreenEvent;
import vazkii.zeta.client.event.ZScreenKeyPressed;

public class ForgeZScreenKeyPressed implements ZScreenKeyPressed {
	protected final ScreenEvent.KeyPressed e;

	public ForgeZScreenKeyPressed(ScreenEvent.KeyPressed e) {
		this.e = e;
	}

	@Override
	public Screen getScreen() {
		return e.getScreen();
	}

	@Override
	public int getKeyCode() {
		return e.getKeyCode();
	}

	@Override
	public int getScanCode() {
		return e.getScanCode();
	}

	@Override
	public int getModifiers() {
		return e.getModifiers();
	}

	@Override
	public boolean isCanceled() {
		return e.isCanceled();
	}

	@Override
	public void setCanceled(boolean cancel) {
		e.setCanceled(true);
	}

	public static class Pre extends ForgeZScreenKeyPressed implements ZScreenKeyPressed.Pre {
		public Pre(ScreenEvent.KeyPressed.Pre e) {
			super(e);
		}
	}

	public static class Post extends ForgeZScreenKeyPressed implements ZScreenKeyPressed.Post {
		public Post(ScreenEvent.KeyPressed.Post e) {
			super(e);
		}
	}
}