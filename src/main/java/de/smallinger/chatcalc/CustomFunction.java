/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright (c) 2024 RealRTTV (Original Fabric Version)
 * Copyright (c) 2025 Smallinger (NeoForge Port)
 */
package de.smallinger.chatcalc;

import com.mojang.datafixers.util.Pair;

import java.util.Optional;

public record CustomFunction(String name, String eval, String[] params) {
    public static Optional<CustomFunction> fromString(String text) {
        int equalsIdx = text.indexOf('=');
        if (equalsIdx == -1) {
            return Optional.empty();
        }
        String lhs = text.substring(0, equalsIdx);
        String rhs = text.substring(equalsIdx + 1);
        if (!ChatCalculator.FUNCTION.matcher(lhs).matches()) {
            return Optional.empty();
        }
        int functionNameEnd = lhs.indexOf('(');
        String functionName = lhs.substring(0, functionNameEnd);
        int paramsEnd = lhs.substring(functionNameEnd).indexOf(')') + functionNameEnd;
        String[] params = lhs.substring(functionNameEnd + 1, paramsEnd).split(ChatCalculator.SEPARATOR);
        return Optional.of(new CustomFunction(functionName, rhs, params));
    }

    public double get(double... values) {
        if (values.length != params.length) {
            throw new IllegalArgumentException("Invalid amount of arguments for custom function");
        }

        Pair<String, Integer> pair = new Pair<>(name, params.length);

        if (ChatCalculator.FUNCTION_TABLE.contains(pair)) {
            throw new IllegalArgumentException("Tried to call function twice, recursively");
        }

        FunctionParameter[] parameters = new FunctionParameter[values.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = new FunctionParameter(params[i], values[i]);
        }
        ChatCalculator.FUNCTION_TABLE.add(pair);
        double value = Config.makeEngine().eval(eval, parameters);
        ChatCalculator.FUNCTION_TABLE.remove(pair);
        return value;
    }

    @Override
    public String toString() {
        return name + '(' + String.join(ChatCalculator.SEPARATOR, params) + ")=" + eval;
    }
}
