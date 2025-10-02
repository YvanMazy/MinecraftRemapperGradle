/*
 * MIT License
 *
 * Copyright (c) 2025 Yvan Mazy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.yvanmazy.minecraftremapper.gradle.extension;

import be.yvanmazy.minecraftremapper.DirectionType;
import org.gradle.api.InvalidUserDataException;

import java.io.File;
import java.util.List;

public class RemapperExtension {

    private File homePath;

    private String version;
    private DirectionType directionType;

    private boolean remapOnCompile = true;
    private boolean cancelCompileCache = true;
    private boolean includeRemappedJarDependency = true;
    private boolean includeRawJarDependency;
    private boolean includeLibrariesDependency;

    private List<String> remappedJarDependenciesConfigurations = List.of("compileOnly");
    private List<String> rawJarDependenciesConfigurations = List.of("compileOnly");
    private List<String> librariesDependenciesConfigurations = List.of("compileOnly");

    public void validate() {
        if (this.version == null) {
            throw new InvalidUserDataException("Version is not set");
        }
        if (this.directionType == null) {
            throw new InvalidUserDataException("Direction type is not set");
        }
    }

    public File getHomePath() {
        return this.homePath;
    }

    public void setHomePath(final File homePath) {
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

    public boolean isIncludeRemappedJarDependency() {
        return this.includeRemappedJarDependency;
    }

    public void setIncludeRemappedJarDependency(final boolean includeRemappedJarDependency) {
        this.includeRemappedJarDependency = includeRemappedJarDependency;
    }

    public boolean isIncludeRawJarDependency() {
        return this.includeRawJarDependency;
    }

    public void setIncludeRawJarDependency(final boolean includeRawJarDependency) {
        this.includeRawJarDependency = includeRawJarDependency;
    }

    public boolean isIncludeLibrariesDependency() {
        return this.includeLibrariesDependency;
    }

    public void setIncludeLibrariesDependency(final boolean includeLibrariesDependency) {
        this.includeLibrariesDependency = includeLibrariesDependency;
    }

    public List<String> getRemappedJarDependenciesConfigurations() {
        return this.remappedJarDependenciesConfigurations;
    }

    public void setRemappedJarDependenciesConfigurations(final List<String> remappedJarDependenciesConfigurations) {
        this.remappedJarDependenciesConfigurations = remappedJarDependenciesConfigurations;
    }

    public List<String> getRawJarDependenciesConfigurations() {
        return this.rawJarDependenciesConfigurations;
    }

    public void setRawJarDependenciesConfigurations(final List<String> rawJarDependenciesConfigurations) {
        this.rawJarDependenciesConfigurations = rawJarDependenciesConfigurations;
    }

    public List<String> getLibrariesDependenciesConfigurations() {
        return this.librariesDependenciesConfigurations;
    }

    public void setLibrariesDependenciesConfigurations(final List<String> librariesDependenciesConfigurations) {
        this.librariesDependenciesConfigurations = librariesDependenciesConfigurations;
    }

}