/*
 * EpicGuard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EpicGuard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.ishift.epicguard.bukkit.listener;

import me.ishift.epicguard.api.ChatUtil;
import me.ishift.epicguard.api.EpicGuardAPI;
import me.ishift.epicguard.common.Config;
import me.ishift.epicguard.common.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        final String cmd = event.getMessage();
        final String[] args = cmd.split(" ");

        // OP Protection module.
        if (Config.opProtectionEnable && !Config.opProtectionList.contains(player.getName()) && player.isOp()) {
            event.setCancelled(true);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Config.opProtectionCommand.replace("{PLAYER}", player.getName()));
            Bukkit.broadcast(ChatUtil.fix(Config.opProtectionAlert.replace("{PLAYER}", player.getName())), "epicguard.protection.notify");
            return;
        }

        // Allowed Commands module.
        if (Config.allowedCommandsEnable && !Config.allowedCommands.contains(args[0])) {
            if (Config.allowedCommandsBypass && player.hasPermission("epicguard.bypass.allowed-commands")) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(ChatUtil.fix(Messages.notAllowedCommand));
            EpicGuardAPI.getLogger().info("Player " + player.getName() + " tried to use command " + cmd + " but has no permission for it!");
            return;
        }

        // Blocked Commands module.
        if (Config.blockedCommandsEnable && Config.blockedCommands.contains(args[0])) {
            event.setCancelled(true);
            player.sendMessage(ChatUtil.fix(Messages.prefix + Messages.blockedCommand));
            EpicGuardAPI.getLogger().info("Player " + player.getName() + " tried to use forbidden command! (" + cmd + ")");
        }
    }
}
