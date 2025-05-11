<head>
  <title>NoSubsequentBlankLines</title>
</head>

## NoSubsequentBlankLines

Since checkstyle-extension 1.0

### Description

Checks that files do not have multiple blank lines in a row.

### Examples

To configure the default check:

```xml
<module name="NoSubsequentBlankLines"/>
```

Example:

```java
class Valid {
// OK: single blank line
}

class Invalid {

// Violation: Two or more blank lines in a row.
}
```

### Violation Messages

* [whitespace.multipleBlankLinesInARow](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22whitespace.multipleBlankLinesInARow%22)

All messages can be customized if the default message doesn't suit you. Please [see the documentation](https://checkstyle.org/config.html#Custom_messages) to learn how to.

### Package

com.github.robtimus.checkstyle.checks

### Parent Module

[Checker](https://checkstyle.org/config.html#Checker)
