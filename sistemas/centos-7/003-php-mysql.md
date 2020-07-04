# Hoja de trucos - Instalación de PHP + MySQL

Se asume que se han instalado los repositorios recomendados de Epel Release y
DeltaRPM detallados en
[001-configuración-inicial.md](./001-configuración-inicial.md).

También se asume que se ha instalado y configurado el servicio HTTPD detallado
en [002-httpd.md](./002-httpd.md)

## Instalación y configuración

```bash
# Instalación de MySQL a traves de MariaDB
root@server:~# yum -y install mariadb mariadb-server;
root@server:~# systemctl enable mariadb.service;
root@server:~# systemctl start mariadb.service;

# Detiene el servicio (es importante que el servicio sea iniciado antes de
# comenzar con la instalación del schema, por eso se inicia y luego se detiene).
root@server:~# systemctl stop mariadb.service;
root@server:~# mysql_install_db;

# Configura MariaDB
root@server:~# cat /etc/my.cnf;
[mysqld]
bind-address=127.0.0.1              # Evita exponer el servicio
max_allowed_packet = 1024M          # Evita denegaciones de servicio
character-set-server = utf8         # Sólo para mariadb10
collation-server = utf8_general_ci  # Sólo para mariadb10
...

# Vuelve a iniciar el servicio
root@server:~# systemctl start mariadb.service;

# Establece la contraseña por defecto para el usuario root
root@server:~# mysqladmin -u root password "********";

# Elimina del historial del bash la contraseña de MySQL
root@server:~# history clear;

# Crea un archivo de configuración para acceder al servidor MySQL sin ingresar
# la contraseña.
root@server:~# echo '
[client]
host=127.0.0.1
user=root
password=********
' > ~/.my.cnf;
root@server:~# chmod 700 ~/.my.cnf;

# Valida las configuraciones y el acceso
root@server:~# mysql -e 'select @@version';
+----------------+
| @@version      |
+----------------+
| 5.5.65-MariaDB |
+----------------+

# Instala los paquetes de PHP pata HTTPD

# PHP esencial
root@server:~# yum -y install php php-cli php-devel;

# Conectividad para bases de datos
root@server:~# yum -y install php-mysqlnd php-odbc php-pgsql php-mssql;

# Uso de envío de correos
root@server:~# yum -y install php-imap php-posix;

# Adicionales básicos requeridos por la grán mayoría de los sistemas CMS conocidos
root@server:~# yum -y install php-pspell php-intl php-snmp php-xmlrpc php-xml \
                              php-soap php-bcmath php-mbstring php-fileinfo  \
                              php-mcrypt php-ldap php-zip php-curl php-gd php-tidy;

# Opcionales útiles
root@server:~# yum -y install php-pear php-pecl-memcache php-pecl-oauth \
                              php-pecl-geoip php-pecl-mcrypt;

# Actualiza los repositorios pecl de PHP
root@server:~# pecl channel-update pecl.php.net;
root@server:~# pecl update-channels;

# Configura PHP para HTTP-ITK instalado previamente
root@server:~# echo '
<IfModule prefork.c>
    LoadModule php5_module modules/libphp5.so
</IfModule>
<IfModule worker.c>
    LoadModule php5_module modules/libphp5-zts.so
</IfModule>
<IfModule itk.c>
    LoadModule php5_module modules/libphp5.so
</IfModule>
' >> /etc/httpd/conf.d/php.conf

# Configuración básica de PHP

# Directorios por defecto
root@server:~# sed -c -i "s/\(session\.save_path *= *\).*/\1\"\/tmp\"/" /etc/php.ini
root@server:~# sed -i "s/^;upload_tmp_dir =$/upload_tmp_dir=\"\/tmp\"/" /etc/php.ini | grep "^upload_tmp_dir" /etc/php.ini

# Zona horaria por defecto (en mi caso es Santiago de Chile)
root@server:~# sed -i "s/^;date.timezone =$/date.timezone = \"America\/Santiago\"/" /etc/php.ini | grep "^timezone" /etc/php.ini

# Oculta la información privilegiada para evitar ataques (válido para todos los
# host virtuales).
root@server:~# sed -c -i "s/\(expose_php *= *\).*/\1Off/" /etc/php.ini
root@server:~# sed -c -i "s/\(error_reporting *= *\).*/\1E_ALL \& ~E_NOTICE \& ~E_STRICT \& ~E_DEPRECATED/" /etc/php.ini
root@server:~# sed -c -i "s/\(display_errors *= *\).*/\1On/" /etc/php.ini

# Límites para evitar denegaciones de servicio
root@server:~# sed -c -i "s/\(memory_limit *= *\).*/\1512M/" /etc/php.ini
root@server:~# sed -c -i "s/\(post_max_size *= *\).*/\1128M/" /etc/php.ini
root@server:~# sed -c -i "s/\(upload_max_filesize *= *\).*/\1512M/" /etc/php.ini
root@server:~# sed -c -i "s/\(max_execution_time *= *\).*/\1420/" /etc/php.ini
root@server:~# sed -c -i "s/\(implicit_flush *= *\).*/\1On/" /etc/php.ini

# Revisa los cambios realizados
root@server:~# apachectl configtest;

# Aplica los cambios
root@server:~# systemctl restart httpd.service;
```