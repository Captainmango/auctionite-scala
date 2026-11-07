CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(320),
    password_hash TEXT,
    created_at    TIMESTAMP
);

-- Look up a user at login.
CREATE INDEX idx_users_email ON users (email);


CREATE TABLE items (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     BIGINT,
    name        VARCHAR(200),
    description TEXT,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

-- List an owner's items.
CREATE INDEX idx_items_user_id ON items (user_id);

CREATE TABLE item_pictures (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id    BIGINT,
    url        TEXT,
    sort_order INTEGER,
    created_at TIMESTAMP
);

-- Fetch all pictures for an item, in display order.
CREATE INDEX idx_item_pictures_item_id ON item_pictures (item_id, sort_order);


CREATE TABLE listings (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    item_id        BIGINT,
    starting_price NUMERIC(12, 2),
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- Find the listing(s) for an item.
CREATE INDEX idx_listings_item_id ON listings (item_id);


CREATE TABLE listing_schedules (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id     BIGINT,
    activates_at   TIMESTAMP,
    deactivates_at TIMESTAMP,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

-- Find the schedule for a listing.
CREATE INDEX idx_listing_schedules_listing_id ON listing_schedules (listing_id);

-- Find schedules due for activation/deactivation.
CREATE INDEX idx_listing_schedules_activates_at ON listing_schedules (activates_at);
CREATE INDEX idx_listing_schedules_deactivates_at ON listing_schedules (deactivates_at);


CREATE TABLE listing_state_transitions (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id BIGINT,
    status     VARCHAR(16),         -- DRAFT, ACTIVE, INACTIVE, VOID, COMPLETE
    created_at TIMESTAMP
);

-- Resolve a listing's current state: most recent row WHERE deleted_at IS NULL.
CREATE INDEX idx_listing_state_transitions_listing_id
    ON listing_state_transitions (listing_id, created_at DESC);


CREATE TABLE bids (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    listing_id BIGINT,
    user_id    BIGINT,
    amount     NUMERIC(12, 2),
    created_at TIMESTAMP
);

-- Fetch all bids for a listing.
CREATE INDEX idx_bids_listing_id ON bids (listing_id);

-- Fetch all bids placed by a user.
CREATE INDEX idx_bids_user_id ON bids (user_id);

-- Determine the leading bid for a listing (highest amount; earliest bid
-- breaks ties when two users bid the same amount).
CREATE INDEX idx_bids_listing_leading ON bids (listing_id, amount DESC, created_at ASC);