-- ⚠️ Ejecutar sobre una DB con esquema legal_documents ya creado por Flyway.
-- Requiere pgcrypto para digest/gen_random_uuid (instalado en V1).

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
  now(),
  now(),
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
  now(),
  now(),
  now(), 'SYSTEM', now(), 'SYSTEM', false
);
