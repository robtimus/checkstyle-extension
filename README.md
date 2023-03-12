# checkstyle-extension
<!--[![Maven Central](https://img.shields.io/maven-central/v/com.github.robtimus/checkstyle-extension)](https://search.maven.org/artifact/com.github.robtimus/checkstyle-extension)-->
[![Build Status](https://github.com/robtimus/checkstyle-extension/actions/workflows/build.yml/badge.svg)](https://github.com/robtimus/checkstyle-extension/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Acheckstyle-extension&metric=alert_status)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Acheckstyle-extension)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.github.robtimus%3Acheckstyle-extension&metric=coverage)](https://sonarcloud.io/summary/overall?id=com.github.robtimus%3Acheckstyle-extension)
[![Known Vulnerabilities](https://snyk.io/test/github/robtimus/checkstyle-extension/badge.svg)](https://snyk.io/test/github/robtimus/checkstyle-extension)

## Additional Checkstyle checks

| Check                                                                                                            | Description                                                                           |
|------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| [LicenseComment](https://robtimus.github.io/checkstyle-extension/checks/LicenseComment.html)                     | Checks that Java source files start with a comment with a properly formatted license. |
| [NoBlankLineAfterSymbols](https://robtimus.github.io/checkstyle-extension/checks/NoBlankLineAfterSymbols.html)   | Checks that lines containing only specific symbols are not followed by a blank line.  |
| [NoBlankLineBeforeSymbols](https://robtimus.github.io/checkstyle-extension/checks/NoBlankLineBeforeSymbols.html) | Checks that lines containing only specific symbols are not preceded by a blank line.  |
| [NoSubsequentBlankLines](https://robtimus.github.io/checkstyle-extension/checks/NoSubsequentBlankLines.html)     | Checks that there are no occurrences of two or more blank lines in a row.             |
| [NoTrailingWhitespace](https://robtimus.github.io/checkstyle-extension/checks/NoTrailingWhitespace.html)         | Checks that lines have no trailing whitespace.                                        |

## Maven integration

Add a dependency to your existing `maven-checkstyle-plugin` definition. For instance:

```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>...</version>
  <configuration>
    ...
  </configuration>
  <dependencies>
    <dependency>
      <groupId>com.puppycrawl.tools</groupId>
      <artifactId>checkstyle</artifactId>
      <version>...</version>
    </dependency>
    <dependency>
      <groupId>com.github.robtimus</groupId>
      <artifactId>checkstyle-extension</artifactId>
      <version>...</version>
    </dependency>
  </dependencies>
  <executions>
    ...
  </executions>
</plugin>
```

## Eclipse integration

Download the latest JAR file from Maven Central or the [GitHub release page](https://github.com/robtimus/checkstyle-extension/releases), copy it to Eclipse's `dropins` folder, and restart Eclipse.

## IntelliJ integration

First, make sure that you have the [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) plugin installed. Next, download the latest JAR file from Maven Central or the [GitHub release page](https://github.com/robtimus/checkstyle-extension/releases). Finally, configure IntelliJ to include it as part of its third-party checks:

* Open `Settings...`
* Open the Checkstyle settings
* Add the JAR under the `Third-Party Checks` section
