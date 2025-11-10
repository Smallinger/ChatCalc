/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = ChatCalc.MODID, dist = Dist.CLIENT)
public class ChatCalcClient {
    public ChatCalcClient() {
        ChatCalc.LOGGER.info("ChatCalc Client initialized");
    }
}
