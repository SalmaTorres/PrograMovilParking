# --- CONFIGURACIÓN ---
# Esta llave tiene permisos para SUBIR (POST)
$IMPORT_KEY ="R1A8b4FfYtBGyy7c8cOa1V8K0sg2eDEft:"

# Esta llave solo tiene permisos para DESCARGAR (GET)
$EXPORT_KEY = "xOSycCwdPIuVZUty4hAU10A36iTElxF0:"

$BASE_PATH = "composeApp/src/commonMain/composeResources"
$LANGUAGES = @("en") # Agrega aquí más idiomas: @("en", "pt", "fr")

# --- 1. PUSH: SUBIR EL ESPAÑOL (FUENTE DE VERDAD) ---
# Usamos la IMPORT_KEY porque vamos a realizar un envío (POST)
Write-Host "Subiendo strings nuevos de Español a Loco.biz..." -ForegroundColor Cyan
curl.exe -u $IMPORT_KEY --data-binary "@$BASE_PATH/values/strings.xml" "https://localise.biz/api/import/xml?locale=es&index=id"

# --- 2. PULL: DESCARGAR TRADUCCIONES ---
foreach ($lang in $LANGUAGES) {
    Write-Host "Descargando idioma [$lang]..." -ForegroundColor Green

    # Crear la carpeta si no existe (values-en, values-pt, etc)
    $folder = "$BASE_PATH/values-$lang"
    if (!(Test-Path $folder)) { New-Item -ItemType Directory -Path $folder }

    # Usamos la EXPORT_KEY porque solo vamos a leer datos (GET)
    curl.exe -L -u $EXPORT_KEY "https://localise.biz/api/export/locale/$lang.xml" -o "$folder/strings.xml"
}

Write-Host "Localización sincronizada correctamente" -ForegroundColor Yellow