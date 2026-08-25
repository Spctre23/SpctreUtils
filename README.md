# SpctreUtils

A client-side fabric mod for Minecraft 26.2 which adds various QOL features I find useful.


## Features

   - **Easy Build**
       - Allows you to effortlessly draw and fill an area with blocks.
       - Significantly quicker than using WorldEdit or vanilla fill commands for simple building operations.
   - **Noclip**
       - Gives you flight and allows you to phase through blocks. 
       - Must be in singleplayer.
   - **Secondary Place Toggle**
       - A more convenient toggle for the "SHIFT + PLACE" action.
       - Allows for easier placement of chests without holding shift.
   - **No Place Restrictions**
       - Removes placement restrictions.
           - Makes it possible to place blocks within entity hitboxes.
       - Must be in singleplayer.
   - **Copy Aimed Block Position**
       - Copies the coordinates of the block you are looking at to clipboard.
   - **Player Tracker**
       - Prints the estimated coordinates of all players on a server, retrieved from the player locator bar.
   - **Scrollable Flight Speed**
       - Lets you easily adjust creative mode flight speed similar with CTRL+Scroll.
       - Also gives the option to disable glide / momentum for more precise flying.
   - **Get Distance to Aimed Block**
       - Prints the distance along each axis to the block you are looking at.
   - **Get Entity Count**
       - Prints number of entities in simulation distance (default), or within the block you are looking at if holding CTRL on activation.
   - **Highlight Rare Entities**
       - Highlights rare entities with an outline.
   - **Invulnerable**
       - Makes you invincible. Must be in singleplayer.
   - **Metadata Search**
       - Highlights signs or item frames that contain a specified string.
   - **Remove Resource Pack Loading Screen**
       - Removes the annoying resource pack reloading screen.
   - **Remove Experimental Warning**
       - Removes the annoying experimental warning. Useful for datapack developers.
   - **Gamemode Switch Keybinds**
       - Adds keybinds for each gamemode, for people who want to switch gamemodes even faster than F3+F4.
   - **Fast Use**
       - Removes right click delay.
   - **No Break Delay**
       - Removes block breaking delay.
   
        
## Informational HUD
   - Configurable overlay which displays various types of useful information. Info lines can be selectively enabled / disabled to avoid screen clutter.

   - **Position**
        - Displays your coordinates. Opposite dimension coordinates are optional as well.
   - **Durability**
        - Displays the durability of the item you are holding.
   - **Entity Health**
        - Displays the health of the entity you are looking at.
   - **Entity Owner**
        - Displays the owner of the tamed entity you are looking at.
   - **Goat Variant**
        - Displays the variant of the goat you are looking at.
   - **Horse Speed**
        - Displays the maximum speed stat of the horse you are looking at.
   - **Horse Jump Height**
        - Displays the jump height stat of the horse you are looking at.
   - **Speed**
        - Displays your speed in m/s. Vertical speed is optional as well.
   - **Acceleration**
        - Displays player acceleration in m/s² or g's.
   - **Rotation**
        - Displays yaw and pitch in degrees.
   - **Biome**
   - **Dimension**
   - **Light Level**
   - **Armor**
   - **Ping**
   - **FPS**
   - **TPS**

## Dependencies

- **Yet Another Config Lib:** [Download](https://cdn.modrinth.com/data/1eAoo2KR/versions/cnfPzuFU/yet_another_config_lib_v3-3.9.6%2B26.2-fabric.jar?mr_download_reason=standalone)
- **Mod Menu:** [Download](https://cdn.modrinth.com/data/mOgUt4GM/versions/njXb639R/modmenu-20.0.1.jar?mr_download_reason=standalone)
