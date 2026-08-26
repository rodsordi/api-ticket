CREATE TABLE IF NOT EXISTS auth (
    id uuid,
    authority text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS clients (
    id uuid,
    full_name text,
    email text,
    document text,
    birth_date date,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_clients_email ON clients (email);
CREATE INDEX IF NOT EXISTS idx_clients_document ON clients (document);

CREATE TABLE IF NOT EXISTS events (
    id uuid,
    name text,
    status text,
    description text,
    price decimal,
    launching_date_time timestamp,
    event_date date,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_events_status ON events (status);

CREATE TABLE IF NOT EXISTS reservations (
    id uuid,
    status text,
    invoice_pdf_url text,
    client_id uuid,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_reservations_client_id ON reservations (client_id);

CREATE TABLE IF NOT EXISTS users (
    id uuid,
    username text,
    password text,
    name text,
    email text,
    authorities set<text>,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

CREATE TABLE IF NOT EXISTS users_by_email (
    email text,
    id uuid,
    username text,
    password text,
    name text,
    authorities set<text>,
    PRIMARY KEY (email)
);

CREATE TABLE IF NOT EXISTS reservations_by_client (
    client_id uuid,
    id uuid,
    status text,
    invoice_pdf_url text,
    PRIMARY KEY ((client_id), id)
);
