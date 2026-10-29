--
-- PostgreSQL database dump
--

-- Dumped from database version 16.4 (Debian 16.4-1.pgdg120+1)
-- Dumped by pg_dump version 16.4 (Debian 16.4-1.pgdg120+1)


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cell_claim_counts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cell_claim_counts (
    cell_index integer NOT NULL,
    claim_count integer,
    game_id bigint NOT NULL
);


--
-- Name: cell_first_ownership; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.cell_first_ownership (
    cell_index integer NOT NULL,
    game_id bigint NOT NULL,
    user_id bigint
);


--
-- Name: game_chat_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_chat_messages (
    created_at timestamp(6) with time zone,
    game_id bigint NOT NULL,
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    message character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    CONSTRAINT game_chat_messages_message_type_check CHECK (((message_type)::text = ANY ((ARRAY['MESSAGE'::character varying, 'INFO'::character varying, 'TEMPLATE'::character varying])::text[])))
);


--
-- Name: game_event_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.game_event_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: game_event_sequence; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_event_sequence (
    game_id bigint NOT NULL,
    next_val bigint NOT NULL
);


--
-- Name: game_events; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_events (
    created_at timestamp(6) with time zone,
    game_id bigint NOT NULL,
    id bigint NOT NULL,
    sequence_number bigint NOT NULL,
    user_id bigint NOT NULL,
    event_type character varying(255) NOT NULL,
    message character varying(255) NOT NULL,
    CONSTRAINT game_events_event_type_check CHECK (((event_type)::text = ANY ((ARRAY['CELL_UPDATE_SUBMISSION'::character varying, 'CELL_UPDATE_ACCEPTED'::character varying, 'CELL_UPDATE_REJECTED'::character varying, 'CELL_UPDATE_INVALID'::character varying, 'SCORE_UPDATE'::character varying, 'STREAK_UPDATE'::character varying, 'BOARD_COMPLETED'::character varying, 'GAME_PLAYER_FORFEIT'::character varying])::text[])))
);


--
-- Name: game_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.game_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: game_message_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.game_message_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: game_player_states; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_player_states (
    active_multiplier double precision NOT NULL,
    consecutive_mistake_count integer,
    current_streak integer NOT NULL,
    game_id bigint NOT NULL,
    multiplier_ends_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    user_id bigint NOT NULL,
    version bigint,
    current_board_state character varying(255),
    notes bytea NOT NULL
);


--
-- Name: game_players; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_players (
    finished_game boolean,
    firsts integer,
    game_loaded boolean,
    leaderboard_score integer,
    max_streak integer NOT NULL,
    mistakes integer,
    score integer,
    finished_game_timestamp timestamp(6) with time zone,
    game_id bigint NOT NULL,
    game_loaded_timestamp timestamp(6) with time zone,
    game_message_timestamp timestamp(6) with time zone,
    user_id bigint NOT NULL,
    version bigint,
    game_result character varying(255) NOT NULL,
    player_colour character varying(255) NOT NULL,
    CONSTRAINT game_players_game_result_check CHECK (((game_result)::text = ANY ((ARRAY['PENDING'::character varying, 'WIN'::character varying, 'LOSS'::character varying, 'DRAW'::character varying, 'FORFEIT'::character varying, 'COMPLETE'::character varying, 'INCOMPLETE'::character varying])::text[]))),
    CONSTRAINT game_players_player_colour_check CHECK (((player_colour)::text = ANY ((ARRAY['ONE'::character varying, 'TWO'::character varying, 'THREE'::character varying, 'FOUR'::character varying])::text[])))
);


--
-- Name: game_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.game_settings (
    game_id bigint NOT NULL,
    id bigint NOT NULL,
    version bigint,
    difficulty character varying(255),
    game_mode character varying(255),
    game_type character varying(255),
    time_limit character varying(255),
    CONSTRAINT game_settings_difficulty_check CHECK (((difficulty)::text = ANY ((ARRAY['EASY'::character varying, 'MEDIUM'::character varying, 'HARD'::character varying, 'EXTREME'::character varying])::text[]))),
    CONSTRAINT game_settings_game_mode_check CHECK (((game_mode)::text = ANY ((ARRAY['CLASSIC'::character varying, 'DOMINATION'::character varying, 'TIMEATTACK'::character varying])::text[]))),
    CONSTRAINT game_settings_game_type_check CHECK (((game_type)::text = ANY ((ARRAY['RANKED'::character varying, 'CASUAL'::character varying])::text[]))),
    CONSTRAINT game_settings_time_limit_check CHECK (((time_limit)::text = ANY ((ARRAY['QUICK'::character varying, 'STANDARD'::character varying, 'MARATHON'::character varying, 'UNLIMITED'::character varying])::text[])))
);


--
-- Name: game_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.game_settings_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: games; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.games (
    ended_prematurely boolean,
    created_at timestamp(6) with time zone,
    game_ended_at timestamp(6) with time zone,
    game_ends_at timestamp(6) with time zone,
    game_starts_at timestamp(6) with time zone,
    id bigint NOT NULL,
    lobby_id bigint NOT NULL,
    puzzle_id bigint NOT NULL,
    version bigint,
    game_status character varying(255),
    CONSTRAINT games_game_status_check CHECK (((game_status)::text = ANY ((ARRAY['LOADING'::character varying, 'COUNTDOWN'::character varying, 'IN_PROGRESS'::character varying, 'FINISHED'::character varying, 'CLOSED'::character varying, 'ABORTED'::character varying])::text[])))
);


--
-- Name: leaderboards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.leaderboards (
    current_win_streak integer,
    draws integer,
    games_played integer,
    losses integer,
    max_win_streak integer,
    wins integer,
    id bigint NOT NULL,
    total_score bigint,
    updated_at timestamp(6) with time zone,
    user_id bigint NOT NULL,
    game_mode character varying(255),
    CONSTRAINT leaderboards_game_mode_check CHECK (((game_mode)::text = ANY ((ARRAY['CLASSIC'::character varying, 'DOMINATION'::character varying, 'TIMEATTACK'::character varying])::text[])))
);


--
-- Name: leaderboards_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.leaderboards_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lobbies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lobbies (
    in_game boolean,
    is_active boolean,
    created_at timestamp(6) with time zone,
    current_game_id bigint,
    host_user_id bigint NOT NULL,
    id bigint NOT NULL,
    version bigint,
    lobby_name character varying(255) NOT NULL
);


--
-- Name: lobby_chat_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lobby_chat_messages (
    created_at timestamp(6) with time zone,
    id bigint NOT NULL,
    lobby_id bigint NOT NULL,
    user_id bigint NOT NULL,
    message character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    CONSTRAINT lobby_chat_messages_message_type_check CHECK (((message_type)::text = ANY ((ARRAY['MESSAGE'::character varying, 'INFO'::character varying, 'TEMPLATE'::character varying])::text[])))
);


--
-- Name: lobby_countdown_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lobby_countdown_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lobby_countdowns; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lobby_countdowns (
    countdown_active boolean,
    countdown_ends_at timestamp(6) with time zone,
    countdown_initiated_by bigint,
    id bigint NOT NULL,
    lobby_id bigint NOT NULL,
    version bigint
);


--
-- Name: lobby_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lobby_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lobby_message_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lobby_message_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: lobby_players; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lobby_players (
    joined_at timestamp(6) with time zone NOT NULL,
    lobby_id bigint NOT NULL,
    lobby_message_timestamp timestamp(6) with time zone,
    ready_at timestamp(6) with time zone,
    user_id bigint NOT NULL,
    lobby_status character varying(255),
    CONSTRAINT lobby_players_lobby_status_check CHECK (((lobby_status)::text = ANY ((ARRAY['WAITING'::character varying, 'READY'::character varying, 'INGAME'::character varying])::text[])))
);


--
-- Name: lobby_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lobby_settings (
    is_public boolean,
    id bigint NOT NULL,
    lobby_id bigint NOT NULL,
    version bigint,
    difficulty character varying(255),
    game_mode character varying(255),
    game_type character varying(255),
    time_limit character varying(255),
    CONSTRAINT lobby_settings_difficulty_check CHECK (((difficulty)::text = ANY ((ARRAY['EASY'::character varying, 'MEDIUM'::character varying, 'HARD'::character varying, 'EXTREME'::character varying])::text[]))),
    CONSTRAINT lobby_settings_game_mode_check CHECK (((game_mode)::text = ANY ((ARRAY['CLASSIC'::character varying, 'DOMINATION'::character varying, 'TIMEATTACK'::character varying])::text[]))),
    CONSTRAINT lobby_settings_game_type_check CHECK (((game_type)::text = ANY ((ARRAY['RANKED'::character varying, 'CASUAL'::character varying])::text[]))),
    CONSTRAINT lobby_settings_time_limit_check CHECK (((time_limit)::text = ANY ((ARRAY['QUICK'::character varying, 'STANDARD'::character varying, 'MARATHON'::character varying, 'UNLIMITED'::character varying])::text[])))
);


--
-- Name: lobby_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.lobby_settings_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: player_mistaken_cells; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.player_mistaken_cells (
    cell_index integer NOT NULL,
    number_of_mistakes integer,
    game_id bigint NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: puzzle_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.puzzle_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: shared_game_states; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.shared_game_states (
    game_id bigint NOT NULL,
    last_first_winner_user_id bigint,
    updated_at timestamp(6) with time zone,
    version bigint,
    current_shared_board_state character varying(255)
);


--
-- Name: sudoku_puzzles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.sudoku_puzzles (
    id bigint NOT NULL,
    difficulty character varying(255) NOT NULL,
    initial_board_state character varying(255) NOT NULL,
    solution character varying(255) NOT NULL,
    CONSTRAINT sudoku_puzzles_difficulty_check CHECK (((difficulty)::text = ANY ((ARRAY['EASY'::character varying, 'MEDIUM'::character varying, 'HARD'::character varying, 'EXTREME'::character varying])::text[])))
);


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_oauth_providers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_oauth_providers (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    provider character varying(255) NOT NULL,
    provider_id character varying(255) NOT NULL
);


--
-- Name: user_oauth_providers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_oauth_providers_id_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_settings (
    audio boolean,
    game_chat_notifications boolean,
    highlighted_firsts boolean,
    highlighted_houses boolean,
    opponent_highlighted_squares boolean,
    score_notifications boolean,
    streak_notifications boolean,
    theme smallint,
    user_id bigint NOT NULL,
    CONSTRAINT user_settings_theme_check CHECK (((theme >= 0) AND (theme <= 3)))
);


--
-- Name: user_stats; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_stats (
    current_win_streak integer,
    draws integer,
    games_played integer,
    losses integer,
    max_win_streak integer,
    total_score integer,
    wins integer,
    updated_at timestamp(6) with time zone,
    user_id bigint NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    is_online boolean,
    created_at timestamp(6) with time zone,
    id bigint NOT NULL,
    recovery_email_hash character varying(255),
    username character varying(255) NOT NULL
);


--
-- Name: cell_claim_counts cell_claim_counts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cell_claim_counts
    ADD CONSTRAINT cell_claim_counts_pkey PRIMARY KEY (cell_index, game_id);


--
-- Name: cell_first_ownership cell_first_ownership_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cell_first_ownership
    ADD CONSTRAINT cell_first_ownership_pkey PRIMARY KEY (cell_index, game_id);


--
-- Name: game_chat_messages game_chat_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_chat_messages
    ADD CONSTRAINT game_chat_messages_pkey PRIMARY KEY (id);


--
-- Name: game_event_sequence game_event_sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_event_sequence
    ADD CONSTRAINT game_event_sequence_pkey PRIMARY KEY (game_id);


--
-- Name: game_events game_events_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_events
    ADD CONSTRAINT game_events_pkey PRIMARY KEY (id);


--
-- Name: game_player_states game_player_states_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_player_states
    ADD CONSTRAINT game_player_states_pkey PRIMARY KEY (game_id, user_id);


--
-- Name: game_players game_players_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_players
    ADD CONSTRAINT game_players_pkey PRIMARY KEY (game_id, user_id);


--
-- Name: game_settings game_settings_game_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_settings
    ADD CONSTRAINT game_settings_game_id_key UNIQUE (game_id);


--
-- Name: game_settings game_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_settings
    ADD CONSTRAINT game_settings_pkey PRIMARY KEY (id);


--
-- Name: games games_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT games_pkey PRIMARY KEY (id);


--
-- Name: leaderboards leaderboards_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leaderboards
    ADD CONSTRAINT leaderboards_pkey PRIMARY KEY (id);


--
-- Name: lobbies lobbies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobbies
    ADD CONSTRAINT lobbies_pkey PRIMARY KEY (id);


--
-- Name: lobby_chat_messages lobby_chat_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_chat_messages
    ADD CONSTRAINT lobby_chat_messages_pkey PRIMARY KEY (id);


--
-- Name: lobby_countdowns lobby_countdowns_lobby_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_countdowns
    ADD CONSTRAINT lobby_countdowns_lobby_id_key UNIQUE (lobby_id);


--
-- Name: lobby_countdowns lobby_countdowns_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_countdowns
    ADD CONSTRAINT lobby_countdowns_pkey PRIMARY KEY (id);


--
-- Name: lobby_players lobby_players_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_players
    ADD CONSTRAINT lobby_players_pkey PRIMARY KEY (lobby_id, user_id);


--
-- Name: lobby_settings lobby_settings_lobby_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_settings
    ADD CONSTRAINT lobby_settings_lobby_id_key UNIQUE (lobby_id);


--
-- Name: lobby_settings lobby_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_settings
    ADD CONSTRAINT lobby_settings_pkey PRIMARY KEY (id);


--
-- Name: player_mistaken_cells player_mistaken_cells_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.player_mistaken_cells
    ADD CONSTRAINT player_mistaken_cells_pkey PRIMARY KEY (cell_index, game_id, user_id);


--
-- Name: shared_game_states shared_game_states_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shared_game_states
    ADD CONSTRAINT shared_game_states_pkey PRIMARY KEY (game_id);


--
-- Name: sudoku_puzzles sudoku_puzzles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.sudoku_puzzles
    ADD CONSTRAINT sudoku_puzzles_pkey PRIMARY KEY (id);


--
-- Name: game_events uc_game_sequence_number; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_events
    ADD CONSTRAINT uc_game_sequence_number UNIQUE (game_id, sequence_number);


--
-- Name: leaderboards uc_user_game_mode; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leaderboards
    ADD CONSTRAINT uc_user_game_mode UNIQUE (user_id, game_mode);


--
-- Name: user_oauth_providers user_oauth_providers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_oauth_providers
    ADD CONSTRAINT user_oauth_providers_pkey PRIMARY KEY (id);


--
-- Name: user_settings user_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT user_settings_pkey PRIMARY KEY (user_id);


--
-- Name: user_stats user_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_stats
    ADD CONSTRAINT user_stats_pkey PRIMARY KEY (user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_game_id_sequence_number; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_game_id_sequence_number ON public.game_events USING btree (game_id, sequence_number);


--
-- Name: idx_game_messages_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_game_messages_created ON public.game_chat_messages USING btree (game_id, created_at DESC);


--
-- Name: idx_game_mode_total_score_desc; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_game_mode_total_score_desc ON public.leaderboards USING btree (game_mode, total_score DESC);


--
-- Name: idx_lobby_messages_created; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lobby_messages_created ON public.lobby_chat_messages USING btree (lobby_id, created_at DESC);


--
-- Name: lobby_chat_messages fk19r4abu3tcyjyb5o5bnrjwwm1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_chat_messages
    ADD CONSTRAINT fk19r4abu3tcyjyb5o5bnrjwwm1 FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id) ON DELETE CASCADE;


--
-- Name: player_mistaken_cells fk1ggkpssmeig87hy94ssu8p1d8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.player_mistaken_cells
    ADD CONSTRAINT fk1ggkpssmeig87hy94ssu8p1d8 FOREIGN KEY (game_id, user_id) REFERENCES public.game_player_states(game_id, user_id);


--
-- Name: game_players fk5rtr7894cak3lsc62m3sdwaf3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_players
    ADD CONSTRAINT fk5rtr7894cak3lsc62m3sdwaf3 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: game_settings fk7lw8bal59nsfmasad2yhf6wn9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_settings
    ADD CONSTRAINT fk7lw8bal59nsfmasad2yhf6wn9 FOREIGN KEY (game_id) REFERENCES public.games(id);


--
-- Name: user_settings fk8v82nj88rmai0nyck19f873dw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT fk8v82nj88rmai0nyck19f873dw FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: lobby_players fkb1uqib3f6qyah9dqsl1xkabg1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_players
    ADD CONSTRAINT fkb1uqib3f6qyah9dqsl1xkabg1 FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);


--
-- Name: games fkb7yjuipshnhx8keugtj3qtfx2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT fkb7yjuipshnhx8keugtj3qtfx2 FOREIGN KEY (puzzle_id) REFERENCES public.sudoku_puzzles(id);


--
-- Name: lobby_players fkcpad6cy7u90j3iub612uemc13; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_players
    ADD CONSTRAINT fkcpad6cy7u90j3iub612uemc13 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: games fkdvwyb174qq6o0th2cbqn01kme; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.games
    ADD CONSTRAINT fkdvwyb174qq6o0th2cbqn01kme FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);


--
-- Name: game_player_states fked2eda2myehfqnenqwv2kut0v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_player_states
    ADD CONSTRAINT fked2eda2myehfqnenqwv2kut0v FOREIGN KEY (game_id, user_id) REFERENCES public.game_players(game_id, user_id);


--
-- Name: lobbies fkf1xxiqwq2va04850omong0wta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobbies
    ADD CONSTRAINT fkf1xxiqwq2va04850omong0wta FOREIGN KEY (host_user_id) REFERENCES public.users(id);


--
-- Name: game_chat_messages fkfelv7otierodrpbd94gonpd71; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_chat_messages
    ADD CONSTRAINT fkfelv7otierodrpbd94gonpd71 FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: cell_claim_counts fkfs931oi9m4rwhotxv452chs8v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cell_claim_counts
    ADD CONSTRAINT fkfs931oi9m4rwhotxv452chs8v FOREIGN KEY (game_id) REFERENCES public.shared_game_states(game_id);


--
-- Name: game_chat_messages fkfwe1wdfbajdi4shyacwpdnv8v; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_chat_messages
    ADD CONSTRAINT fkfwe1wdfbajdi4shyacwpdnv8v FOREIGN KEY (game_id) REFERENCES public.games(id) ON DELETE CASCADE;


--
-- Name: lobby_chat_messages fkl2blgmt7r6vvsofwe7fp5pa2b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_chat_messages
    ADD CONSTRAINT fkl2blgmt7r6vvsofwe7fp5pa2b FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: cell_first_ownership fkmqkcnpumedksuivbe5jt0typn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.cell_first_ownership
    ADD CONSTRAINT fkmqkcnpumedksuivbe5jt0typn FOREIGN KEY (game_id) REFERENCES public.shared_game_states(game_id);


--
-- Name: lobby_countdowns fkoj7uaypof9kovgicc1rpwufjd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_countdowns
    ADD CONSTRAINT fkoj7uaypof9kovgicc1rpwufjd FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);


--
-- Name: shared_game_states fkol132txb09wcdomobpc5g6k8t; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.shared_game_states
    ADD CONSTRAINT fkol132txb09wcdomobpc5g6k8t FOREIGN KEY (game_id) REFERENCES public.games(id);


--
-- Name: game_events fkotnyeymqg1stlsqrl5jhbxwm4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_events
    ADD CONSTRAINT fkotnyeymqg1stlsqrl5jhbxwm4 FOREIGN KEY (game_id) REFERENCES public.games(id);


--
-- Name: leaderboards fkpu3rih0o4o3ir0q70q18otuxg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.leaderboards
    ADD CONSTRAINT fkpu3rih0o4o3ir0q70q18otuxg FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: game_players fkrbr2flqdav5ovyjas7q92u64r; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_players
    ADD CONSTRAINT fkrbr2flqdav5ovyjas7q92u64r FOREIGN KEY (game_id) REFERENCES public.games(id);


--
-- Name: lobby_settings fks5cs8g105ecmoviqekm058usi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lobby_settings
    ADD CONSTRAINT fks5cs8g105ecmoviqekm058usi FOREIGN KEY (lobby_id) REFERENCES public.lobbies(id);


--
-- Name: user_oauth_providers fkt59ckvo6b474wlmpwydp9ddfo; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_oauth_providers
    ADD CONSTRAINT fkt59ckvo6b474wlmpwydp9ddfo FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: game_events fkt6gxbi2x8sdo2h19yuelkx9eh; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.game_events
    ADD CONSTRAINT fkt6gxbi2x8sdo2h19yuelkx9eh FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

