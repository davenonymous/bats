package org.dave.bats.util.autoreg;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dave.bats.base.BaseWorldSavedData;
import org.dave.bats.util.Logz;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BaseWorldSavedDataRegistry {
    private static Map<Integer, Map<String, Class<? extends BaseWorldSavedData>>> knownWorldSaves = new HashMap<>();
    private static Map<Integer, Map<Class<? extends BaseWorldSavedData>, BaseWorldSavedData>> worldSavedData = new HashMap<>();

    @Nullable
    public static <T> T getWorldSaveData(int dimension, Class<T> registry) {
        if(!worldSavedData.containsKey(dimension)) {
            return null;
        }

        if(!worldSavedData.get(dimension).containsKey(registry)) {
            return null;
        }

        return (T) worldSavedData.get(dimension).get(registry);
    }

    @SubscribeEvent
    public static void loadWorld(WorldEvent.Load event) {
        if(event.getWorld().isRemote) {
            return;
        }



        int dimensionId = event.getWorld().provider.getDimension();
        if(!knownWorldSaves.containsKey(dimensionId)) {
            Logz.info("No know world save for dimension: %d", dimensionId);
            return;
        }

        for(Map.Entry<String, Class<? extends BaseWorldSavedData>> entry : knownWorldSaves.get(dimensionId).entrySet()) {
            BaseWorldSavedData saveData = (BaseWorldSavedData) event.getWorld().getMapStorage().getOrLoadData(entry.getValue(), entry.getKey());
            if(saveData == null) {
                saveData = getWorldSaveInstance(entry.getValue(), entry.getKey());
                saveData.markDirty();
                event.getWorld().getMapStorage().setData(entry.getKey(), saveData);
            }
            saveData.afterLoad();

            if(!worldSavedData.containsKey(dimensionId)) {
                worldSavedData.put(dimensionId, new HashMap<>());
            }

            worldSavedData.get(dimensionId).put(entry.getValue(), saveData);
        }
    }

    @Nullable
    private static BaseWorldSavedData getWorldSaveInstance(Class clz, String id) {
        try {
            Constructor<BaseWorldSavedData> constructor = clz.getConstructor(String.class);
            return constructor.newInstance(id);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void init() {
        for(Map.Entry<Class<? extends BaseWorldSavedData>, Map<String, Object>> worldSaveData : AnnotationLoader.getWorldSavedDataBases().entrySet()) {
            Class clz = worldSaveData.getKey();
            Map<String, Object> annotation = worldSaveData.getValue();

            String id = (String) annotation.get("id");
            BaseWorldSavedData dataInstance = getWorldSaveInstance(clz, id);
            if(dataInstance == null) {
                continue;
            }

            int dimensionId = dataInstance.getDimension();

            if(!knownWorldSaves.containsKey(dimensionId)) {
                knownWorldSaves.put(dimensionId, new HashMap<>());
            }

            if(knownWorldSaves.get(dimensionId).containsKey(id)) {
                Logz.warn("Skipping duplicate world save data '%s' for dimension=%d [class=%s].", id, dimensionId, clz.getName());
                continue;
            }

            Logz.info("Registering world saved data for dimension=%d. Id=%s, clazz=%s", dimensionId, id, clz.getName());
            knownWorldSaves.get(dimensionId).put(id, clz);
        }
    }
}
