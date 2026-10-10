# Makes a color variant of an enemy combatant, by the color variants of its walking sprites.
# MV walking sprite sheets have the same drawing in 8 colors. For every hue of the source variant, it is measured
# what color the same pixels have in the target variant. That change of hue, saturation and brightness is put on the
# combatant. So every part (fur, wing edge, wing skin) gets its own target color, and a part that is the same in
# every variant (like clothes), stays as it is.
#
# -minShare is the part of the sprite that a hue must cover to count (default 0.03). A small detail on the sheet,
# like the red mouth of a bat, can otherwise pull a big part of the combatant along. But when a small part really
# differs per variant, like the trousers of an orc, use 0.
# -keepHues are ranges of hue in degrees that stay as they are on the combatant, like "0-70" for a fire spell
# that the walking sprite does not have. "grey" keeps the grey and white pixels, like a silver staff.
# -keepAreas are rectangles "x,y,w,h" on the combatant that stay as they are, separated by ";". For when a part
# that must stay (a red gem) has the same hue as a part that must change (a red symbol).
# -ignoreFixed leaves pixels that have the same color in all 8 variants out of the measuring. Use it when such a part
# (like the boots of an orc) has the same hue as a part that does differ per variant (like its trousers).
#
# What works per enemy, from MV's sv_enemies, all with -sourceCol 1 -sourceRow 1:
#   bats  (Bat)        default, and then the red mouth with tools/recolor_hue_rules.ps1, see its example.
#   orcs  (Orc)        -minShare 0 -ignoreFixed
#   imps  (Imp)        not with this tool, its wings stay blue. Use tools/recolor_hue_rules.ps1, see its example.
#   ardor (Mage)       not with this tool, but with tools/recolor_hue_rules.ps1, see its example.
#   bandit (Swordsman) not with this tool: his black hair and grey vest are both grey, so the vest and his face get
#                      the hair color too. Use tools/recolor_hue_rules.ps1, see its example.
#
# A block is the place of a variant on the sheet: 3 frames x 4 directions, with col 1-4 and row 1-2,
# the same as "col" and "row" in configs/sprites.
#
# Example, the blue bat from MV's bat, which looks like the variant at col 1, row 1:
#   powershell -File tools/recolor_by_sprite_sheet.ps1 -src <MV>/img/sv_enemies/Bat.png
#              -sheet assets/sprites/characters/bats.png -sourceCol 1 -sourceRow 1 -targetCol 2 -targetRow 2
#              -out assets/sprites/combatants/blue_bat.png
#   Then its red mouth (see tools/recolor_hue_rules.ps1), and put it on combatants/bats.png with
#   tools/build_enemy_sheet.ps1 -source bats.
param(
    [Parameter(Mandatory)][string]$src,
    [Parameter(Mandatory)][string]$sheet,
    [Parameter(Mandatory)][int]$sourceCol,
    [Parameter(Mandatory)][int]$sourceRow,
    [Parameter(Mandatory)][int]$targetCol,
    [Parameter(Mandatory)][int]$targetRow,
    [Parameter(Mandatory)][string]$out,
    [double]$minShare = 0.03,
    [string[]]$keepHues = @(),
    [string]$keepAreas = '',
    [switch]$ignoreFixed
)

Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @"
using System; using System.Drawing;
public static class RecolorBySheet {

  // a walking sprite sheet has 4 x 2 blocks of 3 frames of 48 wide and 4 directions of 48 high.
  const int BLOCK_WIDTH = 144, BLOCK_HEIGHT = 192, BLOCK_COLS = 4, BLOCK_ROWS = 2;
  // a pixel is fixed when its hue differs less than this between all variants.
  const double FIXED_HUE_DIFFERENCE = 15;
  // the hues are measured in bins of 10 degrees. pixels below this saturation are grey, with their own bin.
  const int BIN_SIZE = 10, BINS = 36, GREY = 36;
  const double GREY_SATURATION = 0.15;
  // a hue of the combatant that the sheet does not have, takes a measured hue this many bins away at most.
  // a walking sprite has only a few hues, the combatant has many shades around them.
  const int MAX_BIN_DISTANCE = 4;

  public static Bitmap Apply(Bitmap combatant, Bitmap sheet, int sourceCol, int sourceRow, int targetCol, int targetRow,
                             double minShare, double[] keepFrom, double[] keepTo, bool keepGrey, Rectangle[] keepAreas,
                             bool ignoreFixed) {
    int sourceX = sourceCol * BLOCK_WIDTH, sourceY = sourceRow * BLOCK_HEIGHT;
    int targetX = targetCol * BLOCK_WIDTH, targetY = targetRow * BLOCK_HEIGHT;
    double[] shiftX = new double[BINS + 1], shiftY = new double[BINS + 1], satRatio = new double[BINS + 1], valRatio = new double[BINS + 1];
    int[] count = new int[BINS + 1];
    double greyHueX = 0, greyHueY = 0, greyTargetSat = 0;

    for (int y = 0; y < BLOCK_HEIGHT; y++) for (int x = 0; x < BLOCK_WIDTH; x++) {
      Color s = sheet.GetPixel(sourceX + x, sourceY + y), t = sheet.GetPixel(targetX + x, targetY + y);
      if (s.A < 128 || t.A < 128 || (ignoreFixed && IsFixed(sheet, x, y))) continue;
      double sh, ss, sv, th, ts, tv; ToHsv(s, out sh, out ss, out sv); ToHsv(t, out th, out ts, out tv);
      if (sv < 0.05) continue;
      int bin = BinOf(sh, ss);
      double delta = (th - sh) * Math.PI / 180;
      shiftX[bin] += Math.Cos(delta); shiftY[bin] += Math.Sin(delta);
      satRatio[bin] += ss > 0.01 ? ts / ss : 1; valRatio[bin] += tv / sv; count[bin]++;
      if (bin == GREY) { greyHueX += Math.Cos(th * Math.PI / 180); greyHueY += Math.Sin(th * Math.PI / 180); greyTargetSat += ts; }
    }
    // a hue that is only a small detail on the sheet, like a mouth, says too little. it counts as not measured,
    // so the combatant takes a nearby hue there, that is a real part of the sprite.
    int total = 0;
    for (int i = 0; i <= BINS; i++) total += count[i];
    for (int i = 0; i <= BINS; i++) if (count[i] < total * minShare) count[i] = 0;

    double[] hueShift = new double[BINS + 1];
    for (int i = 0; i <= BINS; i++) {
      if (count[i] == 0) continue;
      hueShift[i] = Math.Atan2(shiftY[i], shiftX[i]) * 180 / Math.PI; satRatio[i] /= count[i]; valRatio[i] /= count[i];
    }
    double greyHue = Wrap(Math.Atan2(greyHueY, greyHueX) * 180 / Math.PI);
    if (count[GREY] > 0) greyTargetSat /= count[GREY];

    Bitmap o = new Bitmap(combatant.Width, combatant.Height);
    for (int y = 0; y < combatant.Height; y++) for (int x = 0; x < combatant.Width; x++) {
      Color c = combatant.GetPixel(x, y); double hue, sat, val; ToHsv(c, out hue, out sat, out val);
      int bin = NearestMeasuredBin(BinOf(hue, sat), count);
      if (bin < 0 || IsKept(hue, sat, keepFrom, keepTo, keepGrey) || IsInArea(x, y, keepAreas)) { o.SetPixel(x, y, c); continue; }
      if (bin == GREY) {
        // a grey pixel takes the hue of the target greys, and a bit of their color.
        hue = greyHue; sat = Math.Min(1, sat + greyTargetSat * (1 - sat));
      } else {
        hue = Wrap(hue + hueShift[bin]); sat = Math.Min(1, sat * satRatio[bin]);
      }
      val = Math.Min(1, val * valRatio[bin]);
      o.SetPixel(x, y, FromHsv(c.A, hue, sat, val));
    }
    return o;
  }

  // the same pixel in all variants, with nearly the same hue everywhere.
  static bool IsFixed(Bitmap sheet, int x, int y) {
    double firstHue = -1;
    for (int row = 0; row < BLOCK_ROWS; row++) for (int col = 0; col < BLOCK_COLS; col++) {
      Color c = sheet.GetPixel(col * BLOCK_WIDTH + x, row * BLOCK_HEIGHT + y);
      double h, s, v; ToHsv(c, out h, out s, out v);
      if (s < GREY_SATURATION) return false;
      if (firstHue < 0) { firstHue = h; continue; }
      double d = Math.Abs(h - firstHue); if (Math.Min(d, 360 - d) > FIXED_HUE_DIFFERENCE) return false;
    }
    return true;
  }

  static bool IsKept(double hue, double sat, double[] keepFrom, double[] keepTo, bool keepGrey) {
    if (sat < GREY_SATURATION) return keepGrey;
    for (int i = 0; i < keepFrom.Length; i++) if (hue >= keepFrom[i] && hue <= keepTo[i]) return true;
    return false;
  }

  static bool IsInArea(int x, int y, Rectangle[] areas) {
    foreach (Rectangle area in areas) if (area.Contains(x, y)) return true;
    return false;
  }

  static int BinOf(double hue, double sat) { return sat < GREY_SATURATION ? GREY : ((int)(hue / BIN_SIZE)) % BINS; }

  static int NearestMeasuredBin(int bin, int[] count) {
    if (count[bin] > 0) return bin;
    if (bin == GREY) return -1;
    for (int d = 1; d <= MAX_BIN_DISTANCE; d++) {
      if (count[(bin + d) % BINS] > 0) return (bin + d) % BINS;
      if (count[(bin - d + BINS) % BINS] > 0) return (bin - d + BINS) % BINS;
    }
    return -1;
  }

  static double Wrap(double h) { return ((h % 360) + 360) % 360; }

  static void ToHsv(Color c, out double h, out double s, out double v) {
    double r = c.R / 255.0, g = c.G / 255.0, b = c.B / 255.0;
    double max = Math.Max(r, Math.Max(g, b)), min = Math.Min(r, Math.Min(g, b)), d = max - min;
    v = max; s = max == 0 ? 0 : d / max; h = 0;
    if (d > 0) { if (max == r) h = 60 * (((g - b) / d) % 6); else if (max == g) h = 60 * ((b - r) / d + 2); else h = 60 * ((r - g) / d + 4); }
    h = Wrap(h);
  }

  static Color FromHsv(int a, double h, double s, double v) {
    double c = v * s, x = c * (1 - Math.Abs((h / 60) % 2 - 1)), m = v - c, r = 0, g = 0, b = 0;
    if (h < 60) { r = c; g = x; } else if (h < 120) { r = x; g = c; } else if (h < 180) { g = c; b = x; }
    else if (h < 240) { g = x; b = c; } else if (h < 300) { r = x; b = c; } else { r = c; b = x; }
    return Color.FromArgb(a, (int)Math.Round((r + m) * 255), (int)Math.Round((g + m) * 255), (int)Math.Round((b + m) * 255));
  }
}
"@

$combatantImage = New-Object System.Drawing.Bitmap (Resolve-Path $src).Path
$sheetImage = New-Object System.Drawing.Bitmap (Resolve-Path $sheet).Path
# powershell -File passes "0-70,grey" as one text, so it is split here.
$keepHues = @($keepHues | ForEach-Object { $_ -split ',' } | Where-Object { $_ })
$keepRanges = @($keepHues | Where-Object { $_ -ne 'grey' })
$keepFrom = [double[]]@($keepRanges | ForEach-Object { [double]($_ -split '-')[0] })
$keepTo = [double[]]@($keepRanges | ForEach-Object { [double]($_ -split '-')[1] })
$areas = [System.Drawing.Rectangle[]]@($keepAreas -split ';' | Where-Object { $_ } | ForEach-Object {
    $n = [int[]]($_ -split ','); New-Object System.Drawing.Rectangle $n[0], $n[1], $n[2], $n[3] })
$result = [RecolorBySheet]::Apply($combatantImage, $sheetImage, $sourceCol - 1, $sourceRow - 1, $targetCol - 1, $targetRow - 1,
                                  $minShare, $keepFrom, $keepTo, ($keepHues -contains 'grey'), $areas, $ignoreFixed.IsPresent)
$combatantImage.Dispose(); $sheetImage.Dispose()
$result.Save([IO.Path]::GetFullPath($out), [System.Drawing.Imaging.ImageFormat]::Png)
$result.Dispose()
