package indi.wenyan.content.handler;

import indi.wenyan.content.block.BlockRunner;
import indi.wenyan.interpreter.structure.WenyanException;
import indi.wenyan.interpreter.structure.WenyanNativeValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CommunicateHandler implements JavacallHandler {
    private final BlockPos pos;
    private final BlockState state;
    private final Level level;

    public static final WenyanNativeValue.Type[] ARG_TYPES =
            {WenyanNativeValue.Type.INT, WenyanNativeValue.Type.INT, WenyanNativeValue.Type.INT};

    public CommunicateHandler(BlockPos pos, BlockState state, Level level) {
        this.pos = pos;
        this.state = state;
        this.level = level;
    }

    @Override
    public WenyanNativeValue handle(WenyanNativeValue[] wenyanValues) throws WenyanException.WenyanThrowException {
        Object[] args = JavacallHandler.getArgs(wenyanValues, ARG_TYPES);
        args[0] = Math.max(-10, Math.min(10, (int) args[0]));
        args[1] = Math.max(-10, Math.min(10, (int) args[1]));
        args[2] = Math.max(-10, Math.min(10, (int) args[2]));
            BlockRunner blockRunner = (BlockRunner) level.getBlockEntity(pos);
            assert blockRunner != null;
            blockRunner.communicate = new Vec3((int) args[0], (int) args[1], (int) args[2]);
            blockRunner.isCommunicating = true;
            level.sendBlockUpdated(pos, state, state, 3);
        return WenyanNativeValue.NULL;
    }
    @Override
    public boolean isLocal() {
        return false;
    }
}
