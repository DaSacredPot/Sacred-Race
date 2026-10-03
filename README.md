# Sacred Race

An open-source Fabric 26.3 modpack for building, tuning, and racing nine original and parody-brand cars, with Sodium graphics and performance mods.

## Included mods

- Race Mod (included directly in the pack)
- Fabric API
- Sodium and Sodium Extra
- Lithium, FerriteCore, ImmediatelyFast, and EntityCulling

All performance mods in the pack have published Fabric builds for Minecraft 26.3. Sodium is currently an alpha release for this Minecraft version.

## Install the modpack

Download `Race-Mod-Performance-Pack-1.0.4.mrpack` from [GitHub Releases](https://github.com/DaSacredPot/Sacred-Race/releases) and import it into the Modrinth App. To add only the cars to another compatible Fabric profile, download `racemod-1.0.4.jar` from the same release and install Fabric API in that profile. Both options target Minecraft 26.3, Fabric Loader 0.19.5, and Java 25 or newer.

## Play

- Find the **Race Mod** creative tab, or run `/race cars` to receive every car for free.
- Place a car item on a block to spawn it and take the driver's seat. **W** accelerates forward, **S** reverses, and **A/D** steer left/right; **Shift** exits. These controls use the profile's normal Minecraft movement keybinds. Driving input is sent directly to the server while you are in the car.
- Players spawn normally in the Overworld. Run `/race` to visit the shared Sacred Speedway dimension, which has a generated oval circuit, pit grid, grandstands, and static spectator mannequins. Joining the race world does not start a race: use `/race start` when ready and `/race restart` to run it again. The top HUD shows lap/time during a race; the first-person cockpit HUD shows a steering wheel, analog-style speedometer, throttle, and fuel. Hold coal and right-click a car to add 25% fuel; use `/race return` to visit the Overworld.
- Hold diamonds and interact with a car to upgrade its engine. While riding, run `/race upgrade wheels`, `/race upgrade handling`, or `/race upgrade chassis` to choose another part.
- Upgrade each part up to level 5. Moving from level N costs 5 × N diamonds (5, 10, 15, then 20).
- Engine and wheels improve speed and acceleration; handling increases steering; chassis improves collision recovery. Car upgrades are kept when you pick up the car.
- Create a race with the included start, checkpoint, and finish blocks. Speed radars track your bounty.

Multiplayer requires the modpack on the server and each client. Each marque has its own full-size body/material atlas and side-profile inventory icon. The larger open-cockpit model has lowered, animated wheels, glass, lights, a spoiler, seats, dashboard, roll bars, and a steering wheel. The renderer uses Minecraft's normal entity-rendering pipeline; there are no Sodium-specific mixins or boat-control UI.

## Build

Run `.\gradlew.bat build modrinthPack` on Windows or `./gradlew build modrinthPack` on macOS/Linux. The mod JAR is written to `build/libs/`, and `Race-Mod-Performance-Pack-1.0.4.mrpack` is written to the project root.

## License

Source and original assets are dedicated to the public domain under [CC0 1.0](LICENSE).
