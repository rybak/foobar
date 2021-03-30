package dev.andrybak.qc.os;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class UrlOpener {

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
            openUrlNative(url);
        }
    }

    private static void openUrlNative(String url) {
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
            System.out.println("Using '" + command + "' to open '" + url + "'...");
            Runtime.getRuntime().exec(command + " " + url);
        } catch (IOException e) {
            System.err.println("Could not execute '" + command + "'. Got exception " + e);
        }
    }

}
