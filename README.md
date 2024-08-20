# TrulyRandom - Randomise the World

[![Modrinth](https://img.shields.io/modrinth/dt/trulyrandom?colour=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/trulyrandom)
[![CurseForge](https://cf.way2muchnoise.eu/full_1087424_downloads.svg)](https://curseforge.com/minecraft/mc-mods/trulyrandom)

### Modules:
Enable TrulyRandom modules from the Other tab in world creation, or in-game with the `g` key or the `/trulyrandom server` command.

- Block Models:
  - All block models will be replaced by similar block models
  - Replacements will have the same states and shape to prevent x-ray and visual artifacting
- Item Models:
  - All item models will be replaced by other item models
  - Only exceptions are the trident and spyglass, which are hard-coded
- Loot Tables:
  - All loot tables are replaced with other loot tables
- Recipes:
  - All recipes produce the output of another random recipe
  - This includes special recipes like crafting suspicious stew, or trimming armour
- Villager Trades:
  - Villagers will accept and give any item in the game
  - Randomisation is based on module seed and villager id, thus, each villager will have a unqiue set of trades regardless of profession
- Structures:
  - Removes any and all checks for structure placement allowing them to generate everywhere
- Features:
  - Coming Soon!

#### Note:
- Structures and Features cannot be enabled or disabled in-game, only from the world creation tab.
- `/trulyrandom Player` will configure the client modules for `Player`, allowing for per-player item/block model randomisation

### Tracker:
Trackers exist for each module allowing you to track the randomisation as you play along.

- Loot Tracker:
  - Opened from the Red Book in the survival inventory
  - Displays entries of items you have triggered the loot tables for (even if they didn't drop)
  - Entries open a graph showing their sources up to a depth of 20
  - Hovering a source displays the full path to get to the entry
  - Holding shift while hovering an item will show which loot tables you have discovered that drop said item
- Rest Coming Soon!
