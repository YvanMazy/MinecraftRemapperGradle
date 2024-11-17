/*
 * MIT License
 *
 * Copyright (c) 2024 Yvan Mazy
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