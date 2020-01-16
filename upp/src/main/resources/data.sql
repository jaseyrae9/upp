-----------------------------------------
-- dodavanje naucnih oblasti
INSERT INTO academic_field(id, name) 
SELECT 1, 'fizika'
WHERE 
NOT EXISTS (
    SELECT 1 FROM academic_field WHERE name = 'fizika'
);

INSERT INTO academic_field(id, name) 
SELECT 2, 'matematika'
WHERE 
NOT EXISTS (
    SELECT 2 FROM academic_field WHERE name = 'matematika'
);
------------------------------------------------------------
-- dodavanja uloga
INSERT INTO authority(id, name) 
SELECT 1, 'ADMIN'
WHERE 
NOT EXISTS (
    SELECT 1 FROM authority WHERE name = 'ADMIN'
);

INSERT INTO authority(id, name) 
SELECT 2, 'CUSTOMER'
WHERE 
NOT EXISTS (
    SELECT 2 FROM authority WHERE name = 'CUSTOMER'
);

INSERT INTO authority(id, name) 
SELECT 3, 'REVIEWER'
WHERE 
NOT EXISTS (
    SELECT 3 FROM authority WHERE name = 'REVIEWER'
);

INSERT INTO authority(id, name) 
SELECT 4, 'EDITORINCHIEF'
WHERE 
NOT EXISTS (
    SELECT 4 FROM authority WHERE name = 'EDITORINCHIEF'
);

INSERT INTO authority(id, name) 
SELECT 5, 'EDITOR'
WHERE 
NOT EXISTS (
    SELECT 5 FROM authority WHERE name = 'EDITOR'
);
---------------------------------------------------------------------------
-- dodavanje demo korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, city, country, title, wants_to_be_reviewer, journal_id) 
SELECT 'admin', 999, true, 'demo@camunda.org', 'Demo', 'Demo', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'demo', false, true, 'grad', 'drzava', 'title', false, null
WHERE 
NOT EXISTS (
    SELECT 999 FROM users WHERE username = 'demo'
);

-------------------------------------------------------------------------------
-- dodavanje veze izmedju demo korisnika i admin uloge

INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'demo' AND a.name = 'ADMIN' AND 
	NOT EXISTS (
    	SELECT 999 FROM users_authorities WHERE my_user_id = u.id
	);
----------------------------------------------------------------------------
-- dodavanje urednik korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, city, country, title, wants_to_be_reviewer, journal_id) 
SELECT 'customer', 9999, true, 'urednik@camunda.org', 'Urednik', 'Urednik', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik', false, true, 'grad', 'drzava', 'title', false, null
WHERE 
NOT EXISTS (
    SELECT 9999 FROM users WHERE username = 'urednik'
);	

----------------------------------------------------------------
-- dodavanje veze izmedju urednik korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik' AND a.name = 'EDITOR' AND 
	NOT EXISTS (
    	SELECT 9999 FROM users_authorities WHERE my_user_id = u.id
	);
--------------------------------------------------------------------	

-- dodavanje urednik1 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, city, country, title, wants_to_be_reviewer, journal_id) 
SELECT 'customer', 99999, true, 'urednik1@camunda.org', 'Urednik', 'Urednik', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik1', false, true, 'grad', 'drzava', 'title', false, null
WHERE 
NOT EXISTS (
    SELECT 99999 FROM users WHERE username = 'urednik1'
);	
----------------------------------------------------------------
-- dodavanje veze izmedju urednik1 korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik1' AND a.name = 'EDITOR' AND 
	NOT EXISTS (
    	SELECT 99999 FROM users_authorities WHERE my_user_id = u.id
	);
--------------------------------------------------------------------	
-- dodavanje veze izmedju urednik1 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik1' AND a.name = 'fizika' AND 
	NOT EXISTS (
    	SELECT 99999 FROM  users_academic_fields WHERE customer_id = u.id
	);
--------------------------------------------------------------------
	
-- dodavanje recenzenta korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, city, country, title, wants_to_be_reviewer, journal_id) 
SELECT 'customer', 2017, true, 'recenzent@camunda.org', 'recenzent', 'recenzent', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent', true, true, 'grad', 'drzava', 'title', false, null
WHERE 
NOT EXISTS (
    SELECT 2017 FROM users WHERE username = 'recenzent'
);

-------------------------------------------------------------------------------

-- dodavanje veze izmedju recenzent korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent' AND a.name = 'REVIEWER' AND 
	NOT EXISTS (
    	SELECT 2017 FROM users_authorities WHERE my_user_id = u.id
	);
--------------------------------------------------------------------	
-- dodavanje veze izmedju recenzenta korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent' AND a.name = 'matematika' AND 
	NOT EXISTS (
    	SELECT 2017 FROM  users_academic_fields WHERE customer_id = u.id
	);
--------------------------------------------------------------------
	
-- dodavanje urednik2 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, city, country, title, wants_to_be_reviewer, journal_id) 
SELECT 'customer', 2000, true, 'urednik2@camunda.org', 'Urednik', 'Urednik', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik2', false, true, 'grad', 'drzava', 'title', false, null
WHERE 
NOT EXISTS (
    SELECT 2000 FROM users WHERE username = 'urednik2'
);	
----------------------------------------------------------------
-- dodavanje veze izmedju urednik korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik2' AND a.name = 'EDITOR' AND 
	NOT EXISTS (
    	SELECT 2000 FROM users_authorities WHERE my_user_id = u.id
	);
--------------------------------------------------------------------	
-- dodavanje veze izmedju urednik1 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik2' AND a.name = 'fizika' AND 
	NOT EXISTS (
    	SELECT 2000 FROM  users_academic_fields WHERE customer_id = u.id
	);
--------------------------------------------------------------------
