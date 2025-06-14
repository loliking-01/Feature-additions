package indi.wenyan.content.handler;

import indi.wenyan.content.entity.HandRunnerEntity;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.world.phys.Vec3;

public class MoveHandler implements JavacallHandler {
    public final HandRunnerEntity entity;
    public static final WenyanNativeValue.Type[] ARGS_TYPE =
            {WenyanNativeValue.Type.DOUBLE, WenyanNativeValue.Type.DOUBLE, WenyanNativeValue.Type.DOUBLE};

    public MoveHandler(HandRunnerEntity entity) {
        super();
        this.entity = entity;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] args) throws WenyanException.WenyanThrowException {
        Object[] newArgs = JavacallHandler.getArgs(args, ARGS_TYPE);
        newArgs[0] = Math.max(-20, Math.min(20, (double) newArgs[0]));
        newArgs[1] = Math.max(-20, Math.min(20, (double) newArgs[1]));
        newArgs[2] = Math.max(-20, Math.min(20, (double) newArgs[2]));
        entity.setDeltaMovement(new Vec3((double) newArgs[0]/10, (double) newArgs[1]/10, (double) newArgs[2]/10));
        return WenyanNativeValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
