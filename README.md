# Instalación y ejecución automática (Windows)

**La forma más sencilla de instalar y ejecutar el reproductor musical es usando el script incluido:**

## Requisitos previos
- Windows 10 o superior
- PowerShell 5.0 o superior
- Conexión a internet (para descargar JDK 21 y JavaFX 21)

## Instrucciones rápidas

1. **Abre PowerShell** en la carpeta del proyecto
2. **Ejecuta el siguiente comando:**

```powershell
powershell -ExecutionPolicy Bypass -File .\run-windows.ps1
```

**O simplemente (si tu política de ejecución lo permite):**

```powershell
.\run-windows.ps1
```

**¡Eso es todo!** El script hará automáticamente:
- ✅ Descargar e instalar **JDK 21** (si no lo tienes)
- ✅ Descargar e instalar **JavaFX SDK 21** (si no lo tienes)
- ✅ Compilar todo el código Java
- ✅ Copiar el archivo FXML necesario
- ✅ Lanzar la aplicación gráfica

## Información técnica del script

El archivo `run-windows.ps1` automatiza estos 5 pasos:

1. **[1/5] Descargar e instalar JDK 21** → Descarga desde Adoptium si no existe
2. **[2/5] Descargar e instalar JavaFX SDK 21** → Descarga desde Gluon si no existe
3. **[3/5] Compilar el código** → Compila todos los `.java` con `javac`
4. **[4/5] Preparar archivos** → Copia `MainApp.fxml` al directorio de clases
5. **[5/5] Ejecutar la aplicación** → Lanza la GUI con `java`

### Carpetas que se crean automáticamente:
- `jdk-25/` → Instalación de OpenJDK 21
- `javafx-sdk-25.0.1/` → SDK de JavaFX 21
- `out/` → Código compilado (.class)

### Archivos de datos (se crean/actualizan automáticamente):
- `biblioteca.txt` → Canciones guardadas
- `favoritas.txt` → Canciones marcadas como favoritas
- `playlists.txt` → Playlists guardadas

---

# Reproductor Musical — Instrucciones de instalación y ejecución (Windows)

Este proyecto es un reproductor musical sencillo en Java que incluye una UI en JavaFX. Contiene:

- `src/` — código fuente Java (modelo, servicio, UI en FXML y controlador).
- `biblioteca.txt`, `favoritas.txt`, `playlists.txt` — archivos de datos (se crean/actualizan automáticamente al usar la app).

**Objetivo de este README**: poder instalar lo necesario en Windows, compilar y ejecutar la aplicación gráfica.

**Recomendación**: Usa el script `run-windows.ps1` (ver arriba). Las siguientes instrucciones son para instalación manual.

**Archivos importantes**
- `src/app/MainApp.java` — carga el FXML y arranca la UI.
- `src/app/MainApp.fxml` — vista FXML principal.
- `src/app/MainAppController.java` — controlador FXML (gestión de biblioteca y playlists).
- `src/utils/ArchivoHelper.java` — lectura/escritura de `biblioteca.txt`, `favoritas.txt`, `playlists.txt`.

**Preparar entorno en Windows (PowerShell)**
1. Instalar JDK y JavaFX.
2. Extrae el JavaFX SDK en una carpeta, por ejemplo `C:\javafx-sdk-25.0.1`.
3. Abre PowerShell y configura variables temporales (para la sesión actual):

```powershell
# Ajusta las rutas según dónde instalaste
$env:JAVA_HOME = 'C:\ruta\a\jdk-25.0.1'
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH
$env:PATH_TO_FX = 'C:\javafx-sdk-25.0.1\lib'
```

4. Verifica `javac`/`java`:
```powershell
javac -version
java -version
```

**Compilar desde PowerShell (forma recomendada)**
Desde la raíz del proyecto (la carpeta que contiene `src`):

```powershell
cd C:\ruta\al\repositorio\Trabajo-final
mkdir out -ErrorAction SilentlyContinue
# Recolectar archivos .java
$src = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
# Compilar pasando la lista de fuentes a javac
& $env:JAVA_HOME\bin\javac --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.fxml -d out $src
# Copiar el FXML al classpath de salida
Copy-Item .\src\app\MainApp.fxml out\app\
```

Si la compilación finaliza sin errores, continúa.

**Ejecutar la app desde PowerShell**

```powershell
& $env:JAVA_HOME\bin\java --module-path $env:PATH_TO_FX --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp out app.MainApp
```

La ventana JavaFX debería abrirse. Si la app no abre o muestra errores, revisa la salida en consola.

### Script de ayuda (PowerShell) — RECOMENDADO

**Para facilitar la tarea, existe el script `run-windows.ps1` en la raíz del repositorio que:**
- Descarga e instala automáticamente **JDK 21** (si no existe)
- Descarga e instala automáticamente **JavaFX SDK 21** (si no existe)
- Compila los `.java`, copia el `MainApp.fxml` y lanza la aplicación con los argumentos correctos

**Uso (desde PowerShell en la carpeta del repositorio):**

```powershell
.\run-windows.ps1
```

¡Eso es todo! La aplicación se abrirá automáticamente.

**Alternativa (CMD)**
En `cmd.exe` puedes usar comandos equivalentes. Ejemplo rápido (ajusta rutas):

```cmd
set PATH_TO_FX=C:\javafx-sdk-25.0.1\lib
javac --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml -d out <lista_de_archivos_java>
copy src\app\MainApp.fxml out\app\
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp out app.MainApp
```

Nota: en `cmd` la construcción de la lista de archivos es más manual; por eso recomendamos PowerShell o usar un IDE/build tool.

**Recomendación: usar un IDE (Eclipse/IntelliJ/VS Code)**
- Importa el proyecto como proyecto de Java simple.
- Añade la carpeta `lib` de JavaFX (`C:\javafx-sdk-25.0.1\lib`) como librería/module path (según tu IDE) y marca los módulos `javafx.controls` y `javafx.fxml`.
- Asegúrate de configurar la VM run configuration para añadir `--module-path` y `--add-modules` o configura el SDK de JavaFX en el IDE.

Ejemplo en Eclipse (Run Configuration → Arguments → VM arguments):

1. En Eclipse, importa el proyecto: `File > Import > General > Existing Projects into Workspace` y selecciona la carpeta del repositorio. Alternativamente crea un nuevo `Java Project` y añade la carpeta `src` al proyecto.
2. Añade los JARs de JavaFX al `Modulepath`: clic derecho sobre el proyecto → `Build Path > Configure Build Path...` → pestaña `Libraries` → `Modulepath` → `Add External JARs...` y selecciona todos los JARs dentro de `C:\javafx-sdk-25.0.1\lib`.
3. Configura la ejecución: `Run > Run Configurations...` → crea una nueva `Java Application` apuntando a la clase `app.MainApp`.
4. En la pestaña `Arguments` pega en `VM arguments`:

```
--module-path "C:\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics
```

5. Aplica y ejecuta la configuración.

Nota: si tu versión de Eclipse soporta proyectos modulares, añade los JARs al `Modulepath` en lugar de al `Classpath`. Para IntelliJ o VS Code las opciones son similares: añade la carpeta `lib` de JavaFX y configura los `VM options` equivalentes.

**Archivos de datos**
- `biblioteca.txt`: se crea/actualiza cuando añades o eliminas canciones.
- `favoritas.txt`: se actualiza al marcar/desmarcar favoritas.
- `playlists.txt`: se guarda con formato `playlistName;id1,id2,id3` y se carga automáticamente al iniciar la app.


**Problemas comunes y soluciones**
- `javac: command not found`: asegúrate de que el JDK está instalado y `JAVA_HOME`/`PATH` apuntan al binario.
- Errores de FXML al iniciar: verifica que `MainApp.fxml` esté copiado dentro de `out/app/` (la carpeta de clases) antes de ejecutar.
- Problemas con módulos JavaFX: confirma que `--module-path` apunta a la carpeta `lib` del SDK JavaFX y que `--add-modules` incluye `javafx.controls,javafx.fxml`.
- Si usas Windows y no quieres liarte con comandos, abrir el proyecto en IntelliJ y configurar la VM options (ver más arriba) suele ser lo más fácil.

---
