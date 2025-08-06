@echo off
echo Running database migration to fix userId column sizes...
echo.
echo This script will fix the "value too long for type character varying(255)" error
echo by updating the userId columns to support longer user IDs.
echo.
echo Make sure you have access to your PostgreSQL database before running this.
echo.
pause

echo Executing migration script...
psql -h ep-quiet-dawn-a180e803-pooler.ap-southeast-1.aws.neon.tech -U neondb_owner -d failforward_db -f fix_user_id_columns.sql

echo.
echo Migration completed!
pause 