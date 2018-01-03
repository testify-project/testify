# Developer Notes

## Troubleshooting Ambiguity Issues
- There have been issues in the past with ambiguity resolution not working. To debug and report these issues to ByteBuddy team start the debugging process by looking at
 - `MethodDelegationBinder$Processor#bind`
 - `MethodDelegationBinder$Default#doResolve` 
- Make sure you interceptor method signatures are correct:
 - Methods that don't take parameters cant have a parameter annotated with `@AllArguments`
 - Static methods can't have a parameter annotated with `@This` unless its optional attribute is set
 - Catch all methods must have different parameter lengths than any other declared method.
- Catch-method should look like this:
```java
@RuntimeType
@BindingPriority(Integer.MAX_VALUE)
public Object anyMethod(@SuperCall Callable<?> zuper, @This(optional = true) Object object)
        throws Exception {
    return zuper.call();
}
```
