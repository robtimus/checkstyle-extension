/*
 * NoSubsequentBlankLinesCheck.java
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
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Checks that files do not have multiple blank lines in a row.
 *
 * @author Rob Spoor
 */
@SuppressWarnings("nls")
public class NoSubsequentBlankLinesCheck extends AbstractFileSetCheck {

    @Override
    protected void processFiltered(File file, FileText fileText) throws CheckstyleException {
        int lineNumber = 0;
        int lineCount = fileText.size();

        while (lineNumber < lineCount) {
            if (CommonUtil.isBlank(fileText.get(lineNumber))) {
                int nextNonBlankLine = findNextNonBlankLine(fileText, lineNumber, lineCount);
                if (nextNonBlankLine - lineNumber > 1) {
                    // Line numbers start at 1, not 0
                    log(lineNumber + 1, "blankLines.multipleBlankLinesInARow");
                    lineNumber = nextNonBlankLine;
                }
            }
            lineNumber++;
        }
    }

    private int findNextNonBlankLine(FileText fileText, int lineNumber, int lineCount) {
        for (int i = lineNumber + 1; i < lineCount; i++) {
            if (!CommonUtil.isBlank(fileText.get(i))) {
                return i;
            }
        }
        return lineCount;
    }
}
