/*
 * This file is part of PluginsLib, licensed under the MIT License.
 *
 * Copyright (c) Lorenzo0111
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lorenzo0111.pluginslib.command;

import me.lorenzo0111.pluginslib.ChatColor;
import me.lorenzo0111.pluginslib.audience.User;
import me.lorenzo0111.pluginslib.command.annotations.Permission;
import me.lorenzo0111.pluginslib.exceptions.LibraryException;
import net.kyori.adventure.text.Component;

public abstract class SubCommand {
    private String permission = null;
    private String message = null;
    private final ICommand<?> command;

    /**
     * @param command Parent of the subcommand
     */
    public SubCommand(ICommand<?> command) {
        this.command = command;
    }

    /**
     * @return Name of the subcommand ( /command name )
     */
    public abstract String getName();

    /**
     * @return Parent of the subcommand
     */
    public ICommand<?> getCommand() {
        return command;
    }

    /**
     * Parent of the handleSubcommand abstract method.
     * This method is used for permission check
     * @param sender Command executor
     * @param args Args of the command. The args[0] will be the {@link SubCommand#getName()}
     */
    public void perform(User<?> sender, String[] args) {
        if (!can(sender)) {
            return;
        }

        this.handleSubcommand(sender,args);
    }

    /**
     * Abstract method to handle the subcommand
     * @param sender Command executor
     * @param args Args of the command. The args[0] will be the {@link SubCommand#getName()}
     */
    public abstract void handleSubcommand(User<?> sender, String[] args);

    /**
     * This method is used for permission check. You can add a permission just adding the {@link Permission} annotation to the {@link SubCommand#handleSubcommand} method
     * @param sender Command executor
     * @return true if player can execute the command
     */
    private boolean can(User<?> sender) {
        if (message == null || permission == null) {
            return true;
        }

        if (!sender.hasPermission(permission)) {
            sender.audience().sendMessage(Component.text(ChatColor.translateAlternateColorCodes('&', message)));
            return false;
        }

        return true;
    }

    /**
     * <b>Do not use this method</b>
     * @param permission Permission required from the subcommand
     * @param message Error message
     * @throws NoSuchMethodException Strange, that methods exists..
     * @throws LibraryException Caused when someone manually call this method.
     */
    public void setPermission(String permission, String message) throws NoSuchMethodException, LibraryException {
        if (!this.getClass().getMethod("handleSubcommand", User.class, String[].class).isAnnotationPresent(Permission.class)) {
            throw new LibraryException("You can't use this method. If you want to add a permission to the subcommand just add the @Permission annotation to the handleSubcommand method.");
        }

        this.permission = permission;
        this.message = message;
    }
}
