# Puts the loose combatant images of enemies on one sheet, like assets/sprites/combatants/slimes.png.
# The sheet has the same 4 x 2 layout as the walking sprite sheet of those enemies: every enemy in configs/sprites
# with this source gets its cell at its col and row.
#
# When the sheet already exists, it is read first: a loose image replaces its cell, all other cells stay as they are.
# So a new color variant can be added by putting it loose next to the sheet, as combatants/<id>.png.
# All loose images must have the size of one cell. They are removed afterwards, but only when every cell that
# they went into is exactly the same as its loose image.
#
# Example:
#   powershell -File tools/build_enemy_sheet.ps1 -source bats
param(
    [Parameter(Mandatory)][string]$source
)

$assets = Join-Path $PSScriptRoot '..\assets'
$combatants = "$assets\sprites\combatants"
$sheetPath = "$combatants\$source.png"
$cols = 4
$rows = 2

Add-Type -AssemblyName System.Drawing

# the enemies with this source that have a loose image, from all sprite configs.
$enemies = @(Get-Content "$assets\configs\sprites\_files.txt" | Where-Object { $_.Trim() } | ForEach-Object {
    $configs = Get-Content "$assets\configs\sprites\$($_.Trim())" -Raw | ConvertFrom-Json
    $configs.PSObject.Properties | Where-Object { $_.Value.source -eq $source } |
        ForEach-Object { [PSCustomObject]@{ Id = $_.Name; Col = $_.Value.col; Row = $_.Value.row } }
} | Where-Object { Test-Path "$combatants\$($_.Id).png" })
if ($enemies.Count -eq 0) { throw "no loose images found in $combatants for source '$source'." }

$images = @{}
foreach ($enemy in $enemies) { $images[$enemy.Id] = New-Object System.Drawing.Bitmap "$combatants\$($enemy.Id).png" }

# the size of a cell comes from the existing sheet, or else from the first loose image.
if (Test-Path $sheetPath) {
    $existing = New-Object System.Drawing.Bitmap $sheetPath
    $cellWidth = $existing.Width / $cols
    $cellHeight = $existing.Height / $rows
} else {
    $existing = $null
    $cellWidth = $images[$enemies[0].Id].Width
    $cellHeight = $images[$enemies[0].Id].Height
}
foreach ($enemy in $enemies) {
    $image = $images[$enemy.Id]
    if ($image.Width -ne $cellWidth -or $image.Height -ne $cellHeight) {
        throw "$($enemy.Id) is $($image.Width)x$($image.Height), but a cell is ${cellWidth}x${cellHeight}."
    }
}

# pixel by pixel, because drawing would round the half transparent edges.
function Copy-Pixels($from, $fromX, $fromY, $to, $toX, $toY, $width, $height) {
    for ($y = 0; $y -lt $height; $y++) { for ($x = 0; $x -lt $width; $x++) {
        $to.SetPixel($toX + $x, $toY + $y, $from.GetPixel($fromX + $x, $fromY + $y))
    } }
}

$sheet = New-Object System.Drawing.Bitmap ($cellWidth * $cols), ($cellHeight * $rows), ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
if ($existing) {
    Copy-Pixels $existing 0 0 $sheet 0 0 $sheet.Width $sheet.Height
    $existing.Dispose()
}
foreach ($enemy in $enemies) {
    Copy-Pixels $images[$enemy.Id] 0 0 $sheet (($enemy.Col - 1) * $cellWidth) (($enemy.Row - 1) * $cellHeight) $cellWidth $cellHeight
}
$sheet.Save($sheetPath, [System.Drawing.Imaging.ImageFormat]::Png)
$sheet.Dispose()

$saved = New-Object System.Drawing.Bitmap $sheetPath
foreach ($enemy in $enemies) {
    $image = $images[$enemy.Id]
    $x0 = ($enemy.Col - 1) * $cellWidth
    $y0 = ($enemy.Row - 1) * $cellHeight
    for ($y = 0; $y -lt $cellHeight; $y++) { for ($x = 0; $x -lt $cellWidth; $x++) {
        $a = $image.GetPixel($x, $y); $b = $saved.GetPixel($x0 + $x, $y0 + $y)
        if ($a.A -ne $b.A -or ($a.A -gt 0 -and $a.ToArgb() -ne $b.ToArgb())) { throw "$($enemy.Id) differs at $x,$y on the sheet." }
    } }
    "$($enemy.Id) at $($enemy.Col),$($enemy.Row)"
}
$saved.Dispose()
foreach ($enemy in $enemies) { $images[$enemy.Id].Dispose(); Remove-Item "$combatants\$($enemy.Id).png" }
"$source.png: $($cellWidth * $cols)x$($cellHeight * $rows), cells of ${cellWidth}x${cellHeight}"
