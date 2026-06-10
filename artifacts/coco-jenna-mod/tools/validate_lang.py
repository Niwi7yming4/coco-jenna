#!/usr/bin/env python3
import json
from pathlib import Path

LANG = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"

for name in ("zh_tw.json", "en_us.json", "zh_cn.json"):
    p = LANG / name
    if not p.exists():
        print(f"{name}: MISSING")
        continue
    try:
        d = json.loads(p.read_text(encoding="utf-8"))
        print(f"{name}: OK ({len(d)} keys)")
    except json.JSONDecodeError as e:
        print(f"{name}: ERROR line {e.lineno} col {e.colno}: {e.msg}")

if (LANG / "zh_tw.json").exists() and (LANG / "en_us.json").exists():
    en = json.loads((LANG / "en_us.json").read_text(encoding="utf-8"))
    zh = json.loads((LANG / "zh_tw.json").read_text(encoding="utf-8"))
    missing = sorted(set(en) - set(zh))
    print(f"missing in zh_tw: {len(missing)}")
    for k in missing[:20]:
        print(" ", k)
