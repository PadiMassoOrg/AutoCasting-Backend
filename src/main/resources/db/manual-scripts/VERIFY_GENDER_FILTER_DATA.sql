-- Diagnostic query (read-only, no changes made).
-- Run this AFTER HARD_DELETE -> backend restart -> reseed, directly against the DB
-- (e.g. via DBeaver/psql), to confirm gender data is correctly wired before testing
-- the filter in the app. If this returns 6 rows with counts > 0, the DATA is correct
-- and any "0 results" issue in the app is a client-side (stale cache / wrong UUID sent)
-- problem, not a seed/backend problem.

SELECT
  go.string_code,
  go.id AS gender_option_id,
  COUNT(tbi.id) AS talent_count
FROM public.gender_option go
LEFT JOIN public.talent_basic_info tbi ON tbi.gender_id = go.id
LEFT JOIN public.talent_profile tp ON tp.id = tbi.talent_profile_id
LEFT JOIN public.users u ON u.id = tp.user_id AND u.email ~ '^asd[0-9]+@asd\.com$'
GROUP BY go.string_code, go.id
ORDER BY go.string_code;

-- Also confirms every seeded talent has the media required by the search's
-- hasRequiredMedia() filter (headshot + full body) -- if either is blank/null,
-- that talent is excluded from EVERY search result regardless of any other filter.
SELECT
  COUNT(*) FILTER (WHERE tm.headshot_image_url IS NULL OR tm.headshot_image_url = '') AS missing_headshot,
  COUNT(*) FILTER (WHERE tm.full_body_image_url IS NULL OR tm.full_body_image_url = '') AS missing_full_body,
  COUNT(*) AS total_seed_talents
FROM public.talent_profile tp
JOIN public.users u ON u.id = tp.user_id AND u.email ~ '^asd[0-9]+@asd\.com$'
LEFT JOIN public.talent_media tm ON tm.talent_profile_id = tp.id;
