<#
.SYNOPSIS
  Servidor web local leve em PowerShell para testar o BeerGod Frontend localmente.
  Executa na porta 3000 e abre automaticamente no navegador.
#>

param(
    [int]$port = 3000,
    [switch]$NoBrowser = $false
)

$folder = $PSScriptRoot
if (-not $folder) { $folder = Get-Location }

$listener = New-Object System.Net.HttpListener
$prefix = "http://localhost:$port/"
$listener.Prefixes.Add($prefix)

try {
    $listener.Start()
} catch {
    Write-Host "Falha ao iniciar na porta $port. Tentando porta 3001..." -ForegroundColor Yellow
    $port = 3001
    $listener = New-Object System.Net.HttpListener
    $prefix = "http://localhost:$port/"
    $listener.Prefixes.Add($prefix)
    $listener.Start()
}

Write-Host "==========================================================" -ForegroundColor DarkYellow
Write-Host "  BeerGod Tap House - Servidor Web Local Ativo" -ForegroundColor Yellow
Write-Host "  URL: $prefix" -ForegroundColor Cyan
Write-Host "  Diretório: $folder" -ForegroundColor Gray
Write-Host "  Pressione Ctrl+C para encerrar o servidor." -ForegroundColor DarkGray
Write-Host "==========================================================" -ForegroundColor DarkYellow

$mimeTypes = @{
    ".html" = "text/html; charset=utf-8"
    ".htm"  = "text/html; charset=utf-8"
    ".js"   = "application/javascript; charset=utf-8"
    ".mjs"  = "application/javascript; charset=utf-8"
    ".css"  = "text/css; charset=utf-8"
    ".png"  = "image/png"
    ".jpg"  = "image/jpeg"
    ".jpeg" = "image/jpeg"
    ".gif"  = "image/gif"
    ".svg"  = "image/svg+xml"
    ".ico"  = "image/x-icon"
    ".json" = "application/json; charset=utf-8"
}

if (-not $NoBrowser) {
    Start-Process $prefix
}

try {
    while ($listener.IsListening) {
        try {
            $context = $listener.GetContext()
            $request = $context.Request
            $response = $context.Response

            $rawUrl = $request.Url.LocalPath
            if ($rawUrl -eq "/" -or [string]::IsNullOrWhiteSpace($rawUrl)) {
                $rawUrl = "/index.html"
            }

            $localPath = Join-Path $folder ($rawUrl.TrimStart('/'))

            if (Test-Path $localPath -PathType Leaf) {
                $ext = [System.IO.Path]::GetExtension($localPath).ToLower()
                $mime = $mimeTypes[$ext]
                if (-not $mime) { $mime = "application/octet-stream" }

                $bytes = [System.IO.File]::ReadAllBytes($localPath)
                $response.ContentType = $mime
                $response.ContentLength64 = $bytes.Length
                $response.AddHeader("Access-Control-Allow-Origin", "*")
                $response.StatusCode = 200

                if ($request.HttpMethod -ne "HEAD") {
                    $response.OutputStream.Write($bytes, 0, $bytes.Length)
                }
            } else {
                $msg = [System.Text.Encoding]::UTF8.GetBytes("404 - Arquivo não encontrado: $rawUrl")
                $response.StatusCode = 404
                $response.ContentType = "text/plain; charset=utf-8"
                $response.ContentLength64 = $msg.Length

                if ($request.HttpMethod -ne "HEAD") {
                    $response.OutputStream.Write($msg, 0, $msg.Length)
                }
            }

            $response.Close()
        } catch {
            Write-Verbose "Erro ao processar requisição: $_"
        }
    }
} finally {
    $listener.Stop()
    $listener.Close()
}
