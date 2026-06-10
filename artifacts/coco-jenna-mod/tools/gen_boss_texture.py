"""Generate a simple dark humanoid boss texture for coco-jenna mod."""
from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/textures/entity/boss_humanoid.png"

try:
    from PIL import Image
except ImportError:
    print("PIL not installed; skip texture gen")
    raise SystemExit(0)

W, H = 64, 64
img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
px = img.load()

# Dark coat + purple mud veins (Steve layout approximation)
base = (28, 24, 36, 255)
highlight = (55, 48, 72, 255)
mud = (18, 8, 28, 255)
eye = (180, 40, 220, 255)

for y in range(H):
    for x in range(W):
        # head region
        if 0 <= y < 16 and 8 <= x < 24:
            px[x, y] = highlight if (x + y) % 5 == 0 else base
        elif 16 <= y < 32:  # body
            px[x, y] = mud if (x * y) % 11 == 0 else base
        elif 32 <= y < 48:  # legs
            px[x, y] = base
        else:
            px[x, y] = (0, 0, 0, 0)

# eyes
px[10, 10] = eye
px[21, 10] = eye

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT)
print("Wrote", OUT)
