# VillagerProfessionControl

Plugin Bukkit/Spigot para controlar desde `config.yml` qué profesiones pueden tener los aldeanos vanilla.

## Abrir en GitHub Codespaces

1. Crea un repositorio en GitHub.
2. Sube todo el contenido de este proyecto al repositorio.
3. En GitHub abre el repositorio y selecciona **Code → Codespaces → Create codespace on main**.
4. Codespaces usará `.devcontainer/devcontainer.json` para preparar Java 21 y Maven.
5. Abre una terminal en Codespaces y ejecuta:

```bash
mvn clean package
```

6. El plugin estará en:

```text
target/VillagerProfessionControl-1.0.0.jar
```

## Configuración

Edita `src/main/resources/config.yml` antes de compilar para cambiar la configuración por defecto, o el `config.yml` generado dentro de `plugins/VillagerProfessionControl/` cuando el servidor ya esté instalado.

Ejemplo, solo granjeros:

```yaml
allowed-professions:
  - FARMER

default-profession: FARMER

enforce-existing: true
check-on-chunk-load: true
```

Ejemplo, granjero + bibliotecario + flechero:

```yaml
allowed-professions:
  - FARMER
  - LIBRARIAN
  - FLETCHER

default-profession: FARMER

enforce-existing: true
check-on-chunk-load: true
```

## Nota

`default-profession` debe ser una profesión incluida en `allowed-professions`.

El plugin no modifica aldeanos zombis. Si otro plugin también intenta controlar profesiones de aldeanos, puede existir interacción entre ambos.
