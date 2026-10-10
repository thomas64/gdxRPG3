# Removes an item that a combatant holds, like the pickaxe of MV's orc, and leaves the hand that holds it.
# 1. Everything within the -erase rectangles becomes transparent, except within the -keep rectangles (the hand).
# 2. Within the -keep rectangles, the pixels in the colors of the item are taken away: where the item went through
#    the hand, they get the color of the hand just above or below it, and where it stuck out, they become transparent.
#
# Rectangles are "x,y,w,h", separated by ";". With -cellWidth and -cellHeight the image is a sheet, and the
# rectangles count for every cell on it, like on combatants/orcs.png. The image is changed in place.
# The colors of the item are measured in one cell, -maskCol and -maskRow (default 1,1). Its color variants have the
# same shape, so the same pixels are the item in every cell, also when the item has another color there.
#
# Example, the pickaxes of all orcs:
#   powershell -File tools/remove_held_item.ps1 -image assets/sprites/combatants/orcs.png -cellWidth 264 -cellHeight 286
#              -erase "75,0,180,58;80,58,20,32" -keep "160,30,32,30" -itemHues "0-50,345-360"
#   The pickaxe hangs against the background, only the right hand holds it. In the green orc at 1,1 its wood is
#   orange to dark red-brown.
param(
    [Parameter(Mandatory)][string]$image,
    [Parameter(Mandatory)][string]$erase,
    [Parameter(Mandatory)][string]$keep,
    [Parameter(Mandatory)][string]$itemHues,
    [int]$cellWidth = 0,
    [int]$cellHeight = 0,
    [int]$maskCol = 1,
    [int]$maskRow = 1,
    [double]$minSaturation = 0.35
)

Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @"
using System; using System.Drawing; using System.Collections.Generic;

public static class HeldItem {

  // how far above and below an item pixel the hand is looked for.
  const int SEARCH_DISTANCE = 7;

  public static void Remove(Bitmap bmp, int cellWidth, int cellHeight, int maskCellX, int maskCellY,
                            Rectangle[] erase, Rectangle[] keep, double[] hueFrom, double[] hueTo, double minSaturation) {
    HashSet<Point> mask = FindItemInHands(bmp, maskCellX, maskCellY, keep, hueFrom, hueTo, minSaturation);
    for (int cellY = 0; cellY < bmp.Height; cellY += cellHeight) for (int cellX = 0; cellX < bmp.Width; cellX += cellWidth) {
      EraseCell(bmp, cellX, cellY, erase, keep);
      CleanHands(bmp, cellX, cellY, mask);
    }
  }

  // the pixels of the item within the hands, relative to a cell.
  static HashSet<Point> FindItemInHands(Bitmap bmp, int cellX, int cellY, Rectangle[] keep,
                                        double[] hueFrom, double[] hueTo, double minSaturation) {
    HashSet<Point> mask = new HashSet<Point>();
    foreach (Rectangle area in keep) for (int y = area.Top; y < area.Bottom; y++) for (int x = area.Left; x < area.Right; x++) {
      if (IsItem(bmp.GetPixel(cellX + x, cellY + y), hueFrom, hueTo, minSaturation)) mask.Add(new Point(x, y));
    }
    return mask;
  }

  static void EraseCell(Bitmap bmp, int cellX, int cellY, Rectangle[] erase, Rectangle[] keep) {
    foreach (Rectangle area in erase) for (int y = area.Top; y < area.Bottom; y++) for (int x = area.Left; x < area.Right; x++) {
      if (IsIn(keep, x, y)) continue;
      bmp.SetPixel(cellX + x, cellY + y, Color.FromArgb(0, 0, 0, 0));
    }
  }

  static void CleanHands(Bitmap bmp, int cellX, int cellY, HashSet<Point> mask) {
    // first all new colors, then setting them, so a filled pixel is not taken as hand by the next one.
    Dictionary<Point, Color> result = new Dictionary<Point, Color>();
    foreach (Point p in mask) {
      Color? above = FindHand(bmp, cellX, cellY, p, -1, mask);
      Color? below = FindHand(bmp, cellX, cellY, p, 1, mask);
      // taking turns between above and below keeps the shading of the hand.
      result[p] = above.HasValue && below.HasValue ? (p.Y % 2 == 1 ? above.Value : below.Value) : Color.FromArgb(0, 0, 0, 0);
    }
    foreach (KeyValuePair<Point, Color> entry in result) bmp.SetPixel(cellX + entry.Key.X, cellY + entry.Key.Y, entry.Value);
  }

  static Color? FindHand(Bitmap bmp, int cellX, int cellY, Point p, int step, HashSet<Point> mask) {
    for (int d = 1; d <= SEARCH_DISTANCE; d++) {
      Point next = new Point(p.X, p.Y + d * step);
      if (mask.Contains(next)) continue;
      if (cellY + next.Y < 0 || cellY + next.Y >= bmp.Height) return null;
      Color c = bmp.GetPixel(cellX + next.X, cellY + next.Y);
      if (c.A >= 200) return c;
    }
    return null;
  }

  static bool IsItem(Color c, double[] hueFrom, double[] hueTo, double minSaturation) {
    if (c.A < 20) return false;
    int max = Math.Max(c.R, Math.Max(c.G, c.B)), min = Math.Min(c.R, Math.Min(c.G, c.B));
    if (max < 15 || (double)(max - min) / max < minSaturation) return false;
    double hue = c.GetHue();
    for (int i = 0; i < hueFrom.Length; i++) if (hue >= hueFrom[i] && hue <= hueTo[i]) return true;
    return false;
  }

  static bool IsIn(Rectangle[] areas, int x, int y) {
    foreach (Rectangle area in areas) if (area.Contains(x, y)) return true;
    return false;
  }
}
"@

function ConvertTo-Rectangles([string]$text) {
    [System.Drawing.Rectangle[]]@($text -split ';' | Where-Object { $_.Trim() } | ForEach-Object {
        $n = [int[]]($_ -split ','); New-Object System.Drawing.Rectangle $n[0], $n[1], $n[2], $n[3] })
}

$path = (Resolve-Path $image).Path
# the bitmap is copied by its bytes, so the file is not locked while it is written again.
$bytes = [System.IO.File]::ReadAllBytes($path)
$stream = New-Object System.IO.MemoryStream (, $bytes)
$loaded = New-Object System.Drawing.Bitmap $stream
$bmp = $loaded.Clone((New-Object System.Drawing.Rectangle 0, 0, $loaded.Width, $loaded.Height), [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$loaded.Dispose(); $stream.Dispose()
if ($cellWidth -eq 0) { $cellWidth = $bmp.Width; $cellHeight = $bmp.Height }
$ranges = @($itemHues -split ',' | Where-Object { $_ })
[HeldItem]::Remove($bmp, $cellWidth, $cellHeight, ($maskCol - 1) * $cellWidth, ($maskRow - 1) * $cellHeight,
                   # @() because a function that returns no rectangles gives null instead of an empty list.
                   [System.Drawing.Rectangle[]]@(ConvertTo-Rectangles $erase), [System.Drawing.Rectangle[]]@(ConvertTo-Rectangles $keep),
                   [double[]]@($ranges | ForEach-Object { [double]($_ -split '-')[0] }),
                   [double[]]@($ranges | ForEach-Object { [double]($_ -split '-')[1] }), $minSaturation)
$bmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
