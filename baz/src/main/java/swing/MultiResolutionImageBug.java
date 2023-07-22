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
 * Reproducer for <a href="https://bugs.openjdk.org/browse/JDK-9075572">JDK bug 9075572</a>.
 * <p>
 * Tested on:
 * <ul>
 *     <li>
 *         Linux:
 *         <ul>
 *             <li>Java 11.0.19         ❌ Fail</li>
 *             <li>Java 17.0.7          ❌ Fail</li>
 *             <li>Java 19.0.2          ❌ Fail</li>
 *             <li>Java 20.0.1-testing  ❌ Fail</li>
 *         </ul>
 *     </li>
 *     <li>
 *         Windows:
 *         <ul>
 *             <li>11.0.17  ✅ Success</li>
 *             <li>15.0.2   ✅ Success</li>
 *             <li>18.0.2.1 ✅ Success</li>
 *         </ul>
 *     </li>
 *     <li>
 *         macOS: not applicable, because JFrame's icons aren't rendered there.
 *         Instead, only {@link Taskbar#setIconImage} is rendered in the Dock.
 *     </li>
 * </ul>
 */
public class MultiResolutionImageBug {
	public static final String ICON_64 = "icon64x64.png";
	public static final String ICON_32 = "icon32x32.png";
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
	private void showDialog(JFrame mainWindow, boolean multi) {
		attemptsCounter++;
		counterLabel.setText(getLabelText());
		JDialog dialog = new JDialog(mainWindow, "Dialog attempt #" + attemptsCounter, Dialog.ModalityType.MODELESS);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.setIconImage(multi ? getMultiResolutionImage() : getFixedResolutionImage());

		dialog.setLocation(200, 200);
		dialog.setSize(400, 400);
		final String explanation;
		if (!multi) {
			explanation = "Fixed resolution works fine.";
		} else {
			if (attemptsCounter == 1) {
				explanation = "On Linux, on the first attempt, the Duke icon is shown.";
			} else {
				explanation = "On subsequent attempts, the correct icon is shown.";
			}
		}
		JTextArea textDisplay = new JTextArea(explanation);
		dialog.getContentPane().add(textDisplay);
		setUpEscapeKeyClosing(dialog, textDisplay);
		dialog.setVisible(true);
	}

	private void go() {
		attemptsCounter = 0;
		String windowTitle = "Bug demo on Java version " + System.getProperty("java.version");
		System.out.println(windowTitle);
		mainWindow = new JFrame(windowTitle);
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel(new GridLayout(3, 1));
		mainWindow.setContentPane(contentPane);

		counterLabel = new JLabel(getLabelText());
		contentPane.add(counterLabel);

		JButton showMultiIconDialogButton = new JButton("Show dialog with BaseMultiResolutionImage");
		showMultiIconDialogButton.addActionListener(ignored -> showDialog(mainWindow, true));
		contentPane.add(showMultiIconDialogButton);
		JButton showFixedIconDialogButton = new JButton("Show dialog with fixed resolution Image");
		showFixedIconDialogButton.addActionListener(ignored -> showDialog(mainWindow, false));
		contentPane.add(showFixedIconDialogButton);

		mainWindow.setSize(400, 400);
		mainWindow.setVisible(true);
	}

	@NotNull
	private String getLabelText() {
		return "Attempts: " + attemptsCounter;
	}

	private Image getMultiResolutionImage() {
		Image icon32 = getFixedResolutionImage(ICON_32);
		Image icon64 = getFixedResolutionImage(ICON_64);
		return new BaseMultiResolutionImage(icon32, icon64);
	}

	private Image getFixedResolutionImage() {
		return getFixedResolutionImage(ICON_64);
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
