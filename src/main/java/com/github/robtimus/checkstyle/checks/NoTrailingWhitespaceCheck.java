/*
 * NoTrailingWhitespaceCheck.java
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
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;

/**
 * Checks that files have no trailing whitespace.
 *
 * @author Rob Spoor
 */
@SuppressWarnings("nls")
public class NoTrailingWhitespaceCheck extends AbstractFileSetCheck {

    @Override
    protected void processFiltered(File file, FileText fileText) {
        int lineNumber = 0;
        int lineCount = fileText.size();

        while (lineNumber < lineCount) {
            String line = fileText.get(lineNumber);
            if (!line.isEmpty()) {
                int indexOfLastNonWhitespace = indexOfLastNonWhitespace(line);
                if (indexOfLastNonWhitespace != line.length() - 1) {
                    // Line numbers start at 1, not 0
                    log(lineNumber + 1, indexOfLastNonWhitespace + 1, "whitespace.trailing");
                }
            }
            lineNumber++;
        }
    }

    private int indexOfLastNonWhitespace(String line) {
        for (int i = line.length() - 1; i >= 0; i--) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
