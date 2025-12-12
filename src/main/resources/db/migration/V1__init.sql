CREATE TABLE transactions (
  id bigserial PRIMARY KEY,
  external_tx_id varchar(128) NOT NULL UNIQUE,
  amount numeric NOT NULL,
  currency varchar(8) NOT NULL,
  status varchar(32),
  created_at timestamptz DEFAULT now()
);

CREATE TABLE outbox (
  id bigserial PRIMARY KEY,
  aggregate_id bigint NOT NULL,
  type varchar(64) NOT NULL,
  payload jsonb NOT NULL,
  sent boolean DEFAULT false,
  created_at timestamptz DEFAULT now()
);