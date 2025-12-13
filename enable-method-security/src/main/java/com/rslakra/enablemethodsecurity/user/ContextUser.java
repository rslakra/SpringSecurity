package com.rslakra.enablemethodsecurity.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ContextUser implements UserDetails {

    private String userName;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;
    private boolean accessToRestrictedPolicy;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    /**
     * @param restrictedPolicy whether user has access to restricted policy
     * @return this ContextUser
     */
    public ContextUser withAccessToRestrictedPolicy(boolean restrictedPolicy) {
        this.accessToRestrictedPolicy = restrictedPolicy;
        return this;
    }

    /**
     * @return true if user has access to restricted policy
     */
    public boolean hasAccessToRestrictedPolicy() {
        return accessToRestrictedPolicy;
    }

    /**
     * @param grantedAuthorities the granted authorities
     * @return this ContextUser
     */
    public ContextUser withGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
        return this;
    }

    /**
     * @param roles the roles
     * @return this ContextUser
     */
    public ContextUser withRoles(String... roles) {
        return withGrantedAuthorities(fromRoles(roles));
    }

    /**
     * @return the granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorities;
    }

    /**
     * @param password the password
     * @return this ContextUser
     */
    public ContextUser withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * @param userName the username
     * @return this ContextUser
     */
    public ContextUser withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * @return the username
     */
    @Override
    public String getUsername() {
        return this.userName;
    }

    /**
     * @return true if account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * @return true if account is not locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * @return true if credentials are not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * @return true if user is enabled
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return new ContextUser builder
     */
    public static ContextUser builder() {
        return new ContextUser();
    }

    /**
     * @param roles the roles
     * @return list of GrantedAuthority
     */
    public static List<GrantedAuthority> fromRoles(String... roles) {
        return Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
