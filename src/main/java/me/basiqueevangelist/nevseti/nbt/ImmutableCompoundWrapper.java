package me.basiqueevangelist.nevseti.nbt;

import net.minecraft.nbt.CompoundTag;

public class ImmutableCompoundWrapper implements CompoundTagView {
    private final CompoundTag tag;

    public ImmutableCompoundWrapper(CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public byte getType(String key) {
        return tag.getType(key);
    }

    @Override
    public boolean contains(String key) {
        return tag.contains(key);
    }

    @Override
    public boolean contains(String key, int type) {
        return tag.contains(key, type);
    }

    @Override
    public byte getByte(String key) {
        return tag.getByte(key);
    }

    @Override
    public short getShort(String key) {
        return tag.getShort(key);
    }

    @Override
    public int getInt(String key) {
        return tag.getInt(key);
    }

    @Override
    public long getLong(String key) {
        return tag.getLong(key);
    }

    @Override
    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    @Override
    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    @Override
    public String getString(String key) {
        return tag.getString(key);
    }

    @Override
    public byte[] getByteArray(String key) {
        return tag.getByteArray(key);
    }

    @Override
    public int[] getIntArray(String key) {
        return tag.getIntArray(key);
    }

    @Override
    public long[] getLongArray(String key) {
        return tag.getLongArray(key);
    }

    @Override
    public CompoundTagView getCompound(String key) {
        return CompoundTagView.take(tag.getCompound(key));
    }

    @Override
    public ListTagView getList(String key, int type) {
        return ListTagView.take(tag.getList(key, type));
    }

    @Override
    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    @Override
    public CompoundTag copy() {
        return tag.copy();
    }
}
