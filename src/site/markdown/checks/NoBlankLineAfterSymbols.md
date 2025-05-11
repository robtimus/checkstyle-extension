<head>
  <title>NoBlankLineAfterSymbols</title>
</head>

## NoBlankLineAfterSymbols

Since checkstyle-extension 1.0

### Description

Checks that files do not contain blank lines after lines that only consist of specific symbols. Trailing semicolons are ignored while checking.

### Properties

| name    | description               | type   | default value | since |
|---------|---------------------------|--------|---------------|-------|
| symbols | The symbols to check for. | string | ({[           | 1.0   |

### Examples

To configure the default check:

```xml
<module name="NoBlankLineAfterSymbols"/>
```

Example:

```java
class Invalid {
    {
// Violation: Blank line after a line containing only symbols from '({['.
    }
}
```

### Violation Messages

* [whitespace.blankLineAfterSymbols](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22whitespace.blankLineAfterSymbols%22)

All messages can be customized if the default message doesn't suit you. Please [see the documentation](https://checkstyle.org/config.html#Custom_messages) to learn how to.

### Package

com.github.robtimus.checkstyle.checks

### Parent Module

[Checker](https://checkstyle.org/config.html#Checker)
