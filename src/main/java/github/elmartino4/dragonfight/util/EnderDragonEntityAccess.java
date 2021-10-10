package github.elmartino4.dragonfight.util;

import net.minecraft.entity.data.TrackedData;

public interface EnderDragonEntityAccess {
    <T> void putData(TrackedData<T> tag, Object data);

    Object getData(TrackedData tag);
}
