package com.betterhome.backend.service;

import com.betterhome.backend.exception.AppException;
import com.betterhome.backend.model.AppUser;
import com.betterhome.backend.model.Role;
import com.betterhome.backend.repository.AppUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    public static final String SESSION_USER_ID = "betterhome.userId";
    public static final String SESSION_ROLE = "betterhome.role";

    private final AppUserRepository userRepository;

    public SessionService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setSession(HttpSession session, AppUser user) {
        session.setAttribute(SESSION_USER_ID, user.getId());
        session.setAttribute(SESSION_ROLE, user.getRole().name());
    }

    public AppUser requireUser(HttpSession session) {
        Object id = session.getAttribute(SESSION_USER_ID);
        if (!(id instanceof Long userId)) {
            throw new AppException(HttpStatus.UNAUTHORIZED, "You must be logged in.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "Session is no longer valid."));
    }

    public AppUser requireAdmin(HttpSession session) {
        AppUser user = requireUser(session);
        if (user.getRole() != Role.ADMIN) {
            throw new AppException(HttpStatus.FORBIDDEN, "Admin access is required.");
        }
        return user;
    }
}
