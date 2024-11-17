package be.yvanmazy.minecraftremapper.gradle.extension;

import be.yvanmazy.minecraftremapper.DirectionType;
import org.gradle.api.InvalidUserDataException;

import java.nio.file.Path;

public class RemapperExtension {

    private Path homePath;

    private String version;
    private DirectionType directionType;

    private boolean remapOnCompile = true;
    private boolean cancelCompileCache = true;
    private boolean includeDependency = true;
    private boolean includeLibrariesDependency;

    public void validate() {
        if (this.version == null) {
            throw new InvalidUserDataException("Version is not set");
        }
        if (this.directionType == null) {
            throw new InvalidUserDataException("Direction type is not set");
        }
    }

    public Path getHomePath() {
        return this.homePath;
    }

    public void setHomePath(final Path homePath) {
        this.homePath = homePath;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public DirectionType getDirectionType() {
        return this.directionType;
    }

    public void setDirectionType(final DirectionType directionType) {
        this.directionType = directionType;
    }

    public boolean isRemapOnCompile() {
        return this.remapOnCompile;
    }

    public void setRemapOnCompile(final boolean remapOnCompile) {
        this.remapOnCompile = remapOnCompile;
    }

    public boolean isCancelCompileCache() {
        return this.cancelCompileCache;
    }

    public void setCancelCompileCache(final boolean cancelCompileCache) {
        this.cancelCompileCache = cancelCompileCache;
    }

    public boolean isIncludeDependency() {
        return this.includeDependency;
    }

    public void setIncludeDependency(final boolean includeDependency) {
        this.includeDependency = includeDependency;
    }

    public boolean isIncludeLibrariesDependency() {
        return this.includeLibrariesDependency;
    }

    public void setIncludeLibrariesDependency(final boolean includeLibrariesDependency) {
        this.includeLibrariesDependency = includeLibrariesDependency;
    }

}