package common;

public class OperationSystem {
    private boolean is64bit;
    private String osName;
    public static final OperationSystem instance = OperationSystem.detectOS();
    private boolean isLinux;

    private static OperationSystem detectOS() {
        OperationSystem os = new OperationSystem();
        os.setOsName(System.getProperty("os.name"));
        if (os.getOsName().contains("Windows")) {
            os.set64bit(System.getenv("ProgramFiles(x86)") != null);
        } else {
            os.set64bit(System.getProperty("os.arch").contains("64"));
            os.setLinux(true);
        }
        return os;
    }

    public boolean is64bit() {
        return is64bit;
    }

    public void set64bit(boolean is64bit) {
        this.is64bit = is64bit;
    }

    public void setOsName(String name) {
        this.osName = name;
    }

    public String getOsName() {
        return this.osName;
    }

    public boolean isLinux() {
        return isLinux;
    }

    public void setLinux(boolean isLinux) {
        this.isLinux = isLinux;
    }

}
