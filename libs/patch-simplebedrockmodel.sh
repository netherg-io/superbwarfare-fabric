#!/usr/bin/env bash
# SimpleBedrockModel-Fabric 2.5.1 держит common.FabricItemMixin в общей секции миксинов,
# а тот зовёт клиентский FirstPersonRenderHandler. На выделенном сервере клиентский класс
# вырезан, и мод роняет запуск ещё в Bootstrap. Миксин отвечает только за анимацию
# перевыбора предмета в руке, то есть ему место в клиентской секции.
#   ./patch-simplebedrockmodel.sh simplebedrockmodel-fabric-2.5.1+mc1.21.1.jar
set -euo pipefail
SRC=$1
OUT=${SRC%.jar}-bf1.jar
WORK=$(mktemp -d)
trap 'rm -rf "$WORK"' EXIT
cp "$SRC" "$OUT"
unzip -q -o "$OUT" simplebedrockmodel.fabric.mixins.json -d "$WORK"
python3 - "$WORK/simplebedrockmodel.fabric.mixins.json" <<'PY'
import json, sys
p = sys.argv[1]
d = json.load(open(p))
moved = [m for m in d.get('mixins', []) if m == 'common.FabricItemMixin']
if not moved:
    sys.exit('common.FabricItemMixin уже не в общей секции -- патч не нужен')
d['mixins'] = [m for m in d['mixins'] if m not in moved]
d['client'] = d.get('client', []) + moved
json.dump(d, open(p, 'w'), indent=2)
PY
(cd "$WORK" && zip -q "$OLDPWD/$OUT" simplebedrockmodel.fabric.mixins.json)
echo "$OUT"
