# ChatCal Development Documentation

## Port Information

This document contains important information about porting the ChatCalc Fabric mod to NeoForge for Minecraft 1.21.10.

## Version Information

- **Source Mod**: ChatCalc (Fabric) for Minecraft 1.20.2
- **Target Platform**: NeoForge 21.10.49-beta for Minecraft 1.21.10
- **Java Version**: 21
- **Gradle Version**: 9.2.0
- **Parchment Mappings**: 2025.10.12

## Critical API Changes from Fabric 1.20.2 to NeoForge 1.21.10

### 1. Mixin Injection Points

#### ChatScreen Key Press Handler
**Problem**: The original Fabric mod used `keyPressed(III)Z` signature.

**Solution**: In Minecraft 1.21.10, the signature changed to `keyPressed(KeyEvent)`.

**Fabric 1.20.2**:
```java
@Inject(method = "keyPressed", at = @At("HEAD"))
public boolean keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
    if (keyCode == GLFW.GLFW_KEY_TAB) {
        // ...
    }
}
```

**NeoForge 1.21.10**:
```java
@Inject(method = "keyPressed", at = @At("HEAD"))
public void keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
    if (event.key() == 258) { // TAB key code
        // ...
    }
}
```

**How to Research**: Always check decompiled sources in Gradle cache before guessing:
```
C:\Users\<USER>\.gradle\caches\ng_execute\<hash>\transformed\net\minecraft\client\gui\screens\ChatScreen.java
```

### 2. Chat Message Display

**Problem**: `sendSystemMessage` method no longer exists.

**Solution**: Use `displayClientMessage` with boolean parameter.

**Fabric 1.20.2**:
```java
client.player.sendSystemMessage(Component.literal("message"));
```

**NeoForge 1.21.10**:
```java
client.player.displayClientMessage(Component.literal("message"), false);
```

### 3. ClickEvent and HoverEvent

**Problem**: In Fabric, these were abstract classes with constructors. In 1.21.10, they are interfaces with record implementations.

**Fabric 1.20.2**:
```java
new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "text")
new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("tooltip"))
```

**NeoForge 1.21.10**:
```java
new ClickEvent.CopyToClipboard("text")
new HoverEvent.ShowText(Component.literal("tooltip"))
```

**Available ClickEvent Types**:
- `ClickEvent.OpenUrl(String url)`
- `ClickEvent.RunCommand(String command)`
- `ClickEvent.SuggestCommand(String command)`
- `ClickEvent.CopyToClipboard(String value)`
- `ClickEvent.ChangePage(int page)`

**Available HoverEvent Types**:
- `HoverEvent.ShowText(Component value)`
- `HoverEvent.ShowItem(ItemStack item)`
- `HoverEvent.ShowEntity(EntityType<?>, UUID, Component)`

### 4. Tooltip Rendering

**Problem**: Fabric used `DrawContext.drawTooltip()`, which doesn't exist in NeoForge.

**Solution**: Use `GuiGraphics.setTooltipForNextFrame()`.

**Fabric 1.20.2**:
```java
context.drawTooltip(textRenderer, text, x - 8, y - 4);
```

**NeoForge 1.21.10**:
```java
context.setTooltipForNextFrame(font, component, x - 8, y - 4);
```

**Note**: Tooltips are rendered on the next frame, not immediately.

### 5. EditBox Rendering

**Problem**: Method names changed from `renderButton` to `renderWidget`.

**Solution**: Update Mixin injection point.

**Fabric 1.20.2**:
```java
@Inject(method = "renderButton", at = @At("TAIL"))
private void renderButton(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    // ...
}
```

**NeoForge 1.21.10**:
```java
@Inject(method = "renderWidget", at = @At("TAIL"))
private void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    // ...
}
```

### 6. Component Style Application

**Fabric 1.20.2**:
```java
MutableComponent line = Component.literal(text);
line.setStyle(line.getStyle()
    .withClickEvent(clickEvent)
    .withHoverEvent(hoverEvent));
```

**NeoForge 1.21.10**:
```java
MutableComponent line = Component.literal(text);
line.withStyle(style -> style
    .withClickEvent(clickEvent)
    .withHoverEvent(hoverEvent));
```

## Package Structure Changes

### Fabric (1.20.2)
```
ca.rttv.chatcalc/
├── ChatCalc.java
├── ChatHelper.java
├── Config.java
├── mixin/
│   ├── ChatInputSuggesterMixin.java
│   ├── ChatScreenMixin.java
│   └── TextFieldWidgetMixin.java
└── duck/
    └── ChatInputSuggesterDuck.java
```

### NeoForge (1.21.10)
```
de.smallinger.chatcal/
├── ChatCal.java
├── ChatCalClient.java
├── ChatCalculator.java
├── ChatHelper.java
├── Config.java
├── mixin/
│   ├── ChatScreenMixin.java
│   └── EditBoxMixin.java
└── (all math-related classes)
```

## Mixin Configuration

### chatcal.mixins.json
```json
{
  "required": true,
  "minVersion": "0.8",
  "package": "de.smallinger.chatcal.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
    "ChatScreenMixin",
    "EditBoxMixin"
  ],
  "client": [],
  "injectors": {
    "defaultRequire": 1
  }
}
```

**Important Notes**:
- `"mixins"` array is for common mixins
- `"client"` array is for client-only mixins
- Set `compatibilityLevel` to `JAVA_21` for Java 21
- Both mixins are technically client-side but work in the common array

## Debugging Mixins

### Finding Decompiled Sources
```bash
# Minecraft classes are cached here after first run:
C:\Users\<USER>\.gradle\caches\ng_execute\<hash>\transformed\

# Common locations:
net\minecraft\client\gui\screens\ChatScreen.java
net\minecraft\client\gui\components\EditBox.java
net\minecraft\network\chat\ClickEvent.java
net\minecraft\network\chat\HoverEvent.java
net\minecraft\client\gui\GuiGraphics.java
```

### Verifying Mixin Injection
Check the console output for:
```
[Render thread/DEBUG] [mixin/]: Mixing ChatScreenMixin from chatcal.mixins.json into net.minecraft.client.gui.screens.ChatScreen
[Render thread/DEBUG] [mixin/]: Mixing EditBoxMixin from chatcal.mixins.json into net.minecraft.client.gui.components.EditBox
```

### Common Mixin Errors

1. **Target method not found**:
   - Method signature changed
   - Method was removed
   - Method was renamed
   - Solution: Search in decompiled sources

2. **Injection point not found**:
   - Target instruction doesn't exist
   - Bytecode changed
   - Solution: Use `@At("TAIL")` or `@At("HEAD")` for simpler injections

3. **ClassCastException**:
   - Type mismatch in cast
   - Solution: Check actual class hierarchy in decompiled sources

## Build Configuration

### build.gradle Important Settings

```gradle
minecraft {
    accessTransformers {
        file('src/main/resources/META-INF/accesstransformer.cfg')
    }
}

dependencies {
    implementation "net.neoforged:neoforge:21.10.49-beta"
}

minecraft {
    runs {
        client {
            workingDirectory project.file('run')
            property 'forge.logging.console.level', 'debug'
            property 'mixin.env.remapRefMap', 'true'
            property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
            mods {
                chatcal {
                    source sourceSets.main
                }
            }
        }
    }
}
```

## Testing Checklist

- [ ] TAB key triggers calculation in chat
- [ ] Mathematical expressions evaluate correctly
- [ ] Custom functions can be defined and used
- [ ] Custom constants can be defined and used
- [ ] `functions?` command lists functions with click-to-copy
- [ ] `constants?` command lists constants with click-to-copy
- [ ] Hover tooltips show on function/constant listings
- [ ] Live preview shows above chat input
- [ ] Config file is created and loaded correctly
- [ ] No console errors during startup
- [ ] Mixins inject successfully

## Research Workflow

**NEVER guess API changes. Always research first:**

1. **Check decompiled sources**:
   ```bash
   Get-ChildItem -Path "$env:USERPROFILE\.gradle\caches" -Recurse -Filter "ClassName.java"
   ```

2. **Search for method signatures**:
   ```bash
   Select-String -Path "path\to\Class.java" -Pattern "methodName"
   ```

3. **Read method bodies**:
   ```bash
   Get-Content "path\to\Class.java" | Select-String -Pattern "methodName" -Context 10
   ```

4. **Test compilation** after each change:
   ```bash
   ./gradlew build
   ```

5. **Test in-game** to verify functionality:
   ```bash
   ./gradlew runClient
   ```

## Known Issues & Limitations

1. **Tooltips are not clickable**: Standard Minecraft tooltips don't support click events. The tooltip uses `setTooltipForNextFrame` which doesn't process clicks.

2. **Chat message character limit**: Chat messages are limited to 256 characters, so complex expressions may fail.

3. **Development environment detection**: The original Fabric mod had `FabricLoader.getInstance().isDevelopmentEnvironment()`, which doesn't have a direct NeoForge equivalent. Removed timing display for now.

## Performance Considerations

- **Evaluation Cache**: The EditBoxMixin uses a cache (`chatcal$evaluationCache`) to avoid re-evaluating the same expression multiple times per frame.
- **Clear Tables**: Always clear `CONSTANT_TABLE` and `FUNCTION_TABLE` before evaluation to prevent memory leaks.

## Mod Distribution

Build the mod:
```bash
./gradlew build
```

The output JAR will be in:
```
build/libs/chatcal-1.0.0.jar
```

**Naming Convention**: `chatcal-<version>.jar` or `chatcal-<mcversion>-<version>.jar`

## Future Considerations

- Consider adding a custom tooltip renderer that supports click events
- Add configuration GUI using NeoForge's config screen API
- Consider adding more mathematical functions
- Add localization support for multiple languages
- Performance profiling for complex expressions

## Useful Commands

```bash
# Clean build
./gradlew clean build

# Run client
./gradlew runClient

# Run with debug output
./gradlew runClient --debug

# Refresh dependencies
./gradlew --refresh-dependencies

# Generate IntelliJ project files
./gradlew idea

# Generate Eclipse project files
./gradlew eclipse
```

## References

- [NeoForge Documentation](https://docs.neoforged.net/)
- [Mixin Documentation](https://github.com/SpongePowered/Mixin/wiki)
- [Parchment Mappings](https://parchmentmc.org/)
- [Original ChatCalc Mod](https://github.com/RealRTTV/ChatCalc)
