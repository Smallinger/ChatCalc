# ChatCalc NeoForge
# Copyright Notice

## Original Work
**ChatCalc** - Fabric Mod for Minecraft 1.20.2  
Copyright (c) 2024 RealRTTV  
Original Repository: https://github.com/RealRTTV/chatcalc

## Derivative Work
**ChatCalc NeoForge** - NeoForge Port for Minecraft 1.21.10  
Copyright (c) 2025 Smallinger

## License
This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.  
If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.

## Modifications
This is a port of the original ChatCalc Fabric mod to NeoForge. The following modifications were made:

### Code Changes
- Ported all Java classes from Fabric API to NeoForge API
- Updated Mixin implementations for Minecraft 1.21.10
- Adapted to new Component/Chat message system
- Updated ClickEvent and HoverEvent to use new interface-based implementations
- Changed tooltip rendering from `drawTooltip` to `setTooltipForNextFrame`
- Updated key event handling from primitive parameters to `KeyEvent` objects
- Changed package structure from `ca.rttv.chatcalc` to `de.smallinger.chatcal`

### Build System
- Updated to NeoForge Gradle plugin
- Updated to Minecraft 1.21.10
- Updated to Java 21
- Added Parchment mappings

### Documentation
- Created DEVELOPMENT.md with porting notes and API changes
- Updated README.md for NeoForge installation and usage
- Added this NOTICE.md file

## Third-Party Components
This mod uses the following Minecraft and NeoForge APIs:
- Minecraft Client API - Mojang Studios
- NeoForge API - NeoForged Team
- SpongePowered Mixin - SpongePowered

## Attribution
All original mathematical calculation logic, expression parsing, and core functionality 
credit goes to RealRTTV (original author of ChatCalc).

This port maintains the same functionality while adapting to NeoForge's API differences.

## Source Code Availability
The complete source code for this NeoForge port is available at:
[Your GitHub Repository URL - to be added]

The original Fabric source code is available at:
https://github.com/RealRTTV/ChatCalc
