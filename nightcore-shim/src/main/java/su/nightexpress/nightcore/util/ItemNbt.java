package su.nightexpress.nightcore.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import su.nightexpress.nightcore.util.nbt.NbtUtil;

/**
 * Item (de)serialization helper backed by the native Bukkit object streams instead of internal
 * server NBT classes.
 */
@Deprecated
public class ItemNbt {

    private ItemNbt() {

    }

    @NonNull
    @Deprecated
    public static List<String> compress(@NonNull ItemStack[] items) {
        return compress(Arrays.asList(items));
    }

    @NonNull
    @Deprecated
    public static List<String> compress(@NonNull List<ItemStack> items) {
        return new ArrayList<>(items.stream().filter(Objects::nonNull).map(ItemNbt::compress).toList());
    }

    @Nullable
    @Deprecated
    public static String compress(@NonNull ItemStack item) {
        if (item.getType().isAir() || item.getAmount() <= 0) return null;

        return NbtUtil.toBase64(item);
    }

    @Nullable
    @Deprecated
    public static ItemStack decompress(@NonNull String compressed) {
        if (compressed.isBlank()) return null;

        return NbtUtil.fromBase64(compressed);
    }

    @Deprecated
    public static ItemStack[] decompress(@NonNull List<String> list) {
        List<ItemStack> items = list.stream().map(ItemNbt::decompress).filter(Objects::nonNull).toList();
        return items.toArray(new ItemStack[items.size()]);
    }

    @Nullable
    @Deprecated
    public static ItemStack getHoverEventItem(@NonNull String value) {
        try {
            ItemStack itemStack = Bukkit.getItemFactory().createItemStack(value);
            if (itemStack.getType().isAir()) return null;

            return itemStack;
        }
        catch (IllegalArgumentException exception) {
            return decompress(value);
        }
    }
}
