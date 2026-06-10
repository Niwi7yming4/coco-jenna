#!/usr/bin/env python3
"""Ensure lang JSON files parse; fix missing commas between entries."""
import json
import re
import shutil
from pathlib import Path

LANG = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/lang"


def fix_commas(text: str) -> str:
    return re.sub(r'("\s*)\n(\s*")', r'\1,\n\2', text)


def main() -> None:
    for name in ("zh_tw.json", "en_us.json"):
        p = LANG / name
        raw = p.read_text(encoding="utf-8")
        try:
            json.loads(raw)
            print(f"{name}: already valid")
        except json.JSONDecodeError:
            fixed = fix_commas(raw)
            json.loads(fixed)  # raises if still broken
            p.write_text(fixed, encoding="utf-8")
            print(f"{name}: fixed commas")

  # zh_cn mirrors zh_tw for 簡體中文遊戲語言設定
    src = LANG / "zh_tw.json"
    dst = LANG / "zh_cn.json"
    if src.exists():
        shutil.copy2(src, dst)
        print("zh_cn.json: synced from zh_tw.json")


if __name__ == "__main__":
    main()
