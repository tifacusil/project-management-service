# Nginx dinámico con consul-template

Hace que Nginx descubra los backends desde Consul en lugar de tener IPs/puertos
hardcodeados. Cuando un servicio se registra/desregistra en Consul (como ahora
hace `project-management-service`), consul-template regenera
`/etc/nginx/sites-enabled/default` y recarga Nginx automáticamente.

## Archivos

| Archivo | Destino en el servidor |
|---|---|
| `nginx.conf.ctmpl` | `/etc/consul-template/templates/nginx.conf.ctmpl` |
| `consul-template.hcl` | `/etc/consul-template/consul-template.hcl` |
| `consul-template.service` | `/etc/systemd/system/consul-template.service` |

## ⚠️ Antes de aplicar: nombres de servicio en Consul

La plantilla descubre cada upstream por el **nombre con que el servicio se
registra en Consul** (`spring.application.name`). Verifica los nombres reales:

```bash
curl -s http://127.0.0.1:8500/v1/catalog/services | jq
```

Y ajusta en `nginx.conf.ctmpl` los nombres dentro de `service "<nombre>|passing"`
si no coinciden con:

- `user-management-service`
- `project-management-service`  ← este ya está confirmado
- `data-management-service`
- `early-warning-service`
- `data-analysis-service`

Si un servicio **no** está en Consul todavía, su upstream usa el **fallback
estático** (los mismos `127.0.0.1:<puerto>` que tenías), así que Nginx sigue
funcionando hasta que ese micro también se registre. Nada se rompe.

## Instalación

```bash
# 1. Copiar archivos
sudo mkdir -p /etc/consul-template/templates
sudo cp nginx.conf.ctmpl   /etc/consul-template/templates/
sudo cp consul-template.hcl /etc/consul-template/
sudo cp consul-template.service /etc/systemd/system/

# 2. Respaldar la config actual de Nginx (consul-template la sobrescribirá)
sudo cp /etc/nginx/sites-enabled/default /etc/nginx/sites-enabled/default.bak

# 3. Probar el render UNA vez en seco (no toca Nginx, solo imprime):
sudo consul-template \
  -config=/etc/consul-template/consul-template.hcl \
  -once -dry

#    Revisa la salida: cada upstream debe tener al menos un `server`.

# 4. Si se ve bien, arrancar como servicio:
sudo systemctl daemon-reload
sudo systemctl enable --now consul-template
sudo systemctl status consul-template
```

## Verificación

```bash
# El archivo destino debe reflejar las instancias de Consul:
sudo cat /etc/nginx/sites-enabled/default

# Logs de consul-template (incluye los reload de Nginx):
sudo journalctl -u consul-template -f
```

Levanta o baja una instancia de `project-management-service` y observa cómo el
`upstream project_management` cambia solo y Nginx se recarga.

## Rollback

```bash
sudo systemctl disable --now consul-template
sudo cp /etc/nginx/sites-enabled/default.bak /etc/nginx/sites-enabled/default
sudo nginx -t && sudo nginx -s reload
```
