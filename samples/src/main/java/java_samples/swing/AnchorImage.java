package java_samples.swing;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;

/**
 * Demonstration of {@link javax.swing.text.html.ImageView#DEFAULT_BORDER}.
 */
public class AnchorImage {
	public static void main(String... args) {
		JFrame mainWindow = new JFrame();
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JPanel content = new JPanel(new BorderLayout());

		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet css = htmlEditorKit.getStyleSheet();
		css.addRule("img { border-width: 0; }");
		css.addRule("a { color: red; }");
		css.addRule("a { border: 0px; }");


//		htmlEditorKit.setStyleSheet(css);

		JTextPane rawHtml = new JTextPane();
		rawHtml.setEditable(false);
		rawHtml.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JTextPane htmlDisplay = new JTextPane();
		htmlDisplay.setEditorKit(htmlEditorKit);
		htmlDisplay.setContentType("text/html");
		htmlDisplay.setEditable(false);
		JCheckBox withBorderAttribute = new JCheckBox();
		setHtmlContent(htmlDisplay, rawHtml, withBorderAttribute.isSelected());
		content.add(htmlDisplay, BorderLayout.CENTER);
		content.add(rawHtml, BorderLayout.NORTH);
		JButton refresh = new JButton("refresh");
		refresh.addActionListener(e -> {
			setHtmlContent(htmlDisplay, rawHtml, withBorderAttribute.isSelected());
		});
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(refresh);
		bottomPanel.add(withBorderAttribute);
		content.add(bottomPanel, BorderLayout.SOUTH);

		mainWindow.setContentPane(content);
		mainWindow.setResizable(false);
		mainWindow.setSize(800, 600);
		mainWindow.setVisible(true);
	}

	private static void setHtmlContent(JTextPane htmlDisplay, JTextPane rawHtml, boolean includeBorderFix) {
		String imageUrl = AnchorImage.class.getResource("image.png").toString();
		System.err.println(imageUrl);
		String html = String.format(
			"<html><body>" +
				"Just an image: <img src=\"%s\" /><br/>" +
				"<a href=\"https://example.net\">A link</a><br/>Image inside link:" +
				"<a href=\"https://example.net\"><img src=\"%s\" %s></a>" +
				"<html><body>",
			imageUrl,
			imageUrl,
			(includeBorderFix ? "border=\"0\"" : "")
		);
		htmlDisplay.setText(html);
		rawHtml.setText(html);
	}
}
