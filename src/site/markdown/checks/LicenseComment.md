<head>
  <title>LicenseComment</title>
</head>

## LicenseComment

Since checkstyle-extension 1.0

### Description

Checks whether or not a Java source file has a valid leading license comment.

A leading license comment is valid if (in order):

* It contains the name of the file if and only if `includeFilename` is set to true.
* It contains a valid copyright notice if and only if `includeCopyright` is set to true.
  Setting `requiredCopyrightYear` and `requiredCopyrightHolder` imply this.
* There is an empty line before the remainder of the license text if and only if `includeEmptyLineBeforeLicenseText` is set.
* The remainder of the license text is valid according to what's been set with either `predefinedLicenseText` or `customLicenseText`.

### Properties

| name                              | description                                                                                              | type      | default value | since |
|-----------------------------------|----------------------------------------------------------------------------------------------------------|-----------|---------------|-------|
| includeFilename                   | True if the license comment must include the filename, or false if it's not allowed.                     | boolean   | false         | 1.0   |
| includeCopyright                  | True if the license comment must include a copyright, or false if it's not allowed.                      | boolean   | false         | 1.0   |
| requiredCopyrightYear             | The required copyright year, or `current`. If set to a non-blank value, will require a copyright notice. | string    | -             | 1.0   |
| requiredCopyrightHolder           | The required copyright holder. If set to a non-blank value, will require a copyright notice.             | string    | -             | 1.0   |
| includeEmptyLineBeforeLicenseText | True if the license comment must include an empty line, or false if it's not allowed.                    | boolean   | false         | 1.0   |
| predefinedLicenseText             | The expected predefined license.                                                                         | see below | -             | 1.0   |
| customLicenseText                 | The expected license text.                                                                               | string    | -             | 1.0   |

The license text must be defined using either `predefinedLicenseText` or `customLicenseText`. The available pre-defined license texts:

* [Apache-2.0](https://opensource.org/licenses/Apache-2.0): the Apache License 2.0
* [BSD-2-Clause](https://opensource.org/licenses/BSD-2-Clause): the 2-Clause BSD License / FreeBSD License / Simplified BSD License
* [EPL-1.0](https://opensource.org/licenses/EPL-1.0): the Eclipse Public License 1.0
* [EPL-2.0](https://opensource.org/licenses/EPL-2.0): the Eclipse Public License 2.0
* [GPL-2.0](https://opensource.org/licenses/GPL-2.0): the GNU General Public License version 2
* [GPL-3.0](https://opensource.org/licenses/GPL-3.0): the GNU General Public License version 3
* [LGPL-2.0](https://opensource.org/licenses/LGPL-2.0): the GNU Library General Public License version 2
* [LGPL-2.1](https://opensource.org/licenses/LGPL-2.1): the GNU Lesser General Public License version 2.1
* [MIT](https://opensource.org/licenses/MIT): the MIT License
* [MPL-2.0](https://opensource.org/licenses/MPL-2.0): the Mozilla Public License 2.0

### Examples

To require a simple MPL-2.0 license:

```
<module name="LicenseComment">
  <property name="predefinedLicenseText" value="MPL-2.0"/>
</module>
```

Example:

```
/**
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
class Valid {
}
```

To require a filename, copyright notice and empty line before the license text:

```
<module name="LicenseComment">
  <property name="includeFilename" value="true"/>
  <property name="includeCopyright" value="true"/>
  <property name="includeEmptyLineBeforeLicenseText" value="true"/>
  <property name="predefinedLicenseText" value="MPL-2.0"/>
</module>
```

Example:

```
/**
 * Valid.java
 * Copyright 2023 John Doe
 *
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
class Valid {
}

/**
 * OtherFile.java // Violation: Invalid filename in license. Expected 'Invalid.java'.
 * // Violation: Missing copyright in license.
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
class Invalid {
}
```

### Violation Messages

* [licenseComment.noLicense](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.noLicense%22)
* [licenseComment.disallowedFilename](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.disallowedFilename%22)
* [licenseComment.missingFilename](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.missingFilename%22)
* [licenseComment.invalidFilename](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.invalidFilename%22)
* [licenseComment.disallowedCopyright](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.disallowedCopyright%22)
* [licenseComment.missingCopyright](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.missingCopyright%22)
* [licenseComment.invalidCopyright](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.invalidCopyright%22)
* [licenseComment.invalidCopyrightYearRange](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.invalidCopyrightYearRange%22)
* [licenseComment.invalidCopyrightYear](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.invalidCopyrightYear%22)
* [licenseComment.invalidCopyrightHolder](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.invalidCopyrightHolder%22)
* [licenseComment.disallowedEmptyLine](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.disallowedEmptyLine%22)
* [licenseComment.missingEmptyLine](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.missingEmptyLine%22)
* [licenseComment.licenseTextMismatch](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22licenseComment.licenseTextMismatch%22)

All messages can be customized if the default message doesn't suit you. Please [see the documentation](https://checkstyle.org/config.html#Custom_messages) to learn how to.

### Package

com.github.robtimus.checkstyle.checks

### Parent Module

[Checker](https://checkstyle.org/config.html#Checker)
