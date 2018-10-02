package info.ljungqvist.sun

inline fun <T> T.buildOnIf(predicate: (T) -> Boolean, builder: T.() -> T): T =
        takeUnless(predicate) ?: builder()
