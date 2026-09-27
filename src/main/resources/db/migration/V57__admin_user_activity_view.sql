-- One row per user with the data the admin users list displays and sorts by.
-- last_saved_at mirrors TalentProfileLastSaved (the "Último guardado" the talent sees in the Frontend)
-- plus the employer profile, its basic info and social links. Keep both definitions in sync.
CREATE VIEW admin_user_activity AS
SELECT
    u.id AS user_id,
    tbi.stage_name AS talent_stage_name,
    ebi.company_name AS employer_company_name,
    GREATEST(
        tp.modified_at,
        tbi.modified_at,
        tc.modified_at,
        tm.modified_at,
        tch.modified_at,
        (SELECT MAX(c.modified_at) FROM talent_credit c WHERE c.talent_profile_id = tp.id AND c.deleted = false),
        (SELECT MAX(e.modified_at) FROM talent_education e WHERE e.talent_profile_id = tp.id AND e.deleted = false),
        (SELECT MAX(l.modified_at) FROM talent_social_media_link l WHERE l.talent_profile_id = tp.id AND l.deleted = false),
        ep.modified_at,
        ebi.modified_at,
        (SELECT MAX(l.modified_at) FROM talent_social_media_link l WHERE l.employer_basic_info_id = ebi.id AND l.deleted = false)
    ) AS last_saved_at
FROM users u
LEFT JOIN talent_profile tp ON tp.user_id = u.id
LEFT JOIN talent_basic_info tbi ON tbi.talent_profile_id = tp.id
LEFT JOIN talent_contact tc ON tc.talent_profile_id = tp.id
LEFT JOIN talent_media tm ON tm.talent_profile_id = tp.id
LEFT JOIN talent_characteristics tch ON tch.talent_profile_id = tp.id
LEFT JOIN employer_profile ep ON ep.user_id = u.id
LEFT JOIN employer_basic_info ebi ON ebi.employer_profile_id = ep.id;
