--
-- PostgreSQL database dump
--

-- Dumped from database version 16.8 (Ubuntu 16.8-0ubuntu0.24.04.1)
-- Dumped by pg_dump version 17.4

-- Started on 2025-05-02 20:00:20

--SET statement_timeout = 0;
--SET lock_timeout = 0;
--SET idle_in_transaction_session_timeout = 0;
--SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
--SET standard_conforming_strings = on;
--SELECT pg_catalog.set_config('search_path', '', false);
--SET check_function_bodies = false;
--SET xmloption = content;
--SET client_min_messages = warning;
--SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 27032)
-- Name: cliente; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.cliente (
    clienteid integer NOT NULL,
    "contraseña" character varying(100) NOT NULL,
    estado boolean DEFAULT true,
    identificacion character varying(20) NOT NULL
);


ALTER TABLE public.cliente OWNER TO admin;

--
-- TOC entry 216 (class 1259 OID 27031)
-- Name: cliente_clienteid_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.cliente_clienteid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cliente_clienteid_seq OWNER TO admin;

--
-- TOC entry 3380 (class 0 OID 0)
-- Dependencies: 216
-- Name: cliente_clienteid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.cliente_clienteid_seq OWNED BY public.cliente.clienteid;


--
-- TOC entry 219 (class 1259 OID 27045)
-- Name: cuenta; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.cuenta (
    numero_cuenta integer NOT NULL,
    tipo_cuenta character varying(20) NOT NULL,
    saldo_inicial numeric(12,2) DEFAULT 0.00 NOT NULL,
    estado boolean DEFAULT true,
    clienteid integer NOT NULL,
    CONSTRAINT cuenta_tipo_cuenta_check CHECK (((tipo_cuenta)::text = ANY ((ARRAY['AHORRO'::character varying, 'CORRIENTE'::character varying])::text[])))
);


ALTER TABLE public.cuenta OWNER TO admin;

--
-- TOC entry 218 (class 1259 OID 27044)
-- Name: cuenta_numero_cuenta_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.cuenta_numero_cuenta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cuenta_numero_cuenta_seq OWNER TO admin;

--
-- TOC entry 3381 (class 0 OID 0)
-- Dependencies: 218
-- Name: cuenta_numero_cuenta_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.cuenta_numero_cuenta_seq OWNED BY public.cuenta.numero_cuenta;


--
-- TOC entry 221 (class 1259 OID 27060)
-- Name: movimientos; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.movimientos (
    codigo integer NOT NULL,
    fecha timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    tipo_movimiento character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL,
    saldo numeric(12,2) NOT NULL,
    numero_cuenta integer NOT NULL,
    CONSTRAINT movimientos_tipo_movimiento_check CHECK (((tipo_movimiento)::text = ANY ((ARRAY['DEPOSITO'::character varying, 'RETIRO'::character varying])::text[])))
);


ALTER TABLE public.movimientos OWNER TO admin;

--
-- TOC entry 220 (class 1259 OID 27059)
-- Name: movimientos_codigo_seq; Type: SEQUENCE; Schema: public; Owner: admin
--

CREATE SEQUENCE public.movimientos_codigo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.movimientos_codigo_seq OWNER TO admin;

--
-- TOC entry 3382 (class 0 OID 0)
-- Dependencies: 220
-- Name: movimientos_codigo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: admin
--

ALTER SEQUENCE public.movimientos_codigo_seq OWNED BY public.movimientos.codigo;


--
-- TOC entry 215 (class 1259 OID 27024)
-- Name: persona; Type: TABLE; Schema: public; Owner: admin
--

CREATE TABLE public.persona (
    identificacion character varying(20) NOT NULL,
    nombre character varying(100) NOT NULL,
    genero character(1),
    edad integer,
    direccion character varying(200),
    telefono character varying(20),
    CONSTRAINT persona_edad_check CHECK ((edad >= 0)),
    CONSTRAINT persona_genero_check CHECK ((genero = ANY (ARRAY['M'::bpchar, 'F'::bpchar, 'O'::bpchar])))
);


ALTER TABLE public.persona OWNER TO admin;

--
-- TOC entry 3198 (class 2604 OID 27035)
-- Name: cliente clienteid; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cliente ALTER COLUMN clienteid SET DEFAULT nextval('public.cliente_clienteid_seq'::regclass);


--
-- TOC entry 3200 (class 2604 OID 27048)
-- Name: cuenta numero_cuenta; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cuenta ALTER COLUMN numero_cuenta SET DEFAULT nextval('public.cuenta_numero_cuenta_seq'::regclass);


--
-- TOC entry 3203 (class 2604 OID 27063)
-- Name: movimientos codigo; Type: DEFAULT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.movimientos ALTER COLUMN codigo SET DEFAULT nextval('public.movimientos_codigo_seq'::regclass);


--
-- TOC entry 3370 (class 0 OID 27032)
-- Dependencies: 217
-- Data for Name: cliente; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.cliente (clienteid, "contraseña", estado, identificacion) FROM stdin;
1	123456	t	0923146120
\.


--
-- TOC entry 3372 (class 0 OID 27045)
-- Dependencies: 219
-- Data for Name: cuenta; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, estado, clienteid) FROM stdin;
\.


--
-- TOC entry 3374 (class 0 OID 27060)
-- Dependencies: 221
-- Data for Name: movimientos; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.movimientos (codigo, fecha, tipo_movimiento, valor, saldo, numero_cuenta) FROM stdin;
\.


--
-- TOC entry 3368 (class 0 OID 27024)
-- Dependencies: 215
-- Data for Name: persona; Type: TABLE DATA; Schema: public; Owner: admin
--

COPY public.persona (identificacion, nombre, genero, edad, direccion, telefono) FROM stdin;
0923146120	Christian Arreaga	M	25	Quito	0980658301
\.


--
-- TOC entry 3383 (class 0 OID 0)
-- Dependencies: 216
-- Name: cliente_clienteid_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.cliente_clienteid_seq', 1, true);


--
-- TOC entry 3384 (class 0 OID 0)
-- Dependencies: 218
-- Name: cuenta_numero_cuenta_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.cuenta_numero_cuenta_seq', 1, false);


--
-- TOC entry 3385 (class 0 OID 0)
-- Dependencies: 220
-- Name: movimientos_codigo_seq; Type: SEQUENCE SET; Schema: public; Owner: admin
--

SELECT pg_catalog.setval('public.movimientos_codigo_seq', 1, false);


--
-- TOC entry 3213 (class 2606 OID 27038)
-- Name: cliente cliente_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT cliente_pkey PRIMARY KEY (clienteid);


--
-- TOC entry 3216 (class 2606 OID 27053)
-- Name: cuenta cuenta_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT cuenta_pkey PRIMARY KEY (numero_cuenta);


--
-- TOC entry 3221 (class 2606 OID 27067)
-- Name: movimientos movimientos_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT movimientos_pkey PRIMARY KEY (codigo);


--
-- TOC entry 3211 (class 2606 OID 27030)
-- Name: persona persona_pkey; Type: CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.persona
    ADD CONSTRAINT persona_pkey PRIMARY KEY (identificacion);


--
-- TOC entry 3214 (class 1259 OID 27074)
-- Name: idx_cliente_identificacion; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_cliente_identificacion ON public.cliente USING btree (identificacion);


--
-- TOC entry 3217 (class 1259 OID 27075)
-- Name: idx_cuenta_clienteid; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_cuenta_clienteid ON public.cuenta USING btree (clienteid);


--
-- TOC entry 3218 (class 1259 OID 27077)
-- Name: idx_movimientos_fecha; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_movimientos_fecha ON public.movimientos USING btree (fecha);


--
-- TOC entry 3219 (class 1259 OID 27076)
-- Name: idx_movimientos_numero_cuenta; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_movimientos_numero_cuenta ON public.movimientos USING btree (numero_cuenta);


--
-- TOC entry 3209 (class 1259 OID 27073)
-- Name: idx_persona_nombre; Type: INDEX; Schema: public; Owner: admin
--

CREATE INDEX idx_persona_nombre ON public.persona USING btree (nombre);


--
-- TOC entry 3223 (class 2606 OID 27054)
-- Name: cuenta fk_cliente; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cuenta
    ADD CONSTRAINT fk_cliente FOREIGN KEY (clienteid) REFERENCES public.cliente(clienteid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3224 (class 2606 OID 27068)
-- Name: movimientos fk_cuenta; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.movimientos
    ADD CONSTRAINT fk_cuenta FOREIGN KEY (numero_cuenta) REFERENCES public.cuenta(numero_cuenta) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3222 (class 2606 OID 27039)
-- Name: cliente fk_persona; Type: FK CONSTRAINT; Schema: public; Owner: admin
--

ALTER TABLE ONLY public.cliente
    ADD CONSTRAINT fk_persona FOREIGN KEY (identificacion) REFERENCES public.persona(identificacion) ON UPDATE CASCADE ON DELETE CASCADE;


-- Completed on 2025-05-02 20:00:30

--
-- PostgreSQL database dump complete
--

