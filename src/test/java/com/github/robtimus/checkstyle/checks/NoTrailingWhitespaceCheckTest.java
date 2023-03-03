/*
 * NoTrailingWhitespaceCheckTest.java
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
import com.github.robtimus.junit.support.extension.testresource.TestResource;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.Violation;

@SuppressWarnings("nls")
class NoTrailingWhitespaceCheckTest {

    @Test
    void testProcess(@TestResource("BlankLines.java") @AsLines List<String> lines) {
        File file = new File("BlankLines.java");
        FileText fileText = new FileText(file, lines);

        NoTrailingWhitespaceCheck check = newCheck();

        Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
        assertThat(violations, contains(
                violation(3, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(5, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(6, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(7, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(9, 32, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(10, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(11, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(13, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(15, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(17, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(19, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(22, 1, "whitespace.trailing", TestMessages.whitespace.trailing()),
                violation(25, 1, "whitespace.trailing", TestMessages.whitespace.trailing())
        ));
    }

    private NoTrailingWhitespaceCheck newCheck() {
        NoTrailingWhitespaceCheck check = new NoTrailingWhitespaceCheck();
        DefaultConfiguration configuration = new DefaultConfiguration("default");
        assertDoesNotThrow(() -> check.configure(configuration));
        return check;
    }
}
