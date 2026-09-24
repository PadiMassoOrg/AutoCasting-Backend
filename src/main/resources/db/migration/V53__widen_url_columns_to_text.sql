ALTER TABLE talent_media
    ALTER COLUMN headshot_image_url TYPE text,
    ALTER COLUMN full_body_image_url TYPE text,
    ALTER COLUMN introduction_video_url TYPE text,
    ALTER COLUMN show_reel_video_url TYPE text;

ALTER TABLE talent_media_other_pictures_url
    ALTER COLUMN url TYPE text;

ALTER TABLE employer_basic_info
    ALTER COLUMN image_url TYPE text,
    ALTER COLUMN website_url TYPE text;
