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
  p_legal_version := '3.4.0',
  p_terms_md := $terms_md$
**Última actualización:** 17/06/2026

**Versión legal:** 3.4.0

# Términos y Condiciones

Estos Términos y Condiciones regulan el acceso y uso de Autocasting (la "Plataforma"). Al registrarte, iniciar sesión o utilizar la Plataforma, declarás que leíste y aceptaste estos términos. Si no estás de acuerdo, debés abstenerte de usarla.

## 1. Identificación del proveedor

El servicio es prestado por **Matias Sebastián Di Masso**, CUIT/CUIL **20-3525773-9**, con domicilio legal en **11 de Septiembre 4431, CABA** (en adelante, **"Autocasting"**, **"nosotros"** o **"la Plataforma"**).

Consultas legales: [info@autocasting.app](mailto:info@autocasting.app)

## 2. Objeto del servicio

Autocasting es una plataforma tecnológica de intermediación que permite publicar, buscar, gestionar y compartir oportunidades laborales o artísticas. La Plataforma facilita el contacto entre usuarios, pero no es parte de los contratos, acuerdos, negociaciones ni relaciones que celebren entre sí.

## 3. Alcance del servicio

Autocasting no actúa como empleador, agencia, productora, representante, mandataria, intermediaria laboral ni garante de la veracidad, idoneidad o disponibilidad de usuarios, perfiles, castings, publicaciones o mensajes.

Cada usuario es responsable de evaluar, verificar y decidir con quién interactúa.

## 4. Requisitos de uso y capacidad

- Debés tener capacidad legal para contratar conforme a la normativa aplicable.
- Si sos menor de edad, el uso requiere autorización de madre, padre o tutor legal.
- Debés brindar información veraz, completa y actualizada.
- No podés usar la Plataforma si la legislación aplicable te lo prohíbe.

## 5. Cuenta y credenciales

- La cuenta es personal e intransferible.
- Sos responsable por la confidencialidad de tus credenciales y por toda actividad realizada en tu cuenta.
- Debés notificarnos inmediatamente cualquier uso no autorizado, sospecha de acceso indebido o incidente de seguridad.
- Podemos solicitar verificaciones adicionales para proteger la cuenta, prevenir fraude o cumplir obligaciones legales.

## 6. Contenido del usuario y licencias

- Conservás la titularidad sobre el contenido que subís o compartís, incluyendo texto, imágenes, videos, audios, enlaces y demás materiales.
- Nos otorgás una licencia no exclusiva, mundial, gratuita, sublicenciable en la medida necesaria y revocable conforme la ley aplicable, para alojar, reproducir, transformar técnicamente, mostrar, distribuir y comunicar tu contenido dentro de la Plataforma para su funcionamiento.
- Garantizás que contás con derechos y autorizaciones suficientes sobre el contenido subido, incluyendo derechos de imagen, voz, marca y demás derechos de terceros cuando corresponda.
- Nos autorizás a generar copias técnicas, miniaturas, cachés, transcodificaciones y otras adaptaciones operativas necesarias para prestar el servicio.

## 7. Comunicaciones y contactos fuera de la Plataforma

Cualquier conversación, contacto, coordinación, negociación, contratación, intercambio de datos o relación que ocurra fuera de Autocasting, incluyendo por email, WhatsApp, llamadas, redes sociales o encuentros presenciales, queda completamente fuera de nuestra intervención, supervisión y responsabilidad.

No verificamos esos contactos, no garantizamos su autenticidad, no controlamos sus términos y no asumimos obligación alguna derivada de ellos.

## 8. Conductas prohibidas

- Publicar contenido ilícito, discriminatorio, difamatorio, violento, sexual no autorizado o que infrinja derechos de terceros.
- Suplantar identidad, falsificar datos, manipular procesos de selección o publicar oportunidades engañosas.
- Intentar vulnerar la seguridad, disponibilidad o integridad de la Plataforma o de sus sistemas.
- Usar scraping automatizado, bots no autorizados, ingeniería inversa o técnicas equivalentes sin consentimiento expreso.
- Cargar malware, contenido corrupto o mecanismos destinados a degradar el servicio.

## 9. Disponibilidad, respaldos y pérdida de datos

La Plataforma se presta con esfuerzos razonables de continuidad, respaldo y recuperación. Sin embargo, no garantizamos disponibilidad ininterrumpida, ausencia total de errores ni preservación absoluta de la información.

Pueden ocurrir interrupciones, caídas, fallas de proveedores, errores humanos, ataques, corrupción de datos o eventos de fuerza mayor que afecten perfiles, castings, archivos, mensajes o cualquier otro contenido.

En esos casos, podremos intentar restaurar información desde respaldos disponibles, pero no prometemos recuperación total ni nos hacemos responsables por pérdidas no imputables a dolo o culpa grave conforme la ley aplicable. Te recomendamos mantener copias propias de cualquier información crítica.

## 10. Moderación, suspensión y baja

Autocasting podrá, a su exclusivo criterio y sin generar derecho a indemnización, remover contenido, limitar, suspender o cancelar cuentas por incumplimientos, riesgos de seguridad, requerimientos legales o protección de terceros.

## 11. Naturaleza de la relación entre usuarios

Autocasting no garantiza contrataciones, resultados de castings, ingresos ni continuidad de oportunidades. Toda negociación, verificación, coordinación y contratación entre usuarios es de exclusiva responsabilidad de las partes intervinientes.

## 12. Limitación de responsabilidad

- La Plataforma se ofrece **"tal como está"** y **"según disponibilidad"**.
- No garantizamos funcionamiento ininterrumpido ni ausencia absoluta de errores o vulnerabilidades.
- En la máxima medida permitida por la ley, Autocasting no será responsable por daños indirectos, lucro cesante, pérdida de chance, pérdida de datos, pérdida de información, pérdida de oportunidades, ni por daños derivados de relaciones, comunicaciones o acuerdos entre usuarios.
- Tampoco responderemos por perjuicios derivados de contactos o acuerdos realizados fuera de la Plataforma.
- Nada en estos términos limita derechos irrenunciables del consumidor bajo la **Ley 24.240** y normativa aplicable.

## 13. Indemnidad

El usuario se obliga a mantener indemne a Autocasting, sus directivos, empleados, contratistas y afiliadas frente a reclamos, sanciones, daños, costos y gastos, incluyendo honorarios razonables, derivados del uso indebido de la Plataforma, incumplimientos de estos términos o violaciones a derechos de terceros.

## 14. Datos personales y privacidad

El tratamiento de datos personales se rige por la **Política de Privacidad** vigente, que integra estos términos.

Autocasting podrá registrar evidencia técnica de aceptación, incluyendo usuario, versión, hash del documento, IP, agente de usuario y fecha/hora, para fines de trazabilidad contractual y defensa legal.

## 15. Modificaciones y versionado

Podremos modificar estos términos en cualquier momento. Las nuevas versiones se publicarán con fecha de vigencia. Para continuar operando, podrá requerirse aceptación expresa de la versión vigente.

Si no aceptás la versión vigente de Términos y/o Política de Privacidad requerida para operar, no podrás continuar utilizando funcionalidades protegidas de la Plataforma y podremos cerrar tu sesión.

## 16. Ley aplicable y jurisdicción

Estos términos se rigen por las leyes de la República Argentina, incluyendo, según corresponda, el Código Civil y Comercial de la Nación, la **Ley 24.240** y la **Ley 25.326**. Para toda controversia, las partes se someten a los tribunales ordinarios competentes de la Ciudad Autónoma de Buenos Aires, salvo competencia imperativa en contrario.

## 17. Contacto legal

Consultas legales: [info@autocasting.app](mailto:info@autocasting.app)
$terms_md$,
  p_privacy_md := $privacy_md$
**Última actualización:** 17/06/2026

**Versión legal:** 3.4.0

# Política de Privacidad

Esta Política de Privacidad describe cómo Autocasting recopila, usa, almacena, comparte y protege los datos personales de quienes utilizan la Plataforma.

## 1. Responsable del tratamiento

El responsable de la base de datos y del tratamiento es **Matias Sebastián Di Masso**, CUIT/CUIL **20-3525773-9**, con domicilio legal en **11 de Septiembre 4431, CABA**.

Consultas legales y de privacidad: [info@autocasting.app](mailto:info@autocasting.app)

## 2. Marco normativo

Tratamos datos conforme la **Ley 25.326** de Protección de Datos Personales, su reglamentación, disposiciones de la **AAIP** y demás normativa aplicable en la República Argentina.

## 3. Datos que recolectamos

- **Datos de cuenta:** email, contraseña cifrada, proveedor de autenticación y estado de la cuenta.
- **Datos de perfil:** nombre artístico, biografía, redes, experiencia, formación, imágenes, videos y material audiovisual.
- **Datos de actividad:** postulaciones, publicaciones, interacciones, mensajes, cambios de estado y eventos de uso.
- **Datos técnicos:** IP, user-agent, identificadores de sesión, registros de acceso y eventos de seguridad.
- **Datos de soporte:** solicitudes de ayuda, reportes de error y comunicaciones enviadas al equipo.

## 4. Finalidades del tratamiento

- Prestar y mantener el servicio, autenticar usuarios y gestionar cuentas.
- Permitir publicación de castings, búsqueda de perfiles, postulaciones y demás funciones de la Plataforma.
- Prevenir fraude, abuso, accesos indebidos, spam e incidentes de seguridad.
- Administrar respaldos, recuperación operativa, auditoría y continuidad del servicio.
- Cumplir obligaciones legales, regulatorias, fiscales y requerimientos de autoridad competente.
- Comunicar novedades operativas, contractuales, de seguridad o de funcionamiento de la Plataforma.

## 5. Base legal

- Ejecución de la relación contractual y prestación del servicio solicitado.
- Consentimiento del titular cuando resulte exigible.
- Interés legítimo para seguridad, mejora del servicio, prevención de abuso y defensa legal.
- Cumplimiento de obligaciones legales.

## 6. Compartición de datos

- Con otros usuarios, según la configuración, visibilidad del perfil o alcance de la publicación.
- Con proveedores tecnológicos para infraestructura, almacenamiento, respaldo, analítica, mensajería, soporte y envío de comunicaciones.
- Con autoridades públicas cuando exista requerimiento legal válido.

No vendemos datos personales a terceros.

## 7. Transferencias internacionales

Algunos proveedores pueden procesar datos fuera de Argentina. En esos casos aplicamos salvaguardas contractuales y medidas razonables de seguridad para proteger la información.

## 8. Plazos de conservación

- Conservamos los datos mientras la cuenta esté activa y durante los plazos necesarios por obligaciones legales, contables, regulatorias o de defensa.
- Podemos mantener información mínima bloqueada para defensa legal, auditoría, prevención de fraude y trazabilidad operativa.
- Cuando corresponda, anonimizamos o eliminamos datos de manera segura.

## 9. Seguridad de la información, respaldos y pérdida de datos

Implementamos medidas técnicas y organizativas razonables para proteger datos contra acceso no autorizado, alteración, pérdida o divulgación indebida.

Esto incluye, entre otras medidas, controles de acceso, registros técnicos, respaldo y procedimientos de recuperación. Sin embargo, ningún sistema es absolutamente invulnerable y pueden producirse incidentes, caídas, errores humanos, ataques, fallas de proveedores, corrupción de datos o eventos de fuerza mayor.

Si ocurriera una pérdida parcial o total de datos, castings, archivos o información almacenada, podremos intentar su recuperación desde respaldos disponibles y aplicar medidas de mitigación. No garantizamos recuperación íntegra ni continuidad absoluta del servicio.

## 10. Derechos del titular

Podés ejercer derechos de acceso, rectificación, actualización, supresión, oposición y confidencialidad enviando una solicitud a [info@autocasting.app](mailto:info@autocasting.app).

Si considerás vulnerados tus derechos, podés efectuar una denuncia ante la Agencia de Acceso a la Información Pública (AAIP).

## 11. Cookies y tecnologías similares

Utilizamos cookies, almacenamiento local y tecnologías equivalentes para autenticación, seguridad, preferencias y funcionamiento técnico. Podés configurar tu navegador para bloquearlas, aunque esto puede afectar funcionalidades.

## 12. Menores de edad

Si detectamos tratamiento de datos de menores sin representación o autorización válida, podremos limitar o eliminar la cuenta y su contenido hasta regularizar la situación.

## 13. Evidencia de aceptación y versionado legal

Al aceptar TyC/PP registramos versión, hash del documento, fecha/hora, usuario autenticado, IP y agente de usuario para fines de trazabilidad contractual y defensa legal.

## 14. Cambios a esta política

Podemos actualizar esta política periódicamente. Publicaremos la versión vigente con su fecha de actualización y, cuando corresponda, solicitaremos una nueva aceptación expresa.

## 15. Contacto

Consultas sobre privacidad o protección de datos: [info@autocasting.app](mailto:info@autocasting.app)
$privacy_md$
);

-- Opcional: evitar dejar el objeto creado en schema
DROP PROCEDURE IF EXISTS publish_legal_release(text, text, text);
