package su.nightexpress.excellentenchants.script;

import java.nio.file.Path;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.NonNull;

import su.nightexpress.excellentenchants.EnchantsPlugin;
import su.nightexpress.excellentenchants.api.EnchantPriority;
import su.nightexpress.excellentenchants.api.enchantment.type.ArrowEnchant;
import su.nightexpress.excellentenchants.enchantment.EnchantContext;
import su.nightexpress.excellentenchants.manager.EnchantManager;

/**
 * Script backed enchantment that also handles arrow projectile events.
 */
public class ScriptArrowEnchantment extends ScriptEnchantment implements ArrowEnchant {

    public ScriptArrowEnchantment(@NonNull EnchantsPlugin plugin,
                                  @NonNull EnchantManager manager,
                                  @NonNull Path file,
                                  @NonNull EnchantContext context,
                                  @NonNull EnchantScript script) {
        super(plugin, manager, file, context, script);
    }

    @Override
    public void onHit(@NonNull ProjectileHitEvent event, @NonNull LivingEntity shooter, @NonNull Arrow projectile,
                      int level) {
        this.run(HOOK_HIT, new ScriptContext(this, HOOK_HIT, level, shooter, null, null, event, projectile, null));
    }

    @Override
    public void onDamage(@NonNull EntityDamageByEntityEvent event, @NonNull LivingEntity shooter,
                         @NonNull LivingEntity victim, @NonNull Arrow projectile, int level) {
        this.run(HOOK_PROJECTILE_DAMAGE, new ScriptContext(this, HOOK_PROJECTILE_DAMAGE, level, shooter, victim, null,
            event, projectile, null));
    }

    @Override
    @NonNull
    public EnchantPriority getShootPriority() {
        return this.getPriority();
    }
}
