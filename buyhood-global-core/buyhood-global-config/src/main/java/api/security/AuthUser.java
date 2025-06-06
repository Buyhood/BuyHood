package api.security;

import api.enums.UserRole;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class AuthUser {

	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	public AuthUser(Long id, String email, UserRole role) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(role.getRole()));
	}
}
