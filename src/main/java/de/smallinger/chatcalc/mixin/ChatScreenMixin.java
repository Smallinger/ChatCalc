/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc.mixin;

import de.smallinger.chatcalc.ChatCalculator;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", cancellable = true)
    private void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        // TAB key code is 258
        if (event.key() == 258 && ChatCalculator.tryParse(input)) {
            cir.setReturnValue(true);
        }
    }
}
