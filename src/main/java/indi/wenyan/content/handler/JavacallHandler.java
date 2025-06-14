package indi.wenyan.content.handler;

import indi.wenyan.interpreter.runtime.WenyanRuntime;
import indi.wenyan.interpreter.runtime.WenyanThread;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanFunction;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public interface JavacallHandler extends WenyanFunction {
    WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException;

    /**
     * Decided if this handler is running at program thread.
     * <p>
     * the handler will be executed in the main thread of MC if it is not local,
     * This is important since the MC is not thread-safe,
     * and can cause strange bug and unmatched exception, making it really hard to debug.
     *
     * @return true if local, false otherwise
     */
    boolean isLocal();

    /**
     * The step of this handler.
     * <p>
     * It can be decided by the feature of the handler for game balance,
     * e.g. powerful handler may take more time to execute.
     * However, it's better to keep the handler time not longer than O(step),
     * which may cause the program to be stuck.
     *
     * @return the step of this handler
     */
    @SuppressWarnings("unused")
    default int getStep(int args, WenyanThread thread) {
        return 1;
    }

    static Object[] getArgs(WenyanNativeValue[] args, WenyanNativeValue.Type[] args_type) throws WenyanException.WenyanTypeException {
        Object[] newArgs = new Object[args.length];
        if (args.length != args_type.length)
            throw new WenyanException.WenyanTypeException(Component.translatable("error.wenyan_nature.number_of_arguments_does_not_match").getString());
        for (int i = 0; i < args.length; i++)
            newArgs[i] = args[i].casting(args_type[i]).getValue();
        return newArgs;
    }

    default void handle(WenyanThread thread, WenyanNativeValue[] args, boolean noReturn) throws WenyanException.WenyanThrowException {
        WenyanNativeValue value = handle(args);
        if (!noReturn)
            thread.currentRuntime().processStack.push(value);
    }

    @Override
    default void call(WenyanNativeValue.FunctionSign sign, WenyanNativeValue self,
                      WenyanThread thread, int args, boolean noReturn)
            throws WenyanException.WenyanThrowException{
        List<WenyanNativeValue> argsList = new ArrayList<>(args);
        if (self != null)
            argsList.add(self);
        WenyanRuntime runtime = thread.currentRuntime();
        for (int i = 0; i < args; i++)
            argsList.add(runtime.processStack.pop());

        if (isLocal()) {
            handle(thread, argsList.toArray(new WenyanNativeValue[0]), noReturn);
        } else {
            JavacallHandler.Request request = new JavacallHandler.Request(
                    thread, argsList.toArray(new WenyanNativeValue[0]), noReturn, this);
            thread.program.requestThreads.add(request);
            thread.block();
        }
    }

    @FunctionalInterface
    interface WenyanFunction {
        WenyanNativeValue apply(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException;
    }

    record Request(
            WenyanThread thread,
            WenyanNativeValue[] args,
            boolean noReturn,
            JavacallHandler handler
    ) {
        public void handle() throws WenyanException.WenyanThrowException {
            handler.handle(thread, args, noReturn);
            thread.program.readyQueue.add(thread);
            thread.state = WenyanThread.State.READY;
        }
    }
}
