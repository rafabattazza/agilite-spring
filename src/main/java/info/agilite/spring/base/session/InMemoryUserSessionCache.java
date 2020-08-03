package info.agilite.spring.base.session;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import info.agilite.spring.base.security.SecurityUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Cache padrão para armazenamento dos usuários logados.
 * <p>
 * 	<strong>Atenção...</strong> esse cache utiliza memória local para armazenamento. Para projetos que utilizem mais de um web-service ele deve ser substiuido por outro mecanismo de cache como o Redis por exemplo  
 * </p>
 * @author Rafael
 *
 */
@Service
@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal=true)
public class InMemoryUserSessionCache implements UserSessionCacheByToken {
	Cache<String, SecurityUser> cache;
	
	public InMemoryUserSessionCache(
			@Value("${info.agilite.base.memory-user-session-cache-max-size:100}")	Integer maxSize,
			@Value("${info.agilite.base.memory-user-session-cache-expire-minutes:15}") Integer expireMinutes) {
		this.cache = CacheBuilder.newBuilder().maximumSize(maxSize).expireAfterAccess(expireMinutes, TimeUnit.MINUTES).build();
	}
	
	@Override
	public Optional<SecurityUser> get(String token) {
		return Optional.ofNullable(cache.getIfPresent(token));
	}

	@Override
	public void remove(String token) {
		cache.invalidate(token);
	}
	
	@Override
	public void put(String token, SecurityUser username) {
		cache.put(token, username);
	}
}
