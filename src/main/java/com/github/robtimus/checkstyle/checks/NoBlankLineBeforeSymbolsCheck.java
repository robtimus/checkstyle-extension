/*
 * NoBlankLineBeforeSymbolsCheck.java
 * Copyright 2023 Rob Spoor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.robtimus.checkstyle.checks;

import java.io.File;
import java.util.Objects;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Checks that files do not contain blank lines before lines that only consist of specific symbols. Trailing semicolons are ignored while checking,
 * except if semicolons are part of the symbols to check for.
 * <p>
 * By default, the symbols are ), }, ] and ;.
 *
 * @author Rob Spoor
 */
@SuppressWarnings("nls")
public class NoBlankLineBeforeSymbolsCheck extends AbstractFileSetCheck {

    private String symbols;
    private boolean trimSemicolons;

    /**
     * Creates a new check.
     */
    public NoBlankLineBeforeSymbolsCheck() {
        setSymbols(")}];");
    }

    @Override
    protected void processFiltered(File file, FileText fileText) throws CheckstyleException {
        int lineNumber = 0;
        int lineCount = fileText.size();

        while (lineNumber < lineCount) {
            String line = fileText.get(lineNumber);
            if (isMatchingLine(line) && previousLineIsBlank(fileText, lineNumber)) {
                // Line numbers start at 1, not 0
                log(lineNumber, "whitespace.blankLineBeforeSymbols", symbols);
            }
            lineNumber++;
        }
    }

    private boolean isMatchingLine(String line) {
        int end = line.length();
        if (trimSemicolons) {
            while (end > 0 && line.charAt(end - 1) == ';') {
                end--;
            }
        }
        int numberOfSymbols = 0;
        for (int i = 0; i < end; i++) {
            char c = line.charAt(i);
            boolean isSymbol = symbols.indexOf(c) != -1;
            if (!isSymbol && !Character.isWhitespace(c)) {
                return false;
            }
            // whitespace or a symbol
            if (isSymbol) {
                numberOfSymbols++;
            }
        }
        // Don't return true for lines containing only whitespace
        return numberOfSymbols > 0;
    }

    private boolean previousLineIsBlank(FileText fileText, int lineNumber) {
        return lineNumber - 1 >= 0 && CommonUtil.isBlank(fileText.get(lineNumber - 1));
    }

    /**
     * Sets the symbols to check for.
     *
     * @param symbols A string with symbols to check for.
     * @throws NullPointerException If the given string is {@code null}.
     */
    public final void setSymbols(String symbols) {
        this.symbols = Objects.requireNonNull(symbols);
        this.trimSemicolons = symbols.indexOf(';') == -1;
    }
}
