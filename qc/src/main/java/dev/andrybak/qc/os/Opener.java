package dev.andrybak.qc.os;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Opener {

    public static void openUrl(String url) {
        System.out.println("Opening '" + url + "' in browser...");
        if (url == null)
            return;
        URI parsed;
        try {
            parsed = new URI(url);
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI format: " + e);
            return;
        }
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(parsed);
            } catch (IOException e) {
                System.err.println("Could not open URL. Got exception " + e);
            }
        } else {
            openNative(url, new File("."));
        }
    }

    public static void browseFileDirectory(Window parent, File f) {
        System.out.println("Opening '" + f + "' in file browser...");
        if (f == null)
            return;
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
            try {
                Desktop.getDesktop().browseFileDirectory(f);
            } catch (Exception e) {
                System.err.println("Could not browse file '" + f + "' . Got exception " + e);
            }
        } else {
            JTextField message = new JTextField(f.getAbsolutePath());
            message.setEditable(false);
            JOptionPane.showMessageDialog(parent, message, "Cannot open this file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void openNative(String arg, File dir) {
        final String command;
        switch (OperatingSystem.CURRENT) {
            case LINUX:
                command = "xdg-open";
                break;
            case WINDOWS:
                command = "explorer";
                break;
            case MACOS:
                command = "open";
                break;
            case UNKNOWN:
            default:
                System.err.println("Unknown operating system. Cannot open browser.");
                return;
        }
        try {
            System.out.println("Using '" + command + "' to open '" + arg + "'...");
            ProcessBuilder processBuilder = new ProcessBuilder(command, arg)
                    .directory(dir);
            processBuilder.start();
        } catch (IOException e) {
            System.err.println("Could not execute '" + command + "'. Got exception " + e);
        }
    }

}
