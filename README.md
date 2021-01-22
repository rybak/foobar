
### Steps to reproduce
```
$ ./gradlew --no-daemon --stacktrace :component:bazHello
```

### Actual result

```
org.gradle.api.GradleScriptException: A problem occurred evaluating project ':component'.
...
Caused by: java.lang.ClassNotFoundException: BazTask
        at org.gradle.groovy.scripts.internal.DefaultScriptCompilationHandler$ScriptClassLoader.loadClass(DefaultScriptCompilationHandler.java:391)
        ... 141 more
```

### Expected result

No exception
