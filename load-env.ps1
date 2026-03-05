# load-env.ps1
# Script para cargar variables de un archivo .env en PowerShell

$envFilePath = Join-Path (Get-Location) ".env"

if (Test-Path $envFilePath) {
    Get-Content $envFilePath | ForEach-Object {
        # Ignorar líneas vacías o comentarios
        if ($_ -match '^\s*#|^\s*$') {
            return
        }
        # Dividir la línea en clave y valor
        if ($_ -match '^([^=]+)=(.*)$') {
            $key = $Matches[1].Trim()
            $value = $Matches[2].Trim()

            # Establecer la variable de entorno usando la sintaxis correcta
            # Asegúrate de que los valores no tengan comillas si no son parte del valor
            Set-Item -Path Env:$key -Value ($value.Trim('"').Trim("'"))
            Write-Host "Cargada variable de entorno: $key"
        }
    }
} else {
    Write-Warning "Archivo .env no encontrado en $envFilePath"
}