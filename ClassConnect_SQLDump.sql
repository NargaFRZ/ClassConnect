--
-- PostgreSQL database dump
--

-- Dumped from database version 16.3
-- Dumped by pg_dump version 16.2

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
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: roles; Type: TYPE; Schema: public; Owner: Fairuz
--

CREATE TYPE public.roles AS ENUM (
    'student',
    'teacher',
    'admin'
);


ALTER TYPE public.roles OWNER TO "Fairuz";

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: assignments; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.assignments (
    assignment_id integer NOT NULL,
    class_id uuid NOT NULL,
    title character varying(255),
    assignment text,
    description text,
    due_date date
);


ALTER TABLE public.assignments OWNER TO "Fairuz";

--
-- Name: assignments_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: Fairuz
--

CREATE SEQUENCE public.assignments_assignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.assignments_assignment_id_seq OWNER TO "Fairuz";

--
-- Name: assignments_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: Fairuz
--

ALTER SEQUENCE public.assignments_assignment_id_seq OWNED BY public.assignments.assignment_id;


--
-- Name: classes; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.classes (
    class_id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(255),
    description text,
    enrollment_key character varying(255)
);


ALTER TABLE public.classes OWNER TO "Fairuz";

--
-- Name: grades; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.grades (
    grade_id integer NOT NULL,
    submission_id integer,
    score double precision,
    feedback text
);


ALTER TABLE public.grades OWNER TO "Fairuz";

--
-- Name: grades_grade_id_seq; Type: SEQUENCE; Schema: public; Owner: Fairuz
--

CREATE SEQUENCE public.grades_grade_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.grades_grade_id_seq OWNER TO "Fairuz";

--
-- Name: grades_grade_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: Fairuz
--

ALTER SEQUENCE public.grades_grade_id_seq OWNED BY public.grades.grade_id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.messages (
    message_id integer NOT NULL,
    sender_id uuid,
    receiver_id uuid,
    content text
);


ALTER TABLE public.messages OWNER TO "Fairuz";

--
-- Name: messages_message_id_seq; Type: SEQUENCE; Schema: public; Owner: Fairuz
--

CREATE SEQUENCE public.messages_message_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.messages_message_id_seq OWNER TO "Fairuz";

--
-- Name: messages_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: Fairuz
--

ALTER SEQUENCE public.messages_message_id_seq OWNED BY public.messages.message_id;


--
-- Name: studentclass; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.studentclass (
    class_id uuid NOT NULL,
    student_id uuid NOT NULL
);


ALTER TABLE public.studentclass OWNER TO "Fairuz";

--
-- Name: submission; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.submission (
    submission_id integer NOT NULL,
    assignment_id integer,
    student_id uuid,
    submission text,
    submitted_date date
);


ALTER TABLE public.submission OWNER TO "Fairuz";

--
-- Name: submission_submission_id_seq; Type: SEQUENCE; Schema: public; Owner: Fairuz
--

CREATE SEQUENCE public.submission_submission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.submission_submission_id_seq OWNER TO "Fairuz";

--
-- Name: submission_submission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: Fairuz
--

ALTER SEQUENCE public.submission_submission_id_seq OWNED BY public.submission.submission_id;


--
-- Name: teacherclass; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.teacherclass (
    class_id uuid NOT NULL,
    teacher_id uuid NOT NULL
);


ALTER TABLE public.teacherclass OWNER TO "Fairuz";

--
-- Name: users; Type: TABLE; Schema: public; Owner: Fairuz
--

CREATE TABLE public.users (
    user_id uuid DEFAULT gen_random_uuid() NOT NULL,
    name character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(255) NOT NULL,
    approved boolean DEFAULT false,
    role public.roles NOT NULL
);


ALTER TABLE public.users OWNER TO "Fairuz";

--
-- Name: assignments assignment_id; Type: DEFAULT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.assignments ALTER COLUMN assignment_id SET DEFAULT nextval('public.assignments_assignment_id_seq'::regclass);


--
-- Name: grades grade_id; Type: DEFAULT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.grades ALTER COLUMN grade_id SET DEFAULT nextval('public.grades_grade_id_seq'::regclass);


--
-- Name: messages message_id; Type: DEFAULT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.messages ALTER COLUMN message_id SET DEFAULT nextval('public.messages_message_id_seq'::regclass);


--
-- Name: submission submission_id; Type: DEFAULT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.submission ALTER COLUMN submission_id SET DEFAULT nextval('public.submission_submission_id_seq'::regclass);


--
-- Data for Name: assignments; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.assignments (assignment_id, class_id, title, assignment, description, due_date) FROM stdin;
1	201a9a50-e191-4338-a746-8ce2f9f44c28	Minecraft	D:\\Documents\\Term 4\\Netlab Oprec\\Proyek OOP\\ClassConnect-API\\uploads\\TI10_Evandita_Wiratama.pdf	Get diamond armor before the deadlone	2024-07-01
\.


--
-- Data for Name: classes; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.classes (class_id, name, description, enrollment_key) FROM stdin;
201a9a50-e191-4338-a746-8ce2f9f44c28	Class 1	This is class 1	class1
\.


--
-- Data for Name: grades; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.grades (grade_id, submission_id, score, feedback) FROM stdin;
1	4	100	Nice Job
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.messages (message_id, sender_id, receiver_id, content) FROM stdin;
1	3c89df00-888f-4424-8fec-403c368c3eae	f432c016-b5fd-4125-bc86-512d4df1f48b	Hello teach, i am here to extend you about warrantu
2	f432c016-b5fd-4125-bc86-512d4df1f48b	3c89df00-888f-4424-8fec-403c368c3eae	Whats up bron
3	f432c016-b5fd-4125-bc86-512d4df1f48b	3c89df00-888f-4424-8fec-403c368c3eae	Get good lil homie
4	3c89df00-888f-4424-8fec-403c368c3eae	f432c016-b5fd-4125-bc86-512d4df1f48b	Damn bro
5	3c89df00-888f-4424-8fec-403c368c3eae	f432c016-b5fd-4125-bc86-512d4df1f48b	Thats Crazy
\.


--
-- Data for Name: studentclass; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.studentclass (class_id, student_id) FROM stdin;
201a9a50-e191-4338-a746-8ce2f9f44c28	3c89df00-888f-4424-8fec-403c368c3eae
\.


--
-- Data for Name: submission; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.submission (submission_id, assignment_id, student_id, submission, submitted_date) FROM stdin;
4	1	3c89df00-888f-4424-8fec-403c368c3eae	D:\\Documents\\Term 4\\Netlab Oprec\\Proyek OOP\\ClassConnect-API\\uploads\\TI7_FairuzMuhammad_2206814324.pdf	2024-06-10
\.


--
-- Data for Name: teacherclass; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.teacherclass (class_id, teacher_id) FROM stdin;
201a9a50-e191-4338-a746-8ce2f9f44c28	f432c016-b5fd-4125-bc86-512d4df1f48b
201a9a50-e191-4338-a746-8ce2f9f44c28	592e2cba-51e1-45aa-b3ce-a5e2573971f9
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: Fairuz
--

COPY public.users (user_id, name, username, password, email, approved, role) FROM stdin;
216adb10-6969-40b9-a6d9-773eda42117a	Fairuz	admin	$2a$06$xzf.HGJx8Hx2gnXUDFnlvewerNNo4WmfwrjefnfMjhXJn2XDWANRa	admin@gmail.com	t	admin
3c89df00-888f-4424-8fec-403c368c3eae	Lebron	James	$2a$10$UQkoQc7DHrf8rDItFbW.L.b/bTkL0l/uRvV0bK0bcnV39BhYlN3xS	james23@example.com	t	student
f432c016-b5fd-4125-bc86-512d4df1f48b	Teacher	teacher	$2a$10$TVPBGb2X4fwTve3iksf5zecFXpDXYplVPFcfO7zGb7l65N6MtYiB6	teacher@example.com	t	teacher
592e2cba-51e1-45aa-b3ce-a5e2573971f9	Teacher 2	teacher2	$2a$10$axIOCuf4pkHJCgtc6pm5WO8MoXjCck78q2T0uopYzeEKPYPETSTdm	teacher2@example.com	t	teacher
\.


--
-- Name: assignments_assignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: Fairuz
--

SELECT pg_catalog.setval('public.assignments_assignment_id_seq', 1, true);


--
-- Name: grades_grade_id_seq; Type: SEQUENCE SET; Schema: public; Owner: Fairuz
--

SELECT pg_catalog.setval('public.grades_grade_id_seq', 1, true);


--
-- Name: messages_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: Fairuz
--

SELECT pg_catalog.setval('public.messages_message_id_seq', 5, true);


--
-- Name: submission_submission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: Fairuz
--

SELECT pg_catalog.setval('public.submission_submission_id_seq', 4, true);


--
-- Name: assignments assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (assignment_id);


--
-- Name: classes classes_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.classes
    ADD CONSTRAINT classes_pkey PRIMARY KEY (class_id);


--
-- Name: grades grades_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (grade_id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (message_id);


--
-- Name: submission submission_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.submission
    ADD CONSTRAINT submission_pkey PRIMARY KEY (submission_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: Fairuz
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO neon_superuser WITH GRANT OPTION;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON TABLES TO neon_superuser WITH GRANT OPTION;


--
-- PostgreSQL database dump complete
--

