<#
  run-windows.ps1
  Script para compilar y ejecutar el Reproductor Musical en Windows.
  
  Descarga e instala automáticamente:
  - JDK 25.0.1 (si no existe)
  - JavaFX SDK 25.0.1 (si no existe)
  
  Luego compila y ejecuta la aplicación.
#>

$ScriptDir = $PSScriptRoot
$JdkDir = "$ScriptDir\jdk-25"
$JavaFxDir = "$ScriptDir\javafx-sdk-25.0.1"
$JavaExe = "$JdkDir\bin\java.exe"
$JavacExe = "$JdkDir\bin\javac.exe"
$FxLibPath = "$JavaFxDir\lib"
$OutDir = "$ScriptDir\out"
$SrcDir = "$ScriptDir\src"

function ExitWith($msg) { 
    Write-Host $msg -ForegroundColor Red
    pause
    exit 1 
}

function DownloadFile($url, $outPath) {
    Write-Host "  Descargando: $url" -ForegroundColor Cyan
    try {
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $url -OutFile $outPath -UseBasicParsing -ErrorAction Stop
        Write-Host "  Descarga completada." -ForegroundColor Green
        return $true
    } catch {
        Write-Host "  Error descargando: $_" -ForegroundColor Red
        return $false
    }
}

Write-Host "`n=== Reproductor Musical - Compilador y Ejecutor ===" -ForegroundColor Green
Write-Host "Directorio del proyecto: $ScriptDir`n" -ForegroundColor Cyan

# ==========================================
# 1. DESCARGAR E INSTALAR JDK 25
# ==========================================
if (-not (Test-Path "$JdkDir\bin\javac.exe")) {
    Write-Host "[1/5] Descargando e instalando JDK 21 (alternativa)..." -ForegroundColor Cyan
    $jdkZip = "$ScriptDir\jdk-21.zip"
    # URL directa de Adoptium para JDK 21
    $jdkUrl = 'https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1%2B12/OpenJDK21U-jdk_x64_windows_hotspot_21.0.1_12.zip'
    
    if (DownloadFile $jdkUrl $jdkZip) {
        Write-Host "  Extrayendo JDK..." -ForegroundColor Cyan
        Expand-Archive -Path $jdkZip -DestinationPath $ScriptDir -Force -ErrorAction Stop
        Remove-Item $jdkZip -Force
        # Renombrar la carpeta extraída
        $extractedFolder = Get-ChildItem -Path $ScriptDir -Directory -Filter "jdk-*" -ErrorAction SilentlyContinue | Where-Object {$_.Name -ne "jdk-25"} | Select-Object -First 1
        if ($extractedFolder) {
            Rename-Item -Path $extractedFolder.FullName -NewName "jdk-25"
        }
        Write-Host "  JDK instalado exitosamente.`n" -ForegroundColor Green
    } else {
        ExitWith "Error: No se pudo descargar el JDK. Intenta manualmente desde https://adoptium.net/"
    }
} else {
    Write-Host "[1/5] JDK ya está instalado.`n" -ForegroundColor Green
}

if (-not (Test-Path $JavacExe)) {
    ExitWith "Error: No se encontró javac.exe en $JdkDir"
}

# ==========================================
# 2. DESCARGAR E INSTALAR JAVAFX
# ==========================================
if (-not (Test-Path $FxLibPath)) {
    Write-Host "[2/5] Descargando e instalando JavaFX SDK 21..." -ForegroundColor Cyan
    $javafxZip = "$ScriptDir\javafx-sdk-21.zip"
    # JavaFX 21 compatible con JDK 21
    $javafxUrl = 'https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_windows-x64_bin-sdk.zip'
    
    if (DownloadFile $javafxUrl $javafxZip) {
        Write-Host "  Extrayendo JavaFX..." -ForegroundColor Cyan
        Expand-Archive -Path $javafxZip -DestinationPath $ScriptDir -Force -ErrorAction Stop
        Remove-Item $javafxZip -Force
        # Renombrar la carpeta extraída a javafx-sdk-21
        $extractedFolder = Get-ChildItem -Path $ScriptDir -Directory -Filter "javafx-sdk*" -ErrorAction SilentlyContinue | Select-Object -First 1
        if ($extractedFolder -and $extractedFolder.Name -ne "javafx-sdk-25.0.1") {
            Rename-Item -Path $extractedFolder.FullName -NewName "javafx-sdk-25.0.1"
        }
        Write-Host "  JavaFX instalado exitosamente.`n" -ForegroundColor Green
    } else {
        ExitWith "Error: No se pudo descargar JavaFX."
    }
} else {
    Write-Host "[2/5] JavaFX ya está instalado.`n" -ForegroundColor Green
}

if (-not (Test-Path $FxLibPath)) {
    ExitWith "Error: No se encontró la carpeta lib en $JavaFxDir"
}

# ==========================================
# 3. COMPILAR EL CÓDIGO
# ==========================================
Write-Host "[3/5] Compilando el código fuente..." -ForegroundColor Cyan

if (-not (Test-Path $SrcDir)) {
    ExitWith "Error: No se encontró la carpeta 'src' en $ScriptDir"
}

# Crear directorio de salida
if (-not (Test-Path $OutDir)) {
    New-Item -ItemType Directory -Path $OutDir -Force | Out-Null
}

# Recolectar archivos .java
$javaFiles = @(Get-ChildItem -Path $SrcDir -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })

if ($javaFiles.Count -eq 0) {
    ExitWith "Error: No se encontraron archivos .java en $SrcDir"
}

Write-Host "  Archivos Java encontrados: $($javaFiles.Count)" -ForegroundColor Cyan

# Compilar
& $JavacExe --module-path "$FxLibPath" --add-modules javafx.controls,javafx.fxml -d "$OutDir" $javaFiles
if ($LASTEXITCODE -ne 0) {
    ExitWith "Error: Compilación fallida."
}

Write-Host "  Compilación completada exitosamente.`n" -ForegroundColor Green

# ==========================================
# 4. COPIAR ARCHIVOS NECESARIOS
# ==========================================
Write-Host "[4/5] Preparando archivos para ejecución..." -ForegroundColor Cyan

# Crear directorio app en out
$appOutDir = "$OutDir\app"
if (-not (Test-Path $appOutDir)) {
    New-Item -ItemType Directory -Path $appOutDir -Force | Out-Null
}

# Copiar FXML
$fxmlSrc = "$SrcDir\app\MainApp.fxml"
if (Test-Path $fxmlSrc) {
    Copy-Item -Path $fxmlSrc -Destination $appOutDir -Force
    Write-Host "  FXML copiado.`n" -ForegroundColor Green
} else {
    Write-Host "  Advertencia: No se encontró MainApp.fxml`n" -ForegroundColor Yellow
}

# ==========================================
# 5. EJECUTAR LA APLICACIÓN
# ==========================================
Write-Host "[5/5] Lanzando la aplicación..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Green
Write-Host "La interfaz gráfica debería abrirse en un momento..." -ForegroundColor Green
Write-Host "================================================`n" -ForegroundColor Green

Push-Location $ScriptDir
try {
    & $JavaExe --module-path "$FxLibPath" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "$OutDir" app.MainApp
} catch {
    ExitWith "Error al ejecutar la aplicación: $_"
} finally {
    Pop-Location
}
