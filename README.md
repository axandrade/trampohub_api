# Backup do ambiente Jenkins local (trampohub-api)

Backup e restore dos volumes Docker usados pelo ambiente local de CI/CD
(Jenkins, SonarQube, MongoDB) definido em `docker-compose.yml`.

```
README.md                  (este arquivo)
backups/
├── volumes/
│   ├── jenkins_home.tar.gz
│   ├── sonarqube_data.tar.gz
│   └── mongodb_data.tar.gz
├── credentials/
│   └── TOKENS.txt
└── backup-volumes.ps1
```

> ⚠️ **NUNCA commite `backups/volumes/*.tar.gz` nem
> `backups/credentials/TOKENS.txt` no Git.** O `jenkins_home.tar.gz`
> contém as credenciais do Jenkins (mesmo criptografadas, a chave
> mestra está no mesmo arquivo) e o `TOKENS.txt` pode conter tokens em
> texto puro. Ambos já estão no `.gitignore` do projeto — não remova
> essas linhas.

## Backup automático (recomendado)

```powershell
cd C:\desenvolvimento\desenvolvimento_meu\backend\java\trampohub-api\backups
./backup-volumes.ps1
```

Isso gera/atualiza os três arquivos em `backups/volumes`. Para um backup mais
seguro do MongoDB (para containers antes, faz o backup, sobe de novo):

```powershell
./backup-volumes.ps1 -StopContainers
```

## Backup manual

O script acima nada mais faz do que rodar, para cada volume, um
container Alpine descartável que compacta o conteúdo do volume em um
`.tar.gz`. Você pode fazer isso manualmente com os comandos abaixo
(rode a partir da pasta `backups`):

```powershell
# Jenkins (credenciais, pipelines, configs, plugins)
docker run --rm -v jenkins_home:/source:ro -v ${PWD}/volumes:/backup alpine sh -c "tar czf /backup/jenkins_home.tar.gz -C /source ."

# SonarQube (histórico de análises)
docker run --rm -v trampohub-api_sonarqube_data:/source:ro -v ${PWD}/volumes:/backup alpine sh -c "tar czf /backup/sonarqube_data.tar.gz -C /source ."

# MongoDB (dados da aplicação)
docker run --rm -v trampohub-api_mongodb_data:/source:ro -v ${PWD}/volumes:/backup alpine sh -c "tar czf /backup/mongodb_data.tar.gz -C /source ."
```

Para conferir o nome exato dos volumes no seu ambiente (pode variar
conforme o nome da pasta do projeto):

```powershell
docker volume ls
```

## Restaurar um volume a partir de um backup

⚠️ Isso **sobrescreve** o conteúdo atual do volume. Se o volume ainda
não existir, o `docker run -v` abaixo já o cria automaticamente vazio
antes de restaurar.

```powershell
# 1. Pare o container que usa o volume (exemplo: Jenkins)
docker stop jenkins

# 2. Restaure o conteúdo do backup dentro do volume
docker run --rm -v jenkins_home:/target -v ${PWD}/volumes:/backup alpine sh -c "rm -rf /target/* /target/..?* /target/.[!.]* 2>/dev/null; tar xzf /backup/jenkins_home.tar.gz -C /target"

# 3. Suba o container de novo
docker start jenkins
```

Repita trocando `jenkins` / `jenkins_home` / `jenkins_home.tar.gz` por:

| Serviço    | Container       | Volume                           | Arquivo                 |
|------------|-----------------|-----------------------------------|--------------------------|
| Jenkins    | `jenkins`       | `jenkins_home`                    | `jenkins_home.tar.gz`    |
| SonarQube  | `sonarqube`     | `trampohub-api_sonarqube_data`    | `sonarqube_data.tar.gz`  |
| MongoDB    | `mongo-db-local`| `trampohub-api_mongodb_data`      | `mongodb_data.tar.gz`    |

Se o volume já existia com dados diferentes e você só quer testar a
restauração sem risco, restaure em um volume novo e aponte um
container temporário para ele em vez de sobrescrever o original:

```powershell
docker volume create jenkins_home_restore_test
docker run --rm -v jenkins_home_restore_test:/target -v ${PWD}/volumes:/backup alpine sh -c "tar xzf /backup/jenkins_home.tar.gz -C /target"
```

## Tokens e credenciais

O arquivo `backups/credentials/TOKENS.txt` é um **template** com instruções de
onde gerar e como recriar as credenciais `github-token` e
`sonarqube-token` no Jenkins, caso precise reconfigurar do zero. Ele
não vem com valores reais — preencha localmente e não deixe o arquivo
em texto puro por muito tempo (mova para um gerenciador de senhas
depois de usar).

O backup de `jenkins_home.tar.gz` já contém as credenciais
criptografadas do Jenkins (`credentials.xml` + chave mestra em
`secrets/`), então restaurando esse volume as credenciais voltam a
funcionar sem precisar recolar tokens manualmente — o `TOKENS.txt` é
só um plano B.

## Rotina sugerida

- Antes de mudanças arriscadas no Jenkins (upgrade de plugin, mudança
  de Dockerfile-jenkins, etc.): rode `backups/backup-volumes.ps1`.
- Periodicamente (ex: semanal), se o ambiente estiver em uso contínuo.
- Sempre que for limpar/recriar os containers com `docker compose down -v`.
