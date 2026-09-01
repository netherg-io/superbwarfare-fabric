#!/usr/bin/env bash
# Проверка существования API без запуска gradle: javap по classpath уже скачанных зависимостей.
#   ./api.sh net.minecraft.world.item.ItemStack          -- все сигнатуры класса
#   ./api.sh net.minecraft.world.item.ItemStack burn     -- только строки с 'burn'
#   ./api.sh -f RecipeRemainder                          -- найти класс(ы), где встречается имя
set -euo pipefail
cd "$(dirname "$0")"
JAVAP=$(ls -d "$HOME"/.local/share/mise/installs/java/temurin-21*/bin/javap | head -1)
CACHE=.api-classpath
if [ ! -s "$CACHE" ]; then
    {
        ls "$HOME"/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged/*/*.jar | grep -v sources
        find "$HOME/.gradle/caches/modules-2" -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar'
        find "$HOME/.gradle/caches/fabric-loom" -name '*.jar' ! -name '*-sources.jar' -path '*remapped*'
        ls libs/*.jar 2>/dev/null || true
    } | sort -u > "$CACHE"
fi
CP=$(tr '\n' ':' < "$CACHE")
if [ "${1:-}" = "-f" ]; then
    grep -rl "$2" /dev/null 2>/dev/null || true
    while read -r j; do
        unzip -l "$j" 2>/dev/null | grep -i "$2" | sed "s|^|$(basename "$j"): |"
    done < "$CACHE"
    exit 0
fi
CLASS=$1; shift
if [ $# -gt 0 ]; then
    "$JAVAP" -cp "$CP" "$CLASS" | grep -i "$1"
else
    "$JAVAP" -cp "$CP" "$CLASS"
fi
