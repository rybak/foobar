package java_samples.swing;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CircleRoundabout {
	public static void main(String... args) {
		JFrame mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainWindow.setSize(800, 600);

		AtomicInteger frameCounter = new AtomicInteger();
		AtomicInteger radius = new AtomicInteger();
		CirclePainter circlePainter = new CirclePainter(radius);
		JPanel content = new JPanel(new CenteredPanelLayout(300, 200)) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				circlePainter.paintCircle(g);
			}
		};
		new Timer(10, e -> {
			int f = frameCounter.get();
			if (f >= 100)
				frameCounter.set(0);
			else {
				frameCounter.incrementAndGet();
			}
			SwingUtilities.invokeLater(content::repaint);
		}).start();
		JPanel controlsPanel = new JPanel(new BorderLayout());
		content.add(controlsPanel);
		{
			JPanel labels = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			{
				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
			}
			{
				gbc.gridy = 1;
				labels.add(new JLabel("Radius: ", SwingConstants.RIGHT), gbc);
				gbc.gridy++;
				labels.add(new JLabel("Delay: ", SwingConstants.RIGHT), gbc);
			}
			controlsPanel.add(labels, BorderLayout.WEST);
		}
		{
			JPanel controls = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			{
				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
			}
			{
				gbc.gridy = 1;
				JSlider radiusSlider = new JSlider();
				radiusSlider.addChangeListener(e -> radius.set(radiusSlider.getValue()));
				radius.set(radiusSlider.getValue());
				controls.add(radiusSlider, gbc);
				gbc.gridy++;
				controls.add(new JSlider(), gbc);
			}
			controlsPanel.add(controls, BorderLayout.CENTER);
		}
		mainWindow.setContentPane(content);

		mainWindow.setVisible(true);
	}

	private static class CirclePainter {
		private final AtomicInteger radius;

		private int x = 0;
		private int y = 0;

		CirclePainter(AtomicInteger radius) {
			this.radius = radius;
		}

		private enum Side {
			TOP, RIGHT, BOTTOM, LEFT
		}

		private Side side = Side.TOP;

		private void paintCircle(Graphics orig) {
			Graphics2D g = (Graphics2D) orig.create();
			final int r = radius.get();
			final Rectangle bounds = g.getClipBounds();

			int left = bounds.x;
			int right = bounds.x + bounds.width;
			int top = bounds.y;
			int bottom = bounds.y + bounds.height;

			switch (side) {
				case TOP:
					x++;
					y = top + 1;
					if (x + r >= right)
						side = Side.RIGHT;
					break;
				case RIGHT:
					y++;
					x = right - r - 1;
					if (y + r >= bottom)
						side = Side.BOTTOM;
					break;
				case BOTTOM:
					x--;
					y = bottom - r - 1;
					if (x < left)
						side = Side.LEFT;
					if (x >= right)
						x = right - r - 1;
					break;
				case LEFT:
					y--;
					x = left;
					if (y <= top)
						side = Side.TOP;
					if (y >= bottom)
						y = bottom - r - 1;
					break;
			}
			g.setColor(Color.RED);
			g.drawOval(x, y, r, r);

			g.dispose();
		}
	}

}
