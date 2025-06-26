-- Fix password_hash column type to match Spring Boot JPA expectations
-- The JPA entity expects String but database has bytea

-- Change password_hash column from bytea to varchar
ALTER TABLE public."User" 
ALTER COLUMN password_hash TYPE varchar USING encode(password_hash, 'base64'); 