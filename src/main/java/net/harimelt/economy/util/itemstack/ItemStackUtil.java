/*
 * Copyright (C) 2021 SirOswaldo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.harimelt.economy.util.itemstack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackUtil {

    public static ItemStack replace(ItemStack item, String regex, String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(meta.getDisplayName().replaceAll(regex, value));
        }
        if (meta.hasLore()) {
            for (int i = 0; i < meta.getLore().size(); i++) {
                meta.getLore().set(i, meta.getLore().get(i).replaceAll(regex, value));
            }
        }
        item.setItemMeta(meta);
        return item;
    }

}
