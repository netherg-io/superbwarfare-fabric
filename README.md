# Superb Warfare — Fabric 1.21.1 port

Unofficial port of [Superb Warfare](https://github.com/Mercurows/SuperbWarfare) (NeoForge 1.21.1,
by Atsuishio, Roki27, Light_Quanta and contributors) to **Fabric 1.21.1**, maintained by
[netherg-io](https://github.com/netherg-io) for the Blockfield server modpack.
Not affiliated with or endorsed by the upstream authors.

Upstream READMEs: [中文](./README-zh.md) | [English](./README-en.md) (they describe the original
Forge/NeoForge releases, not this port).

## Base

- Upstream snapshot: tag `upstream-0.8.9.1-1.21` (Superb Warfare 0.8.9.1 for 1.21.1).
- Port branch: `main`. Porting notes: [PORT-BRIEF.md](./PORT-BRIEF.md).
- Minecraft 1.21.1, Fabric Loader 0.19.3, Fabric API 0.116.15+1.21.1, Fabric Language Kotlin 1.13.7.

Required mods (see `depends` in `fabric.mod.json`): Fabric API, Fabric Language Kotlin,
Forge Config API Port, Accessories, GeckoLib, Porting Lib 3.1.0-beta.90 (core, entity, items,
level_events, client_events, transfer) and SimpleBedrockModel-Fabric
(see [libs/README.md](./libs/README.md)). Cloth Config and JEI are optional.

## Build

Requires JDK 21 (e.g. `mise use java@temurin-21`). No private repositories or tokens are needed;
the two non-Maven inputs are committed under `libs/` with checksums.

```sh
git clone https://github.com/netherg-io/superbwarfare-fabric.git
cd superbwarfare-fabric
(cd libs && sha256sum -c SHA256SUMS)
./gradlew build --no-daemon
```

The mod jar is written to `build/libs/superbwarfare-<version>-mc1.21.1.jar`. Release jars are
attached to [GitHub Releases](https://github.com/netherg-io/superbwarfare-fabric/releases) and
named after the release tag.

## License

Code is licensed under the **GNU LGPL-3.0-only** ([COPYING.LESSER](./COPYING.LESSER), which
supplements the GPL-3.0 in [COPYING](./COPYING)), as in the upstream repository. These files are
kept unchanged from upstream.

The upstream README states that models, textures and other art assets are *all rights reserved*
by the Superb Warfare team; they are included here exactly as published in the upstream public
repository and remain the property of their authors.

Port changes are © netherg-io and contributors, under the same license.
