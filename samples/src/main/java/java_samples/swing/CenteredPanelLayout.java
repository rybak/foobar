package java_samples.swing;

import java.awt.*;

class CenteredPanelLayout implements LayoutManager {
	private final int w;
	private final int h;

	CenteredPanelLayout(int w, int h) {
		this.w = w;
		this.h = h;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		return target.getPreferredSize();
	}

	@Override
	public Dimension minimumLayoutSize(Container target) {
		return target.getMinimumSize();
	}

	@Override
	public void layoutContainer(Container target) {
		Dimension size = target.getSize();
		int x = (size.width - w) / 2;
		int y = (size.height - h) / 2;
		for (Component component : target.getComponents()) {
			component.setBounds(x, y, w, h);
		}
	}
}
