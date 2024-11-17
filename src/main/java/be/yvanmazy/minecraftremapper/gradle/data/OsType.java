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

import org.jetbrains.annotations.NotNull;

public enum OsType {

    WINDOWS,
    LINUX,
    OSX,
    UNKNOWN;

    public static @NotNull OsType getCurrentType() {
        return fromString(System.getProperty("os.name").toLowerCase());
    }

    public static @NotNull OsType fromString(final String osId) {
        if (osId == null) {
            return UNKNOWN;
        } else if (osId.contains("win")) {
            return WINDOWS;
        } else if (osId.contains("unix") || osId.contains("linux")) {
            return LINUX;
        } else if (osId.contains("osx") || osId.contains("mac")) {
            return OSX;
        }
        return UNKNOWN;
    }

}