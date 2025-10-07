------------------------------------------------------------------------
---- Insert Roles
------------------------------------------------------------------------
INSERT INTO roles (
  id,
  code,
  name_string_code,
  description,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)

VALUES
(gen_random_uuid(), 'ACTOR', 'role.actor', 'Usuario que representa un actor', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
(gen_random_uuid(), 'CASTINERA', 'role.castinera',  'Usuario que busca y gestiona actores', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);


------------------------------------------------------------------------
---- Insert Planes
------------------------------------------------------------------------
INSERT INTO plans (
  id,
  code,
  name_string_code,
  description,
  allows_custom_slug,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted
)
VALUES
  (gen_random_uuid(), 'FREE', 'plan.free', 'Plan gratuito para todos los usuarios', false, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'PREMIUM_ACTOR_1', 'plan.premium_actor_1', 'Permite slug personalizado', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
  (gen_random_uuid(), 'PREMIUM_CASTINERA', 'plan.premium_castinera', 'Acceso a búsquedas de actores', true, NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);


------------------------------------------------------------------------
---- Insert Site Metadata
------------------------------------------------------------------------
INSERT INTO skills (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.skill.athletics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.basketball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.boxing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.cycling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.football', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.gymnastics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.handball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.hockey', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.swimming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.padel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.rugby', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.tennis', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.volleyball', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.sport'),
    (gen_random_uuid(), 'sitemetadata.skill.acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.aerial_acrobatics', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.martial_arts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.capoeira', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stage_combat', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.contortion', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.tightrope_slackline', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.horseback_riding', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.climbing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stage_fencing', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.juggling', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.pantomime', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.physical_theater', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.parkour', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.skating', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stunts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.aerial_skills', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.harness_wirework', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.stilts', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.physical'),
    (gen_random_uuid(), 'sitemetadata.skill.german', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.chinese_mandarin', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.french', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.english', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.italian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.portuguese_br', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.russian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.language'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_arg', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_es', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_neutral', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_co', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_ch', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_pe', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.spanish_ve', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.english_us', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent'),
    (gen_random_uuid(), 'sitemetadata.skill.english_uk', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.accent');

INSERT INTO professions (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.profession.actor', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.dancer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.singer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.influencer', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.model', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.musician', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.standup', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic'),
    (gen_random_uuid(), 'sitemetadata.profession.voice_talent', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.scenic');

INSERT INTO gender_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.gender.male', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.female', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.male_trans', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.female_trans', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.non_binary', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.gender.other', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

INSERT INTO color_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted,
  category_string_code)
VALUES
    (gen_random_uuid(), 'sitemetadata.color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.dark_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.dark_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_blonde', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.red', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.white', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.no_hair', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.hair_color'),
    (gen_random_uuid(), 'sitemetadata.color.amber', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.hazel', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.light_blue', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.gray', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.brown', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.black', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.green', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color'),
    (gen_random_uuid(), 'sitemetadata.color.heterochromia', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false, 'sitemetadata.category.eye_color');

INSERT INTO diet_option (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.diet.omnivore', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.flexitarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.lacto_ovo_vegetarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.vegan', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.pescatarian', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.ketogenic', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.gluten_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.lactose_free', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.kosher', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.diet.halal', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);

INSERT INTO production_type  (
  id,
  string_code,
  created_at,
  created_by,
  modified_at,
  modified_by,
  deleted)
VALUES
    (gen_random_uuid(), 'sitemetadata.production_type.theatre', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.television_streaming', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.film', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false),
    (gen_random_uuid(), 'sitemetadata.production_type.commercial', NOW(), 'SYSTEM', NOW(), 'SYSTEM', false);



--------------------------------
-- TERMS --
--------------------------------
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
  $$<article class="prose prose-neutral max-w-3xl mx-auto">
  <p class="text-sm text-neutral-500 mb-2">Última actualización: <span data-var="updated_at">07/10/2025</span></p>

  <p class="mb-8">Al registrarte en autocasting, aceptás estos términos y condiciones de uso.</p>

  <ol class="space-y-6">
    <li>
      <p>
        <strong>1. Quiénes somos</strong>
      </p>
      <p>
        autocasting es una plataforma digital que conecta actores con productores, directores, agencias y empleadores.
      </p>
    </li>

    <li>
      <p>
        <strong>2. Uso de la plataforma</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Actores: pueden crear un perfil, subir fotos y videos, y compartirlo.</li>
        <li>Productores/empleadores: pueden buscar talentos, filtrar perfiles y publicar castings.</li>
        <li>Cada usuario es responsable de la veracidad de la información que cargue.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>3. Contenido cargado por los usuarios</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>El contenido que subas (fotos, videos, texto) debe ser tuyo o contar con los derechos para usarlo.</li>
        <li>autocasting puede mostrar ese contenido dentro de la plataforma para cumplir con sus funciones.</li>
        <li>No se permite subir contenido ofensivo, ilegal o que infrinja derechos de terceros.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>4. Suscripciones y pagos</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>El MVP es gratuito.</li>
        <li>En el futuro habrá planes pagos con beneficios adicionales.</li>
        <li>Los precios, formas de pago y condiciones se informarán claramente antes de la contratación.</li>
        <li>Las suscripciones se renuevan automáticamente, salvo cancelación.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>5. Responsabilidad</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>autocasting no garantiza que siempre consigas trabajo o talentos; es un espacio de conexión.</li>
        <li>No somos responsables de acuerdos o contratos entre usuarios fuera de la plataforma.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>6. Cancelación de cuentas</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Podés darte de baja cuando quieras.</li>
        <li>autocasting se reserva el derecho de suspender cuentas que incumplan estos términos.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>7. Modificaciones</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Podemos actualizar estos términos y lo notificaremos en la plataforma.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>8. Contacto</strong>
      </p>
      <p>
        Si tenés preguntas, escribinos a
        <a href="mailto:legal@autocasting.app" data-var="legal_email"> [email de contacto legal] </a>
        .
      </p>
    </li>
  </ol>
</article>
$$,
  encode(digest($$<article class="prose prose-neutral max-w-3xl mx-auto">
  <p class="text-sm text-neutral-500 mb-2">Última actualización: <span data-var="updated_at">07/10/2025</span></p>

  <p class="mb-8">Al registrarte en autocasting, aceptás estos términos y condiciones de uso.</p>

  <ol class="space-y-6">
    <li>
      <p>
        <strong>1. Quiénes somos</strong>
      </p>
      <p>
        autocasting es una plataforma digital que conecta actores con productores, directores, agencias y empleadores.
      </p>
    </li>

    <li>
      <p>
        <strong>2. Uso de la plataforma</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Actores: pueden crear un perfil, subir fotos y videos, y compartirlo.</li>
        <li>Productores/empleadores: pueden buscar talentos, filtrar perfiles y publicar castings.</li>
        <li>Cada usuario es responsable de la veracidad de la información que cargue.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>3. Contenido cargado por los usuarios</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>El contenido que subas (fotos, videos, texto) debe ser tuyo o contar con los derechos para usarlo.</li>
        <li>autocasting puede mostrar ese contenido dentro de la plataforma para cumplir con sus funciones.</li>
        <li>No se permite subir contenido ofensivo, ilegal o que infrinja derechos de terceros.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>4. Suscripciones y pagos</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>El MVP es gratuito.</li>
        <li>En el futuro habrá planes pagos con beneficios adicionales.</li>
        <li>Los precios, formas de pago y condiciones se informarán claramente antes de la contratación.</li>
        <li>Las suscripciones se renuevan automáticamente, salvo cancelación.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>5. Responsabilidad</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>autocasting no garantiza que siempre consigas trabajo o talentos; es un espacio de conexión.</li>
        <li>No somos responsables de acuerdos o contratos entre usuarios fuera de la plataforma.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>6. Cancelación de cuentas</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Podés darte de baja cuando quieras.</li>
        <li>autocasting se reserva el derecho de suspender cuentas que incumplan estos términos.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>7. Modificaciones</strong>
      </p>
      <ul class="list-disc pl-6">
        <li>Podemos actualizar estos términos y lo notificaremos en la plataforma.</li>
      </ul>
    </li>

    <li>
      <p>
        <strong>8. Contacto</strong>
      </p>
      <p>
        Si tenés preguntas, escribinos a
        <a href="mailto:legal@autocasting.app" data-var="legal_email"> [email de contacto legal] </a>
        .
      </p>
    </li>
  </ol>
</article>
$$, 'sha256'), 'hex'),
  now(),               -- effective_at
  now(),               -- published_at
  now(), 'SYSTEM', now(), 'SYSTEM', false
);

--------------------------------
-- PRIVACY --
--------------------------------
INSERT INTO legal_documents(
  id, type, locale, version, title, slug,
  status, format, body_template, content_hash,
  effective_at, published_at,
  created_at, created_by, modified_at, modified_by, deleted
)
VALUES (
  gen_random_uuid(),
  'PRIVACY',
  'es',
  '1.0.0',
  'Políticas de Privacidad',
  'privacy-policy',
  'PUBLISHED',
  'HTML',
  $$<article class="prose prose-neutral max-w-3xl mx-auto">
  <p class="text-sm text-neutral-500 mb-2">Última actualización: <span data-var="updated_at">07/10/2025</span></p>

  <p class="mb-8">
    En autocasting cuidamos la información de nuestros usuarios. Esta política explica qué datos recolectamos, cómo los
    usamos y cómo los protegemos.
  </p>

  <ol class="space-y-6">
    <li>
      <p><strong>1. Datos que recolectamos</strong></p>
      <ul class="list-disc pl-6">
        <li><strong>Datos de registro</strong>: nombre, email, contraseña.</li>
        <li><strong>Datos de perfil</strong>: fotos, videos, habilidades, formación, experiencia, etc.</li>
        <li><strong>Datos de uso</strong>: cómo navegás la plataforma, qué castings publicás o a cuáles aplicás.</li>
        <li>
          <strong>Datos de pago</strong> (cuando haya suscripciones): información de tarjeta o cuenta, procesada de
          forma segura por proveedores externos.
        </li>
      </ul>
    </li>

    <li>
      <p><strong>2. Cómo usamos tu información</strong></p>
      <ul class="list-disc pl-6">
        <li>Para crear y mostrar perfiles de actores.</li>
        <li>Para que productores, directores y empleadores publiquen y gestionen castings.</li>
        <li>Para conectar talentos con oportunidades.</li>
        <li>Para mejorar la plataforma y ofrecerte un mejor servicio.</li>
        <li>Para fines de facturación (en caso de suscripciones).</li>
      </ul>
    </li>

    <li>
      <p><strong>3. Con quién compartimos tu información</strong></p>
      <ul class="list-disc pl-6">
        <li>Con otros usuarios: la información que decidas cargar en tu perfil es pública dentro de la plataforma.</li>
        <li>Con proveedores externos de pago o hosting, siempre bajo acuerdos de seguridad.</li>
        <li>Nunca vendemos tu información a terceros.</li>
      </ul>
    </li>

    <li>
      <p><strong>4. Seguridad de los datos</strong></p>
      <ul class="list-disc pl-6">
        <li>Usamos cifrado y medidas técnicas para proteger tu información.</li>
        <li>Vos tenés control sobre editar o eliminar tu perfil en cualquier momento.</li>
      </ul>
    </li>

    <li>
      <p><strong>5. Tus derechos</strong></p>
      <ul class="list-disc pl-6">
        <li>Podés acceder, rectificar o borrar tus datos cuando quieras.</li>
        <li>
          Si tenés dudas, podés escribirnos a
          <a href="mailto:privacy@autocasting.app" data-var="privacy_email">[email de contacto legal]</a>.
        </li>
      </ul>
    </li>

    <li>
      <p><strong>6. Inicio de sesión con Google</strong></p>
      <ul class="list-disc pl-6">
        <li>
          Cuando te registrás con Google, recibimos tu nombre y dirección de correo electrónico. Esa información se
          guarda de forma temporal hasta que confirmes tu aceptación de los Términos de Uso y la Política de Privacidad.
          Si no completás el registro, esos datos podrán eliminarse automáticamente pasado un plazo razonable.
        </li>
      </ul>
    </li>
  </ol>
</article>
$$,
  encode(digest($$<article class="prose prose-neutral max-w-3xl mx-auto">
  <p class="text-sm text-neutral-500 mb-2">Última actualización: <span data-var="updated_at">07/10/2025</span></p>

  <p class="mb-8">
    En autocasting cuidamos la información de nuestros usuarios. Esta política explica qué datos recolectamos, cómo los
    usamos y cómo los protegemos.
  </p>

  <ol class="space-y-6">
    <li>
      <p><strong>1. Datos que recolectamos</strong></p>
      <ul class="list-disc pl-6">
        <li><strong>Datos de registro</strong>: nombre, email, contraseña.</li>
        <li><strong>Datos de perfil</strong>: fotos, videos, habilidades, formación, experiencia, etc.</li>
        <li><strong>Datos de uso</strong>: cómo navegás la plataforma, qué castings publicás o a cuáles aplicás.</li>
        <li>
          <strong>Datos de pago</strong> (cuando haya suscripciones): información de tarjeta o cuenta, procesada de
          forma segura por proveedores externos.
        </li>
      </ul>
    </li>

    <li>
      <p><strong>2. Cómo usamos tu información</strong></p>
      <ul class="list-disc pl-6">
        <li>Para crear y mostrar perfiles de actores.</li>
        <li>Para que productores, directores y empleadores publiquen y gestionen castings.</li>
        <li>Para conectar talentos con oportunidades.</li>
        <li>Para mejorar la plataforma y ofrecerte un mejor servicio.</li>
        <li>Para fines de facturación (en caso de suscripciones).</li>
      </ul>
    </li>

    <li>
      <p><strong>3. Con quién compartimos tu información</strong></p>
      <ul class="list-disc pl-6">
        <li>Con otros usuarios: la información que decidas cargar en tu perfil es pública dentro de la plataforma.</li>
        <li>Con proveedores externos de pago o hosting, siempre bajo acuerdos de seguridad.</li>
        <li>Nunca vendemos tu información a terceros.</li>
      </ul>
    </li>

    <li>
      <p><strong>4. Seguridad de los datos</strong></p>
      <ul class="list-disc pl-6">
        <li>Usamos cifrado y medidas técnicas para proteger tu información.</li>
        <li>Vos tenés control sobre editar o eliminar tu perfil en cualquier momento.</li>
      </ul>
    </li>

    <li>
      <p><strong>5. Tus derechos</strong></p>
      <ul class="list-disc pl-6">
        <li>Podés acceder, rectificar o borrar tus datos cuando quieras.</li>
        <li>
          Si tenés dudas, podés escribirnos a
          <a href="mailto:privacy@autocasting.app" data-var="privacy_email">[email de contacto legal]</a>.
        </li>
      </ul>
    </li>

    <li>
      <p><strong>6. Inicio de sesión con Google</strong></p>
      <ul class="list-disc pl-6">
        <li>
          Cuando te registrás con Google, recibimos tu nombre y dirección de correo electrónico. Esa información se
          guarda de forma temporal hasta que confirmes tu aceptación de los Términos de Uso y la Política de Privacidad.
          Si no completás el registro, esos datos podrán eliminarse automáticamente pasado un plazo razonable.
        </li>
      </ul>
    </li>
  </ol>
</article>
$$, 'sha256'), 'hex'),
  now(),               -- effective_at
  now(),               -- published_at
  now(), 'SYSTEM', now(), 'SYSTEM', false
);
