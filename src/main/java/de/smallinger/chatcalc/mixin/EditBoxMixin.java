/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc.mixin;

import com.mojang.datafixers.util.Pair;
import de.smallinger.chatcalc.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;

@Mixin(EditBox.class)
public abstract class EditBoxMixin extends AbstractWidget {
    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow
    public abstract int getCursorPosition();

    @Shadow
    public abstract String getValue();

    @Shadow
    @Final
    private Font font;

    @Unique
    private Pair<String, OptionalDouble> chatcal$evaluationCache;

    @Inject(method = "renderWidget", at = @At("TAIL"))
    private void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!Config.displayAbove()) {
            chatcal$evaluationCache = null;
            return;
        }

        String word = ChatHelper.getSection(getValue(), getCursorPosition());

        if (ChatCalculator.NUMBER.matcher(word).matches()) {
            chatcal$evaluationCache = null;
            return;
        }

        try {
            double result;
            if (chatcal$evaluationCache != null && chatcal$evaluationCache.getFirst().equals(word)) {
                if (chatcal$evaluationCache.getSecond().isEmpty()) {
                    return;
                }
                result = chatcal$evaluationCache.getSecond().getAsDouble();
            } else {
                ChatCalculator.CONSTANT_TABLE.clear();
                ChatCalculator.FUNCTION_TABLE.clear();
                result = Config.makeEngine().eval(word, new FunctionParameter[0]);
                chatcal$evaluationCache = new Pair<>(word, OptionalDouble.of(result));
            }
            String formattedResult = "=" + Config.getDecimalFormat().format(result);
            // Use proper tooltip rendering instead of drawString
            Component tooltipText = Component.literal(formattedResult);
            context.setTooltipForNextFrame(font, tooltipText, this.getX() - 8, this.getY() - 4);
        } catch (Throwable ignored) {
            chatcal$evaluationCache = new Pair<>(word, OptionalDouble.empty());
        }
    }
}
