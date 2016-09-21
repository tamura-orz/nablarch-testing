CREATE USER ssd
      IDENTIFIED BY ssd
             DEFAULT tablespace USERS
            TEMPORARY tablespace TEMP
;
GRANT DBA TO nablarch_demo,ssd
;
