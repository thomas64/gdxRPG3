# Makes a color variant of an enemy combatant, to match its face in a faces sheet.
# The body gets the hue, saturation and brightness of the face, the pupil gets the pupil hue of the face.
# Made for the MV slime: a blue body and a magenta pupil. Other enemies may need other hue ranges below.
#
# Example, the green slime from the blue one (MV's sv_enemies/Slime.png), with face 2 of faces/slimes.png:
#   powershell -File tools/recolor_combatant.ps1 -src <MV>/img/sv_enemies/Slime.png
#              -faces assets/sprites/faces/slimes.png -faceIndex 2 -out assets/sprites/combatants/green_slime.png
#   Then put it on combatants/slimes.png with tools/build_enemy_sheet.ps1 -source slimes.
param(
    [Parameter(Mandatory)][string]$src,
    [Parameter(Mandatory)][string]$faces,
    [Parameter(Mandatory)][int]$faceIndex,
    [Parameter(Mandatory)][string]$out
)

# a faces sheet has 4 faces of 144x144 next to each other, on 2 rows.
$faceSize = 144
$facesPerRow = 4

Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @"
using System; using System.Drawing;
public static class Recolor {

  // the body of the source: everything blue to cyan. the white of the eye and the pupil fall outside of it.
  const double BODY_HUE_MIN = 150, BODY_HUE_MAX = 260, BODY_SATURATION_MIN = 0.12;
  // the pupil of the source: magenta around 330, including its dark purple edge.
  const double PUPIL_HUE = 330, PUPIL_SATURATION_MIN = 0.1;
  // the hues of the body are pulled together around the target hue, so it does not spread into other colors.
  const double BODY_HUE_SPREAD = 0.5;

  // the average hue, saturation and value of the colored pixels within a rectangle.
  public static double[] Stats(Bitmap bmp, int x0, int y0, int w, int h) {
    double sx = 0, sy = 0, ss = 0, sv = 0; int n = 0;
    for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) {
      Color c = bmp.GetPixel(x, y); if (c.A < 128) continue;
      double hue, s, v; ToHsv(c, out hue, out s, out v); if (s < 0.3 || v < 0.2) continue;
      sx += Math.Cos(hue * Math.PI / 180); sy += Math.Sin(hue * Math.PI / 180); ss += s; sv += v; n++;
    }
    return new double[] { AngleOf(sx, sy), ss / n, sv / n };
  }

  // the average hue of the pixels in the eye area of a face, that differ from the body.
  public static double PupilHue(Bitmap bmp, int x0, int y0, int w, int h, double bodyHue) {
    double sx = 0, sy = 0;
    for (int y = y0; y < y0 + h; y++) for (int x = x0; x < x0 + w; x++) {
      Color c = bmp.GetPixel(x, y); if (c.A < 128) continue;
      double hue, s, v; ToHsv(c, out hue, out s, out v); if (s < 0.3 || v < 0.3) continue;
      double d = Math.Abs(hue - bodyHue); d = Math.Min(d, 360 - d); if (d < 50) continue;
      sx += Math.Cos(hue * Math.PI / 180); sy += Math.Sin(hue * Math.PI / 180);
    }
    return AngleOf(sx, sy);
  }

  public static Bitmap Apply(Bitmap bmp, double[] source, double[] target, double pupilTarget) {
    Bitmap o = new Bitmap(bmp.Width, bmp.Height);
    for (int y = 0; y < bmp.Height; y++) for (int x = 0; x < bmp.Width; x++) {
      Color c = bmp.GetPixel(x, y); double h, s, v; ToHsv(c, out h, out s, out v);
      bool isBody = s > BODY_SATURATION_MIN && h > BODY_HUE_MIN && h < BODY_HUE_MAX;
      bool isPupil = !isBody && s > PUPIL_SATURATION_MIN && (h > BODY_HUE_MAX || h < 20);
      if (isBody) {
        h = Wrap(target[0] + (h - source[0]) * BODY_HUE_SPREAD);
        s = Math.Min(1, s * target[1] / source[1]);
        v = Math.Min(1, v * target[2] / source[2]);
      } else if (isPupil) {
        h = Wrap(pupilTarget + ((h < 180 ? h + 360 : h) - PUPIL_HUE));
      }
      o.SetPixel(x, y, FromHsv(c.A, h, s, v));
    }
    return o;
  }

  static double AngleOf(double sx, double sy) { return Wrap(Math.Atan2(sy, sx) * 180 / Math.PI); }
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

$sourceImage = New-Object System.Drawing.Bitmap (Resolve-Path $src).Path
$facesImage = New-Object System.Drawing.Bitmap (Resolve-Path $faces).Path
$faceX = ($faceIndex % $facesPerRow) * $faceSize
$faceY = [Math]::Floor($faceIndex / $facesPerRow) * $faceSize

$source = [Recolor]::Stats($sourceImage, 0, 0, $sourceImage.Width, $sourceImage.Height)
$target = [Recolor]::Stats($facesImage, $faceX, $faceY, $faceSize, $faceSize)
# the eye is at the top of a face, a bit right of the middle.
$pupilTarget = [Recolor]::PupilHue($facesImage, $faceX + 40, $faceY, 64, 40, $target[0])

$result = [Recolor]::Apply($sourceImage, $source, $target, $pupilTarget)
$sourceImage.Dispose(); $facesImage.Dispose()
$result.Save([IO.Path]::GetFullPath($out), [System.Drawing.Imaging.ImageFormat]::Png)
$result.Dispose()
