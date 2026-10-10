# Exports the battle animations of the attack scene from RPG Maker MV:
# the sheets to assets/sprites/animations/ and the frame data to assets/configs/animations/attack_animations.json.
# Run from anywhere: powershell -File tools/export_attack_animations.ps1 [-mv <path to MV's NewData>]
param([string]$mv = 'D:\SteamLibrary\steamapps\common\RPG Maker MV\NewData')

$game = Join-Path $PSScriptRoot '..\assets'
$sheetDir = "$game\sprites\animations"

# MV sheet name -> our sheet name, named after what it shows.
# a strike is shown on every physical attack, an impact only when a spell hits.
$sheetNames = [ordered]@{ Slash = 'physical_strike'; HitFire = 'fire_impact'; Wind1 = 'wind_impact'; HitThunder = 'thunder_impact' }
# our animation id -> MV animation id, for the animations that are taken over as they are.
$takenOver = @{ swing = 6 }
# our animation id -> MV animation id, for the elements. MV puts its element sheet on top of a general Hit2 base,
# but the element sheet is complete on its own. So only that second sheet is taken over, starting at frame 0.
$elements = @{ fire = 3; thunder = 5 }
# every element uses all of its cells once, so they differ in frames. they last equally long by their own frame duration.
# in seconds. a bit longer than MV, which shows fire in 8 frames of 1/15 second (0.53).
$elementDuration = 0.7

$all = Get-Content "$mv\data\Animations.json" -Raw | ConvertFrom-Json
Remove-Item "$sheetDir\*.png" -ErrorAction Ignore
New-Item -ItemType Directory -Force $sheetDir | Out-Null
New-Item -ItemType Directory -Force "$game\configs\animations" | Out-Null
foreach ($s in $sheetNames.Keys) { Copy-Item "$mv\img\animations\$s.png" "$sheetDir\$($sheetNames[$s]).png" -Force }

# MV's wind is mint green (hue 156). it is turned to the blue that the target lights up in (sky, cyan, blue),
# keeping its saturation and brightness.
Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @"
using System; using System.Drawing;
public static class HueShift {
  public static void Apply(string path, double shift) {
    Bitmap src = new Bitmap(path); Bitmap o = new Bitmap(src.Width, src.Height);
    for (int y = 0; y < src.Height; y++) for (int x = 0; x < src.Width; x++) {
      Color c = src.GetPixel(x, y);
      double r = c.R / 255.0, g = c.G / 255.0, b = c.B / 255.0;
      double max = Math.Max(r, Math.Max(g, b)), min = Math.Min(r, Math.Min(g, b)), d = max - min, h = 0;
      if (d > 0) { if (max == r) h = 60 * (((g - b) / d) % 6); else if (max == g) h = 60 * ((b - r) / d + 2); else h = 60 * ((r - g) / d + 4); }
      h = ((h + shift) % 360 + 360) % 360;
      double s = max == 0 ? 0 : d / max, v = max, cc = v * s, xx = cc * (1 - Math.Abs((h / 60) % 2 - 1)), m = v - cc;
      double rr = 0, gg = 0, bb = 0;
      if (h < 60) { rr = cc; gg = xx; } else if (h < 120) { rr = xx; gg = cc; } else if (h < 180) { gg = cc; bb = xx; }
      else if (h < 240) { gg = xx; bb = cc; } else if (h < 300) { rr = xx; bb = cc; } else { rr = cc; bb = xx; }
      o.SetPixel(x, y, Color.FromArgb(c.A, (int)Math.Round((rr + m) * 255), (int)Math.Round((gg + m) * 255), (int)Math.Round((bb + m) * 255)));
    }
    src.Dispose(); o.Save(path, System.Drawing.Imaging.ImageFormat.Png); o.Dispose();
  }
}
"@
[HueShift]::Apply("$sheetDir\wind_impact.png", 200 - 156)

function Number($value) { [Math]::Round([double]$value, 4).ToString([Globalization.CultureInfo]::InvariantCulture) }
# MV gives scale as a percentage and opacity as 0-255. they are written as factors, like libGDX uses them.
function CellJson($p, $x, $y, $s, $r, $m, $o, $b) {
    '        {"spriteCellIndex":' + $p + ', "x": ' + $x + ', "y": ' + $y + ', "scale": ' + (Number ($s / 100)) + ', "rotation": ' + $r + ', "mirror": ' + $m + ', "opacity": ' + (Number ($o / 255)) + ', "blend": ' + $b + '}'
}
function FrameJson($cells) { if ($cells) { "      [`n" + ($cells -join ",`n") + "`n      ]" } else { "      []" } }
function EntryJson($key, $spriteName, $frames, $frameDuration) {
    $frameDurationJson = if ($frameDuration) { "    `"frameDuration`": " + (Number $frameDuration) + ",`n" } else { "" }
    "  `"$key`": {`n" +
    "    `"spriteName`": `"$spriteName`",`n" +
    $frameDurationJson +
    "    `"frames`": [`n" + ($frames -join ",`n") + "`n    ]`n  }"
}

function TakeOver($key) {
    $a = $all[$takenOver[$key]]
    $frames = foreach ($f in $a.frames) {
        FrameJson @(foreach ($c in $f) {
            if ($c[0] -lt 0) { continue }
            CellJson $c[0] $c[1] $c[2] $c[3] $c[4] ($c[5] -eq 1).ToString().ToLower() $c[6] $c[7]
        })
    }
    EntryJson $key $sheetNames[$a.animation1Name] $frames
}

# a cell that MV shows again is left out, so every cell is used once.
function TakeOverElement($key) {
    $a = $all[$elements[$key]]
    $usedCells = @{}
    $frames = foreach ($f in $a.frames) {
        $cells = @(foreach ($c in $f) {
            if ($c[0] -lt 100 -or $usedCells.ContainsKey($c[0])) { continue }
            $usedCells[$c[0]] = $true
            CellJson ($c[0] - 100) $c[1] $c[2] $c[3] $c[4] ($c[5] -eq 1).ToString().ToLower() $c[6] $c[7]
        })
        if ($cells) { FrameJson $cells }
    }
    EntryJson $key $sheetNames[$a.animation2Name] $frames ($elementDuration / @($frames).Count)
}

# MV's pierce animations are starbursts, not a thrust. So 'thrust' is composed:
# the streak of 'swing' mirrored and turned flat (-45 degrees), so its bright head points at the enemy.
# it is used for range attacks as well.
function Thrust {
    function Streak($p, $x) { CellJson $p $x 0 200 -45 'true' 255 1 }
    $frames = @(
        @((Streak 0 -200)),
        @((Streak 1 -160)),
        @((Streak 2 -120)),
        @((Streak 3 -115)),
        @((Streak 4 -115))
    ) | ForEach-Object { FrameJson $_ }
    EntryJson 'thrust' 'physical_strike' $frames
}

# MV has no 'Hit Wind', so it is composed from the 10 whirlwind cells (15-24) of the wind sheet.
# the whirlwind is tall, so it is placed higher than fire and thunder (-12), to stand over the enemy.
function Wind {
    $frames = foreach ($cell in 15..24) { FrameJson @((CellJson $cell 0 -55 225 0 'false' 255 1)) }
    EntryJson 'wind' 'wind_impact' $frames ($elementDuration / @($frames).Count)
}

$entries = @(
    (TakeOver 'swing'),
    (Thrust),
    (TakeOverElement 'fire'),
    (Wind),
    (TakeOverElement 'thunder')
)
$json = "{`n" + ($entries -join ",`n") + "`n}`n"
[System.IO.File]::WriteAllText("$game\configs\animations\attack_animations.json", $json)
[System.IO.File]::WriteAllText("$game\configs\animations\_files.txt", "attack_animations.json`n")
Get-ChildItem $sheetDir | Select-Object -ExpandProperty Name
