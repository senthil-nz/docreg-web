# DocReg+Web

A distributed document management system web interface.
Created by Scott Abernethy (github @scott-abernethy).

## Setup on Ubuntu
The following setup has been tested on Ubuntu 12.04 LTS.

### Install dependencies
1. Install git, sshfs, mysql-server
```bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get install oracle-java6-installer
sudo apt-get install git sshfs mysql-server
```
2. Install Oracle Java 6 (there are many ways to do this, my preference is below)
```bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get install oracle-java6-installer
sudo apt-get install git sshfs mysql-server
```
3. Get the project source
```bash
PROJECTROOT=~/docreg-web
git clone https://github.com/scott-abernethy/docreg-web.git $PROJECTROOT
```
4. Create the database
```base
cd $PROJECTROOT
mysql -u root -p < ./src/main/resources/schema
```
5. Mirror a DocReg server home directory (because the app requires local filesystem access)
```bash
YOURUSERNAME=sabernethy
sudo mkdir -p /home/docreg
sudo chown $YOURUSERNAME /home/docreg
sudo gpasswd -a $YOURUSERNAME fuse
sshfs -o idmap=user,nonempty docreg@shelob: /home/docreg
```
6. Configure the app
```bash
sudo cat >>/etc/docreg-web.conf <<EOF
db {
   driver = com.mysql.jdbc.Driver
   url = jdbc:mysql://localhost/docregweb
   user = root
}
agent {
   server = shelob
   home = /home/docreg
   secure = true
}
ldap {
   url = "ldap://dcgnetnz1.gnet.global.vpn:3268"
   user = "gnet\$YOURUSERNAME"
   password = "$YOURPASSWORD"
}
EOF
```
7. Start the app
```bash
cd $PROJECTROOT
./sbt
> container:start
```

## License

DocReg+Web is distributed under the [GNU General Public License v3](http://www.gnu.org/licenses/gpl-3.0.html).
