create table quantities
(
    user_id    String,
    bucket     String,
    quantity   Decimal(19, 3),
    updated_at DateTime
) engine = SummingMergeTree(quantity) order by (user_id, bucket, quantity);

