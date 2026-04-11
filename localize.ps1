# Función para leer el archivo local.properties
function Get-Property($key) {
    $file = "local.properties"
    if (Test-Path $file) {
        $line = Get-Content $file | Select-String -Pattern "^$key="
        if ($line) {
            return $line.ToString().Split("=")[1].Trim()
        }
    }
    return $null
}

# --- CONFIGURACIÓN DINÁMICA ---
$IMPORT_VAL = Get-Property "loco.import.key"
$EXPORT_VAL = Get-Property "loco.export.key"

if (!$IMPORT_VAL -or !$EXPORT_VAL) {
    Write-Host "Error: No se encontraron las llaves en local.properties" -ForegroundColor Red
    exit
}

$IMPORT_KEY = "$IMPORT_VAL`:"
$EXPORT_KEY = "$EXPORT_VAL`:"

$BASE_PATH = "composeApp/src/commonMain/composeResources"
$LANGUAGES = @("en")

# --- 1. PUSH ---
Write-Host "Subiendo strings nuevos de Español a Loco.biz..." -ForegroundColor Cyan
curl.exe -u $IMPORT_KEY --data-binary "@$BASE_PATH/values/strings.xml" "https://localise.biz/api/import/xml?locale=es&index=id"

# --- 2. PULL ---
foreach ($lang in $LANGUAGES) {
    Write-Host "Descargando idioma [$lang]..." -ForegroundColor Green
    $folder = "$BASE_PATH/values-$lang"
    if (!(Test-Path $folder)) { New-Item -ItemType Directory -Path $folder }
    curl.exe -L -u $EXPORT_KEY "https://localise.biz/api/export/locale/$lang.xml" -o "$folder/strings.xml"
}

Write-Host "Localización sincronizada correctamente" -ForegroundColor Yellow