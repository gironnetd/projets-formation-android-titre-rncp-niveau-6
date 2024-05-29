--
-- PostgreSQL database dump
--

-- Dumped from database version 12.4
-- Dumped by pg_dump version 12.4

-- Started on 2020-09-21 13:03:35 CEST

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
-- TOC entry 4 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 3319 (class 0 OID 0)
-- Dependencies: 4
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 207 (class 1259 OID 16422)
-- Name: account; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.account (
    account_id integer NOT NULL,
    first_name character varying NOT NULL,
    last_name character varying NOT NULL,
    address character varying,
    password character varying NOT NULL,
    login character varying NOT NULL,
    email character varying,
    phone character varying NOT NULL
);


ALTER TABLE public.account OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 16414)
-- Name: article; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.article (
    article_id integer NOT NULL,
    name character varying NOT NULL,
    unit_price real NOT NULL
);


ALTER TABLE public.article OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 16393)
-- Name: category; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.category (
    category_id integer NOT NULL,
    parent_category_id integer,
    category character varying NOT NULL
);


ALTER TABLE public.category OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16544)
-- Name: command; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.command (
    command_id integer NOT NULL,
    amount real NOT NULL,
    payment_type_id integer NOT NULL,
    status_id integer NOT NULL,
    customer_id integer NOT NULL,
    command_type_id integer NOT NULL
);


ALTER TABLE public.command OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16542)
-- Name: command_command_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.command_command_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.command_command_id_seq OWNER TO postgres;

--
-- TOC entry 3320 (class 0 OID 0)
-- Dependencies: 226
-- Name: command_command_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.command_command_id_seq OWNED BY public.command.command_id;


--
-- TOC entry 229 (class 1259 OID 16555)
-- Name: command_line; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.command_line (
    command_id integer NOT NULL,
    item_id integer NOT NULL,
    number integer NOT NULL,
    total real NOT NULL
);


ALTER TABLE public.command_line OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16464)
-- Name: command_status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.command_status (
    status_id integer NOT NULL,
    status character varying NOT NULL
);


ALTER TABLE public.command_status OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 16462)
-- Name: command_status_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.command_status_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.command_status_status_id_seq OWNER TO postgres;

--
-- TOC entry 3321 (class 0 OID 0)
-- Dependencies: 211
-- Name: command_status_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.command_status_status_id_seq OWNED BY public.command_status.status_id;


--
-- TOC entry 204 (class 1259 OID 16401)
-- Name: command_type; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.command_type (
    command_type_id integer NOT NULL,
    command_type character varying NOT NULL
);


ALTER TABLE public.command_type OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16536)
-- Name: customer; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer (
    customer_id integer NOT NULL,
    account_id integer NOT NULL
);


ALTER TABLE public.customer OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16534)
-- Name: customer_customer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.customer_customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customer_customer_id_seq OWNER TO postgres;

--
-- TOC entry 3322 (class 0 OID 0)
-- Dependencies: 224
-- Name: customer_customer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.customer_customer_id_seq OWNED BY public.customer.customer_id;


--
-- TOC entry 228 (class 1259 OID 16550)
-- Name: customer_favorite_commands; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customer_favorite_commands (
    customer_id integer NOT NULL,
    command_id integer NOT NULL
);


ALTER TABLE public.customer_favorite_commands OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16529)
-- Name: employee; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employee (
    employee_id integer NOT NULL,
    role_id integer NOT NULL,
    shop_id integer NOT NULL,
    account_id integer NOT NULL
);


ALTER TABLE public.employee OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 16430)
-- Name: ingredient; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ingredient (
    ingredient_id integer NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE public.ingredient OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16699)
-- Name: ingredient_pizza; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ingredient_pizza (
    pizza_id integer NOT NULL,
    ingredient_id integer NOT NULL
);


ALTER TABLE public.ingredient_pizza OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 16409)
-- Name: item; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item (
    item_id integer NOT NULL,
    category_id integer NOT NULL
);


ALTER TABLE public.item OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16475)
-- Name: payment; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment (
    payment_id integer NOT NULL,
    payment character varying NOT NULL
);


ALTER TABLE public.payment OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16473)
-- Name: payment_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.payment_payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.payment_payment_id_seq OWNER TO postgres;

--
-- TOC entry 3323 (class 0 OID 0)
-- Dependencies: 213
-- Name: payment_payment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.payment_payment_id_seq OWNED BY public.payment.payment_id;


--
-- TOC entry 230 (class 1259 OID 16691)
-- Name: pizza; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pizza (
    pizza_id integer NOT NULL,
    size_id integer NOT NULL,
    name character varying NOT NULL,
    recipe character varying NOT NULL,
    unit_price real NOT NULL
);


ALTER TABLE public.pizza OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16497)
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    role_id integer NOT NULL,
    role character varying NOT NULL,
    stock_access_id integer NOT NULL,
    payment_right_id integer NOT NULL
);


ALTER TABLE public.role OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16495)
-- Name: role_role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_role_id_seq OWNER TO postgres;

--
-- TOC entry 3324 (class 0 OID 0)
-- Dependencies: 217
-- Name: role_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_role_id_seq OWNED BY public.role.role_id;


--
-- TOC entry 219 (class 1259 OID 16506)
-- Name: shop; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shop (
    shop_id integer NOT NULL,
    name character varying NOT NULL,
    address character varying NOT NULL,
    postal_code character varying NOT NULL,
    city character varying NOT NULL,
    country character varying NOT NULL,
    phone character varying NOT NULL,
    email character varying NOT NULL
);


ALTER TABLE public.shop OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16440)
-- Name: size; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.size (
    size_id integer NOT NULL,
    size character varying NOT NULL
);


ALTER TABLE public.size OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 16438)
-- Name: size_size_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.size_size_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.size_size_id_seq OWNER TO postgres;

--
-- TOC entry 3325 (class 0 OID 0)
-- Dependencies: 209
-- Name: size_size_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.size_size_id_seq OWNED BY public.size.size_id;


--
-- TOC entry 216 (class 1259 OID 16486)
-- Name: stock_access; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.stock_access (
    stock_access_id integer NOT NULL,
    stock_access character varying NOT NULL
);


ALTER TABLE public.stock_access OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16484)
-- Name: stock_access_stock_access_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.stock_access_stock_access_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.stock_access_stock_access_id_seq OWNER TO postgres;

--
-- TOC entry 3326 (class 0 OID 0)
-- Dependencies: 215
-- Name: stock_access_stock_access_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.stock_access_stock_access_id_seq OWNED BY public.stock_access.stock_access_id;


--
-- TOC entry 222 (class 1259 OID 16524)
-- Name: stock_articles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shop_articles (
    shop_id integer NOT NULL,
    article_id integer NOT NULL,
    quantity integer NOT NULL
);


ALTER TABLE public.shop_articles OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16519)
-- Name: stock_ingredients; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shop_ingredients (
    shop_id integer NOT NULL,
    ingredient_id integer NOT NULL,
    quantity integer NOT NULL
);


ALTER TABLE public.shop_ingredients OWNER TO postgres;

--
-- TOC entry 3121 (class 2604 OID 16547)
-- Name: command command_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command ALTER COLUMN command_id SET DEFAULT nextval('public.command_command_id_seq'::regclass);


--
-- TOC entry 3116 (class 2604 OID 16467)
-- Name: command_status status_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_status ALTER COLUMN status_id SET DEFAULT nextval('public.command_status_status_id_seq'::regclass);


--
-- TOC entry 3120 (class 2604 OID 16539)
-- Name: customer customer_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer ALTER COLUMN customer_id SET DEFAULT nextval('public.customer_customer_id_seq'::regclass);


--
-- TOC entry 3117 (class 2604 OID 16478)
-- Name: payment payment_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment ALTER COLUMN payment_id SET DEFAULT nextval('public.payment_payment_id_seq'::regclass);


--
-- TOC entry 3119 (class 2604 OID 16500)
-- Name: role role_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN role_id SET DEFAULT nextval('public.role_role_id_seq'::regclass);


--
-- TOC entry 3115 (class 2604 OID 16443)
-- Name: size size_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.size ALTER COLUMN size_id SET DEFAULT nextval('public.size_size_id_seq'::regclass);


--
-- TOC entry 3118 (class 2604 OID 16489)
-- Name: stock_access stock_access_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stock_access ALTER COLUMN stock_access_id SET DEFAULT nextval('public.stock_access_stock_access_id_seq'::regclass);


--
-- TOC entry 3131 (class 2606 OID 16429)
-- Name: account account_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pk PRIMARY KEY (account_id);


--
-- TOC entry 3129 (class 2606 OID 16421)
-- Name: article article_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT article_pk PRIMARY KEY (article_id);


--
-- TOC entry 3123 (class 2606 OID 16400)
-- Name: category category_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pk PRIMARY KEY (category_id);


--
-- TOC entry 3161 (class 2606 OID 16559)
-- Name: command_line command_line_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_line
    ADD CONSTRAINT command_line_id PRIMARY KEY (command_id, item_id);


--
-- TOC entry 3125 (class 2606 OID 16408)
-- Name: command_type command_type_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_type
    ADD CONSTRAINT command_type_pk PRIMARY KEY (command_type_id);


--
-- TOC entry 3157 (class 2606 OID 16549)
-- Name: command commandid; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command
    ADD CONSTRAINT commandid PRIMARY KEY (command_id);


--
-- TOC entry 3159 (class 2606 OID 16554)
-- Name: customer_favorite_commands customer_favorite_commands_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_favorite_commands
    ADD CONSTRAINT customer_favorite_commands_pk PRIMARY KEY (customer_id, command_id);


--
-- TOC entry 3155 (class 2606 OID 16541)
-- Name: customer customer_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_id PRIMARY KEY (customer_id);


--
-- TOC entry 3153 (class 2606 OID 16533)
-- Name: employee employee_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT employee_id PRIMARY KEY (employee_id);


--
-- TOC entry 3133 (class 2606 OID 16437)
-- Name: ingredient ingredient_name; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ingredient
    ADD CONSTRAINT ingredient_name PRIMARY KEY (ingredient_id);


--
-- TOC entry 3165 (class 2606 OID 16703)
-- Name: ingredient_pizza ingredient_pizza_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ingredient_pizza
    ADD CONSTRAINT ingredient_pizza_pk PRIMARY KEY (pizza_id, ingredient_id);


--
-- TOC entry 3127 (class 2606 OID 16413)
-- Name: item item_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT item_pk PRIMARY KEY (item_id);


--
-- TOC entry 3139 (class 2606 OID 16483)
-- Name: payment payment_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_id PRIMARY KEY (payment_id);


--
-- TOC entry 3163 (class 2606 OID 16698)
-- Name: pizza pizza_name; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pizza
    ADD CONSTRAINT pizza_name PRIMARY KEY (pizza_id, size_id);


--
-- TOC entry 3143 (class 2606 OID 16505)
-- Name: role role_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_id PRIMARY KEY (role_id);


--
-- TOC entry 3145 (class 2606 OID 16513)
-- Name: shop shop_name; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop
    ADD CONSTRAINT shop_name PRIMARY KEY (shop_id);


--
-- TOC entry 3135 (class 2606 OID 16448)
-- Name: size size_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.size
    ADD CONSTRAINT size_id PRIMARY KEY (size_id);


--
-- TOC entry 3137 (class 2606 OID 16472)
-- Name: command_status status_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_status
    ADD CONSTRAINT status_id PRIMARY KEY (status_id);


--
-- TOC entry 3141 (class 2606 OID 16494)
-- Name: stock_access stock_access_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.stock_access
    ADD CONSTRAINT stock_access_id PRIMARY KEY (stock_access_id);


--
-- TOC entry 3151 (class 2606 OID 16528)
-- Name: stock_articles stock_articles_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_articles
    ADD CONSTRAINT shop_articles_pk PRIMARY KEY (shop_id, article_id);


--
-- TOC entry 3149 (class 2606 OID 16523)
-- Name: stock_ingredients stock_ingredients_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_ingredients
    ADD CONSTRAINT shop_ingredients_pk PRIMARY KEY (shop_id, ingredient_id);


--
-- TOC entry 3179 (class 2606 OID 16600)
-- Name: customer account_customer_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT account_customer_fk FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- TOC entry 3176 (class 2606 OID 16595)
-- Name: employee account_employee_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT account_employee_fk FOREIGN KEY (account_id) REFERENCES public.account(account_id);


--
-- TOC entry 3174 (class 2606 OID 16590)
-- Name: stock_articles article_stock_articles_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_articles
    ADD CONSTRAINT article_shop_articles_fk FOREIGN KEY (article_id) REFERENCES public.article(article_id);


--
-- TOC entry 3166 (class 2606 OID 16560)
-- Name: category category_category_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_category_fk FOREIGN KEY (parent_category_id) REFERENCES public.category(category_id);


--
-- TOC entry 3167 (class 2606 OID 16565)
-- Name: item category_item_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item
    ADD CONSTRAINT category_item_fk FOREIGN KEY (category_id) REFERENCES public.category(category_id);


--
-- TOC entry 3184 (class 2606 OID 16675)
-- Name: customer_favorite_commands customer_customer_orders_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_favorite_commands
    ADD CONSTRAINT customer_customer_orders_fk FOREIGN KEY (customer_id) REFERENCES public.customer(customer_id);


--
-- TOC entry 3183 (class 2606 OID 16670)
-- Name: command customer_order_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command
    ADD CONSTRAINT customer_order_fk FOREIGN KEY (customer_id) REFERENCES public.customer(customer_id);


--
-- TOC entry 3177 (class 2606 OID 16645)
-- Name: employee function_employee_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT function_employee_fk FOREIGN KEY (role_id) REFERENCES public.role(role_id);


--
-- TOC entry 3172 (class 2606 OID 16610)
-- Name: stock_ingredients ingredient_shop_ingredients_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_ingredients
    ADD CONSTRAINT ingredient_shop_ingredients_fk FOREIGN KEY (ingredient_id) REFERENCES public.ingredient(ingredient_id);


--
-- TOC entry 3168 (class 2606 OID 16580)
-- Name: article item_article_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.article
    ADD CONSTRAINT item_article_fk FOREIGN KEY (article_id) REFERENCES public.item(item_id);


--
-- TOC entry 3186 (class 2606 OID 16585)
-- Name: command_line item_order_line_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_line
    ADD CONSTRAINT item_order_line_fk FOREIGN KEY (item_id) REFERENCES public.item(item_id);


--
-- TOC entry 3185 (class 2606 OID 16685)
-- Name: customer_favorite_commands order_customer_orders_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customer_favorite_commands
    ADD CONSTRAINT order_customer_orders_fk FOREIGN KEY (command_id) REFERENCES public.command(command_id);


--
-- TOC entry 3187 (class 2606 OID 16680)
-- Name: command_line order_order_pizzas_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command_line
    ADD CONSTRAINT order_order_pizzas_fk FOREIGN KEY (command_id) REFERENCES public.command(command_id);


--
-- TOC entry 3180 (class 2606 OID 16570)
-- Name: command ordertype_command_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command
    ADD CONSTRAINT ordertype_command_fk FOREIGN KEY (command_type_id) REFERENCES public.command_type(command_type_id);


--
-- TOC entry 3182 (class 2606 OID 16630)
-- Name: command payment_order_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command
    ADD CONSTRAINT payment_order_fk FOREIGN KEY (payment_type_id) REFERENCES public.payment(payment_id);


--
-- TOC entry 3169 (class 2606 OID 16635)
-- Name: role payment_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT payment_role_fk FOREIGN KEY (payment_right_id) REFERENCES public.payment(payment_id);


--
-- TOC entry 3178 (class 2606 OID 16650)
-- Name: employee shop_employee_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employee
    ADD CONSTRAINT shop_employee_fk FOREIGN KEY (shop_id) REFERENCES public.shop(shop_id);

--
-- TOC entry 3181 (class 2606 OID 16625)
-- Name: command status_order_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.command
    ADD CONSTRAINT status_order_fk FOREIGN KEY (status_id) REFERENCES public.command_status(status_id);


--
-- TOC entry 3170 (class 2606 OID 16640)
-- Name: role stock_access_role_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT stock_access_role_fk FOREIGN KEY (stock_access_id) REFERENCES public.stock_access(stock_access_id);


--
-- TOC entry 3173 (class 2606 OID 16665)
-- Name: stock_ingredients stock_shop_ingredients_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_ingredients
    ADD CONSTRAINT shop_shop_ingredients_fk FOREIGN KEY (shop_id) REFERENCES public.shop(shop_id);


--
-- TOC entry 3175 (class 2606 OID 16660)
-- Name: stock_articles stock_stock_articles_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shop_articles
    ADD CONSTRAINT shop_shop_articles_fk FOREIGN KEY (shop_id) REFERENCES public.shop(shop_id);


-- Completed on 2020-09-21 13:03:36 CEST

--
-- PostgreSQL database dump complete
--

