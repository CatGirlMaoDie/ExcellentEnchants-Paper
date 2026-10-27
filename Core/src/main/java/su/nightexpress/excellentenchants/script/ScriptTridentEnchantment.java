package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Trident;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.enchantment.type.TridentEnchant;
import su.nightexpress.excellentenchants.enchantment.EnchantContext;
import su.nightexpress.excellentenchants.manager.EnchantManager;

/**
 * Script backed enchantment that also handles trident projectile events.
 */
public class ScriptTridentEnchantment extends ScriptEnchantment implements TridentEnchant {

    public ScriptTridentEnchantment(@NonNull EnchantsPlugin plugin,
                                    @NonNull EnchantManager manager,
                                    @NonNull Path file,
                                    @NonNull EnchantContext context,
                                    @NonNull EnchantScript script) {
        super(plugin, manager, file, context, script);
    }

    @Override
    public boolean onLaunch(@NonNull ProjectileLaunchEvent event, @NonNull LivingEntity shooter,
                            @NonNull ItemStack trident, int level) {
        return this.runBoolean(HOOK_LAUNCH, new ScriptContext(this, HOOK_LAUNCH, level, shooter, null, trident,
            event));
    }

    @Override
    public void onHit(@NonNull ProjectileHitEvent event, @NonNull LivingEntity shooter, @NonNull Trident projectile,
                      int level) {
        this.run(HOOK_HIT, new ScriptContext(this, HOOK_HIT, level, shooter, null, null, event, projectile, null));
    }

    @Override
    public void onDamage(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity shooter,
                         @NonNull LivingEntity victim, @NonNull Trident projectile, int level) {
        this.run(HOOK_PROJECTILE_DAMAGE, new ScriptContext(this, HOOK_PROJECTILE_DAMAGE, level, shooter, victim, null,
            event, projectile, null));
    }

    @Override
    @NonNull
    public EnchantPriority getLaunchPriority() {
        return this.getPriority();
    }
}
