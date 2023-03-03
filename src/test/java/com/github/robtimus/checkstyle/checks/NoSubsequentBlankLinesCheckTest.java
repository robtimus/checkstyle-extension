/*
 * NoSubsequentBlankLinesCheckTest.java
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

import static com.github.robtimus.checkstyle.checks.CheckUtils.violation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import com.github.robtimus.junit.support.extension.testresource.AsLines;
import com.github.robtimus.junit.support.extension.testresource.Encoding;
import com.github.robtimus.junit.support.extension.testresource.TestResource;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.Violation;

@SuppressWarnings("nls")
@Encoding("UTF-8")
class NoSubsequentBlankLinesCheckTest {

    @Test
    void testProcess(@TestResource("BlankLines.java") @AsLines List<String> lines) {
        File file = new File("BlankLines.java");
        FileText fileText = new FileText(file, lines);

        NoSubsequentBlankLinesCheck check = newCheck();

        Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
        assertThat(violations, contains(
                violation(5, 0, "blankLines.multipleBlankLinesInARow", TestMessages.blankLines.multipleBlankLinesInARow()),
                violation(10, 0, "blankLines.multipleBlankLinesInARow", TestMessages.blankLines.multipleBlankLinesInARow()),
                violation(27, 0, "blankLines.multipleBlankLinesInARow", TestMessages.blankLines.multipleBlankLinesInARow())
        ));
    }

    private NoSubsequentBlankLinesCheck newCheck() {
        NoSubsequentBlankLinesCheck check = new NoSubsequentBlankLinesCheck();
        DefaultConfiguration configuration = new DefaultConfiguration("default");
        assertDoesNotThrow(() -> check.configure(configuration));
        return check;
    }
}
