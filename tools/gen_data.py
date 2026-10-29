#!/usr/bin/env python3
"""Ekmek Modu veri ve varlık JSON dosyalarını üretir.

Kullanım: python3 tools/gen_data.py
Üretilenler: blockstates, modeller, items tanımları, tarifler, loot tabloları,
etiketler ve dil dosyaları (tr_tr, en_us).
"""
import json
import os

NS = "ekmekmodu"
BASE = os.path.join(os.path.dirname(__file__), "..", "src", "main", "resources")
ASSETS = os.path.join(BASE, "assets", NS)
DATA = os.path.join(BASE, "data")


def write(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, indent=2, ensure_ascii=False)
        f.write("\n")


def ns(x):
    return x if ":" in x or x.startswith("#") else f"{NS}:{x}"


def mc(x):
    return f"minecraft:{x}"


# ---------------------------------------------------------------------------
# Ekmekler: id, hamur id, mayalanır mı, ara aşama, TR adı, EN adı,
# TR hamur adı, EN hamur adı, TR açıklama, EN açıklama
BREADS = [
    ("sourdough_bread", "sourdough_dough", True, None, "Ekşi Mayalı Ekmek", "Sourdough Bread",
     "Ekşi Mayalı Ekmek Hamuru", "Sourdough Dough",
     "Doğal ekşi maya ile uzun mayalanır, çıtır kabuklu", "Long-fermented with a wild starter, crackly crust"),
    ("whole_wheat_sourdough_bread", "whole_wheat_sourdough_dough", True, None, "Ekşi Mayalı Tam Buğday Ekmeği", "Whole Wheat Sourdough Bread",
     "Ekşi Mayalı Tam Buğday Hamuru", "Whole Wheat Sourdough Dough",
     "Kepekli tam buğday unu + ekşi maya: en doyurucu ekmek", "Bran-rich whole wheat + sourdough: the most filling loaf"),
    ("whole_wheat_bread", "whole_wheat_dough", True, None, "Tam Buğday Ekmeği", "Whole Wheat Bread",
     "Tam Buğday Hamuru", "Whole Wheat Dough",
     "Kalıpta pişen, ballı tam buğday ekmeği", "Honey-sweetened whole wheat pan loaf"),
    ("village_loaf", "village_loaf_dough", True, None, "Somun Ekmek", "Turkish Loaf (Somun)",
     "Somun Hamuru", "Somun Dough",
     "Fırından yeni çıkmış klasik Türk somunu", "The classic Turkish bakery loaf"),
    ("baguette", "baguette_dough", True, None, "Baget", "Baguette",
     "Baget Hamuru", "Baguette Dough",
     "İnce uzun Fransız ekmeği", "Long and thin French bread"),
    ("ciabatta", "ciabatta_dough", True, None, "Ciabatta", "Ciabatta",
     "Ciabatta Hamuru", "Ciabatta Dough",
     "Yüksek hidrasyonlu, delikli İtalyan ekmeği", "High-hydration, open-crumb Italian bread"),
    ("focaccia", "focaccia_dough", True, None, "Focaccia", "Focaccia",
     "Focaccia Hamuru", "Focaccia Dough",
     "Zeytinyağlı, zeytinli İtalyan tepsi ekmeği", "Italian olive-oil flatbread topped with olives"),
    ("rye_bread", "rye_dough", True, None, "Çavdar Ekmeği", "Rye Bread",
     "Çavdar Hamuru", "Rye Dough",
     "Ekşi mayayla yapılan koyu, yoğun Alman/İskandinav ekmeği", "Dark, dense German/Nordic sourdough rye"),
    ("brioche", "brioche_dough", True, None, "Brioche", "Brioche",
     "Brioche Hamuru", "Brioche Dough",
     "Tereyağı ve yumurtayla zenginleştirilmiş Fransız çöreği", "French bread enriched with butter and eggs"),
    ("challah", "challah_dough", True, None, "Challah (Örgü Ekmek)", "Challah",
     "Challah Hamuru", "Challah Dough",
     "Ballı, yumurtalı örgü ekmek", "Braided bread with eggs and honey"),
    ("pita", "pita_dough", True, None, "Pita", "Pita",
     "Pita Hamuru", "Pita Dough",
     "Fırında balon gibi şişen cepli ekmek", "Pocket bread that puffs up in the oven"),
    ("naan", "naan_dough", True, None, "Naan", "Naan",
     "Naan Hamuru", "Naan Dough",
     "Yoğurtlu, tereyağlı Hint tandır ekmeği", "Indian tandoor bread made with yogurt and butter"),
    ("lavash", "lavash_dough", False, None, "Lavaş", "Lavash",
     "Lavaş Hamuru", "Lavash Dough",
     "Mayasız ince yufka ekmeği", "Thin unleavened flatbread"),
    ("simit", "simit_dough", True, "raw_simit", "Simit", "Simit",
     "Simit Hamuru", "Simit Dough",
     "Pekmeze batırılıp susama bulanan İstanbul simidi", "Istanbul sesame ring dipped in grape molasses"),
    ("pide", "pide_dough", True, None, "Ramazan Pidesi", "Pide",
     "Pide Hamuru", "Pide Dough",
     "Yumurtalı, susamlı, parmak izli Ramazan pidesi", "Egg-washed Turkish flatbread with sesame"),
    ("cornbread", "cornbread_dough", False, None, "Mısır Ekmeği", "Cornbread",
     "Mısır Ekmeği Harcı", "Cornbread Batter",
     "Kabartma tozuyla kabaran Karadeniz/Amerikan mısır ekmeği", "Chemically leavened cornbread"),
    ("bagel", "bagel_dough", True, "boiled_bagel", "Bagel", "Bagel",
     "Bagel Hamuru", "Bagel Dough",
     "Pişirmeden önce kaynar suda haşlanan halka ekmek", "Ring bread boiled before baking"),
]

INTERMEDIATE = {
    "raw_simit": ("Çiğ Simit", "Raw Simit", "Pekmezli ve susamlı; Taş Fırında pişir", "Dipped and coated; bake in a Stone Oven"),
    "boiled_bagel": ("Haşlanmış Bagel", "Boiled Bagel", "Taş Fırında pişir", "Bake in a Stone Oven"),
}

# id: (TR adı, EN adı, TR ipucu, EN ipucu)
INGREDIENTS = {
    "hand_mill": ("El Değirmeni", "Hand Mill", "Tahılı una, zeytini yağa öğütür (tükenmez)", "Grinds grain into flour and olives into oil (reusable)"),
    "sieve": ("Elek", "Sieve", "Tam buğday unundan kepeği ayırıp beyaz un yapar", "Sifts the bran out of whole wheat flour"),
    "stone_oven": ("Taş Fırın", "Stone Oven", "Ekmekler yalnızca burada pişer. Yakıt gerekir", "Breads can only be baked here. Needs fuel"),
    "proofing_trough": ("Mayalama Teknesi", "Proofing Trough", "Hamuru koy, mayalanmasını bekle. Yakında ateş varsa daha hızlı", "Put dough in and let it rise. Faster near a fire"),
    "rye_seeds": ("Çavdar Tohumu", "Rye Seeds", "Çimenlerden düşer. Toprağa ek", "Drops from grass. Plant on farmland"),
    "rye": ("Çavdar", "Rye", None, None),
    "corn_seeds": ("Mısır Taneleri", "Corn Kernels", "Çimenlerden düşer. Toprağa ek", "Drops from grass. Plant on farmland"),
    "corn": ("Mısır Koçanı", "Corn Cob", None, None),
    "sesame_seeds": ("Susam", "Sesame Seeds", "Çimenlerden düşer. Ekilebilir; simit ve pide için", "Drops from grass. Plantable; used for simit and pide"),
    "olives": ("Zeytin", "Olives", "Meşe yapraklarından düşer", "Drops from oak leaves"),
    "whole_wheat_flour": ("Tam Buğday Unu", "Whole Wheat Flour", "Buğday + El Değirmeni", "Wheat + Hand Mill"),
    "white_flour": ("Beyaz Un", "White Flour", "Tam Buğday Unu + Elek", "Whole Wheat Flour + Sieve"),
    "rye_flour": ("Çavdar Unu", "Rye Flour", "Çavdar + El Değirmeni", "Rye + Hand Mill"),
    "corn_flour": ("Mısır Unu", "Corn Flour", "Mısır + El Değirmeni", "Corn + Hand Mill"),
    "salt": ("Tuz", "Salt", "Su kovasını Taş Fırında buharlaştır", "Evaporate a water bucket in a Stone Oven"),
    "yeast": ("Maya", "Yeast", "Meyve, şeker, un ve sudan üretilen yaş maya", "Fresh yeast cultured from fruit, sugar, flour and water"),
    "baking_soda": ("Kabartma Tozu", "Baking Soda", "Mayasız kabartma: mısır ekmeği için", "Chemical leavener for cornbread"),
    "butter": ("Tereyağı", "Butter", "Sütü çalkalayarak elde edilir", "Churned from milk"),
    "yogurt": ("Yoğurt", "Yogurt", "Naan hamuru için; yenebilir", "Used in naan; edible"),
    "olive_oil": ("Zeytinyağı", "Olive Oil", "Zeytin + El Değirmeni + Cam Şişe", "Olives + Hand Mill + Glass Bottle"),
    "berry_must": ("Meyve Şırası", "Berry Must", "Taş Fırında kaynatınca pekmez olur", "Boil it in a Stone Oven to make pekmez"),
    "pekmez": ("Pekmez", "Pekmez (Molasses)", "Simidin kızarık rengini verir", "Gives simit its glossy dark crust"),
    "sourdough_starter_hungry": ("Aç Ekşi Maya", "Hungry Sourdough Starter", "Un ve suyla besle", "Feed it flour and water"),
    "sourdough_starter_fed": ("Beslenmiş Ekşi Maya", "Fed Sourdough Starter", "Mayalama Teknesinde kabarmasını bekle", "Let it rise in a Proofing Trough"),
    "sourdough_starter_active": ("Aktif Ekşi Maya", "Active Sourdough Starter", "Hamura katınca kavanozda aç maya kalır", "Using it leaves a hungry starter to feed again"),
}

# --------------------------------------------------------------- model/varlık

def item_model(name, texture=None):
    write(os.path.join(ASSETS, "models", "item", name + ".json"),
          {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{texture or name}"}})
    write(os.path.join(ASSETS, "items", name + ".json"),
          {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})


def block_item_def(name, model):
    write(os.path.join(ASSETS, "items", name + ".json"),
          {"model": {"type": "minecraft:model", "model": model}})


def crop(block, prefix):
    stage_of_age = [0, 0, 1, 1, 2, 2, 2, 3]
    write(os.path.join(ASSETS, "blockstates", block + ".json"),
          {"variants": {f"age={a}": {"model": f"{NS}:block/{prefix}_stage{s}"} for a, s in enumerate(stage_of_age)}})
    for s in range(4):
        write(os.path.join(ASSETS, "models", "block", f"{prefix}_stage{s}.json"),
              {"parent": "minecraft:block/crop", "textures": {"crop": f"{NS}:block/{prefix}_stage{s}"}})


def oven():
    variants = {}
    for facing, y in (("north", 0), ("east", 90), ("south", 180), ("west", 270)):
        for lit in (False, True):
            v = {"model": f"{NS}:block/stone_oven" + ("_on" if lit else "")}
            if y:
                v["y"] = y
            variants[f"facing={facing},lit={'true' if lit else 'false'}"] = v
    write(os.path.join(ASSETS, "blockstates", "stone_oven.json"), {"variants": variants})
    for on in (False, True):
        write(os.path.join(ASSETS, "models", "block", "stone_oven" + ("_on" if on else "") + ".json"),
              {"parent": "minecraft:block/orientable",
               "textures": {"front": f"{NS}:block/stone_oven_front" + ("_on" if on else ""),
                            "side": f"{NS}:block/stone_oven_side",
                            "top": f"{NS}:block/stone_oven_top"}})
    block_item_def("stone_oven", f"{NS}:block/stone_oven")


def box(frm, to, faces, tex_map):
    out = {"from": frm, "to": to, "faces": {}}
    for f in faces:
        tex = tex_map.get(f, tex_map.get("*"))
        if f in ("up", "down"):
            uv = [frm[0], frm[2], to[0], to[2]]
        elif f in ("north", "south"):
            uv = [frm[0], 16 - to[1], to[0], 16 - frm[1]]
        else:
            uv = [frm[2], 16 - to[1], to[2], 16 - frm[1]]
        face = {"uv": uv, "texture": tex}
        if f == "down":
            face["cullface"] = "down"
        out["faces"][f] = face
    return out


def trough():
    all6 = ["north", "south", "east", "west", "up", "down"]
    walls = [
        box([1, 0, 1], [15, 2, 15], all6, {"*": "#side", "up": "#inner"}),            # taban
        box([1, 2, 1], [15, 8, 3], all6, {"*": "#side", "south": "#inner"}),           # kuzey duvar
        box([1, 2, 13], [15, 8, 15], all6, {"*": "#side", "north": "#inner"}),         # güney duvar
        box([1, 2, 3], [3, 8, 13], all6, {"*": "#side", "east": "#inner"}),            # batı duvar
        box([13, 2, 3], [15, 8, 13], all6, {"*": "#side", "west": "#inner"}),          # doğu duvar
    ]
    base = {"parent": "minecraft:block/block", "textures": {"particle": f"{NS}:block/proofing_trough_side",
                                                             "side": f"{NS}:block/proofing_trough_side",
                                                             "inner": f"{NS}:block/proofing_trough_inner"},
            "elements": walls}
    write(os.path.join(ASSETS, "models", "block", "proofing_trough.json"), base)
    for stage, (tex, h) in ((1, ("proofing_trough_dough", 4)), (2, ("proofing_trough_risen", 7))):
        m = json.loads(json.dumps(base))
        m["textures"]["dough"] = f"{NS}:block/{tex}"
        m["elements"].append(box([3, 2, 3], [13, h, 13], ["up", "north", "south", "east", "west"], {"*": "#dough"}))
        write(os.path.join(ASSETS, "models", "block", f"proofing_trough_{['', 'dough', 'risen'][stage]}.json"), m)
    write(os.path.join(ASSETS, "blockstates", "proofing_trough.json"),
          {"variants": {"stage=0": {"model": f"{NS}:block/proofing_trough"},
                        "stage=1": {"model": f"{NS}:block/proofing_trough_dough"},
                        "stage=2": {"model": f"{NS}:block/proofing_trough_risen"}}})
    block_item_def("proofing_trough", f"{NS}:block/proofing_trough_risen")


# --------------------------------------------------------------------- tarifler

def shapeless(name, ingredients, result, count=1, group=None):
    r = {"type": "minecraft:crafting_shapeless", "category": "misc",
         "ingredients": [ns(i) for i in ingredients], "result": {"id": ns(result)}}
    if count != 1:
        r["result"]["count"] = count
    if group:
        r["group"] = group
    write(os.path.join(DATA, NS, "recipe", name + ".json"), r)


def shaped(name, pattern, key, result, count=1):
    r = {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern,
         "key": {k: ns(v) for k, v in key.items()}, "result": {"id": ns(result)}}
    if count != 1:
        r["result"]["count"] = count
    write(os.path.join(DATA, NS, "recipe", name + ".json"), r)


W = mc("water_bucket")
MILK = mc("milk_bucket")
EGG = "#minecraft:eggs"
HONEY = mc("honey_bottle")
SUGAR = mc("sugar")
WF = "white_flour"
WWF = "whole_wheat_flour"
SALT = "salt"
YEAST = "yeast"
SOUR = "sourdough_starter_active"


def recipes():
    # aletler ve bloklar
    shaped("hand_mill", ["T", "S", "S"], {"T": mc("stick"), "S": mc("smooth_stone_slab")}, "hand_mill")
    shaped("sieve", [" S ", "SRS", " S "], {"S": mc("stick"), "R": mc("string")}, "sieve")
    shaped("stone_oven", ["SSS", "B B", "SCS"], {"S": mc("stone_bricks"), "B": mc("brick"), "C": mc("campfire")}, "stone_oven")
    shaped("proofing_trough", ["P P", "PWP"], {"P": "#minecraft:planks", "W": "#minecraft:wool"}, "proofing_trough")

    # öğütme / eleme
    shapeless("whole_wheat_flour", [mc("wheat"), "hand_mill"], WWF)
    shapeless("white_flour", [WWF, "sieve"], WF)
    shapeless("rye_flour", ["rye", "hand_mill"], "rye_flour")
    shapeless("corn_flour", ["corn", "hand_mill"], "corn_flour")
    shapeless("corn_seeds", ["corn"], "corn_seeds", 3)
    shapeless("olive_oil", ["olives", "olives", "olives", "olives", mc("glass_bottle"), "hand_mill"], "olive_oil")

    # malzemeler
    shapeless("yeast", [mc("sweet_berries"), SUGAR, WWF, W], YEAST, 4)
    shapeless("baking_soda", [SALT, mc("calcite")], "baking_soda", 2)
    shapeless("butter", [MILK], "butter", 2)
    shapeless("yogurt", [MILK, mc("bowl"), mc("bowl"), mc("bowl")], "yogurt", 3)
    shapeless("berry_must", [mc("sweet_berries")] * 4 + [mc("glass_bottle")], "berry_must")

    # ekşi maya
    shapeless("sourdough_starter_fed", [WWF, W, mc("glass_bottle")], "sourdough_starter_fed")
    shapeless("feed_sourdough_starter", ["sourdough_starter_hungry", "#ekmekmodu:starter_flours", W], "sourdough_starter_fed")

    # hamurlar (tezgahta yoğurma)
    g = "ekmekmodu:dough"
    shapeless("sourdough_dough", [WF, WF, W, SALT, SOUR], "sourdough_dough", group=g)
    shapeless("whole_wheat_sourdough_dough", [WWF, WWF, W, SALT, SOUR], "whole_wheat_sourdough_dough", group=g)
    shapeless("whole_wheat_dough", [WWF, WWF, W, SALT, YEAST, HONEY], "whole_wheat_dough", group=g)
    shapeless("village_loaf_dough", [WF, WF, W, SALT, YEAST], "village_loaf_dough", group=g)
    shapeless("baguette_dough", [WF, WF, WF, W, SALT, YEAST], "baguette_dough", group=g)
    shapeless("ciabatta_dough", [WF, WF, W, W, SALT, YEAST, "olive_oil"], "ciabatta_dough", group=g)
    shapeless("focaccia_dough", [WF, WF, W, SALT, YEAST, "olive_oil", "olives"], "focaccia_dough", group=g)
    shapeless("rye_dough", ["rye_flour", "rye_flour", WF, W, SALT, SOUR], "rye_dough", group=g)
    shapeless("brioche_dough", [WF, WF, EGG, EGG, "butter", MILK, SUGAR, YEAST, SALT], "brioche_dough", group=g)
    shapeless("challah_dough", [WF, WF, EGG, EGG, HONEY, "olive_oil", W, YEAST, SALT], "challah_dough", group=g)
    shapeless("pita_dough", [WF, WF, W, SALT, YEAST, "olive_oil"], "pita_dough", 2, group=g)
    shapeless("naan_dough", [WF, WF, "yogurt", "butter", SALT, YEAST], "naan_dough", 2, group=g)
    shapeless("lavash_dough", [WF, WF, W, SALT], "lavash_dough", 3, group=g)
    shapeless("simit_dough", [WF, WF, W, SALT, YEAST, SUGAR], "simit_dough", 2, group=g)
    shapeless("pide_dough", [WF, WF, W, SALT, YEAST, EGG, "sesame_seeds"], "pide_dough", group=g)
    shapeless("cornbread_dough", ["corn_flour", "corn_flour", WF, EGG, MILK, "butter", "baking_soda", SALT, SUGAR], "cornbread_dough", group=g)
    shapeless("bagel_dough", [WF, WF, W, SALT, YEAST, HONEY], "bagel_dough", 2, group=g)

    # ara aşamalar
    shapeless("raw_simit", ["proofed_simit_dough", "pekmez", "sesame_seeds"], "raw_simit")


# ---------------------------------------------------------------- loot tabloları

# Loot tablosu biçimi sürüme göre değişir:
#   v1 = 26.1, 26.2 ("functions"/"conditions")   v2 = 26.3+ ("modifier"/"condition")
LOOT_DIRS = {"v1": ["mc26.1", "mc26.2"], "v2": ["mc26.3"]}


def write_loot(fmt, block, table):
    for d in LOOT_DIRS[fmt]:
        write(os.path.join(BASE, "..", "..", d, "resources", "data", NS, "loot_table", "blocks", block + ".json"), table)


def self_drop(block):
    for fmt in LOOT_DIRS:
        entry = {"type": "minecraft:item", "name": f"{NS}:{block}"}
        copy_name = {"include": ["minecraft:custom_name"], "source": "block_entity"}
        if fmt == "v1":
            if block == "stone_oven":
                entry["functions"] = [dict(function="minecraft:copy_components", **copy_name)]
            pool = {"bonus_rolls": 0.0, "rolls": 1.0, "conditions": [{"condition": "minecraft:survives_explosion"}], "entries": [entry]}
        else:
            if block == "stone_oven":
                entry["modifier"] = dict(type="minecraft:copy_components", **copy_name)
            pool = {"condition": {"type": "minecraft:survives_explosion"}, "entries": [entry], "rolls": 1}
        write_loot(fmt, block, {"type": "minecraft:block", "pools": [pool], "random_sequence": f"{NS}:blocks/{block}"})


def crop_loot(block, produce, seeds, produce_count=None):
    fortune = {"enchantment": "minecraft:fortune", "formula": "minecraft:binomial_with_bonus_count",
               "parameters": {"extra": 3, "probability": 0.5714286}}
    count = {"type": "minecraft:uniform", "min": produce_count[0], "max": produce_count[1]} if produce_count else None
    # v1
    mature = [{"block": f"{NS}:{block}", "condition": "minecraft:block_state_property", "properties": {"age": "7"}}]
    produce_entry = {"type": "minecraft:item", "conditions": mature, "name": f"{NS}:{produce}"}
    if count:
        produce_entry["functions"] = [{"function": "minecraft:set_count", "count": count}]
    write_loot("v1", block, {
        "type": "minecraft:block",
        "functions": [{"function": "minecraft:explosion_decay"}],
        "pools": [
            {"bonus_rolls": 0.0, "rolls": 1.0,
             "entries": [{"type": "minecraft:alternatives", "children": [produce_entry, {"type": "minecraft:item", "name": f"{NS}:{seeds}"}]}]},
            {"bonus_rolls": 0.0, "rolls": 1.0, "conditions": mature,
             "entries": [{"type": "minecraft:item", "name": f"{NS}:{seeds}",
                          "functions": [dict(function="minecraft:apply_bonus", **fortune)]}]},
        ],
        "random_sequence": f"{NS}:blocks/{block}"})
    # v2
    mature2 = {"type": "minecraft:match_block", "blocks": f"{NS}:{block}", "state": {"age": "7"}}
    produce_entry = {"type": "minecraft:item", "condition": mature2, "name": f"{NS}:{produce}"}
    if count:
        produce_entry["modifier"] = {"type": "minecraft:set_count", "count": count}
    write_loot("v2", block, {
        "type": "minecraft:block",
        "modifier": {"type": "minecraft:explosion_decay"},
        "pools": [
            {"entries": [{"type": "minecraft:alternatives", "children": [produce_entry, {"type": "minecraft:item", "name": f"{NS}:{seeds}"}]}],
             "rolls": 1},
            {"condition": mature2,
             "entries": [{"type": "minecraft:item", "name": f"{NS}:{seeds}", "modifier": dict(type="minecraft:apply_bonus", **fortune)}],
             "rolls": 1},
        ],
        "random_sequence": f"{NS}:blocks/{block}"})


def tags():
    write(os.path.join(DATA, "minecraft", "tags", "block", "mineable", "pickaxe.json"), {"values": [f"{NS}:stone_oven"]})
    write(os.path.join(DATA, "minecraft", "tags", "block", "mineable", "axe.json"), {"values": [f"{NS}:proofing_trough"]})
    write(os.path.join(DATA, "minecraft", "tags", "block", "crops.json"),
          {"values": [f"{NS}:rye_crop", f"{NS}:corn_crop", f"{NS}:sesame_crop"]})
    write(os.path.join(DATA, "minecraft", "tags", "item", "villager_plantable_seeds.json"),
          {"values": [f"{NS}:rye_seeds", f"{NS}:corn_seeds", f"{NS}:sesame_seeds"]})
    write(os.path.join(DATA, NS, "tags", "item", "starter_flours.json"),
          {"values": [f"{NS}:white_flour", f"{NS}:whole_wheat_flour", f"{NS}:rye_flour"]})
    write(os.path.join(DATA, "c", "tags", "item", "foods", "bread.json"),
          {"values": [f"{NS}:{b[0]}" for b in BREADS]})
    write(os.path.join(DATA, "c", "tags", "item", "seeds.json"),
          {"values": [f"{NS}:rye_seeds", f"{NS}:corn_seeds", f"{NS}:sesame_seeds"]})


# -------------------------------------------------------------------- diller

def lang():
    tr, en = {}, {}

    def add(key, t, e):
        tr[key] = t
        en[key] = e

    add(f"itemGroup.{NS}.ekmek", "Ekmek Modu", "Bread Mod")
    add(f"tag.item.{NS}.starter_flours", "Ekşi Maya Unları", "Starter Flours")
    add(f"container.{NS}.stone_oven", "Taş Fırın", "Stone Oven")
    add(f"block.{NS}.stone_oven", "Taş Fırın", "Stone Oven")
    add(f"block.{NS}.proofing_trough", "Mayalama Teknesi", "Proofing Trough")
    add(f"block.{NS}.rye_crop", "Çavdar", "Rye")
    add(f"block.{NS}.corn_crop", "Mısır", "Corn")
    add(f"block.{NS}.sesame_crop", "Susam", "Sesame")

    for k, (t, e, tt, et) in INGREDIENTS.items():
        prefix = "block" if k in ("stone_oven", "proofing_trough") else "item"
        add(f"{prefix}.{NS}.{k}", t, e)
        if tt:
            add(f"tooltip.{NS}.{k}", tt, et)

    for (bid, did, proofs, inter, tname, ename, tdough, edough, tdesc, edesc) in BREADS:
        add(f"item.{NS}.{bid}", tname, ename)
        add(f"tooltip.{NS}.{bid}", tdesc, edesc)
        add(f"item.{NS}.{did}", tdough, edough)
        if proofs:
            add(f"tooltip.{NS}.{did}", "Mayalama Teknesinde mayalandır", "Let it rise in a Proofing Trough")
            add(f"item.{NS}.proofed_{did}", "Mayalanmış " + tdough, "Proofed " + edough)
            if bid == "simit":
                t2, e2 = "Pekmez ve susamla şekillendir (tezgah)", "Dip in pekmez and sesame (crafting)"
            elif bid == "bagel":
                t2, e2 = "Altında ateş olan su kazanına sağ tıklayıp haşla", "Right-click a water cauldron with fire below to boil"
            else:
                t2, e2 = "Taş Fırında pişir", "Bake in a Stone Oven"
            add(f"tooltip.{NS}.proofed_{did}", t2, e2)
        else:
            add(f"tooltip.{NS}.{did}", "Mayalanmaz: doğrudan Taş Fırında pişir", "Unleavened: bake directly in a Stone Oven")
    for k, (t, e, tt, et) in INTERMEDIATE.items():
        add(f"item.{NS}.{k}", t, e)
        add(f"tooltip.{NS}.{k}", tt, et)

    add(f"message.{NS}.trough.empty", "Tekne boş. Yoğrulmuş hamur veya beslenmiş ekşi maya koy.", "The trough is empty. Add kneaded dough or a fed starter.")
    add(f"message.{NS}.trough.proofing", "%s mayalanıyor... %%%s", "%s is rising... %s%%")
    add(f"message.{NS}.trough.proofing_warm", "%s ılık ortamda hızla mayalanıyor... %%%s", "%s is rising quickly in the warmth... %s%%")
    add(f"message.{NS}.trough.ready", "%s hazır! Almak için boş elle sağ tıkla.", "%s is ready! Right-click with an empty hand to take it.")
    add(f"message.{NS}.cauldron.not_boiling", "Su kaynamıyor! Kazanın altına ateş, kamp ateşi veya magma koy.", "The water isn't boiling! Put a fire, campfire or magma below the cauldron.")

    # easter egg'ler
    add(f"message.{NS}.easter.nimet_pickup", "Ekmek nimettir! Yerden alıp öptün ve alnına koydun.", "Bread is a blessing! You kissed it and touched it to your forehead.")
    add(f"message.{NS}.easter.nimet_chat", "Ekmek nimettir. Yere düşeni öpüp alnına koy, israf etme!", "Bread is a blessing. Kiss it if it falls, never waste it!")
    add(f"message.{NS}.easter.carpar", "Ekmek çarpar! Nimeti ateşe atılır mı hiç?", "Bread strikes back! Never throw bread into the fire!")
    add(f"message.{NS}.easter.koper", "Ekmek köper! %s tekneden taştı, unutma onu fırına ver!", "The bread is foaming over! %s spilled out of the trough. Bake it before it's too late!")
    add(f"message.{NS}.easter.koper_chat", "Ekmek köper, hamur kabarır, fırıncı güler!", "Bread rises, dough puffs, the baker smiles!")
    add(f"message.{NS}.easter.koper_chat_boost", "Ekmek köper! Sözünü duyan %s teknedeki hamur coştu, köpürdü.", "Bread rises! The dough in %s trough(s) heard you and bubbled up.")

    write(os.path.join(ASSETS, "lang", "tr_tr.json"), tr)
    write(os.path.join(ASSETS, "lang", "en_us.json"), en)


def main():
    for k in INGREDIENTS:
        if k in ("stone_oven", "proofing_trough"):
            continue
        item_model(k)
    for (bid, did, proofs, inter, *_rest) in BREADS:
        item_model(bid)
        item_model(did)
        if proofs:
            item_model("proofed_" + did)
    for k in INTERMEDIATE:
        item_model(k)

    oven()
    trough()
    crop("rye_crop", "rye")
    crop("corn_crop", "corn")
    crop("sesame_crop", "sesame")

    recipes()
    self_drop("stone_oven")
    self_drop("proofing_trough")
    crop_loot("rye_crop", "rye", "rye_seeds")
    crop_loot("corn_crop", "corn", "corn_seeds", (1, 2))
    crop_loot("sesame_crop", "sesame_seeds", "sesame_seeds", (2, 4))
    tags()
    lang()


if __name__ == "__main__":
    main()
