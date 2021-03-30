package dev.andrybak.qc.os;

enum OperatingSystem {
    LINUX,
    WINDOWS,
    MACOS,
    UNKNOWN,
    ;

    public static final OperatingSystem CURRENT = findOS();

    private static OperatingSystem findOS() {
        String name = System.getProperty("os.name");
        if (name == null)
            return UNKNOWN;
        name = name.toLowerCase();
        if (name.contains("linux"))
            return LINUX;
        if (name.contains("windows"))
            return WINDOWS;
        if (name.contains("mac"))
            return MACOS;
        return UNKNOWN;
    }

}
