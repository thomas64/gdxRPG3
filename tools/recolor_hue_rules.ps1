# Recolors parts of a combatant by rules of hue, and leaves all other pixels exactly as they are.
#
# A rule is "from-to>hue" with optional extras, separated by ";":
#   from-to      the hues in degrees that change, like "230-300". It may go over 360, like "340-20".
#   >hue         the new hue. The shades within the range are kept around it, at half their spread.
#   *sat,val     multipliers for saturation and brightness, like "*1.1,1.2". Default "*1,1".
#   %sat         only pixels with at least this saturation, like "%0.3". Default 0.12, below that a pixel is grey.
#   @x,y,w,h     only within this rectangle on the combatant.
#   !x,y,w,h     not within this rectangle. This one may be given more than once.
#                Instead of a rectangle, a polygon of 3 or more points can be given: "x1,y1,x2,y2,x3,y3,...".
#                A point may lie outside the combatant, like -1, so that a polygon also takes its edge.
#   grey>hue+sat a rule for grey pixels (no real hue), like "grey>190+0.3": they get that hue and that much
#                saturation on top of their own. It can have @ and ! too, but no * or %.
# The first rule that fits a pixel is used. Grey pixels only change by a grey rule.
#
# With -over, the rules are used as a repair on a combatant that is already recolored, by another tool:
# the pixels that a rule fits are recolored from -src, the original, and all other pixels are taken from -over.
# So the rules choose by the colors of the original, also when the recolored combatant has lost the difference.
#
# Example, ardor from MV's Mage: a green symbol on his hood, a brown cape and a purple shirt.
#   powershell -File tools/recolor_hue_rules.ps1 -src <MV>/img/sv_enemies/Mage.png -out assets/sprites/combatants/ardor.png
#              -rules "340-20>110*0.9,0.6@117,57,30,16; 280-360>22*1.07,1@131,69,11,7; 280-350>22*1.07,1@127,73,13,23;
#                      300-340>22*1.07,1@111,51,44,32; 280-350>22*1.07,1@100,51,27,62;
#                      230-300>22*1.07,1@0,51,181,172!9,51,38,48;
#                      350-15>269%0.3@123,107,14,14; 350-15>269%0.3@137,112,7,11;
#                      300-350>269%0.3@0,51,149,172!9,51,38,48!98,106,120,106,120,124,126,130,126,146,120,146,100,134,98,132"
#   1: the red symbol on his hood becomes dark green like in his face, without his red eye just below it.
#   2: the pink tip of his hood, between that symbol and his face, becomes brown.
#   3: the dark right edge of his hood, next to his face, becomes brown.
#   4: the purple shade around that symbol becomes the brown of his hood.
#   5: his hood down to its point over his chest, pink shine included, becomes brown.
#   6: his cape becomes brown, but not the ribbons of his staff.
#   7 and 8: the red shine of the fire on his elbow and the end of his sleeve becomes purple.
#   9: his shirt becomes purple, but not his grey trousers (low saturation), the ribbons, the fire from x 149 and
#      his chest pattern. The chest pattern has the same reds as the shirt, so it is left out by a polygon that
#      follows his slanted collar.
#   The top 51 pixels (the gem and the flames) are left out of every rule.
#   Then put it on combatants/warlocks.png with tools/build_enemy_sheet.ps1 -source warlocks.
#
# Example, green_imp from MV's Imp: red wings and a blue-green body, like its walking sprite.
#   powershell -File tools/recolor_hue_rules.ps1 -src <MV>/img/sv_enemies/Imp.png -out assets/sprites/combatants/green_imp.png
#              -rules "180-240>355%0.45@-1,-1,95,-1,95,7,71,27,61,37,55,45,47,39,41,33,29,37,5,45,-1,39;
#                      grey>192+0.3; 180-320>192*1.3,1.1"
#   1: the bright blue skin of the wings, within the polygon around the wings, becomes red.
#   2 and 3: the grey and blue body, tail and wing bones become blue-green.
#   Then put it on combatants/imps.png with tools/build_enemy_sheet.ps1 -source imps.
#
# Example, brown_bandit from MV's Swordsman: only his black hair becomes brown, like his walking sprite.
#   powershell -File tools/recolor_hue_rules.ps1 -src <MV>/img/sv_enemies/Swordsman.png -out assets/sprites/combatants/brown_bandit.png
#              -rules "grey>22+0.5@105,11,112,1,124,-1,139,-1,143,5,141,21,136,23,131,19,124,18,117,19,115,25,115,35,109,35,105,25;
#                      180-330>22@105,11,112,1,124,-1,139,-1,143,5,141,21,136,23,131,19,124,18,117,19,115,25,115,35,109,35,105,25"
#   The polygon is his hair, without his face. His hair is grey and a bit blue, his skin is left out by its hue.
#   Then put it on combatants/bandits1.png with tools/build_enemy_sheet.ps1 -source bandits1.
#   On that sheet his sword is removed by hand afterwards, so this example still gives him his sword.
#
# Example, the red mouth of blue_bat, after tools/recolor_by_sprite_sheet.ps1 made it:
#   powershell -File tools/recolor_hue_rules.ps1 -src <MV>/img/sv_enemies/Bat.png
#              -over assets/sprites/combatants/blue_bat.png -out assets/sprites/combatants/blue_bat.png -rules "290-10>0@88,99,14,10"
#   In MV's bat the mouth has the same pink as the fur below its head, so the mouth got the color of that fur.
#   The rule takes the pink mouth of the original within the rectangle, and makes it red like in its face.
#   The same for purple_bat and green_bat.
param(
    [Parameter(Mandatory)][string]$src,
    [Parameter(Mandatory)][string]$out,
    [Parameter(Mandatory)][string]$rules,
    [string]$over = ''
)

Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @"
using System; using System.Drawing; using System.Drawing.Drawing2D;

// a rectangle or a polygon on the combatant.
public class Area {
  readonly GraphicsPath path = new GraphicsPath();

  public Area(int[] numbers) {
    if (numbers.Length == 4) {
      path.AddRectangle(new Rectangle(numbers[0], numbers[1], numbers[2], numbers[3]));
    } else {
      PointF[] points = new PointF[numbers.Length / 2];
      for (int i = 0; i < points.Length; i++) points[i] = new PointF(numbers[i * 2], numbers[i * 2 + 1]);
      path.AddPolygon(points);
    }
  }

  public bool Contains(int x, int y) { return path.IsVisible(x, y); }
}

public class HueRule {
  public const double GREY_SATURATION = 0.12;
  public double From, To, Hue, SatMul = 1, ValMul = 1, MinSat = GREY_SATURATION, GreySatAdd;
  public bool ForGrey;
  public Area Inside;
  public Area[] Outside = new Area[0];

  public bool Fits(int x, int y, double hue, double sat) {
    if (ForGrey) {
      if (sat >= GREY_SATURATION) return false;
    } else {
      bool inRange = From <= To ? hue >= From && hue <= To : hue >= From || hue <= To;
      if (!inRange || sat < MinSat) return false;
    }
    if (Inside != null && !Inside.Contains(x, y)) return false;
    foreach (Area area in Outside) if (area.Contains(x, y)) return false;
    return true;
  }

  public double Center() {
    double width = From <= To ? To - From : To + 360 - From;
    return (From + width / 2) % 360;
  }
}

public static class HueRules {

  // the shades of a range are kept, but pulled together around the new hue.
  const double SPREAD = 0.5;

  // without a fitting rule, a pixel stays as it is in src, or is taken from over when that is given.
  public static Bitmap Apply(Bitmap src, HueRule[] rules, Bitmap over) {
    Bitmap o = new Bitmap(src.Width, src.Height);
    for (int y = 0; y < src.Height; y++) for (int x = 0; x < src.Width; x++) {
      Color c = src.GetPixel(x, y);
      double h, s, v; ToHsv(c, out h, out s, out v);
      // an invisible pixel has a color too, but it stays as it is.
      HueRule rule = c.A == 0 ? null : FirstFitting(rules, x, y, h, s);
      if (rule == null && over != null) {
        c = over.GetPixel(x, y);
      } else if (rule != null && rule.ForGrey) {
        c = FromHsv(c.A, rule.Hue, Math.Min(1, s + rule.GreySatAdd), v);
      } else if (rule != null) {
        double offset = h - rule.Center(); if (offset > 180) offset -= 360; if (offset < -180) offset += 360;
        h = Wrap(rule.Hue + offset * SPREAD); s = Math.Min(1, s * rule.SatMul); v = Math.Min(1, v * rule.ValMul);
        c = FromHsv(c.A, h, s, v);
      }
      o.SetPixel(x, y, c);
    }
    return o;
  }

  static HueRule FirstFitting(HueRule[] rules, int x, int y, double hue, double sat) {
    foreach (HueRule rule in rules) if (rule.Fits(x, y, hue, sat)) return rule;
    return null;
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

function ConvertTo-Area([string]$text) {
    New-Object Area -ArgumentList (, [int[]]($text -split ','))
}

$parsedRules = [HueRule[]]@($rules -split ';' | ForEach-Object { $_.Trim() } | Where-Object { $_ } | ForEach-Object {
    if ($_ -match '^grey>(\d+)\+([\d.]+)(@([-\d,]+))?((![-\d,]+)*)$') {
        $rule = New-Object HueRule
        $rule.ForGrey = $true; $rule.Hue = [double]$Matches[1]; $rule.GreySatAdd = [double]$Matches[2]
        if ($Matches[3]) { $rule.Inside = ConvertTo-Area $Matches[4] }
        if ($Matches[5]) { $rule.Outside = [Area[]]@($Matches[5] -split '!' | Where-Object { $_ } | ForEach-Object { ConvertTo-Area $_ }) }
        return $rule
    }
    if ($_ -notmatch '^(\d+)-(\d+)>(\d+)(\*([\d.]+),([\d.]+))?(%([\d.]+))?(@([-\d,]+))?((![-\d,]+)*)$') { throw "rule not understood: $_" }
    $rule = New-Object HueRule
    $rule.From = [double]$Matches[1]; $rule.To = [double]$Matches[2]; $rule.Hue = [double]$Matches[3]
    if ($Matches[4]) { $rule.SatMul = [double]$Matches[5]; $rule.ValMul = [double]$Matches[6] }
    if ($Matches[7]) { $rule.MinSat = [double]$Matches[8] }
    if ($Matches[9]) { $rule.Inside = ConvertTo-Area $Matches[10] }
    if ($Matches[11]) { $rule.Outside = [Area[]]@($Matches[11] -split '!' | Where-Object { $_ } | ForEach-Object { ConvertTo-Area $_ }) }
    $rule
})

# the images are read by their bytes, so -over can be the same file as -out.
function Read-Image([string]$path) {
    New-Object System.Drawing.Bitmap (New-Object System.IO.MemoryStream (, [System.IO.File]::ReadAllBytes((Resolve-Path $path).Path)))
}

$sourceImage = Read-Image $src
$overImage = if ($over) { Read-Image $over } else { $null }
if ($overImage -and ($overImage.Width -ne $sourceImage.Width -or $overImage.Height -ne $sourceImage.Height)) {
    throw "-over must have the size of -src"
}
$result = [HueRules]::Apply($sourceImage, $parsedRules, $overImage)
$sourceImage.Dispose()
if ($overImage) { $overImage.Dispose() }
$result.Save([IO.Path]::GetFullPath($out), [System.Drawing.Imaging.ImageFormat]::Png)
$result.Dispose()
