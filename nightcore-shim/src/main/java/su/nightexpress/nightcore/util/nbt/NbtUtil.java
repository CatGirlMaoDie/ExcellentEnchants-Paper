package su.nightexpress.nightcore.util.nbt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Native Paper/Bukkit based item (de)serialization helper.
 * <p>
 * The original implementation relied on Mojang's DataFixerUpper/NBT classes. This version only uses
 * Bukkit's own item serialization API, so no internal server classes are touched.
 */
public final class NbtUtil {

    private NbtUtil() {

    }

    /**
     * Serializes the given item into a Base64 string.
     */
    @NonNull
    public static String toBase64(@NonNull ItemStack itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(itemStack);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        }
        catch (Exception exception) {
            throw new IllegalStateException("Could not serialize item stack.", exception);
        }
    }

    /**
     * Deserializes an item from the given Base64 string.
     */
    @Nullable
    public static ItemStack fromBase64(@Nullable String data) {
        if (data == null || data.isEmpty()) return null;

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            return (ItemStack) dataInput.readObject();
        }
        catch (Exception exception) {
            Bukkit.getLogger().warning("Could not deserialize item stack: " + exception.getMessage());
            return null;
        }
    }
}
