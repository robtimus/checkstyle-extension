<head>
  <title>NoBlankLineBeforeSymbols</title>
</head>

## NoBlankLineBeforeSymbols

Since checkstyle-extension 1.0

### Description

Checks that files do not contain blank lines before lines that only consist of specific symbols. Trailing semicolons are ignored while checking, except if semicolons are part of the symbols to check for.

### Properties

| name    | description               | type   | default value | since |
|---------|---------------------------|--------|---------------|-------|
| symbols | The symbols to check for. | string | )}];          | 1.0   |

### Examples

To configure the default check:

```
<module name="NoBlankLineBeforeSymbols"/>
```

Example:

```
class Invalid {
// Violation: Blank line before a line containing only symbols from ')}];'.
}

enum InvalidEnum {
    CONSTANT1,
    CONSTANT2,
// Violation: Blank line before a line containing only symbols from ')}];'.
    ;
}
```

### Violation Messages

* [whitespace.blankLineBeforeSymbols](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22whitespace.blankLineBeforeSymbols%22)

All messages can be customized if the default message doesn't suit you. Please [see the documentation](https://checkstyle.org/config.html#Custom_messages) to learn how to.

### Package

com.github.robtimus.checkstyle.checks

### Parent Module

[Checker](https://checkstyle.org/config.html#Checker)
