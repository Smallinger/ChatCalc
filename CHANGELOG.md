# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-10

### Added - Initial NeoForge Port
- Complete port of ChatCalc from Fabric 1.20.2 to NeoForge 1.21.10
- All mathematical calculation features from original mod
- In-chat calculations with TAB key
- Live preview tooltip above chat input
- Custom function and constant definitions
- Click-to-copy functionality for functions and constants
- Hover tooltips on function/constant listings
- Configuration file support (chatcal.json)
- All mathematical operations: basic arithmetic, trigonometry, logarithms, etc.
- Summation and product operators
- Test cases command for verification

### Changed - API Adaptations
- Updated from Fabric API to NeoForge API
- Changed package structure from `ca.rttv.chatcalc` to `de.smallinger.chatcal`
- Updated Mixin injections for Minecraft 1.21.10
- Adapted key event handling from primitive parameters to `KeyEvent` objects
- Changed chat message API from `sendSystemMessage` to `displayClientMessage`
- Updated ClickEvent and HoverEvent to use new interface-based implementations
- Changed tooltip rendering from `drawTooltip` to `setTooltipForNextFrame`
- Updated EditBox rendering from `renderButton` to `renderWidget`

### Technical Details
- **Minecraft Version**: 1.21.10
- **NeoForge Version**: 21.10.49-beta
- **Java Version**: 21
- **Gradle Version**: 9.2.0
- **Mappings**: Parchment 2025.10.12

### Documentation
- Added comprehensive README.md with usage instructions
- Created DEVELOPMENT.md with porting notes and API changes
- Added NOTICE.md with copyright and attribution information
- Added MPL 2.0 license headers to all source files
- Added this CHANGELOG.md

### Credits
- Original ChatCalc Mod by RealRTTV
- NeoForge Port by Smallinger

## [Unreleased]

### Planned Features
- Configuration GUI
- Additional mathematical functions
- Localization support for multiple languages
- Performance optimizations for complex expressions

---

## Version History

### Fabric Original (Reference)
- **Version**: 1.20.2
- **Author**: RealRTTV
- **Repository**: https://github.com/RealRTTV/chatcalc

### NeoForge Port
- **First Release**: 1.0.0 (2025-11-10)
- **Platform**: NeoForge
- **Minecraft**: 1.21.10
