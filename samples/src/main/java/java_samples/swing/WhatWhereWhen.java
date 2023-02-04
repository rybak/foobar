package java_samples.swing;

import javax.swing.*;
import java.awt.*;

public class WhatWhereWhen {

	public static void main(String... args) {
		JFrame mainWindow = new JFrame("Что? Где? Когда?");
		mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		JPanel content = new JPanel(new BorderLayout());
		content.add(new JButton("Играть"), BorderLayout.CENTER);
		mainWindow.setContentPane(content);

		mainWindow.setSize(400, 300);
		mainWindow.setVisible(true);
	}
}
