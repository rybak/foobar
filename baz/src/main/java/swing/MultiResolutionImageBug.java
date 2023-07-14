package swing;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BaseMultiResolutionImage;
import java.net.URL;
import java.util.Objects;

/**
 * Tested on:
 * - Linux:
 *   - Java 11.0.19         ❌ Fail
 *   - Java 17.0.7          ❌ Fail
 *   - Java 19.0.2          ❌ Fail
 *   - Java 20.0.1-testing  ❌ Fail
 * - Windows:
 * -
 * - macOS:
 * -
 */
public class MultiResolutionImageBug {
	private int attemptsCounter;
	private JLabel counterLabel;
	private JFrame mainWindow;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MultiResolutionImageBug().go());
	}

	/**
	 * On Linux, first attempt always fails, because image isn't loaded yet,
	 * and {@code sun.awt.IconInfo#isValid} returns {@code false}.
	 */
	private void showDialog(JFrame mainWindow) {
		attemptsCounter++;
		counterLabel.setText(getLabelText());
		JDialog dialog = new JDialog(mainWindow, "Dialog attempt #" + attemptsCounter, Dialog.ModalityType.MODELESS);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setIconImage(getMultiResolutionImage());
		dialog.setLocation(200, 200);
		dialog.setSize(400, 400);
		JTextArea textDisplay = new JTextArea(
				"On the first attempt, the default Duke icon will be shown on Linux."
		);
		dialog.getContentPane().add(textDisplay);
		setUpEscapeKeyClosing(dialog, textDisplay);
		dialog.setVisible(true);
	}

	private void go() {
		attemptsCounter = 0;
		mainWindow = new JFrame("Bug demo on Java version " + System.getProperty("java.version"));
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel(new BorderLayout());
		mainWindow.setContentPane(contentPane);

		counterLabel = new JLabel(getLabelText());
		contentPane.add(counterLabel, BorderLayout.NORTH);

		JButton showDialogButton = new JButton("Show dialog with icon");
		showDialogButton.addActionListener(ignored -> showDialog(mainWindow));
		contentPane.add(showDialogButton, BorderLayout.CENTER);

		mainWindow.setSize(400, 400);
		mainWindow.setVisible(true);
	}

	@NotNull
	private String getLabelText() {
		return "Attempts: " + attemptsCounter;
	}

	private Image getMultiResolutionImage() {
		var icon32 = getFixedResolutionImage("icon32x32.png");
		var icon64 = getFixedResolutionImage("icon64x64.png");
		return new BaseMultiResolutionImage(icon32, icon64);
	}

	private Image getFixedResolutionImage(String filename) {
		URL url = Objects.requireNonNull(getClass().getResource(filename));
		return Toolkit.getDefaultToolkit().getImage(url);
	}

	private void setUpEscapeKeyClosing(JDialog d, JComponent c) {
		Object escapeCloseActionKey = new Object();
		c.getActionMap().put(escapeCloseActionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				d.dispose();
			}
		});
		c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escapeCloseActionKey);
	}
}
