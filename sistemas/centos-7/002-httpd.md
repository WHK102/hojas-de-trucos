# Hoja de trucos - Instalación del servicio HTTPD

Se asume que se han instalado los repositorios recomendados de Epel Release y
DeltaRPM detallados en [001-basic-setup.md](./001-basic-setup.md).

Por razones de seguridad, elimine todo el contenido del archivo de bienvenida
pero **no lo elimine el archivo**, porque cuando httpd se actualice volverá a
crear el archivo con el contenido anterior, pero si este existe, no lo
sobrescribirá y lo omitirá.

## Instalación y configuración

```bash
# --- INSTALACIÓN DE PAQUETES ---

# Servicio requerido
root@server:~# yum -y install httpd;
root@server:~# systemctl enable httpd.service;
root@server:~# systemctl start httpd.service;

# Para la instalación de módulos personalizados, monitoreo y seguridad
# (separación de directorios y procesos por usuario).
root@server:~# yum -y install httpd-devel httpd-itk goaccess;

# Para utilizar HTTPS
root@server:~# yum -y install mod_ssl certbot python2-certbot-apache

# Para la seguridad adicional
root@server:~# yum -y install mod_security;

# Instalación de la aplicación que hará el envío de correos
root@server:~# yum -y install sendmail
root@server:~# systemctl enable sendmail.service
root@server:~# systemctl start sendmail.service


# --- CONFIGURACIÓN ---

# Deja en blanco la página de bienvenida por defecto de Apache HTTPD
root@server:~# echo '' > /etc/httpd/conf.d/welcome.conf;

# Previene la vulnerabilidad de Directory Listing
root@server:~# mkdir -p /var/www/error/;
root@server:~# echo '' > /var/www/error/noindex.html;
root@server:~# echo '' > /etc/httpd/conf.d/autoindex.conf;

# Crea la página por defecto cuando una solicitud HTTP no contiene un
# host virtual válido.
root@server:~# echo '
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html><head>
<title>404 No Encontrado</title>
</head><body>
<h1>No Encontrado</h1>
<p>La URL solicitada no ha sido encontrada en el servidor.</p>
</body></html>
' > /var/www/html/index.html;

# Contenido al final del archivo de configuración de Apache HTTPD
root@server:~# echo '
# Para la separación de directorios y procesos (módulo httpd-itk)
<Directory /home/*/public_html>
    AllowOverride All
    Options None

    <Limit GET POST OPTIONS>
        Order allow,deny
        Allow from all
    </Limit>

    <LimitExcept GET POST OPTIONS>
        Order deny,allow
        Deny from all
    </LimitExcept>
</Directory>

# Nombre y contacto predeterminado. Esto es requerido por cada host virtual,
# pero con este valor predeterminado ya no será necesario y se aplicará a todos
# los que no incluyan esta propiedad.
ServerName localhost
ServerAdmin admin@localhost

# Previene ataques de tipo Clickjacking
Header always append X-Frame-Options SAMEORIGIN

# Habilita los filtros Anti XSS básicos en los navegadores compatibles
Header always append X-XSS-Protection "1; mode=block"

# Previene ataques de tipo Cross Site Tracking (XST)
TraceEnable off

# Previene el Directory Listening para todos los host virtuales
Options -Indexes

# Previene la exposición de información (evita ataques más complejos)
ServerSignature Off
ServerTokens Prod

# Configuraciones de los host virtuales disponibles y habilitados
IncludeOptional sites-enabled/*.conf
' >> /etc/httpd/conf/httpd.conf;

# Crea los directorios para el control de los host virtuales
root@server:~# mkdir -p /etc/httpd/sites-available;
root@server:~# chmod 750 /etc/httpd/sites-available;
root@server:~# mkdir -p /etc/httpd/sites-enabled;
root@server:~# chmod 750 /etc/httpd/sites-enabled;

# Crea el host virtual por defecto para todas las interfaces de red en escucha
echo '
<VirtualHost *:80>
    DocumentRoot /var/www/html
    ErrorLog "|/usr/sbin/rotatelogs /var/log/httpd/error_log_%Y-%m 2G"
    CustomLog "|/usr/sbin/rotatelogs /var/log/httpd/access_log_%Y-%m 2G" combined
</VirtualHost>

<VirtualHost *:443>
    SSLEngine on
    SSLCertificateFile /etc/pki/tls/certs/ca.crt
    SSLCertificateKeyFile /etc/pki/tls/private/ca.key
    <Directory /var/www/html>
        AllowOverride All
    </Directory>
    DocumentRoot /var/www/html
    ServerName localhost
    ErrorLog "|/usr/sbin/rotatelogs /var/log/httpd/error_log_%Y-%m 2G"
    CustomLog "|/usr/sbin/rotatelogs /var/log/httpd/access_log_%Y-%m 2G" combined
</VirtualHost>
' > /etc/httpd/sites-available/000-default.conf;

# Para que el host virtual para SSL funcione deben existir las llaves 
# autofirmadas que fueron declaradas.
root@server:~# openssl genrsa -out ca.key 2048;
root@server:~# openssl req -new -key ca.key -out ca.csr \
               -subj '/C=CL/L=Local/O=Local/CN=Local/emailAddress=admin@localhost';
root@server:~# openssl x509 -req -days 365 -in ca.csr -signkey ca.key -out ca.crt;
root@server:~# cp ca.crt /etc/pki/tls/certs && rm -f ca.crt;
root@server:~# cp ca.key /etc/pki/tls/private/ca.key && rm -f ca.key;
root@server:~# cp ca.csr /etc/pki/tls/private/ca.csr && rm -f ca.csr;

# Escribe al final de la configuracion, el uso del módulo ITK como loader
# de Apache HTTPD.
root@server:~# echo 'HTTPD=/usr/sbin/httpd.itk' >> /etc/sysconfig/httpd;

# Configura el módulo ITK
root@server:~# echo '
LoadModule mpm_itk_module modules/mod_mpm_itk.so
<IfModule itk.c>
    StartServers 8
    MinSpareServers 5
    MaxSpareServers 20
    ServerLimit 256
    MaxClients 256
    MaxRequestsPerChild 4000
</IfModule>
' >> /etc/httpd/conf.modules.d/00-mpm-itk.conf;

# Configura iptables si está instalado
root@server:~# iptables -I INPUT 5 -i eth0 -p tcp --dport 80 -m state \
               --state NEW,ESTABLISHED -j ACCEPT;
root@server:~# iptables -I INPUT 5 -i eth0 -p tcp --dport 443 -m state \
               --state NEW,ESTABLISHED -j ACCEPT;
root@server:~# service iptables save;

# Validando cambios
root@server:~# apachectl configtest;
Syntax OK

# Aplicando cambios
root@server:~# systemctl restart httpd.service;
```