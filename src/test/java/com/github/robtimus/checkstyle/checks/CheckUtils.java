/*
 * CheckUtils.java
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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import org.hamcrest.Matcher;
import com.puppycrawl.tools.checkstyle.api.Violation;

@SuppressWarnings("nls")
final class CheckUtils {

    private CheckUtils() {
    }

    static Matcher<Violation> violation(int lineNumber, int columnNumber, String key, String violation) {
        return allOf(
                hasProperty("lineNo", equalTo(lineNumber)),
                hasProperty("columnNo", equalTo(columnNumber)),
                hasProperty("key", equalTo(key)),
                hasProperty("violation", equalTo(violation))
        );
    }
}
