package com.sismics.docs.core.dao;

import com.sismics.docs.core.model.jpa.RegistrationRequest;
import com.sismics.docs.core.util.AuditLogUtil;
import com.sismics.util.context.ThreadLocalContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * DAO for user registration requests.
 */
public class RegistrationRequestDao {
    /**
     * Creates a new registration request as a guest (no user ID).
     *
     * @param request RegistrationRequest to create
     * @return New request ID
     */
    public void create(RegistrationRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        // Initialize and persist
        request.setCreateDate(new Date());
        request.setId(UUID.randomUUID().toString());
        em.persist(request);
    }

    /**
     * Updates an existing registration request's status and processing info.
     *
     * @param request Updated RegistrationRequest
     *
     */
    public void update(RegistrationRequest request) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        RegistrationRequest requestDb = em.find(RegistrationRequest.class, request.getId());
        if (requestDb == null) {
            throw new NoResultException("RegistrationRequest not found for id: " + request.getId());
        }
        requestDb.setStatus(request.getStatus());
        requestDb.setProcessedDate(request.getProcessedDate());
        requestDb.setProcessedBy(request.getProcessedBy());
        em.merge(requestDb);
    }

    /**
     * Finds a registration request by its ID.
     *
     * @param id Request ID
     * @return RegistrationRequest or null if not found
     */
    public RegistrationRequest getById(String id) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        return em.find(RegistrationRequest.class, id);
    }

    /**
     * Retrieves all pending registration requests, ordered by creation date.
     *
     * @return List of pending RegistrationRequest
     */
    @SuppressWarnings("unchecked")
    public List<RegistrationRequest> findAllPending() {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        Query q = em.createQuery(
                "select r from RegistrationRequest r where r.status = 'PENDING' order by r.createDate"
        );
        return q.getResultList();
    }

    /**
     * Finds a single pending request by username.
     *
     * @param username Username to search
     * @return RegistrationRequest or null if none pending
     */
    public RegistrationRequest findPendingByUsername(String username) {
        EntityManager em = ThreadLocalContext.get().getEntityManager();
        try {
            Query q = em.createQuery(
                    "select r from RegistrationRequest r where r.username = :username and r.status = 'PENDING'"
            );
            q.setParameter("username", username);
            return (RegistrationRequest) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
