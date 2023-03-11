<head>
  <title>NoTrailingWhitespace</title>
</head>

## NoTrailingWhitespace

Since checkstyle-extension 1.0

### Description

Checks that files have no trailing whitespace.

### Examples

To configure the default check:

```
<module name="NoTrailingWhitespace"/>
```

Example:

```
class Valid {
// OK: no trailing whitespace
    int x = 0; // OK: no trailing whitespace
}

class Invalid {
    // Violation: Trailing whitespace.
}
```

### Violation Messages

* [whitespace.trailing](https://github.com/search?q=path%3Asrc%2Fmain%2Fresources%2Fcom%2Fgithub%2Frobtimus%2Fcheckstyle%2Fchecks+filename%3Amessages*.properties+repo%3Arobtimus%2Fcheckstyle-extension+%22whitespace.trailing%22)

All messages can be customized if the default message doesn't suit you. Please [see the documentation](https://checkstyle.org/config.html#Custom_messages) to learn how to.

### Package

com.github.robtimus.checkstyle.checks

### Parent Module

[Checker](https://checkstyle.org/config.html#Checker)
