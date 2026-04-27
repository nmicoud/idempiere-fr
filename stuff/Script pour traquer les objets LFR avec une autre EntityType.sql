SET SERVEROUTPUT ON SIZE UNLIMITED;

DECLARE
    v_sql   VARCHAR2(4000);

    TYPE refcur IS REF CURSOR;
    c       refcur;

    v_name         VARCHAR2(255);
    v_entitytype   VARCHAR2(50);

    -- spécifiques AD
    v_tablename    VARCHAR2(255);
    v_tabname      VARCHAR2(255);
    v_windowname   VARCHAR2(255);
    v_processname  VARCHAR2(255);

BEGIN
    FOR t IN (
        SELECT t.table_name
        FROM all_tables t
        WHERE t.owner = USER
          AND EXISTS (
              SELECT 1
              FROM all_tab_columns c
              WHERE c.table_name = t.table_name
                AND c.column_name = 'ENTITYTYPE'
                AND c.owner = t.owner
          )
          AND EXISTS (
              SELECT 1
              FROM all_tab_columns c
              WHERE c.table_name = t.table_name
                AND c.column_name = 'NAME'
                AND c.owner = t.owner
          )
    )
    LOOP

        -- Cas AD_COLUMN
        IF t.table_name = 'AD_COLUMN' THEN

            v_sql := '
                SELECT c.NAME, c.ENTITYTYPE, t.TABLENAME
                FROM AD_COLUMN c
                JOIN AD_TABLE t ON c.AD_TABLE_ID = t.AD_TABLE_ID
                WHERE c.NAME LIKE ''LFR%''
                  AND c.ENTITYTYPE <> ''LFR''
            ';

            OPEN c FOR v_sql;
            LOOP
                FETCH c INTO v_name, v_entitytype, v_tablename;
                EXIT WHEN c%NOTFOUND;

                DBMS_OUTPUT.PUT_LINE(
                    'AD_COLUMN | ' || v_tablename || ' | ' || v_name || ' | ' || v_entitytype
                );
            END LOOP;
            CLOSE c;

        -- Cas AD_FIELD
        ELSIF t.table_name = 'AD_FIELD' THEN

            v_sql := '
                SELECT f.NAME, f.ENTITYTYPE, t.NAME, w.NAME
                FROM AD_FIELD f
                JOIN AD_TAB t ON f.AD_TAB_ID = t.AD_TAB_ID
                JOIN AD_WINDOW w ON t.AD_WINDOW_ID = w.AD_WINDOW_ID
                WHERE f.NAME LIKE ''LFR%''
                  AND f.ENTITYTYPE <> ''LFR''
            ';

            OPEN c FOR v_sql;
            LOOP
                FETCH c INTO v_name, v_entitytype, v_tabname, v_windowname;
                EXIT WHEN c%NOTFOUND;

                DBMS_OUTPUT.PUT_LINE(
                    'AD_FIELD | ' || v_windowname || ' | ' || v_tabname || ' | ' || v_name || ' | ' || v_entitytype
                );
            END LOOP;
            CLOSE c;

        -- ✅ Cas AD_PROCESS_PARA
        ELSIF t.table_name = 'AD_PROCESS_PARA' THEN

            v_sql := '
                SELECT p.NAME, p.ENTITYTYPE, pr.NAME
                FROM AD_PROCESS_PARA p
                JOIN AD_PROCESS pr ON p.AD_PROCESS_ID = pr.AD_PROCESS_ID
                WHERE p.NAME LIKE ''LFR%''
                  AND p.ENTITYTYPE <> ''LFR''
            ';

            OPEN c FOR v_sql;
            LOOP
                FETCH c INTO v_name, v_entitytype, v_processname;
                EXIT WHEN c%NOTFOUND;

                DBMS_OUTPUT.PUT_LINE(
                    'AD_PROCESS_PARA | ' || v_processname || ' | ' || v_name || ' | ' || v_entitytype
                );
            END LOOP;
            CLOSE c;

        -- Cas général
        ELSE
            v_sql := '
                SELECT NAME, ENTITYTYPE
                FROM ' || t.table_name || '
                WHERE NAME LIKE ''LFR%''
                  AND ENTITYTYPE <> ''LFR''
            ';

            OPEN c FOR v_sql;
            LOOP
                FETCH c INTO v_name, v_entitytype;
                EXIT WHEN c%NOTFOUND;

                DBMS_OUTPUT.PUT_LINE(
                    t.table_name || ' | ' || v_name || ' | ' || v_entitytype
                );
            END LOOP;
            CLOSE c;

        END IF;

    END LOOP;
END;
/