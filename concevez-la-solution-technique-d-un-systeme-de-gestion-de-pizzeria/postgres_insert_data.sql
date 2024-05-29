--
-- PostgreSQL database dump
--

-- Dumped from database version 12.4
-- Dumped by pg_dump version 12.4

-- Started on 2020-09-21 12:55:54 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3318 (class 0 OID 16422)
-- Dependencies: 207
-- Data for Name: account; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (0, 'Gironnet', 'Damien', '2, square Bainville 78150 Le Chesnay', '045069', 'dgironnet', 'dgironnet@yahoo.fr', '0659435643');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (1, 'Durpayre', 'Francois', '34, rue des Hallots', '054958', 'fdurpayre', 'fdurpayre@gmail.com', '0439485760');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (2, 'Soulet', 'Jeanne', '24, square de la cité', '023941', 'souletj', 'souletj@hotmail.fr', '0329493291');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (3, 'Casanova', 'Pierre', '21, allée des soges', '045969', 'pcasanova', 'pcasanova@laposte.net', '0654932124');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (4, 'Dutarte', 'Jean', '32, résidence d''Orly', '043954', 'jdutarte', 'jdutarte@yahoo.fr', '0324432981');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (5, 'Montois', 'Anne', '12, square des pierres', '043945', 'amontois', 'amontois@gmail.com', '0659432541');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (6, 'Neymar', 'Jr', '23, rue de Versailles', '045963', 'jNeymar', 'jneymar@gmail.com', '0439845762');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (7, 'Di Maria', 'Angel', '14, square du peuplier', '123456', 'dimaria', 'dimaria@gmail.com', '0549854319');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (8, 'Griezmann', 'Antoine', '32, rue du tilleuil', '043984', 'agriezmann', 'agriezmann@yahoo.fr', '0694309123');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (9, 'Zidane', 'Zinedine', '45, allée des fougères', '023948', 'zzidane', 'zzidane@gmail.com', '0123914325');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (10, 'Touré', 'José', '54, avenue des pins', '012393', 'jtoure', 'jtoure@gmail.com', '0678912345');
INSERT INTO public.account (account_id, first_name, last_name, address, password, login, email, phone) VALUES (11, 'Messi', 'Léo', '32, rue de Barcelone', '012948', 'lmessi', 'lmessi', '0629384721');

--
-- TOC entry 3314 (class 0 OID 16393)
-- Dependencies: 203
-- Data for Name: category; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.category (category_id, parent_category_id, category) VALUES (0, NULL, 'Pizzas');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (1, 0, 'Classiques');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (2, 0, 'suprêmes');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (3, 0, 'les incontournables');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (4, NULL, 'Boissons');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (5, 4, 'Sans Alcool');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (6, 4, 'Alcoolisés');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (7, 5, 'Canettes');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (8, 5, 'Bouteilles');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (9, 6, 'Canettes');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (10, 6, 'Bouteilles');
INSERT INTO public.category (category_id, parent_category_id, category) VALUES (11, 0, 'Exotiques');


--
-- TOC entry 3316 (class 0 OID 16409)
-- Dependencies: 205
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.item (item_id, category_id) VALUES (0, 1);
INSERT INTO public.item (item_id, category_id) VALUES (1, 1);
INSERT INTO public.item (item_id, category_id) VALUES (2, 2);
INSERT INTO public.item (item_id, category_id) VALUES (3, 2);
INSERT INTO public.item (item_id, category_id) VALUES (4, 3);
INSERT INTO public.item (item_id, category_id) VALUES (5, 3);
INSERT INTO public.item (item_id, category_id) VALUES (6, 3);
INSERT INTO public.item (item_id, category_id) VALUES (7, 7);
INSERT INTO public.item (item_id, category_id) VALUES (8, 8);
INSERT INTO public.item (item_id, category_id) VALUES (9, 9);
INSERT INTO public.item (item_id, category_id) VALUES (10, 10);
INSERT INTO public.item (item_id, category_id) VALUES (11, 3);
INSERT INTO public.item (item_id, category_id) VALUES (12, 1);

--
-- TOC entry 3317 (class 0 OID 16414)
-- Dependencies: 206
-- Data for Name: article; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.article (article_id, name, unit_price) VALUES (7, 'CocaCola 30cl', 2.9);
INSERT INTO public.article (article_id, name, unit_price) VALUES (8, 'Orangina 1L', 4.9);
INSERT INTO public.article (article_id, name, unit_price) VALUES (9, '1664 50cl', 1.9);
INSERT INTO public.article (article_id, name, unit_price) VALUES (10, 'Heineken 75cl', 3.5);
INSERT INTO public.article (article_id, name, unit_price) VALUES (11, 'SevenUp 30 cl', 2.9);

--
-- TOC entry 3325 (class 0 OID 16475)
-- Dependencies: 214
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.payment (payment_id, payment) VALUES (0, 'DELIVERY');
INSERT INTO public.payment (payment_id, payment) VALUES (1, 'INSTORE');
INSERT INTO public.payment (payment_id, payment) VALUES (2, 'ONLINE');
INSERT INTO public.payment (payment_id, payment) VALUES (3, 'NONE');


--
-- TOC entry 3323 (class 0 OID 16464)
-- Dependencies: 212
-- Data for Name: command_status; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.command_status (status_id, status) VALUES (0, 'PLACED');
INSERT INTO public.command_status (status_id, status) VALUES (1, 'PENDING');
INSERT INTO public.command_status (status_id, status) VALUES (2, 'IN_PREPARATION');
INSERT INTO public.command_status (status_id, status) VALUES (3, 'PREPARED');
INSERT INTO public.command_status (status_id, status) VALUES (4, 'IN_COOKING');
INSERT INTO public.command_status (status_id, status) VALUES (5, 'COOKED');
INSERT INTO public.command_status (status_id, status) VALUES (6, 'READY');
INSERT INTO public.command_status (status_id, status) VALUES (7, 'IN_DELIVERY');
INSERT INTO public.command_status (status_id, status) VALUES (8, 'TO_TAKE_AWAY');
INSERT INTO public.command_status (status_id, status) VALUES (9, 'RECEIVED');
INSERT INTO public.command_status (status_id, status) VALUES (10, 'UNPAID');
INSERT INTO public.command_status (status_id, status) VALUES (11, 'PAID');

--
-- TOC entry 3315 (class 0 OID 16401)
-- Dependencies: 204
-- Data for Name: command_type; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.command_type (command_type_id, command_type) VALUES (0, 'INTERNET');
INSERT INTO public.command_type (command_type_id, command_type) VALUES (1, 'NON_INTERNET');


--
-- TOC entry 3336 (class 0 OID 16536)
-- Dependencies: 225
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customer (customer_id, account_id) VALUES (0, 0);
INSERT INTO public.customer (customer_id, account_id) VALUES (1, 1);
INSERT INTO public.customer (customer_id, account_id) VALUES (2, 7);
INSERT INTO public.customer (customer_id, account_id) VALUES (3, 8);
INSERT INTO public.customer (customer_id, account_id) VALUES (4, 9);
INSERT INTO public.customer (customer_id, account_id) VALUES (5, 10);

--
-- TOC entry 3338 (class 0 OID 16544)
-- Dependencies: 227
-- Data for Name: command; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.command (command_id, amount, payment_type_id, status_id, customer_id, command_type_id) VALUES (0, 23.5, 0, 8, 4, 1);
INSERT INTO public.command (command_id, amount, payment_type_id, status_id, customer_id, command_type_id) VALUES (1, 32.9, 1, 5, 2, 0);
INSERT INTO public.command (command_id, amount, payment_type_id, status_id, customer_id, command_type_id) VALUES (2, 31.4, 0, 4, 0, 0);
INSERT INTO public.command (command_id, amount, payment_type_id, status_id, customer_id, command_type_id) VALUES (3, 12.5, 0, 0, 1, 1);

--
-- TOC entry 3340 (class 0 OID 16555)
-- Dependencies: 229
-- Data for Name: command_line; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (0, 1, 1, 12.5);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (0, 3, 2, 23.5);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (1, 4, 3, 21);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (2, 2, 1, 23.5);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (2, 0, 1, 16.5);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (3, 1, 2, 23.5);
INSERT INTO public.command_line (command_id, item_id, number, total) VALUES (3, 6, 2, 12.5);


--
-- TOC entry 3339 (class 0 OID 16550)
-- Dependencies: 228
-- Data for Name: customer_favorite_commands; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.customer_favorite_commands (customer_id, command_id) VALUES (0, 0);
INSERT INTO public.customer_favorite_commands (customer_id, command_id) VALUES (1, 1);
INSERT INTO public.customer_favorite_commands (customer_id, command_id) VALUES (0, 2);
INSERT INTO public.customer_favorite_commands (customer_id, command_id) VALUES (2, 3);

--
-- TOC entry 3327 (class 0 OID 16486)
-- Dependencies: 216
-- Data for Name: stock_access; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (0, 'CREATE');
INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (1, 'READ');
INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (2, 'UPDATE');
INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (3, 'DELETE');
INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (4, 'NONE');
INSERT INTO public.stock_access (stock_access_id, stock_access) VALUES (5, 'ALL');


--
-- TOC entry 3329 (class 0 OID 16497)
-- Dependencies: 218
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.role (role_id, role, stock_access_id, payment_right_id) VALUES (0, 'ADMINISTRATOR', 5, 2);
INSERT INTO public.role (role_id, role, stock_access_id, payment_right_id) VALUES (1, 'DELIVERY', 4, 0);
INSERT INTO public.role (role_id, role, stock_access_id, payment_right_id) VALUES (2, 'COOK', 2, 1);

--
-- TOC entry 3330 (class 0 OID 16506)
-- Dependencies: 219
-- Data for Name: shop; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.shop (shop_id, name, address, postal_code, city, country, phone, email) VALUES (0, 'OCPizza Le Chesnay', '32, avenue de Versailles', '78150', 'Le Chesnay', 'France', '0139555950', 'https://www.pizzahut.com');
INSERT INTO public.shop (shop_id, name, address, postal_code, city, country, phone, email) VALUES (1, 'OCPizza Versailles', '32, boulevard de la reine', '78000', 'Versailles', 'France', '0139434901', 'https://www.lanapoli.com');
INSERT INTO public.shop (shop_id, name, address, postal_code, city, country, phone, email) VALUES (2, 'OCPizza Viroflay', '34, rue des roues', '78590', 'Viroflay', 'France', '0139546958', 'https://www.dominos.com');
INSERT INTO public.shop (shop_id, name, address, postal_code, city, country, phone, email) VALUES (3, 'OCPizza Boulogne-Billancourt', '23, avenue du moulin', '92380', 'Boulogne-Billancourt', 'France', '0123459302', 'https://www.allopizza.fr');


--
-- TOC entry 3334 (class 0 OID 16529)
-- Dependencies: 223
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (0, 0, 0, 0);
INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (1, 0, 1, 1);
INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (2, 1, 0, 2);
INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (3, 1, 1, 3);
INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (4, 2, 0, 4);
INSERT INTO public.employee (employee_id, role_id, shop_id, account_id) VALUES (5, 2, 1, 5);


--
-- TOC entry 3319 (class 0 OID 16430)
-- Dependencies: 208
-- Data for Name: ingredient; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.ingredient (ingredient_id, name) VALUES (0, 'pâte à pizza');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (1, 'sauce tomate');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (2, 'gruyère rappé');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (3, 'oignon');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (4, 'poivron rouge');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (5, 'poivron vert');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (6, 'poivron jaune');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (7, 'boulette de viande');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (8, 'sauce piquante');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (9, 'champignon');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (10, 'chorizzo');
INSERT INTO public.ingredient (ingredient_id, name) VALUES (11, 'poulet');

-- TOC entry 3321 (class 0 OID 16440)
-- Dependencies: 210
-- Data for Name: size; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.size (size_id, size) VALUES (0, 'MEDIUM');
INSERT INTO public.size (size_id, size) VALUES (1, 'LARGE');
INSERT INTO public.size (size_id, size) VALUES (2, 'XLARGE');

--
-- TOC entry 3341 (class 0 OID 16691)
-- Dependencies: 230
-- Data for Name: pizza; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (0, 0, 'Marguerita', 'recette de la marguerita', 14.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (0, 1, 'Marguerita', 'recette de la marguerita', 17.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (0, 2, 'Marguerita', 'recette de la marguerita', 21.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (1, 0, 'Vegane', 'recette de la Vegane', 13.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (1, 1, 'Vegane', 'recette de la Vegane', 16.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (1, 2, 'Vegane', 'recette de la Vegane', 19.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (2, 0, 'Extravaggenza', 'recette de l''Extravaggenza', 13.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (2, 1, 'Extravaggenza', 'recette de l''Extravaggenza', 17.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (2, 2, 'Extravaggenza', 'recette de l''Extravaggenza', 21.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (3, 0, 'Mexicana', 'recette de la Mexicana', 14.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (3, 1, 'Mexicana', 'recette de la Mexicana', 18.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (3, 2, 'Mexicana', 'recette de la Mexicana', 23.5);
INSERT INTO public.pizza (pizza_id, size_id, name, recipe, unit_price) VALUES (12, 0, 'India', 'recette de L''India', 14.5);


--
-- TOC entry 3342 (class 0 OID 16699)
-- Dependencies: 231
-- Data for Name: ingredient_pizza; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (0, 0);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (0, 1);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (0, 2);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (1, 0);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (1, 1);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (1, 2);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (1, 3);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (1, 7);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (2, 0);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (2, 1);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (2, 5);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (2, 6);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (3, 0);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (3, 1);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (3, 2);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (3, 4);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (4, 0);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (4, 1);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (4, 8);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (4, 9);
INSERT INTO public.ingredient_pizza (pizza_id, ingredient_id) VALUES (4, 11);


--



--

--
-- TOC entry 3333 (class 0 OID 16524)
-- Dependencies: 222
-- Data for Name: stock_articles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (0, 7, 21);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (0, 8, 32);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (0, 9, 12);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (0, 10, 10);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (1, 7, 12);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (1, 8, 2);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (1, 9, 10);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (1, 10, 9);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (2, 8, 2);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (2, 9, 12);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (3, 10, 10);
INSERT INTO public.shop_articles (shop_id, article_id, quantity) VALUES (3, 8, 10);


--
-- TOC entry 3332 (class 0 OID 16519)
-- Dependencies: 221
-- Data for Name: stock_ingredients; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (0, 1, 500);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (0, 2, 500);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (0, 3, 500);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (0, 4, 1000);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (1, 1, 600);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (1, 2, 400);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (1, 3, 700);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (1, 4, 800);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (2, 0, 1000);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (2, 3, 500);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (2, 4, 600);
INSERT INTO public.shop_ingredients (shop_id, ingredient_id, quantity) VALUES (2, 1, 500);


--
-- TOC entry 3356 (class 0 OID 0)
-- Dependencies: 226
-- Name: command_command_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.command_command_id_seq', 1, false);


--
-- TOC entry 3357 (class 0 OID 0)
-- Dependencies: 211
-- Name: command_status_status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.command_status_status_id_seq', 1, false);


--
-- TOC entry 3358 (class 0 OID 0)
-- Dependencies: 224
-- Name: customer_customer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.customer_customer_id_seq', 1, false);


--
-- TOC entry 3359 (class 0 OID 0)
-- Dependencies: 213
-- Name: payment_payment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.payment_payment_id_seq', 1, false);


--
-- TOC entry 3360 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.role_role_id_seq', 1, false);


--
-- TOC entry 3361 (class 0 OID 0)
-- Dependencies: 209
-- Name: size_size_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.size_size_id_seq', 1, false);


--
-- TOC entry 3362 (class 0 OID 0)
-- Dependencies: 215
-- Name: stock_access_stock_access_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.stock_access_stock_access_id_seq', 1, false);

SET default_tablespace = '';


