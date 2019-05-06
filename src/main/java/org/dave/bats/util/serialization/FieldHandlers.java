package org.dave.bats.util.serialization;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dave.bats.util.Logz;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class FieldHandlers {
    private static final Map<Class<?>, Pair<Reader, Writer>> handlers = new HashMap<>();
    private static final Map<Class<?>, Pair<NbtReader, NbtWriter>> nbtHandlers = new HashMap<>();

    static {
        addIOHandler(byte.class, buf -> buf.readByte(), (val, buf) -> buf.writeByte(val));
        addIOHandler(int.class, buf -> buf.readInt(), (val, buf) -> buf.writeInt(val));
        addIOHandler(float.class, buf -> buf.readFloat(), (val, buf) -> buf.writeFloat(val));

        addIOHandler(String.class, buf -> ByteBufUtils.readUTF8String(buf), (val, buf) -> ByteBufUtils.writeUTF8String(buf, val));

        addNBTHandler(boolean.class, (key, tag) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.setBoolean(key, aBoolean));
        addNBTHandler(Boolean.class, (key, tag) -> tag.getBoolean(key), (key, aBoolean, tag) -> tag.setBoolean(key, aBoolean));

        addNBTHandler(int.class, (key, tag) -> tag.getInteger(key), (key, val, tag) -> tag.setInteger(key, val));
        addNBTHandler(Integer.class, (key, tag) -> tag.getInteger(key), (key, integer, tag) -> tag.setInteger(key, integer));

        addNBTHandler(float.class, (key, tag) -> tag.getFloat(key), (key, val, tag) -> tag.setFloat(key, val));
        addNBTHandler(Float.class, (key, tag) -> tag.getFloat(key), (key, val, tag) -> tag.setFloat(key, val));

        addNBTHandler(double.class, (key, tag) -> tag.getDouble(key), (key, val, tag) -> tag.setDouble(key, val));
        addNBTHandler(Double.class, (key, tag) -> tag.getDouble(key), (key, val, tag) -> tag.setDouble(key, val));

        addNBTHandler(long.class, (key, tag) -> tag.getLong(key), (key, val, tag) -> tag.setLong(key, val));
        addNBTHandler(Long.class, (key, tag) -> tag.getLong(key), (key, val, tag) -> tag.setLong(key, val));

        // This is actually covered by INBTSerializable, but our class/interface iteration method is too strict about this.
        addNBTHandler(ItemStack.class, (key, tag) -> new ItemStack(tag.getCompoundTag(key)), (key, itemStack, tag) -> tag.setTag(key, itemStack.serializeNBT()));

        addNBTHandler(Enum.class, ((key, tag) -> {
            NBTTagCompound enumTag = tag.getCompoundTag(key);
            try {
                Class clz = Class.forName(enumTag.getString("class"));
                return Enum.valueOf(clz, enumTag.getString("value"));
            } catch (ClassNotFoundException e) {
                Logz.warn("Could not find enum '%s' during NBT deserialization", tag.getString(key));
                e.printStackTrace();
            }
            return null;
        }), (key, anEnum, tag) -> {
            NBTTagCompound result = new NBTTagCompound();
            result.setString("class", anEnum.getClass().getName());
            result.setString("value", anEnum.name());

            tag.setTag(key, result);
        });

        addNBTHandler(Class.class, (key, tag) -> {
            if(key.equals("") || !tag.hasKey(key)) {
                return null;
            }

            try {
                return Class.forName(tag.getString(key));
            } catch (ClassNotFoundException e) {
                Logz.warn("Could not find class '%s' during NBT deserialization", tag.getString(key));
                e.printStackTrace();
            }
            return null;
        }, (key, aClass, tag) -> {
            if(aClass != null) {
                tag.setString(key, aClass.getName());
            }
        });

        addNBTHandler(ResourceLocation.class, (key, tag) -> {
            if(!tag.hasKey(key)) {
                return null;
            }

            return new ResourceLocation(tag.getString(key));
        }, (key, resourceLocation, tag) -> {
            if(resourceLocation == null) {
                return;
            }

            tag.setString(key, resourceLocation.toString());
        });

        addNBTHandler(BlockPos.class, (key, tag) -> {
            NBTTagCompound container = tag.getCompoundTag(key);
            return new BlockPos(container.getInteger("x"), container.getInteger("y"), container.getInteger("z"));
        }, (key, pos, tag) -> {
            NBTTagCompound container = new NBTTagCompound();
            container.setInteger("x", pos.getX());
            container.setInteger("y", pos.getY());
            container.setInteger("z", pos.getZ());
            tag.setTag(key, container);
        });

        addNBTHandler(String.class, (key, tag) -> tag.hasKey(key) ? tag.getString(key) : null, (key, s, tag) -> {
            if(s != null) {
                tag.setString(key, s);
            }
        });
        addNBTHandler(UUID.class, (key, tag) -> {
            if(!tag.hasKey(key)) {
                return null;
            }

            NBTTagCompound containerTag = tag.getCompoundTag(key);
            return containerTag.getUniqueId("");
        }, (key, uuid, tag) -> {
            if(uuid == null) {
                return;
            }

            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag.setUniqueId("", uuid);
            tag.setTag(key, containerTag);
        });

        addNBTHandler(INBTSerializable.class, (key, tag) -> {
            NBTTagCompound containerTag = tag.getCompoundTag(key);
            String className = containerTag.getString("class");
            try {
                Class clz = Class.forName(className);
                INBTSerializable obj = (INBTSerializable)clz.getConstructor().newInstance();
                obj.deserializeNBT(containerTag.getCompoundTag("data"));
                return obj;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            return null;
        }, (key, INBTSerializable, tag) -> {
            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag.setString("class", INBTSerializable.getClass().getName());
            containerTag.setTag("data", INBTSerializable.serializeNBT());
            tag.setTag(key, containerTag);
        });

        addNBTHandler(Map.class, (key, tag) -> {
            NBTTagCompound containerTag = tag.getCompoundTag(key);
            if(!containerTag.hasKey("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.hasKey("entries")) {
                return new HashMap();
            }

            Map result = new HashMap();
            try {
                Class keyClass = Class.forName(containerTag.getString("keyClass"));
                if (!hasNBTHandler(keyClass)) {
                    Logz.warn("No NBT deserialization methods for keys in map (type='%s') exists.", keyClass);
                    return new HashMap();
                }

                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if (!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in map (type='%s') exists.", valueClass);
                    return new HashMap();
                }

                NbtReader keyReader = getNBTHandler(keyClass).getLeft();
                NbtReader valueReader = getNBTHandler(valueClass).getLeft();

                for(NBTBase baseTag : containerTag.getTagList("entries", Constants.NBT.TAG_COMPOUND)) {
                    NBTTagCompound entry = (NBTTagCompound) baseTag;
                    Object keyObject = keyReader.read("key", entry);
                    Object valueObject = valueReader.read("value", entry);

                    result.put(keyObject, valueObject);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, map, tag) -> {
            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag.setBoolean("isEmpty", map.isEmpty());

            if(!map.isEmpty()) {
                Class keyClass = map.keySet().toArray()[0].getClass();
                if(!hasNBTHandler(keyClass)) {
                    Logz.warn("No NBT deserialization methods for keys in map (type='%s') exists.", keyClass);
                    return;
                }

                Class valueClass = map.values().toArray()[0].getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in map (type='%s') exists.", valueClass);
                    return;
                }

                containerTag.setString("keyClass", keyClass.getName());
                containerTag.setString("valueClass", valueClass.getName());

                NbtWriter keyWriter = getNBTHandler(keyClass).getRight();
                NbtWriter valueWriter = getNBTHandler(valueClass).getRight();

                NBTTagList data = new NBTTagList();
                for(Object e : map.entrySet()) {
                    NBTTagCompound entryTag = new NBTTagCompound();
                    Map.Entry entry = (Map.Entry) e;

                    keyWriter.write("key", entry.getKey(), entryTag);
                    valueWriter.write("value", entry.getValue(), entryTag);

                    data.appendTag(entryTag);
                }

                containerTag.setTag("entries", data);
            }

            tag.setTag(key, containerTag);
        });

        addNBTHandler(List.class, (key, tag) -> {
            List result = new ArrayList();
            NBTTagCompound containerTag = tag.getCompoundTag(key);
            if(!containerTag.hasKey("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.hasKey("values")) {
                return result;
            }

            try {
                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in list (type='%s') exists.", valueClass);
                    return result;
                }

                NbtReader reader = getNBTHandler(valueClass).getLeft();
                for(NBTBase baseTag : containerTag.getTagList("values", Constants.NBT.TAG_COMPOUND)) {
                    NBTTagCompound entry = (NBTTagCompound)baseTag;
                    Object value = reader.read("data", entry);
                    result.add(value);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, list, tag) -> {
            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag.setBoolean("isEmpty", list.isEmpty());

            if(!list.isEmpty()) {
                Class valueClass = list.get(0).getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT serialization methods for values in list (type='%s') exists.", valueClass.getName());
                    return;
                }

                containerTag.setString("valueClass", valueClass.getName());

                NbtWriter writer = getNBTHandler(valueClass).getRight();
                NBTTagList data = new NBTTagList();
                for(Object e : list) {
                    NBTTagCompound entryContainerTag = new NBTTagCompound();
                    writer.write("data", e, entryContainerTag);
                    data.appendTag(entryContainerTag);
                }
                containerTag.setTag("values", data);
            }

            tag.setTag(key, containerTag);
        });

        addNBTHandler(Queue.class, (key, tag) -> {
            NBTTagCompound containerTag = tag.getCompoundTag(key);
            if(!containerTag.hasKey("isEmpty") || containerTag.getBoolean("isEmpty") || !containerTag.hasKey("values")) {
                return new ArrayDeque<>();
            }

            Queue result = new ArrayDeque<>();
            try {

                Class valueClass = Class.forName(containerTag.getString("valueClass"));
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT deserialization methods for values in queue (type='%s') exists.", valueClass);
                    return new ArrayDeque<>();
                }

                NbtReader reader = getNBTHandler(valueClass).getLeft();
                for(NBTBase baseTag : containerTag.getTagList("values", Constants.NBT.TAG_COMPOUND)) {
                    NBTTagCompound entry = (NBTTagCompound)baseTag;
                    Object value = reader.read("data", entry);
                    result.add(value);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return result;
        }, (key, queue, tag) -> {
            NBTTagCompound containerTag = new NBTTagCompound();
            containerTag.setBoolean("isEmpty", queue.isEmpty());

            if(!queue.isEmpty()) {
                Class valueClass = queue.peek().getClass();
                if(!hasNBTHandler(valueClass)) {
                    Logz.warn("No NBT serialization methods for values in list (type='%s') exists.", valueClass.getName());
                    return;
                }

                containerTag.setString("valueClass", valueClass.getName());

                NbtWriter writer = getNBTHandler(valueClass).getRight();
                NBTTagList data = new NBTTagList();
                for(Object e : queue) {
                    NBTTagCompound entryContainerTag = new NBTTagCompound();
                    writer.write("data", e, entryContainerTag);
                    data.appendTag(entryContainerTag);
                }
                containerTag.setTag("values", data);
            }

            tag.setTag(key, containerTag);
        });

    }


    public static <T extends Object> void addIOHandler (Class<T> type, Reader<T> reader, Writer<T> writer) {
        handlers.put(type, Pair.of(reader, writer));
    }

    public static boolean hasIOHandler(Class clz) {
        return handlers.containsKey(clz);
    }

    public static Pair<Reader, Writer> getIOHandler(Class clz) {
        return handlers.get(clz);
    }


    public static <T extends Object> void addNBTHandler (Class<T> type, NbtReader<T> reader, NbtWriter<T> writer) {
        nbtHandlers.put(type, Pair.of(reader, writer));
    }

    public static boolean hasNBTHandler(Class clz) {
        if(nbtHandlers.containsKey(clz)) {
            return true;
        }

        for(Class iface : clz.getInterfaces()) {
            if(nbtHandlers.containsKey(iface)) {
                return true;
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return false;
        }

        return hasNBTHandler(superClass);
    }

    public static Pair<NbtReader, NbtWriter> getNBTHandler(Class clz) {
        if(nbtHandlers.containsKey(clz)) {
            return nbtHandlers.get(clz);
        }

        for(Class iface : clz.getInterfaces()) {
            if(nbtHandlers.containsKey(iface)) {
                return nbtHandlers.get(iface);
            }
        }

        Class superClass = clz.getSuperclass();
        if(superClass == null) {
            return null;
        }

        return getNBTHandler(superClass);
    }


    // Functional interfaces
    public interface Writer<T extends Object> {
        void write(T t, ByteBuf buf);
    }

    public interface Reader<T extends Object> {
        T read(ByteBuf buf);
    }

    public interface NbtWriter<T extends Object> {
        void write(String key, T t, NBTTagCompound tag);
    }

    public interface NbtReader<T extends Object> {
        T read(String key, NBTTagCompound tag);
    }

}
