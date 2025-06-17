package indi.wenyan.interpreter.structure;

import indi.wenyan.WenyanNature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;


/**
 * @author I_am_a_lolikong
 * @version 1.0
 * @className WenyanEntityObject
 * @Description TODO
 * @date 2025/6/17 14:18
 */
public class WenyanEntityObject<T extends Entity> implements WenyanObject {
    //任意继承实体的类
    private final T entity;

    public WenyanEntityObject(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public WenyanNativeValue getAttribute(String name) {
        return switch (name){
            case "「「名」」" -> {
                String entityName;
                if (entity.getCustomName()!=null){
                    entityName=entity.getCustomName().getString();
                }else {
                    entityName=entity.getDisplayName().getString();
                }
                yield new WenyanNativeValue(WenyanType.STRING,entityName,false);
            }
            case "「「真名」」" -> {
                EntityType<?> entityType = entity.getType();
                ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
                WenyanNature.LOGGER.info(entityId.toString());
                if (entity instanceof ItemEntity item)
                    yield new WenyanNativeValue(WenyanType.STRING,
                            String.valueOf(item.getItem().getItem()),
                            false);

                throw new WenyanException("Is Not ItemEntity");
            }
            default -> throw new WenyanException("Unknown Entity attribute: " + name);
        };
    }

    @Override
    public void setVariable(String name, WenyanNativeValue value) {
        throw new WenyanException("Cannot set variable on Entity object: " + name);
    }

    @Override
    public @Nullable WenyanObjectType getParent() {
        return null;
    }

    @Override
    public WenyanType type() {
        return WenyanType.ENTITY;
    }
}
