# 3.0.0

### Randomiser Menu
- Now scrollable and "infinitely" extendable (thanks `AbstractGameRuleScreen`)
### Overhauled Worldgen Modules
- Structure randomisation now replaces structures in-place rather than removing structure generation restrictions
    - Old "randomiser" can be re-enabled in settings by setting "Use Legacy Randomiser" to true
- New Feature Randomisation
    - When enabled replace placed features with other placed features if possible, for example if a tree tries tries to generate an iceberg could generate in its place if possible
- New Block Palette Randomisation
    - Replaces the blocks as their set during worldgen
    - Due to how unstable this can be the randomisation has been restricted by state, collision shape and occlusion shape, however these can all be disabled if wished
    - "Keep Stone as Stone" option allows some features (like ores) and surface rules (like setting the top to dirt/grass) to apply as most require stone-like blocks to apply and generate
### Client Module Options
- New "Block States Share Models" option for block model randomiser
    - Forces all states of a randomised block to use the same block model
- New "Match Block Model Randomisation" option for the item model randomiser
    - Where possible the item model will now match the block model, helps with keeping track of what is what
- Block model randomiser is now more restrictive by default to prevent some replacements tanking your frames, this can be ignored if desired

# 2.9.1

Fix translations
Add join message

# 2.9.0

Update to 26.1
Migrate to Stonecutter build system