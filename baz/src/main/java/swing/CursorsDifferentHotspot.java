package swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Objects;

public class CursorsDifferentHotspot {
	private static final Cursor GOOD_DOWN = createCursor("down.png", new Point(17, 17));
	private static final Cursor GOOD_UP = createCursor("up.png", new Point(17, 17));
	private static final Cursor BAD_DOWN = createCursor("down.png", new Point(5, 5));
	private static final Cursor BAD_UP = createCursor("up.png", new Point(30, 30));

	private static Cursor createCursor(String file, Point point) {
		URL url = Objects.requireNonNull(CursorsDifferentHotspot.class.getResource(file));
		return Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(url), point, file);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Cursors with different hotspots");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocation(100, 50);
		JPanel contentPane = new JPanel(new GridLayout(1, 2));
		{
			JButton leftButton = new JButton("Good cursors");
			contentPane.add(leftButton);
			leftButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					leftButton.setCursor(GOOD_DOWN);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					leftButton.setCursor(GOOD_UP);
				}
			});
			JButton rightButton = new JButton("Bad cursors");
			rightButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					rightButton.setCursor(BAD_DOWN);
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					rightButton.setCursor(BAD_UP);
				}
			});
			contentPane.add(rightButton);
		}
		frame.setContentPane(contentPane);
		frame.setVisible(true);
	}
}
