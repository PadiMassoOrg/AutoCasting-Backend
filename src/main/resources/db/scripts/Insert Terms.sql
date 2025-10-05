INSERT INTO legal_documents(
  id, type, locale, version, title, slug,
  status, format, body_template, content_hash,
  effective_at, published_at,
  created_at, created_by, modified_at, modified_by, deleted
)
VALUES (
  gen_random_uuid(),
  'TERMS',
  'es',
  '1.0.0',
  'Términos y Condiciones',
  'terms-and-conditions',
  'PUBLISHED',
  'HTML',
  $$<!doctype html>
  <html lang="es">
  <head>
    <meta charset="utf-8" />
    <title>Términos y Condiciones</title>
  </head>
  <body>
    <h1>Términos y Condiciones</h1>
    <p>Contenido de la versión 1.0.0</p>
  </body>
  </html>$$,
  encode(digest($$<!doctype html>
  <html lang="es">
  <head>
    <meta charset="utf-8" />
    <title>Términos y Condiciones</title>
  </head>
  <body>
    <h1>Términos y Condiciones</h1>
    <p>Contenido de la versión 1.0.0</p>
  </body>
  </html>$$, 'sha256'), 'hex'),
  now(),               -- effective_at
  now(),               -- published_at
  now(), 'SYSTEM', now(), 'SYSTEM', false
);
