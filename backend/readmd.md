# MySQL 개발용 서버 띄우기
```bash
# MySQL 없을 때 띄우는 방법
cd ~ # 운영환경에서는 `cd /`

# 설정파일 만들기
mkdir -p dockerProjects/mysql-1/volumes/etc/mysql/conf.d

# 원하는 설정을 적어주세요.
chmod 644 dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf
echo "[mysqld]
general_log = ON
general_log_file = /etc/mysql/conf.d/general.log" > dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf
chmod 444 dockerProjects/mysql-1/volumes/etc/mysql/conf.d/my.cnf

docker run \
    --name mysql-1 \
    -p 3306:3306 \
    -v /${PWD}/dockerProjects/mysql-1/volumes/var/lib/mysql:/var/lib/mysql \
    -v /${PWD}/dockerProjects/mysql-1/volumes/etc/mysql/conf.d:/etc/mysql/conf.d \
    -e TZ=Asia/Seoul \
    -e MYSQL_ROOT_PASSWORD=lldj123414 \
    -d \
    mysql:8.4.1
```

# MySQL 실시간 쿼리 로그 확인
```bash
echo '' > ~/dockerProjects/mysql-1/volumes/etc/mysql/conf.d/general.log && tail -f ~/dockerProjects/mysql-1/volumes/etc/mysql/conf.d/general.log
```