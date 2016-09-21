# nablarch-testing 

## 依存ライブラリ

本モジュールのコンパイルまたはテストには、下記ライブラリを手動でローカルリポジトリへインストールする必要があります。

ライブラリ             |ファイル名       |グループID     |アーティファクトID   |バージョン   |
:----------------------|:----------------|:--------------|:--------------------|:------------|
Oracle JDBC Driver     |ojdbc6.jar       |com.oracle     |ojdbc6               |11.2.0.2.0   |
Oracle UCP for JDBC    |ucp.jar          |com.oracle     |ucp                  |11.2.0.3.0   |
DB2 JDBC Driver        |db2jcc4.jar      |com.ibm        |db2jcc4              |9.7.200.358  |
SQLServer JDBC Driver  |sqljdbc4.jar     |com.microsoft  |sqljdbc4             |4.0          |


上記ライブラリは、下記コマンドでインストールしてください。


```
mvn install:install-file -Dfile=<ファイル名> -DgroupId=<グループID> -DartifactId=<アーティファクトID> -Dversion=<バージョン> -Dpackaging=jar
```
