package be.yvanmazy.minecraftremapper.gradle.data;

import java.util.List;

public record Library(Downloads downloads, String name, List<Rule> rules) {

    public boolean isAllowed() {
        if (this.rules != null) {
            for (final Rule rule : this.rules) {
                if (!rule.isAllowed()) {
                    return false;
                }
            }
        }
        return true;
    }

    public record Downloads(Artifact artifact) {

        public record Artifact(String path, String sha1, int size, String url) {

        }

    }

    public record Rule(String action, OS os) {

        public boolean isAllowed() {
            return (this.os == null || this.os.isValid()) == this.action.equals("allow");
        }

        public record OS(String name, String version, String arch) {

            public boolean isValid() {
                final OsType type;
                if (this.name != null && (type = OsType.fromString(this.name)) != OsType.getCurrentType() && type != OsType.UNKNOWN) {
                    return false;
                }
                if (this.version != null && !this.version.equals(System.getProperty("os.version"))) {
                    return false;
                }
                return this.arch == null || this.arch.equals(System.getProperty("os.arch"));
            }

        }

    }

}