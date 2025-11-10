/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc;

import java.util.Optional;

public record CustomConstant(String name, String eval) {

    public static Optional<CustomConstant> fromString(String text) {
        int equalsIdx = text.indexOf('=');
        if (equalsIdx == -1) {
            return Optional.empty();
        }

        String lhs = text.substring(0, equalsIdx);
        String rhs = text.substring(equalsIdx + 1);

        if (!ChatCalculator.CONSTANT.matcher(lhs).matches()) {
            return Optional.empty();
        }

        return Optional.of(new CustomConstant(lhs, rhs));
    }

    public double get() {
        if (ChatCalculator.CONSTANT_TABLE.contains(name)) {
            throw new IllegalArgumentException("Tried to compute constant a second time, recursively");
        } else {
            ChatCalculator.CONSTANT_TABLE.add(name);
            double value = Config.makeEngine().eval(eval, new FunctionParameter[0]);
            ChatCalculator.CONSTANT_TABLE.remove(name);
            return value;
        }
    }

    @Override
    public String toString() {
        return name + "=" + eval;
    }
}
