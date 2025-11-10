/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.regex.Pattern;

public class ChatCalculator {
    public static final HashSet<String> CONSTANT_TABLE = new HashSet<>();
    public static final HashSet<Pair<String, Integer>> FUNCTION_TABLE = new HashSet<>();
    public static final Pattern NUMBER = Pattern.compile("[-+]?(\\d,?)*(\\.\\d+)?");
    public static final Pattern FUNCTION = Pattern.compile("[a-zA-Z]+\\(([a-zA-Z]+;)*?([a-zA-Z]+)\\)");
    public static final Pattern CONSTANT = Pattern.compile("[a-zA-Z]+");
    public static final String SEPARATOR = ";";
    public static final char SEPARATOR_CHAR = ';';

    @Contract(value = "_->_", mutates = "param1")
    public static boolean tryParse(@NotNull EditBox field) {
        final Minecraft client = Minecraft.getInstance();
        String originalText = field.getValue();
        int cursor = field.getCursorPosition();
        String text = ChatHelper.getSection(originalText, cursor);
        {
            String[] split = text.split("=");
            if (split.length == 2) {
                if (Config.JSON.has(split[0])) {
                    Config.JSON.addProperty(split[0], split[1]);
                    Config.refreshJson();
                    return ChatHelper.replaceSection(field, "");
                } else {
                    Optional<Either<CustomFunction, CustomConstant>> either = parseDeclaration(text);
                    if (either.isPresent()) {
                        Optional<CustomFunction> left = either.get().left();
                        Optional<CustomConstant> right = either.get().right();
                        if (left.isPresent()) {
                            Config.FUNCTIONS.put(new Pair<>(left.get().name(), left.get().params().length), left.get());
                            Config.refreshJson();
                            return ChatHelper.replaceSection(field, "");
                        } else if (right.isPresent()) {
                            Config.CONSTANTS.put(right.get().name(), right.get());
                            Config.refreshJson();
                            return ChatHelper.replaceSection(field, "");
                        }
                    }
                }
            } else if (split.length == 1) {
                if (Config.JSON.has(split[0])) {
                    return ChatHelper.replaceSection(field, Config.JSON.get(split[0]).getAsString());
                } else if (!split[0].isEmpty() && Config.JSON.has(split[0].substring(0, split[0].length() - 1)) && split[0].endsWith("?") && client.player != null) {
                    client.player.displayClientMessage(Component.translatable("chatcal." + split[0].substring(0, split[0].length() - 1) + ".description"), false);
                    return false;
                } else {
                    Optional<Either<CustomFunction, CustomConstant>> either = parseDeclaration(text);
                    if (either.isPresent()) {
                        Optional<CustomFunction> left = either.get().left();
                        Optional<CustomConstant> right = either.get().right();
                        if (left.isPresent()) {
                            Pair<String, Integer> pair = new Pair<>(left.get().name(), left.get().params().length);
                            if (Config.FUNCTIONS.containsKey(pair)) {
                                Config.FUNCTIONS.remove(pair);
                                Config.refreshJson();
                                return ChatHelper.replaceSection(field, "");
                            }
                        } else if (right.isPresent()) {
                            if (Config.CONSTANTS.containsKey(right.get().name())) {
                                Config.CONSTANTS.remove(right.get().name());
                                Config.refreshJson();
                                return ChatHelper.replaceSection(field, "");
                            }
                        }
                    }
                }
            }
        }
        
        if ((text.equals("config?") || text.equals("cfg?") || text.equals("?")) && client.player != null) {
            client.player.displayClientMessage(Component.translatable("chatcal.config.description"), false);
            return false;
        } else if (text.equals("testcases?")) {
            Testcases.test(Testcases.TESTCASES);
            return false;
        } else if (text.equals("functions?")) {
            MutableComponent msg = Component.literal("Currently defined custom functions are:");
            for (CustomFunction func : Config.FUNCTIONS.values()) {
                String funcStr = func.toString();
                MutableComponent line = Component.literal("\n" + funcStr);
                
                // Add click event to copy to clipboard
                line.withStyle(style -> style
                    .withClickEvent(new ClickEvent.CopyToClipboard(funcStr))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to copy to clipboard")))
                );
                
                msg.append(line);
            }
            client.player.displayClientMessage(msg, false);
            return false;
        } else if (text.equals("constants?")) {
            MutableComponent msg = Component.literal("Currently defined custom constants are:");
            for (CustomConstant constant : Config.CONSTANTS.values()) {
                String constStr = constant.toString();
                MutableComponent line = Component.literal("\n" + constStr);
                
                // Add click event to copy to clipboard
                line.withStyle(style -> style
                    .withClickEvent(new ClickEvent.CopyToClipboard(constStr))
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to copy to clipboard")))
                );
                
                msg.append(line);
            }
            client.player.displayClientMessage(msg, false);
            return false;
        } else if (NUMBER.matcher(text).matches()) {
            return false;
        } else {
            boolean add = false;
            if (text.endsWith("=")) {
                text = text.substring(0, text.length() - 1);
                add = true;
            }
            try {
                long start = System.nanoTime();
                CONSTANT_TABLE.clear();
                FUNCTION_TABLE.clear();
                double result = Config.makeEngine().eval(text, new FunctionParameter[0]);
                double micros = (System.nanoTime() - start) / 1_000.0;
                // Development environment check not available in NeoForge the same way
                String solution = Config.getDecimalFormat().format(result);
                if (solution.equals("-0")) {
                    solution = "0";
                }
                Config.saveToChatHud(originalText);
                Config.saveToClipboard(originalText);
                return add ? ChatHelper.addSectionAfterIndex(field, solution) : ChatHelper.replaceSection(field, solution);
            } catch (Throwable t) {
                return false;
            }
        }
    }

    private static Optional<Either<CustomFunction, CustomConstant>> parseDeclaration(String text) {
        return CustomFunction.fromString(text).map(Either::<CustomFunction, CustomConstant>left).or(() -> CustomConstant.fromString(text).map(Either::<CustomFunction, CustomConstant>right));
    }
}
