package org.dave.bats.util.autoreg;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.bats.base.BaseWorldSavedData;
import org.dave.bats.util.Logz;

import java.util.*;

/**
 * This code is in large parts copied from the JustEnoughItems mod, which is MIT licensed.
 * You can find the original implementation here:
 * https://github.com/mezz/JustEnoughItems/blob/68242be874/src/main/java/mezz/jei/startup/AnnotatedInstanceUtil.java
 *
 * Thanks to Mezz for originally implementing this.
 *
 * When the time comes for 1.13, Mezz also already ported this. Find an updated version here:
 * https://github.com/mezz/JustEnoughItems/blob/582eb87925032083f54e0903d0afefeeae238dd5/src/main/java/mezz/jei/util/AnnotatedInstanceUtil.java
 */

public class AnnotationLoader {
    public static ASMDataTable asmDataTable;

    public static Map<Class<? extends BaseWorldSavedData>, Map<String, Object>> getWorldSavedDataBases() {
        return getClassesWithAnnotations(WorldSavedData.class, BaseWorldSavedData.class);
    }

    private static <T> Map<Class<? extends T>, Map<String, Object>> getClassesWithAnnotations(Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        Map<Class<? extends T>, Map<String, Object>> classes = new HashMap<>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> annotationInfo = asmData.getAnnotationInfo();
                if(annotationInfo.containsKey("mod")) {
                    String requiredMod = (String)annotationInfo.get("mod");
                    if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                classes.put(asmInstanceClass, annotationInfo);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }

        return classes;

    }

    private static <T> List<Class<? extends T>> getClasses(Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        List<Class<? extends T>> classes = new ArrayList<>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> annotationInfo = asmData.getAnnotationInfo();
                if(annotationInfo.containsKey("mod")) {
                    String requiredMod = (String)annotationInfo.get("mod");
                    if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                classes.add(asmInstanceClass);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }

        return classes;
    }

    private static <T> Map<T, Map<String, Object>> getInstancesWithAnnotations(Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        Map<T, Map<String,Object>> instances = new HashMap<>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> annotationInfo = asmData.getAnnotationInfo();
                if(annotationInfo.containsKey("mod")) {
                    String requiredMod = (String)annotationInfo.get("mod");
                    if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();

                instances.put(instance, annotationInfo);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (IllegalAccessException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (InstantiationException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }
        return instances;
    }

    private static <T> List<T> getInstances(Class annotationClass, Class<T> instanceClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        List<T> instances = new ArrayList<T>();
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Map<String, Object> annotationInfo = asmData.getAnnotationInfo();
                if(annotationInfo.containsKey("mod")) {
                    String requiredMod = (String)annotationInfo.get("mod");
                    if(requiredMod.length() > 0 && !Loader.isModLoaded(requiredMod)) {
                        continue;
                    }
                }

                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch (ClassNotFoundException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (IllegalAccessException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (InstantiationException e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            } catch (ExceptionInInitializerError e) {
                Logz.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }
        return instances;
    }
}
