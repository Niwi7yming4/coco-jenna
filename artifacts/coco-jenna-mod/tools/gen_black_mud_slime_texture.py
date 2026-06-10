"""Dark layered slime texture for black mud mobs (64x32, vanilla slime UV layout)."""
from pathlib import Path

OUT = Path(__file__).resolve().parents[1] / "src/main/resources/assets/cocojenna/textures/entity/black_mud_slime.png"

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("PIL required")

W, H = 64, 32
img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# outer blob (left half) — deep black-violet
for y in range(16):
    for x in range(32):
        t = (x / 31 + y / 15) / 2
        r = int(12 + t * 18)
        g = int(10 + t * 14)
        b = int(22 + t * 28)
        a = 255 if 4 < x < 28 and 2 < y < 14 else 0
        if a:
            img.putpixel((x, y), (r, g, b, a))

# inner core (right half) — lighter violet-gray highlight
for y in range(16):
    for x in range(32, 64):
        t = ((x - 32) / 31 + y / 15) / 2
        r = int(38 + t * 40)
        g = int(34 + t * 36)
        b = int(58 + t * 50)
        a = 255 if 36 < x < 60 and 4 < y < 14 else 0
        if a:
            img.putpixel((x, y), (r, g, b, a))

OUT.parent.mkdir(parents=True, exist_ok=True)
img.save(OUT)
print("Wrote", OUT)
