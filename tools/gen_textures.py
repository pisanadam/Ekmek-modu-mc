#!/usr/bin/env python3
"""Ekmek Modu dokularını üretir (16x16, vanilla tarzı piksel sanat).

Kullanım: python3 tools/gen_textures.py
Çıktı: src/main/resources/assets/ekmekmodu/textures/{item,block}/*.png

Tüm dokular bu betikle prosedürel olarak çizilir; vanilla dokuları kopyalanmaz,
yalnızca vanilla ekmek / taş tuğla renk tonlarına benzer rampalar kullanılır.
"""
import math
import os
import random
import zlib

from PIL import Image

ROOT = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources", "assets", "ekmekmodu", "textures")
ITEM_DIR = os.path.join(ROOT, "item")
BLOCK_DIR = os.path.join(ROOT, "block")


def hx(s):
    s = s.lstrip("#")
    return (int(s[0:2], 16), int(s[2:4], 16), int(s[4:6], 16), 255)


def ramp(*cols):
    return [hx(c) for c in cols]


# Rampalar: [kontur, koyu, orta-koyu, orta, açık, parlak]
R = {
    "dough": ramp("#7a6a4c", "#bfae88", "#d6c7a2", "#e6dabb", "#f2ead4", "#faf5e8"),
    "dough_ww": ramp("#5e4a2e", "#9c8058", "#b3966a", "#c6aa7e", "#d6bd94", "#e4cfaa"),
    "dough_rye": ramp("#45341f", "#715a3c", "#856c4a", "#987e5a", "#ab926c", "#bca47e"),
    "dough_egg": ramp("#7c6230", "#c9a75a", "#dbbb6c", "#e8ca7e", "#f2da98", "#f8e8b8"),
    "dough_corn": ramp("#7a5c14", "#c49a2c", "#d6ac38", "#e4bc4a", "#eecc66", "#f6dc8a"),
    "crust": ramp("#3f2e0e", "#654b17", "#8c661e", "#a27924", "#bc8927", "#d4a646"),
    "crust_light": ramp("#4e3812", "#86621e", "#a97f2e", "#c0943c", "#d4aa52", "#e6c274"),
    "crust_sour": ramp("#2e1e0a", "#4f3314", "#6e4a1e", "#8a5f28", "#a87738", "#c49250"),
    "crust_ww": ramp("#33230f", "#53391a", "#6a4b24", "#7f5c2e", "#957040", "#ab8654"),
    "crust_rye": ramp("#1e150b", "#33261a", "#443323", "#55402c", "#665036", "#7a6142"),
    "crust_brioche": ramp("#3e2206", "#7a3e0c", "#a05812", "#bc6c18", "#d88a26", "#f2b44c"),
    "crust_flat": ramp("#6e5634", "#b0946a", "#c8ae82", "#dcc49a", "#eadaba", "#f6ecd4"),
    "crust_corn": ramp("#5a3e0c", "#9c6e16", "#bd8a20", "#d4a02c", "#e6ba44", "#f2d066"),
    "crust_naan": ramp("#6a4e26", "#b49058", "#cca86c", "#dcbc82", "#e8ce9c", "#f2e0ba"),
    "stone": ramp("#3a3a3a", "#5a595a", "#6a6a6a", "#7f7f7f", "#8b898b", "#9c999c"),
    "wood": ramp("#3b2a12", "#5a3f1c", "#6e4e24", "#86602e", "#9c7238", "#b08646"),
    "sack": ramp("#5a4a32", "#8e7a58", "#a8946e", "#bfab84", "#d2c09c", "#e2d4b6"),
    "glass": ramp("#3a5a7e", "#5d8fc2", "#8eb4dc", "#b3cfec", "#d4e5f7", "#ffffff"),
}

SESAME = hx("#f2e4b4")
SESAME_D = hx("#c8b07a")
FLOUR = hx("#efe9dc")
FLOUR_D = hx("#d8d0c0")
CHAR = hx("#4a3018")
OLIVE = hx("#3a2a3a")
OLIVE_G = hx("#5a6a28")
CLEAR = (0, 0, 0, 0)


class Canvas:
    def __init__(self, name, size=16):
        self.name = name
        self.size = size
        self.img = Image.new("RGBA", (size, size), CLEAR)
        self.rng = random.Random(zlib.crc32(name.encode()))

    def put(self, x, y, c):
        if 0 <= x < self.size and 0 <= y < self.size and c is not None:
            self.img.putpixel((int(x), int(y)), c)

    def get(self, x, y):
        if 0 <= x < self.size and 0 <= y < self.size:
            return self.img.getpixel((x, y))
        return CLEAR

    def fill(self, c):
        for y in range(self.size):
            for x in range(self.size):
                self.put(x, y, c)

    def save(self, folder):
        os.makedirs(folder, exist_ok=True)
        self.img.save(os.path.join(folder, self.name + ".png"))


# ----------------------------------------------------------------- maskeler

def m_ellipse(cx, cy, rx, ry, rot=0.0):
    ca, sa = math.cos(rot), math.sin(rot)

    def f(x, y):
        dx, dy = x + 0.5 - cx, y + 0.5 - cy
        u = dx * ca + dy * sa
        v = -dx * sa + dy * ca
        return (u / rx) ** 2 + (v / ry) ** 2 <= 1.0
    return f


def m_capsule(x0, y0, x1, y1, r):
    def f(x, y):
        px, py = x + 0.5, y + 0.5
        dx, dy = x1 - x0, y1 - y0
        t = max(0.0, min(1.0, ((px - x0) * dx + (py - y0) * dy) / (dx * dx + dy * dy)))
        qx, qy = x0 + t * dx, y0 + t * dy
        return (px - qx) ** 2 + (py - qy) ** 2 <= r * r
    return f


def m_rrect(x0, y0, x1, y1, cut=1):
    def f(x, y):
        if not (x0 <= x <= x1 and y0 <= y <= y1):
            return False
        cx = min(x - x0, x1 - x)
        cy = min(y - y0, y1 - y)
        return cx + cy >= cut
    return f


def m_union(*ms):
    return lambda x, y: any(m(x, y) for m in ms)


def m_sub(a, b):
    return lambda x, y: a(x, y) and not b(x, y)


def mask_set(m):
    return {(x, y) for y in range(16) for x in range(16) if m(x, y)}


def shade(cv, m, rp, noise=0.12, gloss=False):
    """Maskeyi vanilla tarzı: kontur + sol-üst ışık + sağ-alt gölge ile boyar."""
    s = mask_set(m) if callable(m) else m
    o, d, md, mid, l, h = rp
    for (x, y) in s:
        edge = any((x + dx, y + dy) not in s for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        if edge:
            cv.put(x, y, o)
            continue
        # sol-üst kenara / sağ-alt kenara uzaklık
        tl = 0
        while (x - tl - 1, y - tl - 1) in s and tl < 4:
            tl += 1
        br = 0
        while (x + br + 1, y + br + 1) in s and br < 4:
            br += 1
        if br <= 1:
            c = d
        elif br == 2:
            c = md
        elif tl <= 1:
            c = l
        elif tl == 2 and cv.rng.random() < 0.5:
            c = l
        else:
            c = mid
        r = cv.rng.random()
        if r < noise / 2:
            c = {d: md, md: mid, mid: md, l: mid}.get(c, c)
        elif r < noise:
            c = {md: d, mid: l, l: h if gloss else l, d: md}.get(c, c)
        cv.put(x, y, c)
    if gloss:
        # tepe parlaması
        pts = sorted(s, key=lambda p: (p[0] + p[1]))
        for p in pts[:40]:
            x, y = p
            if all((x + dx, y + dy) in s for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                tl = 0
                while (x - tl - 1, y - tl - 1) in s and tl < 4:
                    tl += 1
                if tl == 2:
                    cv.put(x, y, h)
    return s


def sprinkle(cv, s, col, n, interior=True, col2=None):
    pts = [p for p in s if not interior or all((p[0] + dx, p[1] + dy) in s for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))]
    cv.rng.shuffle(pts)
    for i, (x, y) in enumerate(pts[:n]):
        cv.put(x, y, col2 if (col2 and i % 3 == 0) else col)


def line(cv, pts, col, s=None):
    for (x, y) in pts:
        if s is None or (x, y) in s:
            cv.put(x, y, col)


def diag_slashes(cv, s, col, positions, length=2, steep=False):
    for (x, y) in positions:
        for i in range(length):
            p = (x + i, y - i) if not steep else (x + i, y - 2 * i)
            if p in s:
                cv.put(p[0], p[1], col)


# ------------------------------------------------------------ ekmek şekilleri
# Her şekil: (maske, süslemeler) — hamur ve pişmiş ekmek aynı şekli kullanır.

def shape_boule(scale=0):
    return m_ellipse(8, 9, 6.2 + scale, 5.2 + scale)


def shape_oval(scale=0):
    return m_ellipse(8, 9, 7.0 + scale, 4.6 + scale, rot=-0.45)


def shape_baguette(scale=0):
    return m_capsule(2.5, 13.5, 13.5, 2.5, 2.1 + scale * 0.6)


def shape_ciabatta(scale=0):
    return m_ellipse(8, 9, 7.2 + scale, 4.0 + scale * 0.7, rot=-0.15)


def shape_focaccia(scale=0):
    return m_rrect(1, 4 - scale, 14, 13, cut=2)


def shape_pan(scale=0):
    # kalıp ekmeği: düz alt, kubbe üst
    return m_union(m_rrect(2, 7 - scale, 13, 14, cut=1), m_ellipse(7.5, 7.5 - scale, 6.0, 4.0 + scale))


def shape_challah(scale=0):
    return m_union(*[m_ellipse(3.6 + i * 2.9, 12.4 - i * 2.9, 2.6 + scale * 0.4, 2.0 + scale * 0.4, rot=-0.78) for i in range(4)])


def shape_pita(scale=0):
    return m_ellipse(8, 8.5, 6.5 + scale * 0.5, 5.6 + scale * 0.5)


def shape_naan(scale=0):
    return m_union(m_ellipse(9, 9.5, 5.6 + scale * 0.4, 5.2 + scale * 0.4), m_ellipse(5, 5.5, 3.2, 3.0))


def shape_lavash(scale=0):
    return m_union(m_ellipse(8, 8.5, 7.4, 6.3), m_ellipse(5, 11, 4, 3.5))


def shape_ring(scale=0, inner=2.0):
    return m_sub(m_ellipse(8, 8.5, 6.6 + scale * 0.4, 6.0 + scale * 0.4), m_ellipse(8, 8.5, inner, inner * 0.9))


def shape_pide(scale=0):
    return m_ellipse(8, 8.5, 7.5, 4.2 + scale * 0.5, rot=-0.6)


def shape_square(scale=0):
    return m_rrect(2, 4, 13, 13, cut=1)


def deco_none(cv, s, rp, baked):
    pass


def deco_boule_cross(cv, s, rp, baked):
    if not baked:
        sprinkle(cv, s, rp[4], 4)
        return
    for i in range(-3, 4):
        if (8 + i, 9 + i) in s:
            cv.put(8 + i, 9 + i, rp[1])
        if (8 + i, 9 - i) in s:
            cv.put(8 + i, 9 - i, rp[1])
    # ekşi maya: kesik kenarında kabaran açık "kulak"
    for i in range(-2, 3):
        if (9 + i, 9 + i) in s:
            cv.put(9 + i, 9 + i, rp[4])
    sprinkle(cv, s, FLOUR, 6, col2=FLOUR_D)


def deco_boule_ww(cv, s, rp, baked):
    deco_boule_cross(cv, s, rp, baked)
    sprinkle(cv, s, rp[1] if baked else rp[2], 5)


def deco_oval_scores(cv, s, rp, baked):
    if not baked:
        return
    for k in range(3):
        x0, y0 = 4 + k * 3, 11 - k * 2
        for i in range(3):
            p = (x0 + i, y0 - i + (i // 2))
            if p in s:
                cv.put(p[0], p[1], rp[1])
                if (p[0], p[1] + 1) in s:
                    cv.put(p[0], p[1] + 1, rp[4])


def deco_baguette(cv, s, rp, baked):
    if not baked:
        return
    for k in range(4):
        cx, cy = 4 + k * 2.6, 11.6 - k * 2.6
        for i in range(3):
            p = (int(cx + i - 1), int(cy))
            if p in s:
                cv.put(p[0], p[1], rp[1])
            q = (p[0], p[1] - 1)
            if q in s and i == 1:
                cv.put(q[0], q[1], rp[5])


def deco_ciabatta(cv, s, rp, baked):
    sprinkle(cv, s, FLOUR if baked else rp[5], 10, col2=FLOUR_D)
    if baked:
        sprinkle(cv, s, rp[1], 3)


def deco_focaccia(cv, s, rp, baked):
    for y in range(6, 13, 3):
        for x in range(3 + (y % 2), 14, 3):
            if (x, y) in s:
                cv.put(x, y, rp[1] if baked else rp[2])
    # zeytinler
    for (x, y) in ((5, 8), (10, 7), (8, 11), (12, 11)):
        if (x, y) in s:
            cv.put(x, y, OLIVE)
            cv.put(x + 1, y, OLIVE_G if baked else OLIVE)


def deco_pan(cv, s, rp, baked):
    # kepek / yulaf serpintisi
    sprinkle(cv, s, rp[5] if baked else rp[4], 6)
    sprinkle(cv, s, rp[1], 4)


def deco_rye(cv, s, rp, baked):
    if baked:
        for y in (7, 10):
            for x in range(3, 14):
                if (x, y) in s and (x + y) % 4 != 0:
                    cv.put(x, y, rp[1])
        sprinkle(cv, s, FLOUR_D, 4)


def deco_challah(cv, s, rp, baked):
    # örgü çizgileri
    for i in range(3):
        cx, cy = 5.0 + i * 2.9, 10.8 - i * 2.9
        for j in range(-1, 2):
            p = (int(cx + j), int(cy - j))
            if p in s:
                cv.put(p[0], p[1], rp[1])
    if baked:
        sprinkle(cv, s, SESAME, 5)


def deco_pita(cv, s, rp, baked):
    if baked:
        sprinkle(cv, s, rp[2], 6)
        sprinkle(cv, s, rp[5], 4)


def deco_naan(cv, s, rp, baked):
    if baked:
        sprinkle(cv, s, CHAR, 7, col2=rp[1])
        sprinkle(cv, s, rp[5], 5)


def deco_lavash(cv, s, rp, baked):
    if baked:
        sprinkle(cv, s, rp[1], 9, col2=CHAR)
    else:
        sprinkle(cv, s, rp[2], 4)


def deco_simit(cv, s, rp, baked):
    sprinkle(cv, s, SESAME, 22 if baked else 0, col2=SESAME_D)


def deco_bagel(cv, s, rp, baked):
    pass


def deco_pide(cv, s, rp, baked):
    # parmak izleri (pide baklava deseni) + susam
    for k in range(-2, 3):
        for i in range(-3, 4):
            p = (8 + i + k * 2, 9 + i // 2 - k * 2 + (i % 2))
            if p in s and (i + k) % 2 == 0:
                cv.put(p[0], p[1], rp[1] if baked else rp[2])
    if baked:
        sprinkle(cv, s, SESAME, 10, col2=CHAR)


def deco_corn(cv, s, rp, baked):
    if baked:
        for x in range(2, 14):
            if (x, 4) in s:
                cv.put(x, 4, rp[1])
        sprinkle(cv, s, rp[5], 6)
        # kesilmiş dilim çizgisi
        for y in range(5, 13):
            if (8, y) in s:
                cv.put(8, y, rp[1])
    else:
        sprinkle(cv, s, rp[5], 5)


# id -> (şekil, hamur rampası, kabuk rampası, süsleme, parlak?, mayalanır?)
BREADS = {
    "sourdough_bread": (shape_boule, "dough", "crust_sour", deco_boule_cross, False, True),
    "whole_wheat_sourdough_bread": (shape_boule, "dough_ww", "crust_ww", deco_boule_ww, False, True),
    "whole_wheat_bread": (shape_pan, "dough_ww", "crust_ww", deco_pan, False, True),
    "village_loaf": (shape_oval, "dough", "crust", deco_oval_scores, False, True),
    "baguette": (shape_baguette, "dough", "crust_light", deco_baguette, False, True),
    "ciabatta": (shape_ciabatta, "dough", "crust_light", deco_ciabatta, False, True),
    "focaccia": (shape_focaccia, "dough", "crust_light", deco_focaccia, True, True),
    "rye_bread": (shape_boule, "dough_rye", "crust_rye", deco_rye, False, True),
    "brioche": (shape_boule, "dough_egg", "crust_brioche", deco_none, True, True),
    "challah": (shape_challah, "dough_egg", "crust_brioche", deco_challah, True, True),
    "pita": (shape_pita, "dough", "crust_flat", deco_pita, False, True),
    "naan": (shape_naan, "dough", "crust_naan", deco_naan, False, True),
    "lavash": (shape_lavash, "dough", "crust_flat", deco_lavash, False, False),
    "simit": (lambda sc=0: shape_ring(sc, 2.4), "dough", "crust_brioche", deco_simit, False, True),
    "pide": (shape_pide, "dough", "crust_light", deco_pide, True, True),
    "cornbread": (shape_square, "dough_corn", "crust_corn", deco_corn, False, False),
    "bagel": (lambda sc=0: shape_ring(sc, 1.8), "dough", "crust_brioche", deco_bagel, True, True),
}


def draw_bread(name, shape, rp_key, deco, gloss, baked, scale):
    cv = Canvas(name)
    rp = R[rp_key]
    s = shade(cv, shape(scale), rp, gloss=gloss and baked)
    deco(cv, s, rp, baked)
    cv.save(ITEM_DIR)


def bread_dough_name(bread):
    base = bread.replace("_bread", "")
    return base + "_dough"


# -------------------------------------------------------------- malzemeler

def draw_flour(name, flour_col):
    cv = Canvas(name)
    sack = mask_set(m_union(m_rrect(3, 6, 12, 14, cut=2), m_ellipse(7.5, 6.5, 4.0, 2.2)))
    shade(cv, sack, R["sack"])
    # boğaz ve ip
    for x in range(5, 11):
        cv.put(x, 4, R["sack"][0])
    for x in range(6, 10):
        cv.put(x, 3, R["sack"][0])
        cv.put(x, 5, hx("#6a4a22"))
    cv.put(7, 2, R["sack"][0])
    cv.put(8, 2, R["sack"][0])
    # un etiketi/bandı
    fc = hx(flour_col)
    for x in range(4, 12):
        for y in (9, 10, 11):
            if (x, y) in sack and cv.get(x, y) != R["sack"][0]:
                cv.put(x, y, fc if y != 11 else shade_col(fc, 0.82))
    # dökülen un
    for (x, y) in ((12, 14), (13, 14), (13, 13), (2, 14)):
        cv.put(x, y, fc)
    cv.save(ITEM_DIR)


def shade_col(c, f):
    return (min(255, int(c[0] * f)), min(255, int(c[1] * f)), min(255, int(c[2] * f)), 255)


def draw_pile(name, light, mid, dark, outline, crystals=True):
    cv = Canvas(name)
    s = mask_set(m_union(m_ellipse(8, 11, 6.5, 3.5), m_ellipse(8, 8.5, 4, 3.5)))
    shade(cv, s, [hx(outline), hx(dark), hx(dark), hx(mid), hx(light), hx("#ffffff")])
    if crystals:
        sprinkle(cv, s, hx("#ffffff"), 8)
        sprinkle(cv, s, hx(dark), 4)
    cv.save(ITEM_DIR)


def draw_block_item(name, rp, top_col=None, label=None):
    """Küçük izometrik küp (maya, tereyağı)."""
    cv = Canvas(name)
    o, d, md, mid, l, h = rp
    top = [(x, y) for y in range(4, 8) for x in range(3, 13) if abs(x - 7.5) + (y - 4) * 2 <= 7]
    for y in range(4, 9):
        for x in range(2, 14):
            pass
    # üst yüz
    for y in range(4, 8):
        w = 2 + (y - 4) * 2 if y < 6 else 2 + (7 - y) * 2 + 2
    poly_top = [(8, 3), (13, 5), (8, 7), (3, 5)]
    poly_left = [(3, 5), (8, 7), (8, 13), (3, 11)]
    poly_right = [(8, 7), (13, 5), (13, 11), (8, 13)]
    fill_poly(cv, poly_left, mid)
    fill_poly(cv, poly_right, d)
    fill_poly(cv, poly_top, top_col or l)
    for p in poly_outline([(8, 3), (13, 5), (13, 11), (8, 13), (3, 11), (3, 5)]):
        cv.put(p[0], p[1], o)
    for p in seg((3, 5), (8, 7)) + seg((8, 7), (13, 5)):
        if cv.get(*p)[3]:
            cv.put(p[0], p[1], h if p[0] < 8 else l)
    for p in seg((8, 8), (8, 12)):
        cv.put(p[0], p[1], md)
    if label:
        for (x, y) in label:
            cv.put(x, y, label[(x, y)])
    cv.save(ITEM_DIR)


def seg(a, b):
    (x0, y0), (x1, y1) = a, b
    n = max(abs(x1 - x0), abs(y1 - y0))
    return [(round(x0 + (x1 - x0) * t / n), round(y0 + (y1 - y0) * t / n)) for t in range(n + 1)] if n else [a]


def poly_outline(pts):
    out = []
    for i in range(len(pts)):
        out += seg(pts[i], pts[(i + 1) % len(pts)])
    return out


def fill_poly(cv, pts, col):
    xs = [p[0] for p in pts]
    ys = [p[1] for p in pts]
    for y in range(min(ys), max(ys) + 1):
        for x in range(min(xs), max(xs) + 1):
            if point_in_poly(x, y, pts):
                cv.put(x, y, col)
    for p in poly_outline(pts):
        cv.put(p[0], p[1], col)


def point_in_poly(x, y, pts):
    inside = False
    j = len(pts) - 1
    for i in range(len(pts)):
        xi, yi = pts[i]
        xj, yj = pts[j]
        if ((yi > y) != (yj > y)) and (x < (xj - xi) * (y - yi) / (yj - yi + 1e-9) + xi):
            inside = not inside
        j = i
    return inside


def draw_bottle(name, liquid, level=9, cork=True):
    """Vanilla bal şişesine benzer cam şişe, içinde sıvı."""
    cv = Canvas(name)
    g = R["glass"]
    body = mask_set(m_union(m_ellipse(8, 10.5, 4.6, 4.4), m_rrect(6, 4, 9, 7, cut=0)))
    lq = [hx(c) for c in liquid]
    for (x, y) in body:
        edge = any((x + dx, y + dy) not in body for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        if edge:
            cv.put(x, y, g[0])
        elif y >= level:
            c = lq[1]
            if x <= 5 or y >= 13:
                c = lq[2]
            if x >= 10:
                c = lq[2]
            if x == 5 and y == level + 1:
                c = lq[0]
            cv.put(x, y, c)
        else:
            cv.put(x, y, g[3] if (x + y) % 3 else g[4])
    # parlama
    cv.put(5, 9, g[5])
    cv.put(5, 10, g[4])
    # mantar tıpa
    if cork:
        for x in range(6, 10):
            cv.put(x, 3, hx("#8a6038"))
            cv.put(x, 2, hx("#a87848"))
        cv.put(6, 2, hx("#5a3a1c"))
        cv.put(9, 3, hx("#5a3a1c"))
    cv.save(ITEM_DIR)


def draw_jar(name, content, level, bubbles=0, hooch=False):
    """Ekşi maya kavanozu."""
    cv = Canvas(name)
    g = R["glass"]
    jar = mask_set(m_rrect(3, 4, 12, 14, cut=1))
    ct = [hx(c) for c in content]
    for (x, y) in jar:
        edge = any((x + dx, y + dy) not in jar for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        if edge:
            cv.put(x, y, g[0])
        elif y >= level:
            c = ct[1]
            if x == 4 or y == 13:
                c = ct[2]
            if x == 11:
                c = ct[2]
            cv.put(x, y, c)
        else:
            cv.put(x, y, g[3] if (x * 2 + y) % 5 else g[4])
    if hooch:
        for x in range(4, 12):
            cv.put(x, level, hx("#6a5a3a"))
    rng = cv.rng
    for _ in range(bubbles):
        x, y = rng.randint(5, 10), rng.randint(level + 1, 12)
        cv.put(x, y, ct[0])
    if bubbles:
        for x in range(4, 12):
            if rng.random() < 0.5:
                cv.put(x, level - 1, ct[1])
    # kapak (bez + ip)
    for x in range(3, 13):
        cv.put(x, 3, hx("#d8ccb0"))
        cv.put(x, 2, hx("#efe6d2") if x not in (3, 12) else CLEAR)
    for x in range(3, 13):
        cv.put(x, 4, hx("#8a3a2a"))
    cv.put(4, 5, g[5])
    cv.put(4, 6, g[4])
    cv.save(ITEM_DIR)


def draw_bowl(name, content):
    cv = Canvas(name)
    bowl = ramp("#1f1502", "#3f2b0b", "#43300b", "#533909", "#714f0f", "#8a6420")
    s = mask_set(m_sub(m_ellipse(8, 8, 7, 6.5), m_rrect(0, 0, 15, 7, cut=0)))
    shade(cv, s, bowl)
    c = [hx(x) for x in content]
    for y in (6, 7, 8):
        for x in range(1, 15):
            if (x - 7.5) ** 2 / 49 + ((y - 7.5) / 2.0) ** 2 <= 1:
                cv.put(x, y, c[0] if y == 6 else c[1])
    for x in range(1, 15):
        if cv.get(x, 8)[3]:
            cv.put(x, 9, bowl[4])
    cv.put(5, 7, hx("#ffffff"))
    cv.put(9, 6, hx("#ffffff"))
    cv.save(ITEM_DIR)


def draw_olives(name):
    cv = Canvas(name)
    for (cx, cy, col) in ((5.5, 10, "olive_b"), (10.5, 9, "olive_g"), (7.5, 5.5, "olive_b")):
        rp = ramp("#1a121a", "#2a1e2a", "#3a2a3a", "#4a3650", "#6a5070", "#a090a8") if col == "olive_b" else ramp("#2a3010", "#4a5a1e", "#5a6a28", "#6e8030", "#8a9c40", "#c0cc70")
        shade(cv, m_ellipse(cx, cy, 3.0, 3.6, rot=0.5), rp, gloss=True)
    # sap ve yaprak
    for p in ((9, 3), (10, 2), (11, 2), (12, 1)):
        cv.put(p[0], p[1], hx("#4a3a1c"))
    for p in ((12, 3), (13, 3), (13, 2), (14, 2)):
        cv.put(p[0], p[1], hx("#6a7a5a"))
    cv.save(ITEM_DIR)


def draw_grain_stalk(name, grain_rp, stem="#565138"):
    """Çavdar başağı: vanilla buğday eşyasına benzer çapraz demet."""
    cv = Canvas(name)
    st = hx(stem)
    for i in range(10):
        cv.put(2 + i // 2 + (i % 2), 14 - i, st)
    for k, (ox, oy) in enumerate(((0, 0), (3, -1), (-1, -3))):
        s = mask_set(m_capsule(6 + ox, 9 + oy, 11 + ox, 3 + oy, 1.6))
        shade(cv, s, grain_rp, noise=0.3)
        for t in range(4):
            x, y = 7 + ox + t, 8 + oy - t * 2
            cv.put(x + 1, y - 1, grain_rp[0])
    # kılçıklar
    for (x, y) in ((12, 1), (13, 0), (14, 2), (9, 1), (10, 0), (15, 3)):
        cv.put(x, y, grain_rp[2])
    cv.save(ITEM_DIR)


def draw_seeds(name, cols, shape="oval", n=6):
    cv = Canvas(name)
    pts = [(4, 10), (8, 12), (11, 9), (6, 6), (10, 5), (7, 9), (12, 13), (3, 13)][:n]
    c = [hx(x) for x in cols]
    for (x, y) in pts:
        if shape == "oval":
            cv.put(x, y, c[1])
            cv.put(x + 1, y, c[2])
            cv.put(x, y - 1, c[0])
            cv.put(x + 1, y + 1, c[3])
        elif shape == "kernel":
            cv.put(x, y, c[1])
            cv.put(x + 1, y, c[1])
            cv.put(x, y + 1, c[2])
            cv.put(x + 1, y + 1, c[3])
            cv.put(x, y - 1, c[0])
        else:  # tear (susam)
            cv.put(x, y, c[1])
            cv.put(x, y + 1, c[2])
            cv.put(x + 1, y + 1, c[3])
    cv.save(ITEM_DIR)


def draw_corn(name):
    cv = Canvas(name)
    cob = mask_set(m_capsule(4, 12, 11.5, 4.5, 2.6))
    rp = R["dough_corn"]
    shade(cv, cob, ramp("#7a5410", "#c49a2c", "#d6ac38", "#e8c040", "#f4d460", "#fff0a0"), noise=0.0)
    for (x, y) in cob:
        if cv.get(x, y) != hx("#7a5410") and (x + y) % 2 == 0:
            cv.put(x, y, hx("#d0a030"))
    # yeşil kabuk yaprakları
    husk = ramp("#1e3a10", "#2e5a18", "#3a6e20", "#4a8228", "#5e9a34", "#7ab84a")
    shade(cv, m_union(m_capsule(2, 14, 6, 9, 1.5), m_capsule(3, 14, 9, 12, 1.3)), husk, noise=0.0)
    for p in ((12, 3), (13, 2), (14, 1), (12, 2)):
        cv.put(p[0], p[1], hx("#c8a060"))
    cv.save(ITEM_DIR)


def draw_hand_mill(name):
    cv = Canvas(name)
    st = R["stone"]
    shade(cv, m_ellipse(8, 11.5, 6.5, 3.0), st)
    shade(cv, m_ellipse(8, 8.0, 6.0, 2.8), ramp("#3a3a3a", "#626062", "#727072", "#888688", "#9c999c", "#b4b2b4"))
    cv.put(8, 8, st[0])
    cv.put(7, 8, st[0])
    # tutamak
    wd = R["wood"]
    for y in range(2, 8):
        cv.put(12, y, wd[3])
        cv.put(13, y, wd[1])
    cv.put(12, 1, wd[0])
    cv.put(13, 1, wd[0])
    cv.save(ITEM_DIR)


def draw_sieve(name):
    cv = Canvas(name)
    rim = m_sub(m_ellipse(8, 8, 7, 6), m_ellipse(8, 8, 5.2, 4.3))
    shade(cv, rim, R["wood"], noise=0.0)
    inner = mask_set(m_ellipse(8, 8, 5.2, 4.3))
    for (x, y) in inner:
        if (x + y) % 2 == 0:
            cv.put(x, y, hx("#c8c0a8"))
        elif x % 2 == 0:
            cv.put(x, y, hx("#8e8670"))
    cv.save(ITEM_DIR)


def draw_box(name, body, label):
    cv = Canvas(name)
    fill_poly(cv, [(4, 4), (11, 4), (11, 14), (4, 14)], hx(body))
    for p in poly_outline([(4, 4), (11, 4), (11, 14), (4, 14)]):
        cv.put(p[0], p[1], hx("#5a5a6a"))
    for x in range(5, 11):
        for y in range(7, 11):
            cv.put(x, y, hx(label))
    for x in range(5, 11):
        cv.put(x, 5, hx("#ffffff"))
    for y in range(5, 14):
        cv.put(10, y, shade_col(hx(body), 0.8))
    cv.put(7, 8, hx("#ffffff"))
    cv.put(8, 9, hx("#ffffff"))
    cv.save(ITEM_DIR)


# ------------------------------------------------------------------ bloklar

def stone_brick_tex(name, rp=None, mortar="#5a5654", seed_shift=0):
    cv = Canvas(name)
    rp = rp or R["stone"]
    for y in range(16):
        for x in range(16):
            row = y // 4
            off = 4 if row % 2 else 0
            mx = (x + off) % 8
            if y % 4 == 3 or mx == 7:
                c = hx(mortar)
            else:
                r = cv.rng.random()
                c = rp[3] if r < 0.5 else rp[2] if r < 0.75 else rp[4] if r < 0.92 else rp[1]
                if y % 4 == 0 or mx == 0:
                    c = rp[4] if cv.rng.random() < 0.7 else rp[5]
                if y % 4 == 2:
                    c = rp[2] if cv.rng.random() < 0.6 else c
            cv.put(x, y, c)
    return cv


def draw_oven(lit):
    side = stone_brick_tex("stone_oven_side")
    side.save(BLOCK_DIR)
    top = stone_brick_tex("stone_oven_top", mortar="#4a4644")
    # baca deliği
    for y in range(6, 10):
        for x in range(6, 10):
            top.put(x, y, hx("#1c1a1a") if 6 < x < 9 and 6 < y < 9 else hx("#3a3838"))
    top.save(BLOCK_DIR)
    for on in (False, True):
        cv = stone_brick_tex("stone_oven_front_on" if on else "stone_oven_front")
        arch = mask_set(m_union(m_rrect(3, 8, 12, 14, cut=0), m_ellipse(7.5, 8.5, 4.6, 4.2)))
        for (x, y) in arch:
            if y > 15:
                continue
            edge = any((x + dx, y + dy) not in arch for dx, dy in ((1, 0), (-1, 0), (0, -1)))
            if edge:
                cv.put(x, y, hx("#8a5a40"))  # tuğla kemer
            else:
                if on:
                    depth = (y - 4) / 10
                    c = hx("#2a1408")
                    if y >= 11:
                        c = hx("#e07a1c") if (x + y) % 3 else hx("#ffc23a")
                    elif y >= 9:
                        c = hx("#8a3a10") if (x + y) % 2 else hx("#c45a14")
                    cv.put(x, y, c)
                else:
                    cv.put(x, y, hx("#141212") if y < 12 else hx("#262222"))
        # kemer taşları
        for x in range(3, 13):
            cv.put(x, 15, hx("#6a6a6a"))
        if not on:
            for (x, y) in ((5, 13), (6, 13), (9, 13), (10, 12)):
                cv.put(x, y, hx("#3a3634"))
        cv.save(BLOCK_DIR)


def draw_trough():
    wd = R["wood"]
    cv = Canvas("proofing_trough_side")
    for y in range(16):
        for x in range(16):
            c = wd[3]
            if y % 5 == 4:
                c = wd[1]
            elif cv.rng.random() < 0.18:
                c = wd[2]
            elif cv.rng.random() < 0.1:
                c = wd[4]
            cv.put(x, y, c)
    cv.save(BLOCK_DIR)
    cv = Canvas("proofing_trough_inner")
    for y in range(16):
        for x in range(16):
            c = wd[2]
            if x % 4 == 3:
                c = wd[1]
            elif cv.rng.random() < 0.15:
                c = wd[3]
            cv.put(x, y, c)
    cv.save(BLOCK_DIR)
    for nm, rp, n in (("proofing_trough_dough", R["dough"], 0.3), ("proofing_trough_risen", R["dough"], 0.15)):
        cv = Canvas(nm)
        for y in range(16):
            for x in range(16):
                r = cv.rng.random()
                c = rp[3] if r > n else rp[2] if r > n / 3 else rp[1]
                if nm.endswith("risen") and cv.rng.random() < 0.08:
                    c = rp[5]
                cv.put(x, y, c)
        # bez örtü kenarı (keten)
        if nm.endswith("risen"):
            for x in range(16):
                for y in range(0, 3):
                    cv.put(x, y, hx("#e8e0cc") if (x + y) % 3 else hx("#cfc4aa"))
        cv.save(BLOCK_DIR)


def draw_crop(prefix, stages, kind):
    for i in range(stages):
        cv = Canvas(f"{prefix}_stage{i}")
        h = 3 + i * 4 if i < 3 else 15
        green = ramp("#1e3a10", "#2e5a18", "#3a6e20", "#4a8228", "#5e9a34", "#7ab84a")
        stems = (2, 5, 9, 12, 14) if kind != "corn" else (3, 8, 12)
        for k, sx in enumerate(stems):
            hh = h - (k % 2) * 2
            for y in range(15, 15 - hh, -1):
                x = sx + (1 if (y + k) % 5 == 0 else 0)
                cv.put(x, y, green[3] if y % 3 else green[2])
                if kind == "corn" and y % 4 == 1 and y < 14:
                    cv.put(x + 1, y, green[4])
                    cv.put(x - 1, y + 1, green[3])
            top = 15 - hh + 1
            if i == stages - 1:
                if kind == "rye":
                    for d in range(4):
                        cv.put(sx, top + d, hx("#a8946a") if d % 2 else hx("#8c7a52"))
                        cv.put(sx + 1, top + d, hx("#bca878"))
                    cv.put(sx + 1, top - 1, hx("#c8b488"))
                elif kind == "sesame":
                    for d in (2, 5, 8):
                        if top + d < 15:
                            cv.put(sx + 1, top + d, hx("#e8dcb0"))
                            cv.put(sx - 1, top + d + 1, hx("#c8b07a"))
                    cv.put(sx, top, hx("#f0f0f0"))
                elif kind == "corn":
                    for d in range(3, 7):
                        cv.put(sx + 1, top + d, hx("#f0c840") if d % 2 else hx("#d8a828"))
                        cv.put(sx + 2, top + d, hx("#4a8228"))
                    cv.put(sx, top - 1, hx("#c8a060"))
            elif i == stages - 2 and kind != "corn":
                cv.put(sx, top, green[5])
        cv.save(BLOCK_DIR)


# -------------------------------------------------------------------- main

def main():
    for bread, (shape, dough_rp, crust_rp, deco, gloss, proofs) in BREADS.items():
        dough = bread_dough_name(bread)
        draw_bread(bread, shape, crust_rp, deco, gloss, True, 0)
        draw_bread(dough, shape, dough_rp, deco, False, False, -0.6)
        if proofs:
            draw_bread("proofed_" + dough, shape, dough_rp, deco, False, False, 0.3)
    # özel ara aşamalar
    draw_bread("raw_simit", lambda sc=0: shape_ring(sc, 2.4), "dough_egg", lambda cv, s, rp, b: sprinkle(cv, s, SESAME, 22, col2=SESAME_D), False, False, 0)
    draw_bread("boiled_bagel", lambda sc=0: shape_ring(sc, 1.8), "dough_egg", deco_none, True, True, 0.2)

    draw_flour("whole_wheat_flour", "#c8a878")
    draw_flour("white_flour", "#f6f3ec")
    draw_flour("rye_flour", "#9a8060")
    draw_flour("corn_flour", "#ecc850")
    draw_pile("salt", "#f8f8fc", "#e4e4ee", "#b9b9cb", "#6a6a80")
    draw_block_item("yeast", ramp("#6a5a3a", "#a8946a", "#bca878", "#d0bc8c", "#e2d0a4", "#f2e6c4"))
    draw_block_item("butter", ramp("#8a7020", "#d8b840", "#e4c650", "#f0d666", "#f8e48a", "#fff4c0"))
    draw_box("baking_soda", "#f4f4f8", "#e05a20")
    draw_jar("sourdough_starter_fed", ("#a89878", "#e4dcc8", "#cfc4aa"), 8)
    draw_jar("sourdough_starter_active", ("#a89878", "#ece4d0", "#d6ccb2"), 6, bubbles=7)
    draw_jar("sourdough_starter_hungry", ("#a89878", "#d8ccb0", "#c0b294"), 11, hooch=True)
    draw_bowl("yogurt", ("#ffffff", "#eeeef2"))
    draw_olives("olives")
    draw_bottle("olive_oil", ("#e8d860", "#c8b830", "#9a8a1c"), level=8)
    draw_bottle("berry_must", ("#c04060", "#8a1c3a", "#5a1028"), level=8)
    draw_bottle("pekmez", ("#7a4a20", "#4a2810", "#2e180a"), level=8)
    draw_grain_stalk("rye", ramp("#4a3e2a", "#6e604a", "#8a7a5c", "#a49270", "#bca984", "#d4c29c"))
    draw_seeds("rye_seeds", ("#5a4a32", "#8a7652", "#a8946a", "#4a3a22"))
    draw_corn("corn")
    draw_seeds("corn_seeds", ("#c89a20", "#f0c840", "#e0b030", "#b08018"), shape="kernel")
    draw_seeds("sesame_seeds", ("#c8b07a", "#f2e4b4", "#e0d098", "#a89058"), shape="tear", n=8)
    draw_hand_mill("hand_mill")
    draw_sieve("sieve")

    draw_oven(False)
    draw_trough()
    draw_crop("rye", 4, "rye")
    draw_crop("corn", 4, "corn")
    draw_crop("sesame", 4, "sesame")


if __name__ == "__main__":
    main()
