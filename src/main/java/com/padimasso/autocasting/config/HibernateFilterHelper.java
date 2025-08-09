package com.padimasso.autocasting.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class HibernateFilterHelper {

    private static final String DELETED_FILTER = "deletedFilter";
    private static final String IS_DELETED = "isDeleted";

    @PersistenceContext
    private final EntityManager em;

    public AutoCloseable withoutDeletedFilter() {
        Session s = em.unwrap(Session.class);
        boolean wasEnabled = s.getEnabledFilter(DELETED_FILTER) != null;
        if (wasEnabled) s.disableFilter(DELETED_FILTER);
        return () -> {
            if (wasEnabled) s.enableFilter(DELETED_FILTER).setParameter(IS_DELETED, false);
        };
    }

    public void showOnlyDeleted() {
        em.unwrap(Session.class).enableFilter(DELETED_FILTER).setParameter(IS_DELETED, true);
    }

    public void showOnlyNotDeleted() {
        em.unwrap(Session.class).enableFilter(DELETED_FILTER).setParameter(IS_DELETED, false);
    }

    // How to
    // 1. Fetch All - Included Deleted = True
    //  try (var ignored = filterHelper.withoutDeletedFilter()) {
    //      return repository.findAll(); // incluye deleted=true y deleted=false
    //  }
    //
    // 2. Fetch Deteled Only - Deleted = true
    //    filterHelper.showOnlyDeleted();
    //    var result = repository.findAll();
    //    filterHelper.showOnlyNotDeleted(); // deja el estado por defecto
    //    return result;
}
