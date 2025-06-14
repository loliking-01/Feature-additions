package indi.wenyan.interpreter.runtime.executor;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.*;

public class ObjectCode extends WenyanCode {
    private final Operation operation;

    public ObjectCode(Operation operation) {
        super(name(operation));
        this.operation = operation;
    }

    @Override
    public void exec(int args, WenyanThread thread) {
        WenyanRuntime runtime = thread.currentRuntime();
        try {
            String id = runtime.bytecode.getIdentifier(args);
            switch (operation) {
                case ATTR -> {
                    WenyanNativeValue attr;
                    WenyanNativeValue value = runtime.processStack.pop();
                    if (value.getType() == WenyanNativeValue.Type.OBJECT_TYPE) { // static
                        WenyanObjectType type = (WenyanObjectType) value
                                .casting(WenyanNativeValue.Type.OBJECT_TYPE).getValue();
                        attr = type.getStaticVariable(id);
                        if (attr == null) {
                            attr = type.getFunction(id);
                        }
                    } else { // object or other(casting to object)
                        WenyanObject object = (WenyanObject) value
                                .casting(WenyanNativeValue.Type.OBJECT).getValue();
                        attr = object.getVariable(id);
                        if (attr == null) {
                            attr = object.getFunction(id);
                        }
                    }
                    runtime.processStack.push(attr);
                }
                case ATTR_REMAIN -> {
                    WenyanNativeValue attr;
                    WenyanNativeValue value = runtime.processStack.peek();
                    // copy from ATTR
                    if (value.getType() == WenyanNativeValue.Type.OBJECT_TYPE) { // static
                        WenyanObjectType type = (WenyanObjectType) value
                                .casting(WenyanNativeValue.Type.OBJECT_TYPE).getValue();
                        attr = type.getStaticVariable(id);
                        if (attr == null) {
                            attr = type.getFunction(id);
                        }
                    } else { // object or other(casting to object)
                        WenyanObject object = (WenyanObject) value
                                .casting(WenyanNativeValue.Type.OBJECT).getValue();
                        attr = object.getVariable(id);
                        if (attr == null) {
                            attr = object.getFunction(id);
                        }
                    }
                    runtime.processStack.push(attr);
                }
                case STORE_STATIC_ATTR -> {
                    WenyanNativeValue value = WenyanNativeValue.varOf(runtime.processStack.pop());
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek()
                            .casting(WenyanNativeValue.Type.OBJECT_TYPE).getValue();
                    type.addStaticVariable(id, value);
                }
                case STORE_FUNCTION_ATTR -> {
                    WenyanNativeValue value = runtime.processStack.pop();
                    WenyanObjectType type = (WenyanObjectType) runtime.processStack.peek()
                            .casting(WenyanNativeValue.Type.OBJECT_TYPE).getValue();
                    type.addFunction(id, value);
                }
                case STORE_ATTR -> {
                    // currently only used at define (mzy SELF ZHI STRING)
                    WenyanObject self = (WenyanObject) runtime.processStack.pop()
                            .casting(WenyanNativeValue.Type.OBJECT).getValue();
                    WenyanNativeValue value = WenyanNativeValue.varOf(runtime.processStack.pop());
                    self.setVariable(id, value);
                }
                case CREATE_TYPE -> {
                    WenyanNativeValue type = new WenyanNativeValue(WenyanNativeValue.Type.OBJECT_TYPE,
                            new WenyanDictObjectType((WenyanObjectType) runtime.processStack.pop()
                                    .casting(WenyanNativeValue.Type.OBJECT_TYPE).getValue(), id), true);
                    runtime.processStack.push(type);
                }
            }
        } catch (WenyanException.WenyanTypeException e) {
            throw new WenyanException(e.getMessage());
        }
    }

    private static String name(Operation op) {
        return switch (op) {
            case ATTR -> "LOAD_ATTR";
            case ATTR_REMAIN -> "LOAD_ATTR_REMAIN";
            case STORE_STATIC_ATTR -> "STORE_STATIC_ATTR";
            case STORE_FUNCTION_ATTR -> "STORE_FUNCTION_ATTR";
            case STORE_ATTR -> "STORE_ATTR";
            case CREATE_TYPE -> "CREATE_TYPE";
        };
    }

    public enum Operation {
        ATTR,
        ATTR_REMAIN,
        STORE_ATTR,
        STORE_STATIC_ATTR,
        STORE_FUNCTION_ATTR,
        CREATE_TYPE
    }
}
