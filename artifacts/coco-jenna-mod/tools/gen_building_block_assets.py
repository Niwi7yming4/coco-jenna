#!/usr/bin/env python3
"""Deprecated — use gen_all_block_textures.py for full pixel-art block textures."""
import subprocess
import sys
from pathlib import Path

if __name__ == "__main__":
    script = Path(__file__).resolve().parent / "gen_all_block_textures.py"
    sys.exit(subprocess.call([sys.executable, str(script)]))
