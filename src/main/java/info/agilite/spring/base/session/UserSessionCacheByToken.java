package info.agilite.spring.base.session;

import java.util.Optional;

import info.agilite.spring.base.security.SecurityUser;

public interface UserSessionCacheByToken {
	Optional<SecurityUser> get(String token);
	void put(String token, SecurityUser user);
	void remove(String token);
}
