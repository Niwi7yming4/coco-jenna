#!/usr/bin/env python3
"""Inline Patchouli i18n keys into per-language book JSON (fixes raw-key display in large packs)."""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BOOK = ROOT / "src/main/resources/assets/cocojenna/patchouli_books/guardian_guide"
LANG_DIR = ROOT / "src/main/resources/assets/cocojenna/lang"
LANGS = ("zh_tw", "zh_cn", "en_us")
TEXT_FIELDS = ("name", "description", "text", "title", "subtitle")


def is_i18n_key(value: str) -> bool:
    return value.startswith(("patchouli.cocojenna.", "item.cocojenna.", "guide.cocojenna."))


def resolve(value: str, lang: dict[str, str], fallback: dict[str, str]) -> str:
    if not is_i18n_key(value):
        return value
    return lang.get(value) or fallback.get(value) or value


def bake_obj(obj, lang: dict[str, str], fallback: dict[str, str]) -> tuple[object, int]:
    changed = 0
    if isinstance(obj, dict):
        out = {}
        for k, v in obj.items():
            if k in TEXT_FIELDS and isinstance(v, str) and is_i18n_key(v):
                out[k] = resolve(v, lang, fallback)
                if out[k] != v:
                    changed += 1
            else:
                baked, n = bake_obj(v, lang, fallback)
                out[k] = baked
                changed += n
        return out, changed
    if isinstance(obj, list):
        out = []
        for item in obj:
            baked, n = bake_obj(item, lang, fallback)
            out.append(baked)
            changed += n
        return out, changed
    return obj, 0


def main() -> None:
    total = 0
    missing: set[str] = set()
    for lang_code in LANGS:
        lang_path = LANG_DIR / f"{lang_code}.json"
        if not lang_path.is_file():
            print(f"skip {lang_code}: no lang file")
            continue
        lang_data = json.loads(lang_path.read_text(encoding="utf-8"))
        fallback_path = LANG_DIR / "zh_tw.json"
        fallback_data = (
            json.loads(fallback_path.read_text(encoding="utf-8"))
            if fallback_path.is_file() and lang_code != "zh_tw"
            else {}
        )
        book_lang = BOOK / lang_code
        if not book_lang.is_dir():
            print(f"skip {lang_code}: no book folder")
            continue
        for path in sorted(book_lang.rglob("*.json")):
            data = json.loads(path.read_text(encoding="utf-8"))
            baked, changed = bake_obj(data, lang_data, fallback_data)
            if changed:
                path.write_text(json.dumps(baked, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
                total += changed
            # track missing keys still present after bake
            def scan(o):
                if isinstance(o, dict):
                    for k, v in o.items():
                        if k in TEXT_FIELDS and isinstance(v, str) and is_i18n_key(v):
                            missing.add(v)
                        else:
                            scan(v)
                elif isinstance(o, list):
                    for i in o:
                        scan(i)
            scan(baked)
    print(f"Baked {total} i18n fields across {', '.join(LANGS)}")
    if missing:
        print(f"Warning: {len(missing)} keys still unresolved (first 5): {list(sorted(missing))[:5]}")


if __name__ == "__main__":
    main()
