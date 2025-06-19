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
    'ROLE_MEMBER',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'local_bot'
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
    '테스트유저1',
    null,
    'testUser1',
    'Default',
    '테스트유저1',
    'test',
    'testUser1',
    'ROLE_MEMBER',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'testUser1'
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
    '테스트유저2',
    null,
    'testUser2',
    'Default',
    '테스트유저2',
    'test',
    'testUser2',
    'ROLE_MEMBER',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'testUser2'
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
    '테스트유저3',
    null,
    'testUser3',
    'Default',
    '테스트유저3',
    'test',
    'testUser3',
    'ROLE_MEMBER',
    false,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM member WHERE provider_id = 'testUser3'
);
