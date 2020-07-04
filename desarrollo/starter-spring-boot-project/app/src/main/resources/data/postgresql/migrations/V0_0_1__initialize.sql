/*
 * Engine: PostgreSQL 9.2.24
 */

-- Required by postgre

    CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
    SET statement_timeout                 = 0;
    SET client_encoding                   = 'UTF8';
    SET standard_conforming_strings       = on;
    SET check_function_bodies             = false;
    SET client_min_messages               = warning;
    SET default_tablespace                = '';
    SET default_with_oids                 = false;


-- Database structure

    CREATE SEQUENCE public.hibernate_sequence
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1;

    CREATE TABLE public.user_roles (
        id bigserial NOT NULL PRIMARY KEY,
        identifier uuid NOT NULL,
        namespace character varying(45)
    );

    CREATE TABLE public.users (
        id bigserial NOT NULL PRIMARY KEY,
        identifier uuid NOT NULL,
        activation_hash character varying(40),
        created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
        enable boolean NOT NULL DEFAULT false,
        last_access timestamp without time zone,
        last_modified_at timestamp without time zone,
        email character varying(256) NOT NULL, -- Default user login
        name character varying(32) NOT NULL,
        last_name character varying(32),
        password_hash character varying(40),
        password_salt character varying(40),
        created_by_user_id bigint NULL REFERENCES public.users(id) ON DELETE SET NULL
    );

    CREATE TABLE public.user_sessions (
        id bigserial NOT NULL PRIMARY KEY,
        identifier uuid NOT NULL,
        user_id bigint NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
        token_xsrf character varying(40) NOT NULL,
        created_at timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
        last_access timestamp without time zone NOT NULL,
        expire_at timestamp without time zone NOT NULL
    );

    CREATE TABLE public.users_roles_relatinships (
        id bigserial NOT NULL PRIMARY KEY,
        user_id bigint NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
        user_role_id bigint NOT NULL REFERENCES public.user_roles(id) ON DELETE CASCADE
    );


-- Database data

    INSERT INTO public.user_roles (identifier, namespace) VALUES
        ('d12f7dea-a18e-40f2-ba9d-ff07b2077db0', 'admin'),
        ('09440594-22af-40e4-a004-7f7db719c324', 'user')
    ;

    INSERT INTO public.users (
        identifier, enable, email, name, password_hash, password_salt
    ) VALUES
        (
            '73634fb7-85c2-461a-a75d-4b7794ff0b4d',
            true,
            'admin@localhost',
            'Administrator',
            '197acda0b6f612ad42b723c893987482c140f2fc', -- admin
            '50da78981bed076a59a1d17824b283508ff971f8'
        )
    ;
