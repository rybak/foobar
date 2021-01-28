package swing;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ColorDemo {
	public static void main(String... args) {
		JFrame mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final int n = 4;
		String[] colorLabels = new String[]{"Red", "Green", "Blue", "Alpha"};
		AtomicInteger[] colorComps = new AtomicInteger[]{
			new AtomicInteger(0),
			new AtomicInteger(0),
			new AtomicInteger(0),
			new AtomicInteger(255),
		};
		Consumer<Graphics2D> colorSetter = g -> g.setColor(new Color(
			colorComps[0].get(),
			colorComps[1].get(),
			colorComps[2].get(),
			colorComps[3].get()
		));

		JPanel content = new JPanel(new CenteredPanelLayout(300, 200)) {
			@Override
			protected void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				Graphics2D g = (Graphics2D) graphics.create();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				colorSetter.accept(g);
				g.fillOval(10, 10, 100, 100);
				g.dispose();
			}
		};
		JPanel controlsPanel = new JPanel(new BorderLayout());
		content.add(controlsPanel);
		{
			JPanel labels = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			{
				gbc.gridx = 0;
				gbc.fill = GridBagConstraints.HORIZONTAL;
			}
			{
				for (int i = 0; i < n; i++) {
					gbc.gridy = i;
					labels.add(new JLabel(colorLabels[i], SwingConstants.LEFT), gbc);
				}
			}
			controlsPanel.add(labels, BorderLayout.WEST);
		}
		{
			JPanel controls = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			{
				gbc.gridx = 0;
				gbc.fill = GridBagConstraints.HORIZONTAL;
			}
			{
				for (int i = 0; i < n; i++) {
					final int index = i;
					gbc.gridy = i;
					JSlider colorSlider = new JSlider(0, 255, colorComps[i].get());
					colorSlider.addChangeListener(e -> {
						colorComps[index].set(colorSlider.getValue());
						SwingUtilities.invokeLater(content::repaint);
					});
					controls.add(colorSlider, gbc);
				}
			}
			controlsPanel.add(controls, BorderLayout.CENTER);
		}

		mainWindow.setContentPane(content);
		mainWindow.setResizable(false);
		mainWindow.setSize(800, 600);
		mainWindow.setVisible(true);
	}
}
