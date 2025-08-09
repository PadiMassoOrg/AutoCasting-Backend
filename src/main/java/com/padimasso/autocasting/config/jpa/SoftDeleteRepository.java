package com.padimasso.autocasting.config.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
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
}

