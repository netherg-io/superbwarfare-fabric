# Vendored build inputs

These jars are committed so that a clean checkout builds without private repositories,
`mavenLocal()` or personal tokens. Verify with `sha256sum -c SHA256SUMS`.

| File | License | Provenance |
|------|---------|------------|
| `rhino-1.8.1-SNAPSHOT.jar` | MPL-2.0 (Mozilla Rhino) | Byte-identical to `libs/rhino-1.8.1-SNAPSHOT.jar` in upstream [Mercurows/SuperbWarfare](https://github.com/Mercurows/SuperbWarfare) (git blob `38672b9448bd2400beafe02a85b777edad841bce`, tag `upstream-0.8.9.1-1.21` here). Shaded by upstream into package `org.mozillaa`. |
| `simplebedrockmodel-fabric-2.5.1+mc1.21.1-bf3.jar` | LGPL-3.0 (Sh1roCu) | `./gradlew build` of [netherg-io/simplebedrockmodel-fabric](https://github.com/netherg-io/simplebedrockmodel-fabric) `main` (last source commit `0dc810614ac04ea6e2e743ecd06805b4e93c2df8`, fork of [Sh1roCu/SimpleBedrockModel-Fabric](https://github.com/Sh1roCu/SimpleBedrockModel-Fabric) branch `1.21.1` @ `23b1a23f99fe274ec49f59718424ad94386b6570`), then `./patch-simplebedrockmodel.sh <jar> bf3`. The patch only moves `common.FabricItemMixin` to the client mixin section in `simplebedrockmodel.fabric.mixins.json`; all class files are unchanged from the fork build. |
