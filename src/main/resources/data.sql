INSERT INTO member (
    nickname,
    email,
    password,
    image_path,
    name,
    provider,
    provider_id,
    role,
    withdrawn,
    show_badge,
    created_at,
    modified_at
)
SELECT
    '운영자',
    null,
    'admin',
    'Default',
    '운영자',
    'local',
    'local_admin',
    'ROLE_ADMIN',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'local_admin'
);

INSERT INTO member (
    nickname,
    email,
    password,
    image_path,
    name,
    provider,
    provider_id,
    role,
    withdrawn,
    show_badge,
    created_at,
    modified_at
)
SELECT
    '모각봇',
    null,
    'local_bot',
    'https://mogakzone-001.s3.ap-northeast-2.amazonaws.com/member/mogak-bot.png',
    '봇',
    'local',
    'local_bot',
    'ROLE_USER',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'local_bot'
);