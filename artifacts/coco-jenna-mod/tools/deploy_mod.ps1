param(
    [string]$Target = "C:\Users\ASUS\curseforge\minecraft\Instances\__\mods\coco-jenna-1.0.1.jar"
)

$Root = Split-Path $PSScriptRoot -Parent
python "$Root\tools\fix_lang_commas.py"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
python "$Root\tools\validate_lang.py"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
$Src = Join-Path $Root "build\libs\coco-jenna-1.0.1.jar"
$Tmp = "$Target.new"

if (-not (Test-Path $Src)) {
    Write-Error "Build output not found: $Src"
    exit 1
}

Copy-Item -Force $Src $Tmp
Push-Location $env:TEMP
try {
    jar xf $Tmp pack.mcmeta 2>$null
    if (-not (Test-Path "pack.mcmeta")) {
        Write-Error "JAR validation failed: pack.mcmeta"
        exit 1
    }
    Remove-Item pack.mcmeta -Force
}
finally {
    Pop-Location
}

$required = @(
    "com/cocojenna/sequence/HiddenSequenceRegistry.class",
    "com/cocojenna/gear/SetBonusHelper.class",
    "com/cocojenna/memforge/MemoryForgeManager.class",
    "data/cocojenna/tags/worldgen/biome/cat_kingdom.json"
)
foreach ($entry in $required) {
    $found = jar tf $Tmp | Select-String -SimpleMatch $entry
    if (-not $found) {
        Remove-Item $Tmp -Force -ErrorAction SilentlyContinue
        Write-Error "JAR missing: $entry"
        exit 1
    }
}

$modsDir = Split-Path $Target -Parent
Get-ChildItem $modsDir -Filter "coco-jenna-*.jar" -ErrorAction SilentlyContinue | Remove-Item -Force
if (Test-Path $Target) { Remove-Item $Target -Force }
Move-Item -Force $Tmp $Target
Write-Host "Deployed: $Target ($((Get-Item $Target).Length) bytes)"
