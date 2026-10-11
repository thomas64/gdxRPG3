# Exports the animations of the battle scenes from RPG Maker MV:
# the sheets to assets/sprites/animations/ and the frame data to assets/configs/animations/battle_scene_animations.json.
# Run from anywhere: powershell -File tools/export_battle_scene_animations.ps1 [-mv <path to MV's NewData>]
#
# This made the first version of battle_scene_animations.json. Since then the json is adjusted by hand, and it is leading.
# Running this again overwrites those adjustments, so only use it as a start for a new animation from MV.
param([string]$mv = 'D:\SteamLibrary\steamapps\common\RPG Maker MV\NewData')

$game = Join-Path $PSScriptRoot '..\assets'
$sheetDir = "$game\sprites\animations"

# MV sheet name -> our sheet name, named after what it shows.
# a strike is shown on every physical attack, an impact only when a spell hits.
$sheetNames = [ordered]@{ Slash = 'physical_strike'; Claw = 'claw_strike'; Hit1 = 'hit_strike';
                          HitFire = 'fire_impact'; Wind1 = 'wind_impact'; HitThunder = 'thunder_impact' }
# our animation id -> MV animation id, for the animations that are taken over as they are.
# claw is for a scratch, hit is for a bite and a body slam.
$takenOver = @{ swing = 6; claw = 16; hit = 1 }
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

# MV's wind is mint green (hue 156). it is turned to blue (hue 200), keeping its saturation and brightness.
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
function EntryJson($key, $spriteName, $frames, $flashes, $frameDuration) {
    $frameDurationJson = if ($frameDuration) { "    `"frameDuration`": " + (Number $frameDuration) + ",`n" } else { "" }
    $flashesJson = if ($flashes) { ",`n    `"flashes`": [`n" + ($flashes -join ",`n") + "`n    ]" } else { "" }
    "  `"$key`": {`n" +
    "    `"spriteName`": `"$spriteName`",`n" +
    $frameDurationJson +
    "    `"frames`": [`n" + ($frames -join ",`n") + "`n    ]" +
    $flashesJson + "`n  }"
}
# a flash of the target, from atAnimationFrame on for frameCount frames, with its color as factors.
function FlashJson($atAnimationFrame, $frameCount, $color) {
    '      {"atAnimationFrame": ' + $atAnimationFrame + ', "frameCount": ' + $frameCount + ', "color": [' + (($color | ForEach-Object { Number $_ }) -join ', ') + ']}'
}
# MV gives a color as 0-255. as factors it is rounded to steps of 0.05, a difference that can't be seen.
function ToRoundedFactors($mvColor) { $mvColor | ForEach-Object { [Math]::Round($_ / 255 * 20) / 20 } }
# an element lets the target blink as long as its animation lasts: one flash per frame, between its color and white.
function ElementBlinkFlashes($frameCount, $elementColor) {
    $white = @(1, 1, 1, 0.85)
    for ($frame = 0; $frame -lt $frameCount; $frame++) {
        FlashJson $frame 1 $(if ($frame % 2 -eq 0) { $elementColor } else { $white })
    }
}

function TakeOver($key) {
    $a = $all[$takenOver[$key]]
    $frames = foreach ($f in $a.frames) {
        FrameJson @(foreach ($c in $f) {
            if ($c[0] -lt 0) { continue }
            CellJson $c[0] $c[1] $c[2] $c[3] $c[4] ($c[5] -eq 1).ToString().ToLower() $c[6] $c[7]
        })
    }
    $flashes = foreach ($t in $a.timings) { if ($t.flashScope -eq 1) { FlashJson $t.frame $t.flashDuration (ToRoundedFactors $t.flashColor) } }
    EntryJson $key $sheetNames[$a.animation1Name] $frames $flashes
}

# a cell that MV shows again is left out, so every cell is used once. the color of the blink is MV's flash color.
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
    $elementColor = ToRoundedFactors ($a.timings | Where-Object { $_.flashScope -eq 1 } | Select-Object -First 1).flashColor
    $flashes = ElementBlinkFlashes @($frames).Count $elementColor
    EntryJson $key $sheetNames[$a.animation2Name] $frames $flashes ($elementDuration / @($frames).Count)
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
    # the same flash as 'swing', at the frame where the streak reaches the enemy.
    EntryJson 'thrust' 'physical_strike' $frames @((FlashJson 2 2 @(1, 1, 1, 0.75)))
}

# MV has no 'Hit Wind', so it is composed from the 10 whirlwind cells (15-24) of the wind sheet.
# the whirlwind is tall, so it is placed higher than fire and thunder (-12), to stand over the enemy.
# without a flash of its own, it blinks in the flash color of MV's 'Hit Ice', which is blue like this wind.
function Wind {
    $frames = foreach ($cell in 15..24) { FrameJson @((CellJson $cell 0 -55 225 0 'false' 255 1)) }
    $elementColor = ToRoundedFactors ($all[4].timings | Where-Object { $_.flashScope -eq 1 } | Select-Object -First 1).flashColor
    $flashes = ElementBlinkFlashes @($frames).Count $elementColor
    EntryJson 'wind' 'wind_impact' $frames $flashes ($elementDuration / @($frames).Count)
}

$entries = @(
    (TakeOver 'swing'),
    (Thrust),
    (TakeOver 'claw'),
    (TakeOver 'hit'),
    (TakeOverElement 'fire'),
    (Wind),
    (TakeOverElement 'thunder')
)
$json = "{`n" + ($entries -join ",`n") + "`n}`n"
[System.IO.File]::WriteAllText("$game\configs\animations\battle_scene_animations.json", $json)
[System.IO.File]::WriteAllText("$game\configs\animations\_files.txt", "battle_scene_animations.json`n")
Get-ChildItem $sheetDir | Select-Object -ExpandProperty Name
