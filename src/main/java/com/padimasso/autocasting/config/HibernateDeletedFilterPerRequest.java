package com.padimasso.autocasting.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@SuppressWarnings("unused")
public class HibernateDeletedFilterPerRequest extends OncePerRequestFilter {

    private static final String DELETED_FILTER = "deletedFilter";

    @PersistenceContext
    private EntityManager em;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        Session session = em.unwrap(Session.class);
        boolean enabledHere = false;

        try {
            // habilitar si no está habilitado todavía
            if (session.getEnabledFilter(DELETED_FILTER) == null) {
                session.enableFilter(DELETED_FILTER).setParameter("isDeleted", false);
                enabledHere = true;
            }
            filterChain.doFilter(request, response);
        } finally {
            // limpia el estado solo si lo habilitaste aquí
            if (enabledHere && session.isOpen()) {
                session.disableFilter(DELETED_FILTER);
            }
        }
    }
}
