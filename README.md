# Despliegue Blue-Green con Ansible (Spring Boot + MySQL + NGINX)

## Estructura
- `app/`: proyecto Spring Boot (endpoint `/hola`) y Dockerfile
- `site.yml`: red Docker, MySQL y contenedores `app-blue` / `app-green`
- `deploy_green.yml`: actualiza green sin afectar al tráfico
- `switch.yml`: NGINX proxy inverso y conmutación (`-e active_color=blue|green`)
- `templates/default.conf.j2`: plantilla de NGINX
- `group_vars/all/vars.yml`: variables; `vault.yml`: credenciales cifradas (Ansible Vault)

## Diferencia entre entornos
- blue: imagen `hola-app:v1` (`/hola` -> "Hola Mundo")
- green: imagen `hola-app:v2` (`/hola` -> "Hola Mundo v2")

## Requisitos
Docker, Ansible, `python3-docker`, colección `community.docker`, JDK 17.
Crear `.vault_pass` con la clave del Vault (no se sube al repo).

## Ejecución
1. `cd app && ./mvnw clean package -DskipTests && docker build -t hola-app:v1 .`
2. `ansible-playbook site.yml`
3. `ansible-playbook switch.yml` (tráfico en blue)
4. Nueva versión: construir `hola-app:v2`, poner `green_version: v2` en `vars.yml`
   y ejecutar `ansible-playbook deploy_green.yml` (blue sigue sirviendo)
5. Conmutar: `ansible-playbook switch.yml -e active_color=green`
6. Rollback: `ansible-playbook switch.yml -e active_color=blue`

## Verificación
`curl -si http://localhost:8080/hola` -> cabecera `X-Active-Color` indica el entorno activo.

## Seguridad
Las credenciales de MySQL están en `vault.yml` cifrado y llegan a los contenedores
como variables de entorno (`DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).
El código no contiene credenciales.
