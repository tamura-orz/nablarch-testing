select =
SELECT
 *
FROM
 BATCH_SAMPLE


update =
UPDATE
  BATCH_SAMPLE
SET
  COUNTER = ?,
  UPDATE_DATE = ?
WHERE
  ID = ?


insert =
INSERT INTO 
  HOGE_TABLE (
    PK_COL1,
    PK_COL2,
    NUMBER_COL,
    NUMBER_COL2,
    DATE_COL,
    TIMESTAMP_COL
  )
  VALUES (
    ?,
    ?,
    0,
    0,
    '1970-01-01',
    '1970-01-01'
  )
