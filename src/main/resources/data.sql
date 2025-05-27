INSERT INTO badge (name, description, icon_url, badge_type, created_at, modified_at)
SELECT '첫 챌린지', '첫 번째 챌린지를 시작한 사용자에게 주어지는 뱃지', 'Default', 'FIRST_CHALLENGE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM badge WHERE badge_type = 'FIRST_CHALLENGE');

INSERT INTO badge (name, description, icon_url, badge_type, created_at, modified_at)
SELECT '일주일 챌린지 달성', '7일 이상 30일 이하 챌린지를 완료한 사용자에게 주어지는 뱃지', 'Default', 'WEEK_CHALLENGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM badge WHERE badge_type = 'WEEK_CHALLENGER');

INSERT INTO badge (name, description, icon_url, badge_type, created_at, modified_at)
SELECT '한달 챌린지 달성', '30일 이상 챌린지를 완료한 사용자에게 주어지는 뱃지', 'Default', 'MONTH_CHALLENGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM badge WHERE badge_type = 'MONTH_CHALLENGER');

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