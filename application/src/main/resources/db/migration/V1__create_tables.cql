CREATE TYPE IF NOT EXISTS client_type (
    id uuid,
    full_name text,
    document text,
    email text,
    birth_date date
);

CREATE TABLE IF NOT EXISTS clients (
    id uuid,
    full_name text,
    email text,
    document text,
    birth_date date,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_clients_email ON clients (email);
CREATE INDEX IF NOT EXISTS idx_clients_document ON clients (document);

CREATE TABLE IF NOT EXISTS events (
    id uuid,
    name text,
    description text,
    price decimal,
    available_quantity int,
    event_date date,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id uuid,
    status text,
    invoice_pdf_url text,
    client frozen<client_type>,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS reservations_by_client (
    client_id uuid,
    id uuid,
    status text,
    invoice_pdf_url text,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY ((client_id), id)
);
