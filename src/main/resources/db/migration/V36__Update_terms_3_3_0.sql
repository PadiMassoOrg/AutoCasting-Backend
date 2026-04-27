-- VXX__legal_documents_release.sql

CREATE OR REPLACE PROCEDURE publish_legal_release(
  p_legal_version text,
  p_terms_md text,
  p_privacy_md text
)
LANGUAGE plpgsql
AS $proc$
BEGIN
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
    p_legal_version,
    'Términos y Condiciones',
    'terms-and-conditions',
    'PUBLISHED',
    'MARKDOWN',
    p_terms_md,
    encode(digest(p_terms_md, 'sha256'), 'hex'),
    NOW(),
    NOW(),
    NOW(), 'SYSTEM', NOW(), 'SYSTEM', false
  )
  ON CONFLICT (type, locale, version) DO UPDATE SET
    title = EXCLUDED.title,
    slug = EXCLUDED.slug,
    status = EXCLUDED.status,
    format = EXCLUDED.format,
    body_template = EXCLUDED.body_template,
    content_hash = EXCLUDED.content_hash,
    effective_at = NOW(),
    published_at = NOW(),
    deleted = false,
    modified_at = NOW(),
    modified_by = 'FLYWAY_RELEASE';

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
    p_legal_version,
    'Políticas de Privacidad',
    'privacy-policy',
    'PUBLISHED',
    'MARKDOWN',
    p_privacy_md,
    encode(digest(p_privacy_md, 'sha256'), 'hex'),
    NOW(),
    NOW(),
    NOW(), 'SYSTEM', NOW(), 'SYSTEM', false
  )
  ON CONFLICT (type, locale, version) DO UPDATE SET
    title = EXCLUDED.title,
    slug = EXCLUDED.slug,
    status = EXCLUDED.status,
    format = EXCLUDED.format,
    body_template = EXCLUDED.body_template,
    content_hash = EXCLUDED.content_hash,
    effective_at = NOW(),
    published_at = NOW(),
    deleted = false,
    modified_at = NOW(),
    modified_by = 'FLYWAY_RELEASE';
END;
$proc$;

CALL publish_legal_release(
  p_legal_version := '3.3.0',
  p_terms_md := $terms_md$
**Última actualización:** 27/04/2026

# Términos y Condiciones

Estos Términos y Condiciones regulan el acceso y uso de autocasting (la “Plataforma”). Al registrarte, iniciar sesión y utilizar la Plataforma, declarás que leíste y aceptaste estos términos.

## 1. Identificación del proveedor

El servicio es prestado por **[RAZÓN SOCIAL]**, CUIT **[CUIT]**, con domicilio en **[DOMICILIO LEGAL]** (en adelante, **“autocasting”**, **“nosotros”** o **“la Plataforma”**).

## 2. Objeto del servicio

autocasting facilita el contacto entre talentos, productoras, agencias, directores y empleadores para publicar, buscar y gestionar oportunidades laborales o artísticas. autocasting actúa como intermediario tecnológico y no es parte de los contratos que celebren los usuarios entre sí.

## 3. Requisitos de uso y capacidad

- Debés tener capacidad legal para contratar conforme la normativa argentina.
- Si sos menor de edad, el uso requiere autorización de madre/padre/tutor legal.
- Debés brindar información veraz, completa y actualizada.

## 4. Cuenta y credenciales

- La cuenta es personal e intransferible.
- Sos responsable por la confidencialidad de tus credenciales y por toda actividad realizada en tu cuenta.
- Debés notificarnos inmediatamente cualquier uso no autorizado o incidente de seguridad.

## 5. Contenido del usuario y licencias

- Conservás la titularidad sobre el contenido que subís (texto, imágenes, videos, audios, enlaces y demás materiales).
- Nos otorgás una licencia no exclusiva, mundial, gratuita, revocable y necesaria para alojar, reproducir, transformar técnicamente, mostrar y comunicar tu contenido dentro de la Plataforma para su funcionamiento.
- Garantizás que contás con derechos y autorizaciones suficientes sobre el contenido subido, incluyendo derechos de imagen de terceros cuando corresponda.

## 6. Funcionalidades actuales y estado comercial

- La Plataforma actualmente permite, entre otras funciones, registro/login (incluyendo OAuth con Google), gestión de perfiles de talento y empleador, publicación/gestión de castings, y postulaciones.
- A la fecha de esta versión, **no se encuentra habilitado un flujo de cobro online dentro de la Plataforma** (pasarela de pago, cobro con tarjeta o billeteras).
- Si en el futuro se habilitan funcionalidades pagas, se informarán de forma previa sus condiciones comerciales específicas.

## 7. Conductas prohibidas

- Publicar contenido ilícito, discriminatorio, difamatorio, violento, sexual no autorizado o que infrinja derechos de terceros.
- Suplantar identidad, falsificar datos, manipular procesos de selección o publicar oportunidades engañosas.
- Intentar vulnerar la seguridad, disponibilidad o integridad de la Plataforma y sus sistemas.
- Usar scraping automatizado, bots no autorizados o ingeniería inversa sin consentimiento expreso.

## 8. Moderación, suspensión y baja

autocasting podrá, a su exclusivo criterio y sin generar derecho a indemnización, remover contenido, limitar, suspender o cancelar cuentas por incumplimientos, riesgos de seguridad, requerimientos legales o protección de terceros.

## 9. Naturaleza de la relación entre usuarios

autocasting no garantiza contrataciones, resultados de castings ni ingresos. Toda negociación, verificación y contratación entre usuarios es de exclusiva responsabilidad de las partes intervinientes.

## 10. Propiedad intelectual de la Plataforma

El software, diseño, marcas, logotipos, bases de datos y demás activos de autocasting están protegidos por normativa de propiedad intelectual e industrial. Queda prohibido su uso no autorizado.

## 11. Limitación de responsabilidad

- La Plataforma se ofrece **“tal como está”** y **“según disponibilidad”**.
- No garantizamos funcionamiento ininterrumpido ni ausencia absoluta de errores o vulnerabilidades.
- En la máxima medida permitida por la ley, autocasting no será responsable por daños indirectos, lucro cesante, pérdida de chance, pérdida de datos o daños derivados de relaciones entre usuarios.
- Nada en estos términos limita derechos irrenunciables del consumidor bajo la **Ley 24.240** y normativa aplicable.

## 12. Indemnidad

El usuario se obliga a mantener indemne a autocasting, sus directivos, empleados y afiliadas frente a reclamos, sanciones, daños, costos y gastos (incluyendo honorarios razonables) derivados del uso indebido de la Plataforma, incumplimientos de estos términos o violaciones a derechos de terceros.

## 13. Datos personales y privacidad

El tratamiento de datos personales se rige por la **Política de Privacidad** vigente, que integra estos términos. autocasting podrá registrar evidencia técnica de aceptación (usuario, versión, hash del documento, IP, agente de usuario y fecha/hora).

## 14. Modificaciones y versionado

Podremos modificar estos términos en cualquier momento. Las nuevas versiones se publicarán con fecha de vigencia. Para continuar operando, podrá requerirse aceptación expresa de la versión vigente al iniciar sesión.

## 15. No aceptación de nuevas versiones

Si no aceptás la versión vigente de Términos y/o Política de Privacidad requerida para operar, no podrás continuar utilizando funcionalidades protegidas de la Plataforma y podremos cerrar tu sesión.

## 16. Ley aplicable y jurisdicción

Estos términos se rigen por las leyes de la República Argentina (incluyendo, según corresponda, Código Civil y Comercial de la Nación, **Ley 24.240** y **Ley 25.326**). Para toda controversia, las partes se someten a los tribunales ordinarios competentes de la Ciudad Autónoma de Buenos Aires, salvo competencia imperativa en contrario.

## 17. Contacto legal

Consultas legales: [legal@autocasting.app](mailto:legal@autocasting.app)

$terms_md$,
  p_privacy_md := $privacy_md$
**Última actualización:** 27/04/2026

# Política de Privacidad

Esta Política de Privacidad describe cómo autocasting recolecta, usa, almacena, comparte y protege los datos personales de quienes utilizan la Plataforma.

## 1. Responsable del tratamiento

El responsable de la base de datos es **[RAZÓN SOCIAL]**, CUIT **[CUIT]**, con domicilio en **[DOMICILIO LEGAL]**. Contacto: [privacy@autocasting.app](mailto:privacy@autocasting.app).

## 2. Marco normativo

Tratamos datos conforme la **Ley 25.326** de Protección de Datos Personales, su reglamentación, disposiciones de la **AAIP** y demás normativa aplicable en la República Argentina.

## 3. Datos que recolectamos

- **Datos de cuenta:** email, contraseña cifrada, proveedor de autenticación.
- **Datos de perfil:** nombre artístico, bio, redes, experiencia, formación, material audiovisual.
- **Datos de actividad:** interacciones, postulaciones, publicaciones, logs técnicos y métricas de uso.
- **Datos técnicos:** IP, user-agent, identificadores de sesión y eventos de seguridad.

## 4. Finalidades del tratamiento

- Prestar y mantener el servicio, autenticar usuarios y gestionar cuentas.
- Permitir publicación de castings, búsqueda de perfiles y postulaciones.
- Prevenir fraude, abuso, accesos indebidos e incidentes de seguridad.
- Cumplir obligaciones legales, regulatorias, fiscales y requerimientos de autoridad competente.
- Comunicar novedades operativas, contractuales o de seguridad.

## 5. Base legal

- Ejecución de la relación contractual y prestación del servicio solicitado.
- Consentimiento del titular cuando resulte exigible.
- Interés legítimo para seguridad, mejora de servicio y prevención de abuso.
- Cumplimiento de obligaciones legales.

## 6. Compartición de datos

- Con otros usuarios, según la configuración y visibilidad del perfil o casting.
- Con proveedores tecnológicos para infraestructura y almacenamiento (incluyendo Supabase para storage de archivos y base de datos), hosting, analítica, mensajería y soporte.
- Con autoridades públicas por requerimiento legal válido.

No vendemos datos personales a terceros.

## 7. Transferencias internacionales

Algunos proveedores pueden procesar datos fuera de Argentina. En esos casos aplicamos salvaguardas contractuales y medidas razonables de seguridad para proteger la información.

## 8. Plazos de conservación

- Conservamos los datos mientras la cuenta esté activa y durante plazos necesarios por obligaciones legales.
- Podemos mantener información mínima bloqueada para defensa legal, auditoría y prevención de fraude.
- Cuando corresponda, anonimizamos o eliminamos datos de manera segura.

## 9. Seguridad de la información

Implementamos medidas técnicas y organizativas razonables para proteger datos contra acceso no autorizado, alteración, pérdida o divulgación indebida. Sin embargo, ningún sistema es absolutamente invulnerable.

## 10. Derechos del titular

Podés ejercer derechos de acceso, rectificación, actualización, supresión, oposición y confidencialidad enviando una solicitud a [privacy@autocasting.app](mailto:privacy@autocasting.app). Si considerás vulnerados tus derechos, podés efectuar denuncia ante la Agencia de Acceso a la Información Pública (AAIP).

## 11. Cookies y tecnologías similares

Utilizamos cookies y almacenamiento local para autenticación, seguridad, preferencias y funcionamiento técnico. Podés configurar tu navegador para bloquearlas, aunque esto puede afectar funcionalidades.

## 12. Menores de edad

Si detectamos tratamiento de datos de menores sin representación o autorización válida, podremos limitar o eliminar la cuenta y su contenido hasta regularizar la situación.

## 13. Evidencia de aceptación y versionado legal

Al aceptar TyC/PP registramos versión, hash del documento, fecha/hora, usuario autenticado, IP y agente de usuario para fines de trazabilidad contractual y defensa legal.

## 14. Cambios a esta política

Podemos actualizar esta política periódicamente. Publicaremos la versión vigente con su fecha de actualización y, cuando corresponda, solicitaremos una nueva aceptación expresa.

## 15. Contacto

Consultas sobre privacidad o protección de datos: [privacy@autocasting.app](mailto:privacy@autocasting.app)

$privacy_md$
);

-- Opcional: evitar dejar el objeto creado en schema
DROP PROCEDURE IF EXISTS publish_legal_release(text, text, text);