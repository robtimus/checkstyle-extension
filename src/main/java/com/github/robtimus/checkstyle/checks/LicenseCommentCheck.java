/*
 * LicenseCommentCheck.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Checks whether or not a Java source file has a valid leading license comment.
 * <p>
 * A leading license comment is valid if:
 * <ul>
 *   <li>It starts with the name of the file if and only if {@link #setIncludeFilename(boolean)} has been called.</li>
 *   <li>It starts with a valid copyright notice if and only if {@link #setIncludeCopyright(boolean)} has been called.
 *       Calling {@link #setRequiredCopyrightYear(String)} and {@link #setRequiredCopyrightHolder(String)} imply this.</li>
 *   <li>There is an empty line before the remainder of the license text if and only if {@link #setIncludeEmptyLineBeforeLicenseText(boolean)} has
 *       been called.</li>
 *   <li>The remainder of the license text is valid according to what's been set with {@link #setLicenseText(String)}.</li>
 * </ul>
 *
 * @author Rob Spoor
 */
@SuppressWarnings("nls")
public class LicenseCommentCheck extends AbstractFileSetCheck {

    private static final Pattern COPYRIGHT_PATTERN = Pattern.compile("Copyright (?<yearFrom>\\d{4})(?:-(?<yearTo>\\d{4}))?(?: +(?<holder>.*))?");

    private static final int START_COLUMN_YEAR_FROM = 10; // skip past 'Copyright '
    private static final int START_COLUMN_YEAR_TO = START_COLUMN_YEAR_FROM + 5; // skip past '<yearFrom>-'
    private static final int START_COLUMN_HOLDER_WITH_YEAR_TO = START_COLUMN_YEAR_TO + 5; // skip past '<yearTo> '
    private static final int START_COLUMN_HOLDER_WITHOUT_YEAR_TO = START_COLUMN_YEAR_FROM + 5; // skip past '<yearFrom> '
    private static final int START_COLUMN_YEAR_RANGE = START_COLUMN_YEAR_FROM;

    private boolean includeFilename;
    private boolean includeCopyright;
    private String requiredCopyrightYear;
    private String requiredCopyrightHolder;
    private boolean includeEmptyLineBeforeLicenseText;

    private List<String> requiredLicenseText = new ArrayList<>();

    /**
     * Creates a new leading license check.
     */
    public LicenseCommentCheck() {
        setFileExtensions("java");
    }

    @Override
    protected void processFiltered(File file, FileText fileText) {
        List<Line> licenseText = readLicenseText(fileText);

        // Line numbers start at 1, not 0
        LicenseTextContext context = new LicenseTextContext(licenseText, 1);

        if (licenseText.isEmpty()) {
            log(context.lineNumber, "licenseComment.noLicense");
            return;
        }

        // skip past the comment start
        context.lineNumber++;

        validateFilename(file, licenseText, context);

        validateCopyright(context);

        validateEmptyLineBeforeLicenseText(context);

        validateLicenseText(context.licenseText, context.lineNumber);
    }

    private List<Line> readLicenseText(FileText fileText) {
        int lineCount = fileText.size();
        if (lineCount == 0 || !"/*".equals(fileText.get(0))) {
            return Collections.emptyList();
        }
        List<Line> result = new ArrayList<>();
        for (int i = 1; i < lineCount && !" */".equals(fileText.get(i)); i++) {
            int startColumn = endOfCommentPrefix(fileText.get(i));
            // Column numbers start at 0
            result.add(new Line(fileText.get(i).substring(startColumn), startColumn));
        }
        // No */ found means a compiler error, no need to report a Checkstyle error
        return result;
    }

    static int endOfCommentPrefix(String line) {
        int index = 0;
        while (index < line.length() && Character.isWhitespace(line.charAt(index))) {
            index++;
        }
        if (index < line.length() && line.charAt(index) == '*') {
            index++;
            if (index < line.length() && Character.isWhitespace(line.charAt(index))) {
                index++;
            }
            return index;
        }
        return 0;
    }

    private void validateFilename(File file, List<Line> licenseText, LicenseTextContext context) {
        if (includeFilename) {
            if (hasPotentialLicenseFile(context.licenseText)) {
                validateFilename(context.licenseText, context.lineNumber, file);
                context.nextLine();
            } else {
                log(context.lineNumber, "licenseComment.missingFilename");
            }
        } else if (hasPotentialLicenseFile(licenseText)) {
            log(context.lineNumber, "licenseComment.disallowedFilename");
            context.nextLine();
        }
    }

    boolean hasPotentialLicenseFile(List<Line> licenseText) {
        if (licenseText.isEmpty()) {
            return false;
        }
        String line = licenseText.get(0).content();
        return !(line.startsWith("Copyright ") || line.isEmpty() || requiredLicenseText.contains(line));
    }

    private void validateFilename(List<Line> licenseText, int firstLineNumber, File file) {
        Line line = licenseText.get(0);
        String candidate = line.content();
        String expected = file.getName();
        if (!candidate.equals(expected)) {
            log(firstLineNumber, line.startColumn(), "licenseComment.invalidFilename", expected);
        }
    }

    private void validateCopyright(LicenseTextContext context) {
        if (includeCopyright) {
            if (hasCopyright(context.licenseText)) {
                validateCopyright(context.licenseText, context.lineNumber);
                context.nextLine();
            } else {
                log(context.lineNumber, "licenseComment.missingCopyright");
            }
        } else if (hasCopyright(context.licenseText)) {
            log(context.lineNumber, "licenseComment.disallowedCopyright");
            context.nextLine();
        }
    }

    boolean hasCopyright(List<Line> licenseText) {
        if (licenseText.isEmpty()) {
            return false;
        }
        Line line = licenseText.get(0);
        String candidate = line.content();
        return candidate.startsWith("Copyright ");
    }

    private void validateCopyright(List<Line> licenseText, int firstLineNumber) {
        Line line = licenseText.get(0);
        validateCopyright(line.content(), firstLineNumber, line.startColumn());
    }

    private void validateCopyright(String line, int lineNumber, int columnNumber) {
        Matcher matcher = COPYRIGHT_PATTERN.matcher(line);
        if (matcher.matches()) {
            String yearFrom = matcher.group("yearFrom");
            String yearTo = matcher.group("yearTo");
            String holder = matcher.group("holder");

            validateCopyrightYear(yearFrom, yearTo, lineNumber, columnNumber);
            validateCopyrightHolder(holder, lineNumber, columnNumber, yearTo != null);
        } else {
            log(lineNumber, columnNumber, "licenseComment.invalidCopyright");
        }
    }

    private void validateCopyrightYear(String yearFrom, String yearTo, int lineNumber, int columnNumber) {
        if (yearTo != null && yearTo.compareTo(yearFrom) <= 0) {
            log(lineNumber, columnNumber + START_COLUMN_YEAR_RANGE, "licenseComment.invalidCopyrightYearRange");
        }
        if (requiredCopyrightYear != null) {
            if (yearTo == null) {
                validateCopyrightYear(yearFrom, lineNumber, columnNumber + START_COLUMN_YEAR_FROM);
            } else {
                validateCopyrightYear(yearTo, lineNumber, columnNumber + START_COLUMN_YEAR_TO);
            }
        }
    }

    private void validateCopyrightYear(String year, int lineNumber, int columnNumber) {
        if (!isValidCopyrightYear(year)) {
            log(lineNumber, columnNumber, "licenseComment.invalidCopyrightYear", requiredCopyrightYear);
        }
    }

    boolean isValidCopyrightYear(String year) {
        return "current".equals(requiredCopyrightYear)
                ? Year.now().toString().equals(year)
                : requiredCopyrightYear.equals(year);
    }

    private void validateCopyrightHolder(String holder, int lineNumber, int columnNumber, boolean hasYearTo) {
        if (requiredCopyrightHolder != null && !requiredCopyrightHolder.equals(holder)) {
            columnNumber += hasYearTo ? START_COLUMN_HOLDER_WITH_YEAR_TO : START_COLUMN_HOLDER_WITHOUT_YEAR_TO;
            log(lineNumber, columnNumber, "licenseComment.invalidCopyrightHolder", requiredCopyrightHolder);
        }
    }

    private void validateEmptyLineBeforeLicenseText(LicenseTextContext context) {
        if (includeEmptyLineBeforeLicenseText) {
            if (hasEmptyLine(context.licenseText)) {
                // No need to validate anything
                context.nextLine();
            } else {
                log(context.lineNumber, "licenseComment.missingEmptyLine");
            }
        } else if (hasEmptyLine(context.licenseText)) {
            log(context.lineNumber, "licenseComment.disallowedEmptyLine");
            context.nextLine();
        }
    }

    boolean hasEmptyLine(List<Line> licenseText) {
        if (licenseText.isEmpty()) {
            return false;
        }
        Line line = licenseText.get(0);
        return line.content().isEmpty();
    }

    private void validateLicenseText(List<Line> licenseText, int firstLineNumber) {
        final String textMismatchKey = "licenseComment.licenseTextMismatch";

        Iterator<Line> actualLicenseTextIterator = licenseText.iterator();
        Iterator<String> requiredLicenseTextIterator = requiredLicenseText.iterator();
        int lineNumber = firstLineNumber;
        while (actualLicenseTextIterator.hasNext() && requiredLicenseTextIterator.hasNext()) {
            Line actualLicenseTextLine = actualLicenseTextIterator.next();
            String requiredLicenseTextLine = requiredLicenseTextIterator.next();
            if (!requiredLicenseTextLine.equals(actualLicenseTextLine.content())) {
                log(lineNumber, actualLicenseTextLine.startColumn(), textMismatchKey, requiredLicenseTextLine);
                return;
            }
            lineNumber++;
        }
        if (actualLicenseTextIterator.hasNext()) {
            log(lineNumber, actualLicenseTextIterator.next().startColumn(), textMismatchKey, "");
        } else if (requiredLicenseTextIterator.hasNext()) {
            log(lineNumber, textMismatchKey, requiredLicenseTextIterator.next());
        }
    }

    /**
     * Sets whether or not the license comment must include the filename. Defaults to {@code false}.
     *
     * @param includeFilename {@code true} if the license comment must include the filename, or {@code false} if it's not allowed.
     */
    public final void setIncludeFilename(boolean includeFilename) {
        this.includeFilename = includeFilename;
    }

    /**
     * Sets whether or not the license comment must include a copyright. Defaults to {@code false}.
     *
     * @param includeCopyright {@code true} if the license comment must include a copyright, or {@code false} if it's not allowed.
     */
    public final void setIncludeCopyright(boolean includeCopyright) {
        this.includeCopyright = includeCopyright;
    }

    /**
     * Sets the required copyright year. If set to a non-blank value, will require a copyright notice.
     *
     * @param requiredCopyrightYear The required copyright year; {@code current} for the current year.
     */
    public final void setRequiredCopyrightYear(String requiredCopyrightYear) {
        this.requiredCopyrightYear = requiredCopyrightYear;
        if (!CommonUtil.isBlank(requiredCopyrightYear)) {
            setIncludeCopyright(true);
        }
    }

    /**
     * Sets the required copyright holder. If set to a non-blank value, will require a copyright notice.
     *
     * @param requiredCopyrightHolder The required copyright holder.
     */
    public final void setRequiredCopyrightHolder(String requiredCopyrightHolder) {
        this.requiredCopyrightHolder = requiredCopyrightHolder;
        if (!CommonUtil.isBlank(requiredCopyrightHolder)) {
            setIncludeCopyright(true);
        }
    }

    /**
     * Sets whether or not the license comment must include an empty line before the license text. Defaults to {@code false}.
     *
     * @param includeEmptyLineBeforeLicenseText {@code true} if the license comment must include an empty line, or {@code false} if it's not allowed.
     */
    public final void setIncludeEmptyLineBeforeLicenseText(boolean includeEmptyLineBeforeLicenseText) {
        this.includeEmptyLineBeforeLicenseText = includeEmptyLineBeforeLicenseText;
    }

    /**
     * Sets the expected license text. This can be specified as a literal text, or using one of the pre-defined licenses:
     * <ul>
     *   <li><a href="https://opensource.org/licenses/Apache-2.0">Apache-2.0</a>: the Apache License 2.0</li>
     *   <li><a href="https://opensource.org/licenses/BSD-2-Clause">BSD-2-Clause</a>: the 2-Clause BSD License / FreeBSD License /
     *       Simplified BSD License</li>
     *   <li><a href="https://opensource.org/licenses/EPL-1.0">EPL-1.0</a>: the Eclipse Public License 1.0</li>
     *   <li><a href="https://opensource.org/licenses/EPL-2.0">EPL-2.0</a>: the Eclipse Public License 2.0</li>
     *   <li><a href="https://opensource.org/licenses/GPL-2.0">GPL-2.0</a>: the GNU General Public License version 2</li>
     *   <li><a href="https://opensource.org/licenses/GPL-3.0">GPL-3.0</a>: the GNU General Public License version 3</li>
     *   <li><a href="https://opensource.org/licenses/LGPL-2.0">LGPL-2.0</a>: the GNU Library General Public License version 2</li>
     *   <li><a href="https://opensource.org/licenses/LGPL-2.1">LGPL-2.1</a>: the GNU Lesser General Public License version 2.1</li>
     *   <li><a href="https://opensource.org/licenses/MIT">MIT</a>: the MIT License</li>
     *   <li><a href="https://opensource.org/licenses/MPL-2.0">MPL-2.0</a>: the Mozilla Public License 2.0</li>
     * </ul>
     *
     * @param licenseText The expected license text.
     */
    public void setLicenseText(String licenseText) {
        try (InputStream predefinedLicenseTextStream = getClass().getResourceAsStream("licenses/" + licenseText)) {
            if (predefinedLicenseTextStream == null) {
                setLicenseText(new StringReader(licenseText), true);
            } else {
                setLicenseText(predefinedLicenseTextStream, false);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setLicenseText(InputStream inputStream, boolean stripIndent) {
        setLicenseText(new InputStreamReader(inputStream, StandardCharsets.UTF_8), stripIndent);
    }

    private void setLicenseText(Reader reader, boolean stripIndent) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        requiredLicenseText = bufferedReader.lines().collect(Collectors.toList());
        if (stripIndent) {
            requiredLicenseText = stripIndent(requiredLicenseText);
        }
    }

    static List<String> stripIndent(List<String> licenseText) {
        if (licenseText.isEmpty()) {
            return licenseText;
        }
        if (licenseText.size() == 1 && CommonUtil.isBlank(licenseText.get(0))) {
            return Collections.emptyList();
        }
        List<String> result = licenseText;
        if (CommonUtil.isBlank(result.get(0))) {
            result = result.subList(1, result.size());
        }
        // result will not be empty
        if (CommonUtil.isBlank(result.get(result.size() - 1))) {
            result = result.subList(0, result.size() - 1);
        }

        int commonIndentation = commonIndentation(result);
        if (commonIndentation == 0) {
            return result;
        }
        return result.stream()
                .map(line -> line.substring(commonIndentation))
                .collect(Collectors.toList());
    }

    private static int commonIndentation(List<String> lines) {
        return lines.stream()
                .mapToInt(LicenseCommentCheck::indexOfNonWhitespace)
                .min()
                .orElse(0);
    }

    private static int indexOfNonWhitespace(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }
        return line.length();
    }

    /**
     * A representation of a single line.
     *
     * @author Rob Spoor
     */
    public static final class Line {

        private final String content;
        private final int startColumn;

        Line(String content, int startColumn) {
            this.content = content;
            this.startColumn = startColumn;
        }

        /**
         * Returns the actual content of the line.
         *
         * @return The actual content of the line.
         */
        public String content() {
            return content;
        }

        /**
         * Returns the start column of the line. This can be used in calls to {@link AbstractFileSetCheck#log(int, int, String, Object...)}.
         *
         * @return The start column of the line.
         */
        public int startColumn() {
            return startColumn;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    private static final class LicenseTextContext {
        private List<Line> licenseText;
        private int lineNumber;

        private LicenseTextContext(List<Line> licenseText, int lineNumber) {
            this.licenseText = licenseText;
            this.lineNumber = lineNumber;
        }

        private void nextLine() {
            licenseText = licenseText.subList(1, licenseText.size());
            lineNumber++;
        }
    }
}
