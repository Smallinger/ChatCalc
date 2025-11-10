/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ChatCalc.MODID)
public class ChatCalc {
    public static final String MODID = "chatcalc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ChatCalc(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        
        LOGGER.info("ChatCalc mod initialized");
    }

    private void clientSetup(FMLClientSetupEvent event) {
        // Register our client-side event handler
        NeoForge.EVENT_BUS.register(new ChatEventHandler());
        LOGGER.info("ChatCalc client setup complete");
    }
}
