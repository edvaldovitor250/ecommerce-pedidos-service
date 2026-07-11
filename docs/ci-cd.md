# CI/CD

## CI

Arquivo: `.github/workflows/ci.yml`

Executa em pull requests para `main` e pushes em `main`/`develop`.

Etapas: checkout → Java 17 → `mvn -B clean verify` → upload JaCoCo → build Docker image.

## CD

Arquivo: `.github/workflows/cd.yml`

Executa em push na `main` e tags `v*.*.*`.

Etapas: checkout → Java 17 → verify → login GHCR → build + push Docker image.

A imagem é publicada em: `ghcr.io/<owner>/<repository>`

Tags: `main`, `v1.0.0`, `sha-<commit>`.

## Release por tag

```bash
git tag v1.0.0
git push origin v1.0.0
```
