<#
.SYNOPSIS
    Faz backup dos volumes Docker do ambiente local do projeto trampohub-api
    (Jenkins, SonarQube, MongoDB) para arquivos .tar.gz em backups/volumes/.

.DESCRIPTION
    Usa um container Alpine descartável para compactar o conteúdo de cada
    volume Docker nomeado, sem precisar parar os containers (os volumes sao
    lidos em modo somente-leitura). Para um backup 100% consistente do
    MongoDB, use o parametro -StopContainers para parar os containers antes
    do backup (recomendado se o ambiente estiver com escrita ativa).

.PARAMETER StopContainers
    Para os containers jenkins, sonarqube e mongo-db-local antes do backup
    e sobe novamente ao final. Mais lento, porem mais seguro para o Mongo.

.EXAMPLE
    ./backup-volumes.ps1

.EXAMPLE
    ./backup-volumes.ps1 -StopContainers
#>

param(
    [switch]$StopContainers
)

$ErrorActionPreference = "Stop"

$ScriptDir  = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackupDir  = Join-Path $ScriptDir "volumes"
$Timestamp  = Get-Date -Format "yyyyMMdd-HHmmss"

if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

# Nome do volume Docker -> nome do arquivo de backup
$Volumes = [ordered]@{
    "jenkins_home"                    = "jenkins_home.tar.gz"
    "trampohub-api_sonarqube_data"    = "sonarqube_data.tar.gz"
    "trampohub-api_mongodb_data"      = "mongodb_data.tar.gz"
}

$ContainersToStop = @("jenkins", "sonarqube", "mongo-db-local")

function Test-DockerVolumeExists {
    param([string]$Name)
    docker volume inspect $Name *> $null
    return ($LASTEXITCODE -eq 0)
}

if ($StopContainers) {
    Write-Host "Parando containers para backup consistente: $($ContainersToStop -join ', ')" -ForegroundColor Yellow
    docker stop $ContainersToStop | Out-Null
}

try {
    foreach ($volumeName in $Volumes.Keys) {
        $outFile = $Volumes[$volumeName]

        if (-not (Test-DockerVolumeExists -Name $volumeName)) {
            Write-Warning "Volume '$volumeName' nao encontrado. Pulando."
            continue
        }

        Write-Host "Fazendo backup de '$volumeName' -> volumes/$outFile ..." -ForegroundColor Cyan

        docker run --rm `
            -v "${volumeName}:/source:ro" `
            -v "${BackupDir}:/backup" `
            alpine `
            sh -c "tar czf /backup/$outFile -C /source ."

        if ($LASTEXITCODE -ne 0) {
            Write-Error "Falha ao fazer backup do volume '$volumeName'."
            continue
        }

        $fullPath = Join-Path $BackupDir $outFile
        $sizeMB = [math]::Round((Get-Item $fullPath).Length / 1MB, 2)
        Write-Host "  OK -> $outFile ($sizeMB MB)" -ForegroundColor Green
    }
}
finally {
    if ($StopContainers) {
        Write-Host "Subindo containers novamente..." -ForegroundColor Yellow
        docker start $ContainersToStop | Out-Null
    }
}

Write-Host ""
Write-Host "Backup concluido em: $BackupDir" -ForegroundColor Green
Write-Host "Timestamp: $Timestamp"
