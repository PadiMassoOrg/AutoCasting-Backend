package com.padimasso.autocasting.config.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class SoftDeleteRepositoryImpl<T, I extends Serializable>
    extends SimpleJpaRepository<T, I>
    implements SoftDeleteRepository<T, I> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    public SoftDeleteRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityInformation = entityInformation;
        this.em = em;
    }

    private Specification<T> notDeleted() {
        return (root, q, cb) -> cb.isFalse(root.get("deleted"));
    }

    private Specification<T> andNotDeleted(@Nullable Specification<T> spec) {
        return (spec == null) ? notDeleted() : spec.and(notDeleted());
    }

    /* --------- overrides con filtro por defecto --------- */

    @Override
    public Optional<T> findById(I id) {
        Specification<T> byId = (root, q, cb) ->
            cb.equal(root.get(entityInformation.getIdAttribute().getName()), id);
        return super.findOne(andNotDeleted(byId));
    }

    @Override
    public boolean existsById(I id) {
        return findById(id).isPresent();
    }

    @Override
    public List<T> findAll() {
        return super.findAll(notDeleted());
    }

    @Override
    public List<T> findAll(Sort sort) {
        return super.findAll(notDeleted(), sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return super.findAll(notDeleted(), pageable);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        return super.findAll(andNotDeleted(spec));
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        return super.findAll(andNotDeleted(spec), pageable);
    }

    @Override
    public long count() {
        return super.count(notDeleted());
    }

    @Override
    public long count(Specification<T> spec) {
        return super.count(andNotDeleted(spec));
    }

    /* --------- soft delete/restore utilitarios --------- */

    @Override
    public void softDeleteById(I id) {
        findById(id).ifPresent(this::softDelete);
    }

    @Override
    public void softDelete(T entity) {
        if (entity instanceof com.padimasso.autocasting.application.common.model.AuditableEntity a) {
            a.setDeleted(true);
            super.save(entity);
        } else {
            super.delete(entity); // fallback
        }
    }

    @Override
    public void restoreById(I id) {
        findByIdIncludingDeleted(id).ifPresent(e -> {
            if (e instanceof com.padimasso.autocasting.application.common.model.AuditableEntity a) {
                a.setDeleted(false);
                super.save(e);
            }
        });
    }

    /* --------- métodos “admin”: incluyen borrados --------- */

    @Override
    public List<T> findAllIncludingDeleted() {
        return super.findAll(); // sin notDeleted()
    }

    @Override
    public Optional<T> findByIdIncludingDeleted(I id) {
        return super.findById(id);
    }

    @Override
    public Page<T> findAllIncludingDeleted(Pageable pageable) {
        return super.findAll(pageable);
    }

    // -------- helper privado para navegar el path "a.b.c" ----------
    private static Path<?> getPath(From<?, ?> root, String path) {
        Path<?> p = root;
        for (String part : path.split("\\.")) {
            p = p.get(part);
        }
        return p;
    }

    // -------- nuevos genéricos (filtrados por deleted=false) ----------
    @Override
    public List<T> findAllByPropertyEquals(String path, Object value) {
        return findAll((root, q, cb) -> cb.equal(getPath(root, path), value));
    }

    @Override
    public Page<T> findAllByPropertyEquals(String path, Object value, Pageable pageable) {
        return findAll((root, q, cb) -> cb.equal(getPath(root, path), value), pageable);
    }

    @Override
    public Instant findMaxModifiedAt() {
        var cb = em.getCriteriaBuilder();

        // No fijamos el tipo de retorno a Instant para evitar conflictos con LocalDateTime
        var cq = cb.createQuery(Object.class);

        @SuppressWarnings("unchecked")
        var root = cq.from(entityInformation.getJavaType());

        // select modifiedAt where deleted=false order by modifiedAt desc limit 1
        var mod = root.get("modifiedAt");                 // Path<?> tipado en runtime
        cq.select(mod);
        cq.where(cb.isFalse(root.get("deleted")));
        cq.orderBy(cb.desc(mod));

        var list = em.createQuery(cq).setMaxResults(1).getResultList();
        if (list.isEmpty() || list.getFirst() == null) return null;

        Object v = list.getFirst();
        if (v instanceof java.time.Instant inst) return inst;
        if (v instanceof java.time.LocalDateTime ldt) return ldt.atZone(java.time.ZoneOffset.UTC).toInstant();
        if (v instanceof java.time.OffsetDateTime odt) return odt.toInstant();
        if (v instanceof java.time.ZonedDateTime zdt) return zdt.toInstant();
        if (v instanceof java.sql.Timestamp ts) return ts.toInstant();

        throw new IllegalStateException("Unsupported modifiedAt type: " + v.getClass());
    }

    // -------- variantes “admin” que NO aplican filtro deleted ----------
    @Override
    public List<T> findAllIncludingDeletedByPropertyEquals(String path, Object value) {
        return findAllIncludingDeleted((root, q, cb) -> cb.equal(getPath(root, path), value));
    }

    @Override
    public Page<T> findAllIncludingDeletedByPropertyEquals(String path, Object value, Pageable pageable) {
        return findAllIncludingDeleted((root, q, cb) -> cb.equal(getPath(root, path), value), pageable);
    }

    // Permite pasar una Specification arbitraria *sin* filtro deleted (admin)
    public List<T> findAllIncludingDeleted(Specification<T> spec) {
        return super.findAll(spec); // sin notDeleted()
    }

    public Page<T> findAllIncludingDeleted(Specification<T> spec, Pageable pageable) {
        return super.findAll(spec, pageable); // sin notDeleted()
    }
}
