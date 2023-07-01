package com.github.vazzzx.prison.Commands;

import com.github.vazzzx.prison.Cache.PlayerAccountCache;
import com.github.vazzzx.prison.Components.PlayerAccount;
import com.github.vazzzx.prison.Components.RegionPreset;
import com.github.vazzzx.prison.Prison;
import com.github.vazzzx.prison.Utils.Cuboid;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


@RequiredArgsConstructor
public class Punish implements CommandExecutor {

    private final PlayerAccountCache playerAccountCache;
    private final Prison prison;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        FileConfiguration config = Prison.config.getConfig();
        if (!(sender.hasPermission("punish.admin"))) {

            sender.sendMessage(config.getString("sem-perm").replace("&", "§"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(new String[]{
                "§cPrison - Comandos",
                "",
                " §c/prison setloc - Seta a localização da prisão",
                " §c/prison enviar (jogador) (qtd) - Envia o jogador pra prisão",
                " §c/prison remover (jogador) - Remove um jogador da prisão",
                " §c/prison setpos1 - Seta o primeiro local das obsidian",
                " §c/prison setpos2 - Seta o segundo local das obsidian",
                " §c/prison criar - Cria o local das obsidians",
                " §c/prison setitem - Seta o item no qual você quer que ele tenha na prison",
                ""

            });
            return true;
        }

        if (args[0].equalsIgnoreCase("setloc")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                config.set("loc", player.getLocation().serialize());
                Prison.prisonlocation = player.getLocation();
                Prison.config.save();
                Prison.config.reloadConfig();

                player.sendMessage("§aVocê setou a localização com sucesso!");
            }

            return true;
        }

        Map<String, List<ItemStack>> inventories = prison.getInventoriesCache().getInventories();
        if (args[0].equalsIgnoreCase("enviar")) {

            if (args.length == 1) {
                sender.sendMessage("§cVocê precisa digitar o nome do jogador para ser enviado para a prisão.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage("§cFalta definir a quantidade de obsidian a ser quebradas!");
                return true;
            }

            try {
                String target = args[1];
                int obsidianQuantity = Integer.parseInt(args[2]);
                Player player = Bukkit.getPlayer(target);

                if (player == null || (!(player.isOnline()))) {
                    sender.sendMessage("§cO jogador está offline.");
                    return true;
                }

                PlayerAccount playerAccount = playerAccountCache.getBlockedPlayerCache().get(player.getName());
                if (playerAccount.isBlocked()) {
                    sender.sendMessage("§cEsse jogador já está preso.");
                    return true;
                }

                playerAccount.setBlocked(true);
                playerAccount.setSentence(obsidianQuantity);
                playerAccount.setRemainBlocks(obsidianQuantity);

                this.saveAccount(playerAccount);

                if (!(player.getInventory().getContents().length == 0 && player.getInventory().getArmorContents().length == 0)) {
                    this.saveInventory(player);
                    inventories.put(player.getName(), getInventoryItems(player));
                }


                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                String message = config.getString("preso-message");
                player.sendMessage(message.replace("{quantidade}", String.valueOf(obsidianQuantity)).replace("{jogador}", sender.getName()).replace("&", "§"));
                player.teleport(Prison.prisonlocation);
                boolean activeItem = config.getBoolean("ativar-item");

                if (activeItem) {
                    player.getInventory().addItem(ItemStack.deserialize(config.getConfigurationSection("item").getValues(false)));
                    String message1 = config.getString("entrar-com-item");
                    player.sendMessage(message1.replace("&", "§"));
                }

            } catch (NumberFormatException var) {
                sender.sendMessage("§cVocê precisa definir um número!");

            }
            return true;
        }


        if (args[0].equalsIgnoreCase("remover")) {

            if (args.length == 1) {
                sender.sendMessage("§cVocê precisa digitar o nome do jogador para ser removido da prisão.");
                return true;
            }

            String target = args[1];
            Player player = Bukkit.getPlayer(target);
            World world = player.getWorld();
            Location spawnLocation = world.getSpawnLocation();

            PlayerAccount playerAccount = playerAccountCache.getBlockedPlayerCache().get(player.getName());

            playerAccount.setBlocked(false);
            playerAccount.setSentence(0);
            playerAccount.setRemainBlocks(0);

            player.sendMessage(config.getString("removido-prison").replace("{jogador}", target).replace("&", "§"));
            player.teleport(spawnLocation);
            this.saveAccount(playerAccount);
            this.deleteInventory(player);

            if (inventories.containsKey(player.getName())) {
                for (ItemStack itemStack : inventories.get(player.getName())) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) return true;
                    player.getInventory().addItem(itemStack);
                }
                inventories.remove(player.getName());
            }

            return true;
        }


        if (args[0].equalsIgnoreCase("setpos1")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 5);

                if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                    player.sendMessage("§cEsse bloco não foi encontrado!");
                    return true;
                }

                prison.getRegionPreset().setPos1(targetBlock.getLocation());
                player.sendMessage("§aVocê setou a posição 1.");
                return true;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setpos2")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                Block targetBlock = player.getTargetBlock((HashSet<Byte>) null, 5);

                if (targetBlock == null || targetBlock.getType() == Material.AIR) {
                    player.sendMessage("§cEsse bloco não foi encontrado!");
                    return true;
                }

                prison.getRegionPreset().setPos2(targetBlock.getLocation());
                player.sendMessage("§aVocê setou a posição 2.");
                return true;
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("criar")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                RegionPreset regionPreset = prison.getRegionPreset();

                if (regionPreset.getPos1() == null || regionPreset.getPos2() == null) {
                    player.sendMessage("§cVocê precisa setar as duas regiões antes de criar!");
                    return true;
                }

                Cuboid cuboid = new Cuboid(regionPreset.getPos1(), regionPreset.getPos2());

                regionPreset.setCuboid(cuboid);
                config.set("regiao-blocos", cuboid.serialize());
                Prison.config.save();
                Prison.config.reloadConfig();

                player.sendMessage("§aVocê criou a região dos blocos com sucesso!");

                return true;

            }
        }


        if (args[0].equalsIgnoreCase("setitem")) {
            if (sender instanceof Player) {

                Player player = (Player) sender;

                ItemStack itemInHand = player.getItemInHand();

                if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                    player.sendMessage("§cVocê não tem nenhum item para ser salvo.");
                    return true;
                }

                config.set("item", itemInHand.serialize());
                Prison.config.save();
                Prison.config.reloadConfig();
                player.sendMessage("§aVocê setou o item que o player vai usar com sucesso!");

            }

        }

        return false;
    }

    private List<ItemStack> getInventoryItems(Player player) {
        List<ItemStack> generalItem = new ArrayList<>();

        generalItem.addAll(Arrays.asList(player.getInventory().getContents()));
        generalItem.addAll(Arrays.asList(player.getInventory().getArmorContents()));

        return generalItem;
    }

    private void saveAccount(PlayerAccount playerAccount) {
        FileConfiguration config = Prison.config.getConfig();

        config.set("Accounts." + playerAccount.getPlayerName() + ".isBlocked", playerAccount.isBlocked());
        config.set("Accounts." + playerAccount.getPlayerName() + ".sentence", playerAccount.getSentence());
        config.set("Accounts." + playerAccount.getPlayerName() + ".remainBlocks", playerAccount.getRemainBlocks());

        Prison.config.save();
        Prison.config.reloadConfig();
    }

    private void deleteInventory(Player player) {
        FileConfiguration config = Prison.config.getConfig();

        config.set("Inventories." + player.getName(), null);
        Prison.config.save();
        Prison.config.reloadConfig();
    }


    private void saveInventory(Player player) {
        FileConfiguration config = Prison.config.getConfig();
        int index = 1;

        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() == Material.AIR) return;

            config.set("Inventories." + player.getName() + ".items.item-" + index, content.serialize());
            Prison.config.save();
            index++;
        }


        for (ItemStack armorContent : player.getInventory().getArmorContents()) {
            if (armorContent == null || armorContent.getType() == Material.AIR) return;

            config.set("Inventories." + player.getName() + ".items.item-" + index, armorContent.serialize());
            Prison.config.save();
            index++;
        }

        Prison.config.reloadConfig();
    }
}
