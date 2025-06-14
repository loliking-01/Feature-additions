package indi.wenyan.interpreter.utils;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.content.handler.LocalCallHandler;
import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanObjectType;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class WenyanPackageBuilder {
    private final WenyanRuntime environment = new WenyanRuntime(null);

    public static WenyanPackageBuilder create() {
        return new WenyanPackageBuilder();
    }

    public WenyanPackageBuilder environment(WenyanRuntime environment) {
        this.environment.importEnvironment(environment);
        return this;
    }

    public WenyanRuntime build() {
        return environment;
    }

    public WenyanPackageBuilder constant(String name, WenyanNativeValue.Type type, Object value) {
        environment.setVariable(name, new WenyanNativeValue(type, value, true));
        return this;
    }

    public WenyanPackageBuilder constant(String name, WenyanNativeValue value) {
        environment.setVariable(name, value);
        return this;
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function,
                                         WenyanNativeValue.Type returnType, WenyanNativeValue.Type[] argTypes) {
        return function(name, args -> {
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < argTypes.length; i++)
                newArgs[i] = args[i].casting(argTypes[i]).getValue();
            return new WenyanNativeValue(returnType, function.apply(newArgs), true);
        }, new WenyanNativeValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, Function<Object[], Object> function, WenyanNativeValue.Type valueType) {
        return function(name, args -> {
            Object[] newArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++)
                newArgs[i] = args[i].casting(valueType).getValue();
            return new WenyanNativeValue(valueType, function.apply(newArgs), true);
        }, new WenyanNativeValue.Type[0]);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function) {
        return function(name, function, new WenyanNativeValue.Type[0]);
    }

    public WenyanPackageBuilder function(String[] name, JavacallHandler.WenyanFunction function) {
        for (String n : name) {
            function(n, function);
        }
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler.WenyanFunction function, WenyanNativeValue.Type[] argTypes) {
        return function(name, new LocalCallHandler(function), argTypes);
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall) {
        return function(name, javacall, new WenyanNativeValue.Type[0]);
    }

    public WenyanPackageBuilder function(String[] name, JavacallHandler javacall) {
        for (String n : name) {
            function(n, javacall);
        }
        return this;
    }

    public WenyanPackageBuilder function(String name, JavacallHandler javacall, WenyanNativeValue.Type[] argTypes) {
        WenyanNativeValue.FunctionSign sign = new WenyanNativeValue.FunctionSign(name, argTypes, javacall);
        environment.setVariable(name, new WenyanNativeValue(WenyanNativeValue.Type.FUNCTION, sign, true));
        return this;
    }

    public WenyanPackageBuilder object(WenyanObjectType objectType) {
        environment.setVariable(objectType.getName(), new WenyanNativeValue(WenyanNativeValue.Type.OBJECT_TYPE, objectType, true));
        return this;
    }

    public static JavacallHandler.WenyanFunction reduceWith(ReduceFunction function) {
        return args -> {
            if (args.length <= 1)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            WenyanNativeValue value = args[0];
            for (int i = 1; i < args.length; i++) {
                value = function.apply(value, args[i]);
            }
            return value;
        };
    }

    public static JavacallHandler.WenyanFunction boolBinaryOperation(java.util.function.BiFunction<Boolean, Boolean, Boolean> function) {
        return args -> {
            if (args.length != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            return new WenyanNativeValue(WenyanNativeValue.Type.BOOL,
                    function.apply((boolean) args[0].casting(WenyanNativeValue.Type.BOOL).getValue(),
                            (boolean) args[1].casting(WenyanNativeValue.Type.BOOL).getValue()),
                    true);
        };
    }

    public static JavacallHandler.WenyanFunction compareOperation(CompareFunction function) {
        return args -> {
            if (args.length != 2)
                throw new WenyanException.WenyanVarException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
            return new WenyanNativeValue(WenyanNativeValue.Type.BOOL,
                    function.apply(args[0], args[1]), true);
        };
    }

    @FunctionalInterface
    public interface ReduceFunction {
        WenyanNativeValue apply(WenyanNativeValue a, WenyanNativeValue b) throws WenyanException.WenyanThrowException;
    }

    @FunctionalInterface
    public interface CompareFunction {
        boolean apply(WenyanNativeValue a, WenyanNativeValue b) throws WenyanException.WenyanThrowException;
    }
}
