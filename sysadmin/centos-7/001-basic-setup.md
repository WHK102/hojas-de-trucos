# Basic initial setups for CentOS 7

It is assumed that all commands are executed with root permissions unless you
say otherwise.


## Initial configurations

Initial configurations and packages required for installation and future
configurations.

Set UTF8 as default input for shell

```bash
export LC_CTYPE=en_US.UTF-8;
export LC_ALL=en_US.UTF-8;
```


### Initial packages

Update the packages to prevent problems when trying to install a package that is
not in the list of available applications.

```bash
yum -y update;
```

Install essential applications

```bash
yum -y install nano tree tmux rsync wget htop net-tools bash-completion openssl man;
```

If `nano` accommodates you more and not `vi`, you can set `nano` as the default
terminal editor in the system:

```bash
export EDITOR=nano;
export VISUAL=nano;
echo 'export EDITOR=nano;' >> ~/.bashrc;
echo 'export VISUAL=nano;' >> ~/.bashrc;
```


### Disabling SeLinux

You can disable `SeLinux` if is required (by example, for `http-itk`). The
content of file `/etc/sysconfig/selinux` should read:

```ini
SELINUX = disabled
```

Change the value from process:

```bash
setenforce 0;
```

You can check the SeLinux status:

```bash
sestatus
SELinux status:                 disabled
```

If you have disabled selinux you must disable it also from boot, otherwise the
kernel will try to load the disabled module and the system will not start. The
content of file `/etc/grub2.cfg` you must replace:

- Find: `crashkernel=auto`
- Replace by: `crashkernel=auto selinux=0`

You can replace this using `nano` editor and `Ctrl+W` for find, and `Ctrl+R` for
replace.

For undo changes you can execute:

```bash
grub2-mkconfig -o /etc/grub2.cfg
```


### Disabling IPv6 and additional network settings

For disable ipv6 you need edit the content of file `/etc/sysctl.conf`:

```bash
cat /etc/sysctl.conf;
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
net.ipv6.conf.lo.disable_ipv6 = 1
net.ipv4.ip_forward = 1
```

Enable port redirections (for ssh socks5 or proxies):

```bash
# Check file content (is required)
cat /proc/sys/net/ipv4/ip_forward;
1

# Force to enable port redirects
sysctl -w net.ipv4.ip_forward=1;
```

### End initial configurations

For finish initial configuration you need restart the system. You can use
`reboot` command. This is the only time that a system reboot will be required.


## Eliminate unnecessary services

Eliminate services that are not required or that may cause conflicts and
security problems in the future:

`rpcbind` in 111 port for remote management is susceptible to UDP attacks.

```bash
systemctl stop rpcbind;
systemctl disable rpcbind;
systemctl mask rpcbind;
systemctl stop rpcbind.socket;
systemctl disable rpcbind.socket;
```

Delete firewalld if you want to use iptables or none:

```bash
systemctl stop firewalld;
systemctl disable firewalld;
systemctl mask firewalld;
```

You can install iptables if you want:

```bash
yum -y install iptables iptables-services;
systemctl enable iptables;

# Make the rules as permanent on reboot
sed -c -i "s/\(IPTABLES_SAVE_ON_RESTART=*\).*/\1\"yes\"/" /etc/sysconfig/iptables-config;
sed -c -i "s/\(IPTABLES_SAVE_ON_STOP=*\).*/\1\"yes\"/" /etc/sysconfig/iptables-config;
```

## Setting SSH

Generate keys and file access:

```bash
# For fast generation using the values by default
ssh-keygen -t rsa -N "" -f ~/.ssh/id_rsa;

# For remote access
touch ~/.ssh/authorized_keys;
chmod 600 ~/.ssh/authorized_keys;
```

Setting the SSH service editing the file `/etc/ssh/sshd_config`:

```text
# For remote administration
PermitRootLogin yes

# For secure access (only using the RSA public key)
PubkeyAuthentication yes

# Disable password authentication for secure reasons (prevent bruteforce access)
PasswordAuthentication no

# Disable if you will not use it, makes authentication faster by avoiding having
# to perform validations for each type of credential management.
KerberosAuthentication no
GSSAPIAuthentication no

# Disable for security reasons. Prevent open remote windows from server using
# SSH
X11Forwarding no

# For fast login. The SSH service can take too long if it is not able to resolve
# the domain name of your remote connection.
UseDNS no
```

You can modify the welcome banner when login into SSH server editing the files
`/etc/issue` and `/etc/motd`.

Apply changes:

```bash
systemctl restart sshd;
```


## Enable repositories

Additional repositories are used to install required applications below, in
addition to very basic tools. It is recommended to always keep these
repositories enabled throughout the life of the server.


### Installing Epel Releases repository

```bash
# Reset the cache content
yum clean all
yum -y update

# Optional but not required: yum -y install centos-release-scl
yum -y install epel-release

# Disable mirrors for fast checkout
cat /etc/yum/pluginconf.d/fastestmirror.conf;
enabled=0
```

Disable mirrors and use the main repository. Depending on the country or
provider, some mirrors are slower and outdated than the main repository. There
is no guarantee using mirrors.

Uncomment `baseurl` and comment `mirrorlist` and `metalink`:

```bash
cat /etc/yum.repos.d/epel.repo
baseurl=http://...
#mirrorlist=...
#metalink=...
```

For finish, clear all cache and renew the applications list:

```bash
yum clean all
rm -rf /var/cache/yum
yum -y update
```


### Installing DeltaRPM repository

Install only after epel.

```bash
yum -y install deltarpm
```

It is necessary to update the packages because epel updates to deltarpm and
deltarpm updates to epel:

```bash
yum -y update
```


## Enable automatic upgrades

In CentOS very rarely you will see a compatibility problem when updating a
package because you will rarely see updates from major versions that use the
same package name. Remember that Redhat prioritizes stability and control.

```bash
# Install the application that will take care of keeping automatic updates.
yum -y install yum-cron;
systemctl enable yum-cron.service;
systemctl start yum-cron.service;

# Enable automatic upgrades without messages
sed -c -i "s/\(update_messages*\).*/\1\ = no/" /etc/yum/yum-cron.conf;
sed -c -i "s/\(apply_updates*\).*/\1\ = yes/" /etc/yum/yum-cron.conf;

# Apply changes
systemctl restart yum-cron.service;
```


## Setting dynamic timezone and date time

In some countries such as Chile, the time zone is not respected by the
government when a time change is made, in many cases it is necessary to manually
set the times on our computers because the government does not use computer
standards. Therefore, in my case I use an ntp server which is officially
provided ( http://www.horaoficial.cl/doc/sincronizacion.pdf ):

```bash
# Install required app
yum -y install ntp ntpdate

# Set the NTP server
echo 'server ntp.shoa.cl' >> /etc/ntp.conf

# Change timezone of server to Chile
rm -f /etc/localtime;
ln -s /usr/share/zoneinfo/Chile/Continental /etc/localtime;

# Apply changes and enable the service
systemctl enable ntpd.service
systemctl restart ntpd.service

# Sync the NTP server
ntpd -gq

# Validate the current date and time
date
```


## Additional and optional applications

Here are a number of applications and packages that can be installed. The vast
majority avoid problems of compilation, installation of other packages, etc.

The `Development Tools` group  install a series of development packages that add
headers, libraries and avoid problems when you want to try to install packages
manually by compiling them:

```bash
yum -y groupinstall 'Development Tools';
```

Other apps:

```bash
# For advanced programed tasks
yum -y install vixie-cron cronie;

# For install helpers and alternative versions of packages, like as
# php 5 and 7 or ruby 2.7 or 2.5
yum -y install yum-plugin-replace yum-utils rpm-build initscripts tidy;

# For sysadmin monitoring
yum -y install top htop iotop net-tools ssldump tcpdump libtool libdnet \
               libdnet-devel bison libpcap libpcap-devel sysstat libaio;

# For paranoic sysadmin monitoring
yum -y install rkhunter tshark nmap;

# DNS client tools like as dig command.
yum -y install bind-utils;

# For fast search files
yum -y install mlocate;

# For sysadmin testing and helpers for remote packages installing
yum -y install nc wget curl links ftp zlib lbzip2 unzip unrar;

# For sysadmin/developers (devops)
yum -y install subversion git hgvgcc g++ cmake make automake autoconf \
               zlib-devel apr-devel gd apr-util-devel openssl-devel curl-devel \
               libpng-devel libX11-devel glib2-devel libexif flex;

# For developers or server compatible wih complex scripts
yum -y install perl perl-core python python-pip urw-fonts libgdi* mencoder;
```


## For OVH dedicated instances

Eliminate OVH monitoring services, avoid security problems and maintain
integrity in system access:

```bash
yum -y remove noderig beamium ovh-rtm-metrics-toolkit ovh-rtm-binaries
rm -rf /opt/noderig/
```
