# 🍞 Ekmek Modu (Fabric)

Minecraft **26.1 → 26.3** için gerçekçi ekmek yapımı modu. Buğdayı değirmende öğüt, unu elekten geçir,
ekşi mayanı besle, hamuru yoğur, **Mayalama Teknesi**'nde mayalandır ve **Taş Fırın**'da pişir.
Dünyanın en sevilen **17 ekmeği**, gerçek hayattaki tarifleriyle oyunda!

Tüm dokular vanilla Minecraft'ın 16x16 piksel stiline ve renk paletine uygun olarak çizilmiştir.

## İndirme / Sürümler

Her Minecraft sürümü için ayrı bir jar üretilir:

| Minecraft | Jar | Gereken |
|-----------|-----|---------|
| 26.1, 26.1.1, 26.1.2 | `ekmek-modu-1.0.0+mc26.1.jar` | Fabric Loader ≥ 0.20.1, Fabric API, Java 25 |
| 26.2 | `ekmek-modu-1.0.0+mc26.2.jar` | Fabric Loader ≥ 0.20.1, Fabric API, Java 25 |
| 26.3 | `ekmek-modu-1.0.0+mc26.3.jar` | Fabric Loader ≥ 0.20.1, Fabric API, Java 25 |

## Ekmek yapımının adımları

```
Buğday ──(El Değirmeni)──► Tam Buğday Unu ──(Elek)──► Beyaz Un
                                │
Un + Su + Tuz + Maya ──(Tezgahta yoğur)──► Hamur
Hamur ──(Mayalama Teknesi)──► Mayalanmış Hamur ──(Taş Fırın + yakıt)──► Ekmek
```

1. **Un öğüt** – Tezgahta `Buğday + El Değirmeni` → Tam Buğday Unu. Değirmen tükenmez, geri gelir.
2. **Ele** – `Tam Buğday Unu + Elek` → Beyaz Un (kepek ayrılır).
3. **Yoğur** – Malzemeleri tezgaha koy → hamur. Su kovaları boş kova olarak geri gelir.
4. **Mayalandır** – Hamuru elindeyken **Mayalama Teknesi**'ne sağ tıkla (16 adede kadar).
   Eğilip sağ tıklayınca yüzdeyi gösterir. Hamur kabarınca tekne bezle örtülü görünür;
   boş elle sağ tıklayıp al. 2 blok yakında yanan fırın, kamp ateşi, ateş veya lav varsa
   hamur **iki kat hızlı** mayalanır (gerçekte de ılık ortam mayalanmayı hızlandırır).
5. **Pişir** – Mayalanmış hamuru **Taş Fırın**'a koy, alta yakıt ekle. Taş fırın **sadece ekmek**
   pişirir (giriş yuvası başka bir şey kabul etmez); ekmekler de normal fırında veya tütsüleyicide pişmez!

### Ekşi maya (sourdough starter)

- `Tam Buğday Unu + Su Kovası + Cam Şişe` → **Beslenmiş Ekşi Maya**
- Mayalama Teknesinde bekletince → **Aktif Ekşi Maya** (kabarcıklı)
- Hamurda kullanınca kavanozda **Aç Ekşi Maya** kalır. Onu `un + su` ile tekrar besle.
  Tıpkı gerçek hayattaki gibi mayanı yaşatman gerekir!

## 17 ekmek ve tarifleri

| # | Ekmek | Hamur malzemeleri (tezgah) | Ek işlem |
|---|-------|----------------------------|----------|
| 1 | **Ekşi Mayalı Ekmek** | 2 Beyaz Un, Su, Tuz, Aktif Ekşi Maya | Uzun mayalanma |
| 2 | **Ekşi Mayalı Tam Buğday Ekmeği** | 2 Tam Buğday Unu, Su, Tuz, Aktif Ekşi Maya | Uzun mayalanma |
| 3 | **Tam Buğday Ekmeği** | 2 Tam Buğday Unu, Su, Tuz, Maya, Bal | |
| 4 | **Somun Ekmek** | 2 Beyaz Un, Su, Tuz, Maya | |
| 5 | **Baget** | 3 Beyaz Un, Su, Tuz, Maya | |
| 6 | **Ciabatta** | 2 Beyaz Un, 2 Su, Tuz, Maya, Zeytinyağı | Yüksek hidrasyon |
| 7 | **Focaccia** | 2 Beyaz Un, Su, Tuz, Maya, Zeytinyağı, Zeytin | |
| 8 | **Çavdar Ekmeği** | 2 Çavdar Unu, Beyaz Un, Su, Tuz, Aktif Ekşi Maya | Uzun mayalanma |
| 9 | **Brioche** | 2 Beyaz Un, 2 Yumurta, Tereyağı, Süt, Şeker, Maya, Tuz | |
| 10 | **Challah** | 2 Beyaz Un, 2 Yumurta, Bal, Zeytinyağı, Su, Maya, Tuz | |
| 11 | **Pita** (×2) | 2 Beyaz Un, Su, Tuz, Maya, Zeytinyağı | |
| 12 | **Naan** (×2) | 2 Beyaz Un, Yoğurt, Tereyağı, Tuz, Maya | |
| 13 | **Lavaş** (×3) | 2 Beyaz Un, Su, Tuz | Mayasız, doğrudan fırına |
| 14 | **Simit** (×2) | 2 Beyaz Un, Su, Tuz, Maya, Şeker | Mayalandıktan sonra `+ Pekmez + Susam` → Çiğ Simit |
| 15 | **Ramazan Pidesi** | 2 Beyaz Un, Su, Tuz, Maya, Yumurta, Susam | |
| 16 | **Mısır Ekmeği** | 2 Mısır Unu, Beyaz Un, Yumurta, Süt, Tereyağı, Kabartma Tozu, Tuz, Şeker | Mayasız, kabartma tozuyla |
| 17 | **Bagel** (×2) | 2 Beyaz Un, Su, Tuz, Maya, Bal | Mayalandıktan sonra **kaynayan suda haşla** |

### Kaynayan kazan

Altında ateş, kamp ateşi, lav veya magma bloğu olan kazan "kaynar":

- **Bagel haşlama:** Su dolu kazana mayalanmış bagel hamuruyla sağ tıkla → Haşlanmış Bagel.
- **Pekmez:** Kazana (dolu ya da boş) Meyve Şırasıyla sağ tıkla → şıra kaynayıp Pekmez olur.
- **Tuz:** Su dolu kazana boş elle sağ tıkla → su bir seviye buharlaşır, 1 Tuz kalır.

Kazan kaynamıyorsa uyarı verir.

## Malzemeler nereden gelir?

| Malzeme | Nasıl elde edilir |
|---------|-------------------|
| Çavdar / Mısır / Susam tohumu | Çimen, uzun çimen ve eğrelti otu kırınca düşer. Tarlaya ekilir, köylüler de eker |
| Zeytin | Meşe ve koyu meşe yapraklarından düşer |
| Tuz | Altında ateş olan su kazanına boş elle sağ tıkla (su buharlaşır; dolu kazan = 3 tuz) |
| Maya | Tatlı Meyve + Şeker + Tam Buğday Unu + Su (×4) — meyve kabuğundaki yabani maya |
| Kabartma Tozu | Tuz + Kalsit (×2) |
| Tereyağı | Süt Kovası → 2 Tereyağı |
| Yoğurt | Süt Kovası + 3 Kase → 3 Yoğurt |
| Zeytinyağı | 4 Zeytin + Cam Şişe + El Değirmeni |
| Pekmez | 4 Tatlı Meyve + Cam Şişe → Meyve Şırası, kaynayan kazanda kaynat → Pekmez |
| Çavdar / Mısır Unu | Çavdar veya Mısır + El Değirmeni |

## 🥚 Easter egg'ler

- **Ekmek nimettir** – Yere düşmüş bir ekmeği yerden aldığında karakterin onu öpüp alnına koyar:
  kalpler çıkar ve 1 dakikalık **Şans** etkisi kazanırsın. Sohbete `ekmek nimettir` yazarsan herkese
  hatırlatılır; elinde ekmek varsa kısa bir **Can Yenileme** de alırsın.
- **Ekmek çarpar** – Ekmeği ateşe veya lava atarsan tam üstüne (zararsız) bir şimşek çakar!
- **Ekmek köper** – Mayalanmış hamuru teknede çok uzun unutursan hamur köpürüp tekneden taşar.
  Sohbete `ekmek köper` yazarsan yakındaki (8 blok) teknelerde mayalanan hamurlar coşar ve biraz
  daha hızlı kabarır (dakikada bir kez).

## Bloklar ve aletler

| Tarif | Desen |
|-------|-------|
| **Taş Fırın** | Üst: 3 Taş Tuğla · Orta: Tuğla, boş, Tuğla · Alt: Taş Tuğla, **Kamp Ateşi**, Taş Tuğla |
| **Mayalama Teknesi** | Üst: Kalas, boş, Kalas · Alt: Kalas, **Yün** (örtü bezi), Kalas |
| **El Değirmeni** | Dikey: Çubuk, Düz Taş Basamak, Düz Taş Basamak |
| **Elek** | Ortada İp, dört yanda Çubuk |

## Derleme (geliştiriciler için)

Java 25 gerekir.

```bash
./gradlew build              # varsayılan: Minecraft 26.1.x
./gradlew build -Pmc=26.2    # Minecraft 26.2
./gradlew build -Pmc=26.3    # Minecraft 26.3
./gradlew runClientGameTest  # uçtan uca oyun testi + ekran görüntüleri
```

Jar dosyaları `build/libs/` altına çıkar. Sürüm ayarları `versions/<sürüm>.properties` dosyalarındadır.

Proje yapısı:

| Klasör | İçerik |
|--------|--------|
| `src/main` | Tüm sürümlerde ortak kod, dokular, modeller, tarifler, dil dosyaları |
| `src/mc26.1`, `src/mc26.2`, `src/mc26.3` | Sürüme özel uyumluluk katmanı (`compat` paketi) ve loot tabloları (26.3'te loot formatı değişti) |
| `src/gametest` | Fırın, tekne, ekin loot'u ve easter egg'leri doğrulayan, ekran görüntüsü alan istemci testi |
| `tools` | Doku ve veri üreten Python betikleri |

Dokular ve JSON verileri betiklerle üretilir:

```bash
python3 tools/gen_textures.py   # 16x16 piksel dokular (Pillow gerekir)
python3 tools/gen_data.py       # modeller, tarifler, loot tabloları, dil dosyaları
```

## Lisans

MIT
