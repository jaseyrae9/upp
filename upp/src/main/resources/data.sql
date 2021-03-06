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

INSERT INTO authority(id, name) 
SELECT 6, 'AUTHOR'
WHERE 
NOT EXISTS (
    SELECT 6 FROM authority WHERE name = 'AUTHOR'
);

INSERT INTO authority(id, name) 
SELECT 7, 'BUYER'
WHERE 
NOT EXISTS (
    SELECT 7  FROM authority WHERE name = 'BUYER'
);
---------------------------------------------------------------------------
-- dodavanje demo korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 999, true, 'demo@camunda.org', 'Demo', 'Demo', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'demo', false, true, true,'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 999 FROM users WHERE username = 'demo'
);
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
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 1000, true, 'urednik@camunda.org', 'Urednik', 'Urednik', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik', false, true, true, 'grad', 'drzava', 'title', false, null, '991c77ae-7eb1-4d5e-be4f-cde7f816ac12'
WHERE 
NOT EXISTS (
    SELECT 1000 FROM users WHERE username = 'urednik'
);  
-- dodavanje veze izmedju urednik korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik' AND a.name = 'EDITOR' AND 
    NOT EXISTS (
        SELECT 1000 FROM users_authorities WHERE my_user_id = u.id
    );
-- dodavanje veze izmedju urednik korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 1000 FROM  users_academic_fields WHERE customer_id = u.id
    );
--------------------------------------------------------------------


-- dodavanje urednik1 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 1001, true, 'urednik1@camunda.org', 'urednik1', 'urednik1', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik1', false, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 1001 FROM users WHERE username = 'urednik1'
);  
-- dodavanje veze izmedju urednik1 korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik1' AND a.name = 'EDITOR' AND 
    NOT EXISTS (
        SELECT 1001 FROM users_authorities WHERE my_user_id = u.id
    );   
-- dodavanje veze izmedju urednik1 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik1' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 1001 FROM  users_academic_fields WHERE customer_id = u.id
    );
--------------------------------------------------------------------
    
-- dodavanje urednik2 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 1002, true, 'urednik2@camunda.org', 'urednik2', 'urednik2', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik2', false, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 1002 FROM users WHERE username = 'urednik2'
);  
-- dodavanje veze izmedju urednik2 korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik2' AND a.name = 'EDITOR' AND 
    NOT EXISTS (
        SELECT 1002 FROM users_authorities WHERE my_user_id = u.id
    );
    
-- dodavanje veze izmedju urednik2 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik2' AND a.name = 'matematika' AND 
    NOT EXISTS (
        SELECT 1002 FROM  users_academic_fields WHERE customer_id = u.id
    );
--------------------------------------------------------------------
    
-- dodavanje urednik3 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 1003, true, 'urednik3@camunda.org', 'Urednik3', 'Urednik3', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'urednik3', false, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 1003 FROM users WHERE username = 'urednik3'
);  
-- dodavanje veze izmedju urednik3 korisnika i editor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'urednik3' AND a.name = 'EDITOR' AND 
    NOT EXISTS (
        SELECT 1003 FROM users_authorities WHERE my_user_id = u.id
    );
    
-- dodavanje veze izmedju urednik3 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'urednik3' AND a.name = 'matematika' AND 
    NOT EXISTS (
        SELECT 1003 FROM  users_academic_fields WHERE customer_id = u.id
    );
--------------------------------------------------------------------    
-- dodavanje recenzenta korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2000, true, 'recenzent@camunda.org', 'recenzent', 'recenzent', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent', true, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2000 FROM users WHERE username = 'recenzent'
);
-- dodavanje veze izmedju recenzent korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2000 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzenta korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2000 FROM  users_academic_fields WHERE customer_id = u.id
    );

--------------------------------------------------------------------
-- dodavanje recenzent1 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2001, true, 'recenzent1@camunda.org', 'recenzent1', 'recenzent1', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent1', true, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2001 FROM users WHERE username = 'recenzent1'
);
-- dodavanje veze izmedju recenzent1 korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent1' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2001 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzent1 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent1' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2001 FROM  users_academic_fields WHERE customer_id = u.id
    );

--------------------------------------------------------------------
-- dodavanje recenzent2 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2002, true, 'recenzent2@camunda.org', 'recenzent2', 'recenzent2', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent2', true, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2002 FROM users WHERE username = 'recenzent2'
);
-- dodavanje veze izmedju recenzent2 korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent2' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2002 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzent2 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent2' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2002 FROM  users_academic_fields WHERE customer_id = u.id
    );
-------------------------------------------------------------------
-- dodavanje recenzent3 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2003, true, 'recenzent3@camunda.org', 'recenzent3', 'recenzent3', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent3', true, true, true,'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2003 FROM users WHERE username = 'recenzent3'
);
-- dodavanje veze izmedju recenzent korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent3' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2003 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzenta korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent3' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2003 FROM  users_academic_fields WHERE customer_id = u.id
    );

--------------------------------------------------------------------
-- dodavanje recenzent4 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2004, true, 'recenzent4@camunda.org', 'recenzent', 'recenzent', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent4', true, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2004 FROM users WHERE username = 'recenzent4'
);
-- dodavanje veze izmedju recenzent4 korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent4' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2004 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzent4 korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent4' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2004 FROM  users_academic_fields WHERE customer_id = u.id
    );

--------------------------------------------------------------------
-- dodavanje recenzent5 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active,active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 2005, true, 'recenzent5@camunda.org', 'recenzent', 'recenzent', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'recenzent5', true, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 2005 FROM users WHERE username = 'recenzent5'
);
-- dodavanje veze izmedju recenzent korisnika i recenzent uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'recenzent5' AND a.name = 'REVIEWER' AND 
    NOT EXISTS (
        SELECT 2005 FROM users_authorities WHERE my_user_id = u.id
    ); 
-- dodavanje veze izmedju recenzenta korisnika i naucnih oblasti
INSERT INTO users_academic_fields(customer_id, academic_fields_id) 
SELECT u.id, a.id
FROM users u, academic_field a
WHERE u.username = 'recenzent5' AND a.name = 'fizika' AND 
    NOT EXISTS (
        SELECT 2005 FROM  users_academic_fields WHERE customer_id = u.id
    );

--------------------------------------------------------------------
    -- dodavanje autor korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 3000, true, 'autor@camunda.org', 'autor', 'autor', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'autor', false, true, false, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 3000 FROM users WHERE username = 'autor'
);
-- dodavanje veze izmedju autor korisnika i autor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'autor' AND a.name = 'AUTHOR' AND 
    NOT EXISTS (
        SELECT 3000 FROM users_authorities WHERE my_user_id = u.id
    );
 --------------------------------------------------------------------
    -- dodavanje autor1 korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'customer', 3001, true, 'autor1@camunda.org', 'autor1', 'autor1', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'autor1', false, true, true, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 3001 FROM users WHERE username = 'autor1'
);
-- dodavanje veze izmedju autor1 korisnika i autor uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'autor1' AND a.name = 'AUTHOR' AND 
    NOT EXISTS (
        SELECT 3001 FROM users_authorities WHERE my_user_id = u.id
    );
 --------------------------------------------------------------------
    -- dodavanje kupac korisnika
INSERT INTO users(type, id, confirmed_mail, email, first_name, last_name, password, username, accepted_as_reviewer, active, active_membership, city, country, title, wants_to_be_reviewer, journal_id, api_key) 
SELECT 'buyer', 4001, true, 'kupac@camunda.org', 'kupac', 'kupac', '$2a$10$Zp/LQilwI4szts.Yc5xBSehHCwICvmxeCOxwbhwSfE5vxAUFvflXO', 'kupac', false, true, false, 'grad', 'drzava', 'title', false, null, null
WHERE 
NOT EXISTS (
    SELECT 4001 FROM users WHERE username = 'kupac'
);
-- dodavanje veze izmedju kupac korisnika i kupac uloge
INSERT INTO users_authorities(my_user_id, authorities_id) 
SELECT u.id, a.id
FROM users u, authority a
WHERE u.username = 'kupac' AND a.name = 'BUYER' AND 
    NOT EXISTS (
        SELECT 4001 FROM users_authorities WHERE my_user_id = u.id
    );
    