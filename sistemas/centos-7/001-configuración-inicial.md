# Hoja de trucos - Configuraciones iniciales básicas para CentOS 7

Comandos básicos y acciones para realizar en una instalación de CentOS 7 en
blanco.


```bash
# Establecer UTF8 como entrada predeterminada para shell
root@server:~# export LC_CTYPE=en_US.UTF-8;
root@server:~# export LC_ALL=en_US.UTF-8;

# Declara el nombre de host del servidor
root@server:~# hostnamectl set-hostname server --static
root@server:~# nano /etc/hosts;

# Actualizar los paquetes para evitar problemas al intentar instalar un paquete 
# nuevo que no esté en la lista de aplicaciones disponibles
root@server:~# yum -y update;

# Aplicaciones esenciales:
root@server:~# yum -y install nano tree tmux rsync wget htop net-tools \
                              bash-completion openssl man;

# Configura nano como el editor de terminal predeterminado en el sistema
root@server:~# export EDITOR=nano;
root@server:~# export VISUAL=nano;
root@server:~# echo 'export EDITOR=nano;' >> ~/.bashrc;
root@server:~# echo 'export VISUAL=nano;' >> ~/.bashrc;

# Deshabilitar SeLinux si es necesario (por ejemplo, para` http-itk`)
root@server:~# cat /etc/sysconfig/selinux | grep SELINUX;
SELINUX=disabled
root@server:~# setenforce 0;
root@server:~# sestatus;
SELinux status:                 disabled

# Si has deshabilitado selinux, debes deshabilitarlo también desde el arranque,
# de lo contrario, el núcleo intentará cargar el módulo deshabilitado y el 
# sistema no iniciará.
# Agrega el argumento selinux=0 después de crashkernel=auto en todos los
# arranques.
root@server:~# sed -c -i "s/\(crashkernel=auto\).*/\1 selinux=0/" /etc/grub2.cfg;

# Deshacer los cambios hechos en el grub
# root@server:~# grub2-mkconfig -o /etc/grub2.cfg;

# Deshabilita IPv6
# En algunos casos como el uso de Zimbra Server, puedes tener problemas de
# seguridad si habilitas IPv6 ya que en algunos casos los softwares abren
# puertos pero no siempre se especifica la dirección ip, solo se indica
# localhost y localhost no siempre significa 127.0.0.1, en algunos casos también
# significa ::1, lo cual es la dirección local en ipv6. Esto puede traer
# confusiones al momento de crear reglas de conectividad.
root@server:~# echo '
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
net.ipv6.conf.lo.disable_ipv6 = 1
net.ipv4.ip_forward = 1
' >> /etc/sysctl.conf;

# Habilita la redirección de puertos (por ejemplo, para habilitar el uso de
# redes VPN, locales, tunnel ssh vía socks5, redirecciones de contenedores o
# máquinas virtuales, etc).
root@server:~# echo '1' > /proc/sys/net/ipv4/ip_forward;
root@server:~# sysctl -w net.ipv4.ip_forward=1;

# Primera y última ves que se requiere reiniciar el servidor para aplicar los
# cambios en SeLinux. El reinicio es necesario o algunas configuraciones no se
# aplicarán debido a las restricciones de SeLinux.
root@server:~# reboot;

# Después de reiniciar ...

# Elimina el servicio RPCBind si no se utilizará porque deja a escucha el puerto
# 111 el cual es susceptible a ataques de Flood UDP o reflexión.
root@server:~# systemctl stop rpcbind;
root@server:~# systemctl disable rpcbind;
root@server:~# systemctl mask rpcbind;
root@server:~# systemctl stop rpcbind.socket;
root@server:~# systemctl disable rpcbind.socket;

# ¿Quieres eliminar firewalld?
root@server:~# systemctl stop firewalld;
root@server:~# systemctl disable firewalld;
root@server:~# systemctl mask firewalld;

# ¿Quieres reemplazar firewalld por iptables?
root@server:~# yum -y install iptables iptables-services;
root@server:~# systemctl enable iptables;

# Hace que las reglas de iptables sean permanentes al reiniciar
root@server:~# sed -c -i "s/\(IPTABLES_SAVE_ON_RESTART=*\).*/\1\"yes\"/" \
               /etc/sysconfig/iptables-config;
root@server:~# sed -c -i "s/\(IPTABLES_SAVE_ON_STOP=*\).*/\1\"yes\"/" \
               /etc/sysconfig/iptables-config;

# Configuración SSH

# Para una generación rápida usando los valores por defecto
root@server:~# ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa;

# Para habilitar el acceso remoto vía llaves RSA, DSA, etc
root@server:~# touch ~/.ssh/authorized_keys;

# Permisos requeridos por el servicio
root@server:~# chmod 600 ~/.ssh/authorized_keys;

# Configura el servicio sshd
root@server:~# echo '
# Para la administración remota
PermitRootLogin yes

# Para acceso seguro (solo usando la clave pública RSA)
PubkeyAuthentication yes

# Deshabilita la autenticación de contraseña por razones seguras. Evita el
# acceso por fuerza bruta.
PasswordAuthentication no

# Deshabilitar si no se usará, agiliza la autenticación al evitar tener que
# realizar validaciones para cada tipo de administración de credenciales.
KerberosAuthentication no
GSSAPIAuthentication no

# Deshabilitar por razones de seguridad. Evite abrir ventanas remotas del
# servidor usando ssh.
X11Forwarding no

# Para un inicio de sesión rápido. El servicio SSH puede tardar demasiado si no
# puede resolver el nombre de dominio de la conexión remota.
UseDNS no
' >> /etc/ssh/sshd_config;

# Cabecera de bienvenida personalizada para el servicio SSH
root@server:~# nano /etc/issue;
root@server:~# nano /etc/motd;

# Aplica los cambios realizados
root@server:~# systemctl restart sshd;

# Limpia el caché
root@server:~# yum clean all;
root@server:~# yum -y update;

# Opcional pero no requerida
# root@server:~# yum -y install centos-release-scl

# Instala el repositorio de Epel Release
root@server:~# yum -y install epel-release;

# Deshabilita los mirrors para una resolución y descarga mas rápida con enabled=0
root@server:~# sed -c -i "s/\(enabled\).*/\1=0/" \
               /etc/yum/pluginconf.d/fastestmirror.conf;

# Deshabilitar los mirrors y habilitar el uso de los repositorios principales.
# Dependiendo del país o proveedor, algunos mirrors son más lentos y obsoletos
# que el repositorio principal. No hay garantías en el uso de mirrors.
# Descomentar `baseurl` y comentar `mirrorlist` y `metalink`.
root@server:~# sed -c -i "s/#\(baseurl=.*\)/\1/" /etc/yum.repos.d/epel.repo;
root@server:~# sed -c -i "s/\(mirrorlist=.*\)/#\1/" /etc/yum.repos.d/epel.repo;
root@server:~# sed -c -i "s/\(metalink=.*\)/#\1/" /etc/yum.repos.d/epel.repo;

# Borrar todo el caché guardado por los mirrors y volver a recargar
root@server:~# yum clean all;
root@server:~# rm -rf /var/cache/yum;
root@server:~# yum -y update;

# Instala el repositorio DeltaRPM (instalar solo después de Epel Release)
root@server:~# yum -y install deltarpm;

# Es necesario actualizar los paquetes porque Epel Releases actualiza a DeltaRPM
# y DeltaRPM actualiza a Epel Releases
root@server:~# yum -y update;

# Habilita las actualizaciones automáticas.
# En CentOS muy raramente verás un problema de compatibilidad al actualizar un
# paquete porque rara vez verás actualizaciones de versiones mayores que usen el
# mismo nombre de paquete. Recordar que Redhat prioriza la estabilidad y el
# control.

# Aplicación que se encargará de mantener las actualizaciones automáticas
root@server:~# yum -y install yum-cron;
root@server:~# systemctl enable yum-cron.service;
root@server:~# systemctl start yum-cron.service;

# Habilita las actualizaciones automáticas sin mensajes de espera.
# update_messages = no
# apply_updates = yes
root@server:~# sed -c -i "s/\(update_messages*\).*/\1\ = no/" /etc/yum/yum-cron.conf;
root@server:~# sed -c -i "s/\(apply_updates*\).*/\1\ = yes/" /etc/yum/yum-cron.conf;

# Aplica los cambios
root@server:~# systemctl restart yum-cron.service;

# Configuración de zona horaria dinámica, fecha y hora.
# En algunos países, como Chile, la zona horaria no es respetada por el gobierno
# cuando se realiza un cambio de hora, en muchos casos es necesario configurar
# manualmente las horas en nuestras computadoras porque el gobierno no utiliza
# los estándares informáticos. Por lo tanto, en mi caso utilizo un servidor ntp
# que se proporciona oficialmente declarado en 
# http://www.horaoficial.cl/doc/sincronizacion.pdf

# Instala la aplicación de sincronización NTP
root@server:~# yum -y install ntp ntpdate;

# Establece el servidor NTP
root@server:~# echo 'server ntp.shoa.cl' >> /etc/ntp.conf;

# Reemplaza la zona horaria del servidor
root@server:~# rm -f /etc/localtime;
root@server:~# ln -s /usr/share/zoneinfo/Chile/Continental /etc/localtime;

# Aplica los cambios
root@server:~# systemctl enable ntpd.service;
root@server:~# systemctl restart ntpd.service;

# Sincroniza la fecha y hora con el servidor NTP configurado
root@server:~# ntpd -gq;

# Valida la fecha y tiempo actual
root@server:~# date;


# Aplicaciones adicionales y opcionales

# El grupo de paquetes `Development Tools` instalará una serie de paquetes de
# desarrollo que agregan encabezados, bibliotecas y evitan problemas cuando
# desea intentar instalar paquetes manualmente compilándolos.
root@server:~# yum -y groupinstall 'Development Tools';

# Para tareas programadas avanzadas
root@server:~# yum -y install vixie-cron cronie;

# Para instalar versiones alternativas de paquetes, como php 5 y 7,
# ruby 2.7 o 2.5, etc.
root@server:~# yum -y install yum-plugin-replace yum-utils rpm-build \
                      initscripts tidy;

# Para administradores de sistemas: Monitoreo en general
root@server:~# yum -y install top htop iotop net-tools ssldump tcpdump libtool \
                      libdnet libdnet-devel bison libpcap libpcap-devel \
                      sysstat libaio;

# Para administradores de sistemas paranoicos: Monitoreo de seguridad
root@server:~# yum -y install rkhunter tshark nmap;

# Herramientas de cliente DNS como dig
root@server:~# yum -y install bind-utils;

# Para aumentar la velocidad de búsqueda de archivos
root@server:~# yum -y install mlocate;

# Para administradores de sistemas: comandos necesarios para el testeo e
# instalación de otros.
root@server:~# yum -y install nc wget curl links ftp zlib lbzip2 unzip unrar;

# Para devops (Administradores de sistemas que también se dedican al desarrollo
# de software).
root@server:~# yum -y install subversion git hgvgcc g++ cmake make automake \
                      autoconf zlib-devel apr-devel gd apr-util-devel \
                      openssl-devel curl-devel libpng-devel libX11-devel \
                      glib2-devel libexif flex;

# Para desarrolladores o compatibilidad con scripts complejos de administración
root@server:~# yum -y install perl perl-core python python-pip urw-fonts \
                      libgdi* mencoder;


# Para instancias dedicadas de OVH, eliminar los servicios de monitoreo, esto
# evita problemas de seguridad y mantiene la integridad en el acceso al sistema

# Elimina los paquetes
root@server:~# yum -y remove noderig beamium ovh-rtm-metrics-toolkit \
                             ovh-rtm-binaries;

# Elimina los archivos
root@server:~# rm -rf /opt/noderig/;
```
