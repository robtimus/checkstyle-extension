/*
 * LicenseCommentCheckTest.java
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.github.robtimus.checkstyle.checks.LicenseCommentCheck.Line;
import com.github.robtimus.junit.support.extension.testresource.AsLines;
import com.github.robtimus.junit.support.extension.testresource.Encoding;
import com.github.robtimus.junit.support.extension.testresource.TestResource;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.Violation;

@SuppressWarnings("nls")
@Encoding("UTF-8")
class LicenseCommentCheckTest {

    @Nested
    class GetRequiredLicenseText {

        @Test
        void testNoLicenseSet() {
            LicenseCommentCheck check = newCheck(false);

            assertThrows(CheckstyleException.class, check::getRequiredLicenseText);
        }

        @Test
        void testMultipleLicensesSet() {
            LicenseCommentCheck check = newCheck(true);
            check.setCustomLicenseText("Dummy license text");

            assertThrows(CheckstyleException.class, check::getRequiredLicenseText);
        }
    }

    @Nested
    class EndOfCommentPrefix {

        @Test
        void testNoAsteriskFound() {
            assertEquals(0, LicenseCommentCheck.endOfCommentPrefix("   "));
        }

        @Test
        void testOtherCharacterFound() {
            assertEquals(0, LicenseCommentCheck.endOfCommentPrefix(" /"));
        }

        @Test
        void testEmptyCommentLine() {
            assertEquals(2, LicenseCommentCheck.endOfCommentPrefix(" *"));
        }

        @Test
        void testBlankCommentLine() {
            assertEquals(3, LicenseCommentCheck.endOfCommentPrefix(" * "));
        }

        @Test
        void testNonEmptyCommentLine() {
            assertEquals(3, LicenseCommentCheck.endOfCommentPrefix(" * Some text"));
        }

        @Test
        void testNonEmptyCommentLineMissingSpace() {
            assertEquals(2, LicenseCommentCheck.endOfCommentPrefix(" *Some text"));
        }
    }

    @Nested
    class HasPotentialLicenseFile {

        @Test
        void testNoLines() {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.emptyList();
            List<String> requiredLicenseText = assertDoesNotThrow(check::getRequiredLicenseText);

            assertFalse(check.hasPotentialLicenseFile(licenseText, requiredLicenseText));
        }

        @ParameterizedTest(name = "first line: {0}")
        @ValueSource(strings = { "Copyright 2023 Rob Spoor", "Licensed under the Apache License, Version 2.0 (the \"License\");" })
        @EmptySource
        void testFirstLineIsNoFilename(String firstLineContent) {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line(firstLineContent, 3));
            List<String> requiredLicenseText = assertDoesNotThrow(check::getRequiredLicenseText);

            assertFalse(check.hasPotentialLicenseFile(licenseText, requiredLicenseText));
        }

        @ParameterizedTest(name = "filename: {0}")
        @ValueSource(strings = { "Filename.java", "Filename" })
        void testFirstLineIsPotentialFilename(String firstLineContent) {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line(firstLineContent, 3));
            List<String> requiredLicenseText = assertDoesNotThrow(check::getRequiredLicenseText);

            assertTrue(check.hasPotentialLicenseFile(licenseText, requiredLicenseText));
        }
    }

    @Nested
    class HasCopyright {

        @Test
        void testNoLines() {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.emptyList();

            assertFalse(check.hasCopyright(licenseText));
        }

        @ParameterizedTest(name = "first line: {0}")
        @ValueSource(strings = { "Filename.java", "Licensed under the Apache License, Version 2.0 (the \"License\");" })
        @EmptySource
        void testFirstLineIsNoCopyright(String firstLineContent) {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line(firstLineContent, 3));

            assertFalse(check.hasCopyright(licenseText));
        }

        @ParameterizedTest(name = "{0}")
        @ValueSource(strings = { "Copyright 2023 Rob Spoor", "Copyright 2023" })
        void testFirstLineIsCopyright(String firstLineContent) {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line(firstLineContent, 3));

            assertTrue(check.hasCopyright(licenseText));
        }
    }

    @Nested
    class HasEmptyLine {

        @Test
        void testNoLines() {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.emptyList();

            assertFalse(check.hasEmptyLine(licenseText));
        }

        @ParameterizedTest(name = "first line: {0}")
        @ValueSource(strings = { "Filename.java", "Copyright 2023 Rob Spoor", "Licensed under the Apache License, Version 2.0 (the \"License\");" })
        void testFirstLineIsNotEmpty(String firstLineContent) {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line(firstLineContent, 3));

            assertFalse(check.hasEmptyLine(licenseText));
        }

        @Test
        void testFirstLineIsEmpty() {
            LicenseCommentCheck check = newCheck();
            List<Line> licenseText = Collections.singletonList(new Line("", 3));

            assertTrue(check.hasEmptyLine(licenseText));
        }
    }

    @Nested
    class IsValidCopyrightYear {

        @Test
        void testValidWithSpecificYear() {
            LicenseCommentCheck check = newCheck();
            check.setRequiredCopyrightYear("2023");

            assertTrue(check.isValidCopyrightYear("2023"));
        }

        @Test
        void testInvalidWithSpecificYear() {
            LicenseCommentCheck check = newCheck();
            check.setRequiredCopyrightYear("2023");

            assertFalse(check.isValidCopyrightYear("2022"));
        }

        @Test
        void testValidWithCurrentYear() {
            LicenseCommentCheck check = newCheck();
            check.setRequiredCopyrightYear("current");

            assertTrue(check.isValidCopyrightYear(Integer.toString(LocalDate.now().getYear())));
        }

        @Test
        void testInvalidWithCurrentYear() {
            LicenseCommentCheck check = newCheck();
            check.setRequiredCopyrightYear("current");

            assertFalse(check.isValidCopyrightYear(Integer.toString(LocalDate.now().minusYears(1).getYear())));
        }
    }

    @Nested
    class Process {

        @Test
        void testEmptyFile(@TestResource("licenseComment/EmptyFile.java") @AsLines List<String> lines) {
            testNoLicense("EmptyFile.java", lines);
        }

        @Test
        void testNoLicenseComment(@TestResource("licenseComment/NoLicenseComment.java") @AsLines List<String> lines) {
            testNoLicense("NoLicenseComment.java", lines);
        }

        @Test
        void testEmptyLicenseComment(@TestResource("licenseComment/EmptyLicenseComment.java") @AsLines List<String> lines) {
            testNoLicense("EmptyLicenseComment.java", lines);
        }

        private void testNoLicense(String filename, List<String> lines) {
            File file = new File(filename);
            FileText fileText = new FileText(file, lines);

            LicenseCommentCheck check = newCheck();

            Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
            assertThat(violations, contains(violation(1, 0, "licenseComment.noLicense", TestMessages.licenseComment.noLicense())));
        }

        @Test
        void testNoCommentEnd(@TestResource("licenseComment/NoCommentEnd.java") @AsLines List<String> lines) {
            File file = new File("NoCommentEnd.java");
            FileText fileText = new FileText(file, lines);

            LicenseCommentCheck check = newCheck();
            check.setIncludeFilename(true);
            check.setIncludeCopyright(true);
            check.setIncludeEmptyLineBeforeLicenseText(true);

            Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
            assertEquals(Collections.emptySet(), violations);
        }

        @Nested
        class ValidateFilename {

            @Nested
            class NoFilename {

                @Test
                void testRequired(@TestResource("licenseComment/NoFilename.java") @AsLines List<String> lines) {
                    File file = new File("NoFilename.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeFilename(true);
                    check.setIncludeCopyright(true);
                    check.setIncludeEmptyLineBeforeLicenseText(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertThat(violations, contains(violation(2, 0, "licenseComment.missingFilename",
                            TestMessages.licenseComment.missingFilename())));
                }

                @Test
                void testNotAllowed(@TestResource("licenseComment/NoFilename.java") @AsLines List<String> lines) {
                    File file = new File("NoFilename.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeCopyright(true);
                    check.setIncludeEmptyLineBeforeLicenseText(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertEquals(Collections.emptySet(), violations);
                }
            }

            @Test
            void testDisallowedFilename(@TestResource("licenseComment/NoCopyright.java") @AsLines List<String> lines) {
                File file = new File("NoCopyright.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(2, 0, "licenseComment.disallowedFilename",
                        TestMessages.licenseComment.disallowedFilename())));
            }

            @Test
            void testInvalidFilename(@TestResource("licenseComment/DifferentFilename.java") @AsLines List<String> lines) {
                File file = new File("DifferentFilename.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(2, 4, "licenseComment.invalidFilename",
                        TestMessages.licenseComment.invalidFilename("DifferentFilename.java"))));
            }
        }

        @Nested
        class ValidateCopyright {

            @Nested
            class NoCopyright {

                @Test
                void testRequired(@TestResource("licenseComment/NoCopyright.java") @AsLines List<String> lines) {
                    File file = new File("NoCopyright.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeFilename(true);
                    check.setIncludeCopyright(true);
                    check.setIncludeEmptyLineBeforeLicenseText(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertThat(violations, contains(violation(3, 0, "licenseComment.missingCopyright",
                            TestMessages.licenseComment.missingCopyright())));
                }

                @Test
                void testNotAllowed(@TestResource("licenseComment/NoCopyright.java") @AsLines List<String> lines) {
                    File file = new File("NoCopyright.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeFilename(true);
                    // test that setting these two to null doesn't affect anything
                    check.setRequiredCopyrightYear(null);
                    check.setRequiredCopyrightHolder(null);
                    check.setIncludeEmptyLineBeforeLicenseText(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertEquals(Collections.emptySet(), violations);
                }
            }

            @Test
            void testDisallowedCopyright(@TestResource("licenseComment/NoFilename.java") @AsLines List<String> lines) {
                File file = new File("NoCopyright.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(2, 0, "licenseComment.disallowedCopyright",
                        TestMessages.licenseComment.disallowedCopyright())));
            }

            @Test
            void testInvalidCopyrightFormat(@TestResource("licenseComment/InvalidCopyrightFormat.java") @AsLines List<String> lines) {
                File file = new File("InvalidCopyrightFormat.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 4, "licenseComment.invalidCopyright", TestMessages.licenseComment.invalidCopyright())));
            }

            @Test
            void testCopyrightFromNotAfterTo(@TestResource("licenseComment/CopyrightFromNotAfterTo.java") @AsLines List<String> lines) {
                File file = new File("CopyrightFromNotAfterTo.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 14, "licenseComment.invalidCopyrightYearRange",
                        TestMessages.licenseComment.invalidCopyrightYearRange())));
            }

            @Test
            void testCopyrightInvalidFrom(@TestResource("licenseComment/CopyrightWithSingleYear.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithSingleYear.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightYear("2022");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 14, "licenseComment.invalidCopyrightYear",
                        TestMessages.licenseComment.invalidCopyrightYear("2022"))));
            }

            @Test
            void testCopyrightFromTo(@TestResource("licenseComment/CopyrightWithSingleYear.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithSingleYear.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightYear("2023");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }

            @Test
            void testCopyrightInvalidTo(@TestResource("licenseComment/CopyrightWithRange.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithRange.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightYear("2022");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 19, "licenseComment.invalidCopyrightYear",
                        TestMessages.licenseComment.invalidCopyrightYear("2022"))));
            }

            @Test
            void testCopyrightValidTo(@TestResource("licenseComment/CopyrightWithRange.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithRange.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightYear("2023");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }

            @Test
            void testCopyrightInvalidHolderWithSingleYear(@TestResource("licenseComment/CopyrightWithSingleYear.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithSingleYear.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightHolder("John Doe");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 19, "licenseComment.invalidCopyrightHolder",
                        TestMessages.licenseComment.invalidCopyrightHolder("John Doe"))));
            }

            @Test
            void testCopyrightValidHolderWithSingleYear(@TestResource("licenseComment/CopyrightWithSingleYear.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithSingleYear.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightHolder("Rob Spoor");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }

            @Test
            void testCopyrightInvalidHolderWithYearRange(@TestResource("licenseComment/CopyrightWithRange.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithRange.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightHolder("John Doe");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 24, "licenseComment.invalidCopyrightHolder",
                        TestMessages.licenseComment.invalidCopyrightHolder("John Doe"))));
            }

            @Test
            void testCopyrightValidHolderWithYearRange(@TestResource("licenseComment/CopyrightWithRange.java") @AsLines List<String> lines) {
                File file = new File("CopyrightWithRange.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setRequiredCopyrightHolder("Rob Spoor");
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }
        }

        @Nested
        class EmptyLineBeforeLicenseText {

            @Nested
            class NoEmptyLine {

                @Test
                void testRequired(@TestResource("licenseComment/NoEmptyLine.java") @AsLines List<String> lines) {
                    File file = new File("NoEmptyLine.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeFilename(true);
                    check.setIncludeCopyright(true);
                    check.setIncludeEmptyLineBeforeLicenseText(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertThat(violations, contains(violation(4, 0, "licenseComment.missingEmptyLine",
                            TestMessages.licenseComment.missingEmptyLine())));
                }

                @Test
                void testNotAllowed(@TestResource("licenseComment/NoEmptyLine.java") @AsLines List<String> lines) {
                    File file = new File("NoEmptyLine.java");
                    FileText fileText = new FileText(file, lines);

                    LicenseCommentCheck check = newCheck();
                    check.setIncludeFilename(true);
                    check.setIncludeCopyright(true);

                    Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                    assertEquals(Collections.emptySet(), violations);
                }
            }

            @Test
            void testDisallowedEmptyLine(@TestResource("licenseComment/NoCopyright.java") @AsLines List<String> lines) {
                File file = new File("NoCopyright.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setIncludeFilename(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(3, 0, "licenseComment.disallowedEmptyLine",
                        TestMessages.licenseComment.disallowedEmptyLine())));
            }
        }

        @Nested
        class ValidateLicenseText {

            @Test
            void testLicenseTextLineMismatch(@TestResource("licenseComment/LicenseTextLineMismatch.java") @AsLines List<String> lines) {
                File file = new File("LicenseTextLineMismatch.java");
                FileText fileText = new FileText(file, lines);

                testInvalidLicenseText(file, fileText, "    http://www.apache.org/licenses/LICENSE-2.0", 9, 4);
            }

            @Test
            void testLicenseTextMissingLineAtStart(@TestResource("licenseComment/LicenseTextMissingLineAtStart.java") @AsLines List<String> lines) {
                File file = new File("LicenseTextMissingLineAtStart.java");
                FileText fileText = new FileText(file, lines);

                testInvalidLicenseText(file, fileText, "Licensed under the Apache License, Version 2.0 (the \"License\");", 5, 4);
            }

            @Test
            void testLicenseTextMissingLineInMiddle(@TestResource("licenseComment/LicenseTextMissingLineInMiddle.java") @AsLines List<String> lines) {
                File file = new File("LicenseTextMissingLineInMiddle.java");
                FileText fileText = new FileText(file, lines);

                testInvalidLicenseText(file, fileText, "you may not use this file except in compliance with the License.", 6, 4);
            }

            @Test
            void testLicenseTextMissingLineAtEnd(@TestResource("licenseComment/LicenseTextMissingLineAtEnd.java") @AsLines List<String> lines) {
                File file = new File("LicenseTextMissingLineAtEnd.java");
                FileText fileText = new FileText(file, lines);

                testInvalidLicenseText(file, fileText, "limitations under the License.", 15, 0);
            }

            @Test
            void testLicenseTextExtraLineAtEnd(@TestResource("licenseComment/LicenseTextExtraLineAtEnd.java") @AsLines List<String> lines) {
                File file = new File("LicenseTextExtraLineAtEnd.java");
                FileText fileText = new FileText(file, lines);

                testInvalidLicenseText(file, fileText, "", 16, 4);
            }

            private void testInvalidLicenseText(File file, FileText fileText, String expectedLine, int lineNumber, int columnNumber) {
                LicenseCommentCheck check = newCheck();
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertThat(violations, contains(violation(lineNumber, columnNumber, "licenseComment.licenseTextMismatch",
                        TestMessages.licenseComment.licenseTextMismatch(expectedLine))));
            }

            @Test
            void testCustomLicenseText(@TestResource("licenseComment/CustomLicenseText.java") @AsLines List<String> lines) {
                File file = new File("CustomLicenseText.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck(false);
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);
                check.setCustomLicenseText("Dummy license text");

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }

            @Test
            void testIndentedLicenseText(@TestResource("licenseComment/IndentedLicenseText.java") @AsLines List<String> lines) {
                String licenseText = "\n"
                        + "    This program and the accompanying materials are made available under the\n"
                        + "    terms of the Eclipse Public License 2.0 which is available at\n"
                        + "    http://www.eclipse.org/legal/epl-2.0.\n"
                        + "\n"
                        + "    SPDX-License-Identifier: EPL-2.0\n"
                        + "   ";

                File file = new File("IndentedLicenseText.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck(false);
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(true);
                check.setCustomLicenseText(licenseText);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }

            @Test
            void testEmptyLicenseText(@TestResource("licenseComment/EmptyLicenseText.java") @AsLines List<String> lines) {
                testEmptyLicenseText("", lines);
            }

            @Test
            void testSingleBlankLineLicenseText(@TestResource("licenseComment/EmptyLicenseText.java") @AsLines List<String> lines) {
                testEmptyLicenseText("    \n", lines);
            }

            private void testEmptyLicenseText(String licenseText, List<String> lines) {
                File file = new File("EmptyLicenseText.java");
                FileText fileText = new FileText(file, lines);

                LicenseCommentCheck check = newCheck(false);
                check.setIncludeFilename(true);
                check.setIncludeCopyright(true);
                check.setIncludeEmptyLineBeforeLicenseText(false);
                check.setCustomLicenseText(licenseText);

                Set<Violation> violations = assertDoesNotThrow(() -> check.process(file, fileText));
                assertEquals(Collections.emptySet(), violations);
            }
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class PredefinedLicense {

        @Test
        void testExistingLicenses() {
            Set<String> expectedPredefinedLicenses = predefinedLicenseNames().collect(Collectors.toSet());
            assertEquals(expectedPredefinedLicenses, LicenseCommentCheck.predefinedLicenses());
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("predefinedLicenses")
        void testExistingPredefinedLicense(String predefinedLicense) {
            LicenseCommentCheck check = newCheck(false);

            assertDoesNotThrow(() -> check.setPredefinedLicenseText(predefinedLicense));
        }

        @Test
        void testNonExistingResource() {
            LicenseCommentCheck check = newCheck(false);

            assertThrows(IllegalArgumentException.class, () -> check.setPredefinedLicenseText("non-existing"));
        }

        @Test
        void testInvalidExistingResource() {
            LicenseCommentCheck check = newCheck(false);

            String resource = "../messages.properties";

            assertNotNull(LicenseCommentCheck.class.getResource("licenses/" + resource));

            assertThrows(IllegalArgumentException.class, () -> check.setPredefinedLicenseText(resource));
        }

        Stream<String> predefinedLicenseNames() {
            return assertDoesNotThrow(() -> {
                URI uri = LicenseCommentCheck.class.getResource("licenses/Apache-2.0").toURI();
                Path parentDirectory = Paths.get(uri).getParent();
                try (Stream<Path> paths = Files.walk(parentDirectory)) {
                    return paths
                            .filter(p -> !parentDirectory.equals(p))
                            .map(p -> p.getFileName().toString())
                            .collect(Collectors.toList())
                            .stream();
                }
            });
        }

        Stream<Arguments> predefinedLicenses() {
            return predefinedLicenseNames()
                    .map(Arguments::arguments);
        }
    }

    private LicenseCommentCheck newCheck() {
        return newCheck(true);
    }

    private LicenseCommentCheck newCheck(boolean useApacheLicense) {
        LicenseCommentCheck check = new LicenseCommentCheck();
        DefaultConfiguration configuration = new DefaultConfiguration("default");
        if (useApacheLicense) {
            configuration.addProperty("predefinedLicenseText", "Apache-2.0");
        }
        assertDoesNotThrow(() -> check.configure(configuration));
        return check;
    }
}
