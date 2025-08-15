package com.padimasso.autocasting.config.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
@SuppressWarnings("unused")
public interface SoftDeleteRepository<T, I extends Serializable>
    extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {

    /* admin – incluyen borrados */
    List<T> findAllIncludingDeleted();

    Page<T> findAllIncludingDeleted(Pageable pageable);

    Optional<T> findByIdIncludingDeleted(I id);

    /* utilitarios soft delete / restore */
    void softDelete(T entity);

    void softDeleteById(I id);

    void restoreById(I id);


    // -------- (filtrados por deleted=false) ----------
    List<T> findAllByPropertyEquals(String path, Object value);

    Page<T> findAllByPropertyEquals(String path, Object value, Pageable pageable);

    Instant findMaxModifiedAt();

    // -------- variantes “admin” que NO aplican filtro deleted ----------
    List<T> findAllIncludingDeletedByPropertyEquals(String path, Object value);

    Page<T> findAllIncludingDeletedByPropertyEquals(String path, Object value, Pageable pageable);

    // Permite pasar una Specification arbitraria *sin* filtro deleted (admin)
    List<T> findAllIncludingDeleted(Specification<T> spec);

    Page<T> findAllIncludingDeleted(Specification<T> spec, Pageable pageable);
}

