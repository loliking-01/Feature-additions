package indi.wenyan.content.handler.feature_additions.string_util_handler;

import indi.wenyan.content.handler.JavacallHandler;
import indi.wenyan.interpreter.structure.JavacallContext;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import indi.wenyan.interpreter.structure.WenyanType;
import indi.wenyan.interpreter.utils.JavacallHandlers;

import java.util.List;

/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className StringUtilSubStringStartAndEndHandler
 * @Description TODO
 * @date 2025/6/15 0:20
 */
public class StringUtilSubStringStartAndEndHandler implements JavacallHandler {
    public static final WenyanType[] ARGS_TYPE =
            {WenyanType.STRING, WenyanType.INT, WenyanType.INT};

    @Override
    public WenyanNativeValue handle(JavacallContext context) throws WenyanException.WenyanThrowException {
        List<Object> args = JavacallHandlers.getArgs(context.args(), ARGS_TYPE);
        String original= (String) args.get(0);
        int start=Integer.parseInt((String) args.get(1));
        int end=Integer.parseInt((String) args.get(2));
        if (start>original.length()){
            throw new WenyanException("謬：始数不可超文长");
        }else if (start<0){
            throw new WenyanException("謬：始数不可为负数");
        }

        return new WenyanNativeValue(WenyanType.STRING,original.substring(start,end),false);
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
